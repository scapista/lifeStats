package DBCollections.statCollectionSubDocuments;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.util.Date;

/**
 * Created by scapista on 3/6/18.
 * +
 * + Priority:
 * + 1. build failing
 * + 2. function not working
 * + 3. nice to have but would optimize
 * + 4. nice to have no benefit
 * + 5. future state
 */

public class SafariNode {
    static Logger logger = LogManager.getLogger(SafariNode.class);

    //safariTabs feildnames
    private final String safariTabs_windowNumber = "windowNumber";
    private final String safariTabs_tab          = "tab";
    private final String safariTabs_url          = "url";
    private final String safariTabs_close_dt     = "close_dt";
    private final String safariTabs_open_dt      = "open_dt";

    //used for object compare logic
    private boolean isNew;

    private int windowNumber;
    private String tab;
    private String url;
    private Date open_dt;
    private Date close_dt;
    private Document docSafariTab;

    public SafariNode(Document inDoc, boolean isNew) {
        this.docSafariTab = inDoc;
        try {
            this.windowNumber = inDoc.getInteger(safariTabs_windowNumber);
        } catch (Exception e){
            this.windowNumber = 1;
            logger.info(inDoc + " --> contains an invalid windowNumber value");
            e.printStackTrace();
        }
        try {
            this.tab = inDoc.getString(safariTabs_tab);
        } catch (Exception e){
            this.tab = null;
            logger.info(inDoc + " --> contains an invalid tab value");
            e.printStackTrace();
        }
        try {
            this.url = inDoc.getString(safariTabs_url);
        } catch (Exception e){
            this.url = null;
            logger.info(inDoc + " --> contains an invalid url value");
            e.printStackTrace();
        }
        try {
            this.open_dt = inDoc.getDate(safariTabs_open_dt);
        } catch (Exception e){
            this.open_dt = null;
            logger.info(inDoc + " --> contains an invalid open_dt value");
            e.printStackTrace();
        }
        try {
            this.close_dt = inDoc.getDate(safariTabs_close_dt);
        } catch (Exception e){
            this.close_dt = null;
            logger.info(inDoc + " --> contains an invalid close_dt value");
            e.printStackTrace();
        }
        this.isNew = isNew;
    }
    public SafariNode(int windowNumber, String tab, String url, boolean isNew){
        this.windowNumber = windowNumber;
        this.tab = tab;
        this.url = url;
        this.open_dt = new Date();
        this.close_dt = null;
        this.isNew = isNew;
        setDocSafariTab();
    }

    public Date getOpen_dt() {
        return open_dt;
    }

    public String getSafariTabs_close_dt() {
        return safariTabs_close_dt;
    }

    public String getSafariTabs_open_dt() {
        return safariTabs_open_dt;
    }

    public String getSafariTabs_tab() {
        return safariTabs_tab;
    }

    public String getSafariTabs_url() {
        return safariTabs_url;
    }

    public String getSafariTabs_windowNumber() {
        return safariTabs_windowNumber;
    }

    public String getTab() {
        return tab;
    }

    public String getUrl() {
        return url;
    }

    public int getWindowNumber() {
        return windowNumber;
    }

    public Date getClose_dt() {
        return close_dt;
    }

    public boolean setClose_dt(){
        if (this.close_dt == null) {
            this.close_dt = new Date();
            this.docSafariTab = this.docSafariTab
                    .append(safariTabs_close_dt, this.close_dt);

            return true;
        }
        else return false;

    }

    public Document getDocSafariTab() {
        return docSafariTab;
    }

    private void setDocSafariTab(){
        this.docSafariTab = new Document(safariTabs_windowNumber, this.windowNumber)
                .append(safariTabs_tab, this.tab)
                .append(safariTabs_url, this.url)
                .append(safariTabs_open_dt, this.open_dt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SafariNode)) return false;

        SafariNode that = (SafariNode) o;
        logger.trace("that --> " + that.getDocSafariTab());
        logger.trace("this --> " + this.getDocSafariTab());
        logger.trace("created new safari node");
        if (getWindowNumber() != that.getWindowNumber()) return false;
        logger.trace("window number passed");
        if (!getTab().equals(that.getTab())) return false;
        logger.trace("tab name passed");
        if (!getUrl().equals(that.getUrl())) return false;
        logger.trace("url name passed");
        if(!(this.isNew() || that.isNew())) {
            if (!getOpen_dt().equals(that.getOpen_dt())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = getWindowNumber();
        result = 31 * result + getTab().hashCode();
        result = 31 * result + getUrl().hashCode();
        return result;
    }

    public boolean isClosed(){
        return this.close_dt != null;
    }

    public boolean isNew(){
        return this.isNew;
    }


}
