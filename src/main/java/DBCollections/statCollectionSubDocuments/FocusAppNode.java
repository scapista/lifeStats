package DBCollections.statCollectionSubDocuments;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.util.Date;

import static DBCollections.statCollectionSubDocuments.SafariNode.logger;

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

public class FocusAppNode {
    private final String focusAppTimeline_AppDt = "App_dt";
    private final String focusAppTimeline_App   = "Application";
    private final String focusAppTimeline_url   = "tabURL";

    private String application;
    private String url;
    private Date app_dt;
    private Document appNodeDoc;

    public FocusAppNode(Document inDoc){
        this.appNodeDoc = inDoc;
        try {
            this.application = inDoc.getString(focusAppTimeline_App);
        } catch (Exception e){
        this.application = null;
        logger.info(inDoc + " --> contains an invalid application value");
        e.printStackTrace();
        }
        try {
            this.app_dt = inDoc.getDate(focusAppTimeline_AppDt);
        } catch (Exception e){
            this.app_dt = null;
            logger.info(inDoc + " --> contains an invalid appDate value");
            e.printStackTrace();
        }
        try {
            this.url = inDoc.getString(focusAppTimeline_url);
        } catch (Exception e){
            this.url = null;
            logger.info(inDoc + " --> contains an invalid appDate value");
            e.printStackTrace();
        }
        logger.debug("FocusAppNode Doc => " + this.appNodeDoc);

    }
    FocusAppNode(String app){
        this.application = app;
        this.app_dt = new Date();
        this.appNodeDoc =
                new Document(focusAppTimeline_App, this.focusAppTimeline_App)
                .append(focusAppTimeline_AppDt, this.focusAppTimeline_AppDt);
    }
    public Document getAppNodeDoc(){
        return this.appNodeDoc;
    }

    public Date getApp_dt() {
        return app_dt;
    }

    public String getApplication() {
        return application;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FocusAppNode)) return false;

        FocusAppNode that = (FocusAppNode) o;

        if (!application.equals(that.application)) return false;
        return app_dt.after(that.app_dt);

    }

    @Override
    public int hashCode() {
        int result = application.hashCode();
        result = 31 * result + app_dt.hashCode();
        return result;
    }
}
