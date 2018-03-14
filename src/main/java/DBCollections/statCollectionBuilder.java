package DBCollections;

import DBCollections.statCollectionSubDocuments.FocusAppNode;
import DBCollections.statCollectionSubDocuments.SafariNode;
import DBConnections.StatCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

public class statCollectionBuilder {
    private static Logger logger = LogManager.getLogger(StatCollection.class.getName());
    public final String insert_dt = "insert_date";
    public final String focusApp_timeline = "focusApp_timeline";
    public final String safari_tabs = "safari_tabs";
    private Document fnlDocument;
    private ArrayList<SafariNode> arrSafariTabs;
    private ArrayList<FocusAppNode> arrFocusApp_timeline;
    boolean isNew;

    public statCollectionBuilder() {
        this.isNew = false;
        this.arrSafariTabs = new ArrayList<>();
        this.arrFocusApp_timeline = new ArrayList<>();
    }

    public statCollectionBuilder(boolean isNew) {
        this.isNew = isNew;
        this.arrSafariTabs = new ArrayList<>();
        this.arrFocusApp_timeline = new ArrayList<>();
    }

    public statCollectionBuilder(Document queryResult, boolean isNew) {
        this.arrSafariTabs = new ArrayList<>();
        this.isNew = isNew;
        setSafariNodes(queryResult);
        setArrFocusApp_timeline(queryResult);
    }

    public Document buildDocument() {
        return buildDocument(new Date(), getFocusAppDocuments(), getSafariNodesDocuments());
    }

    public Document buildDocument(Date createdDate, ArrayList<Document> lstFocusApplication, List<Document> safariTabs) {
        return new Document(this.insert_dt, createdDate)
                .append(this.focusApp_timeline, lstFocusApplication)
                .append(this.safari_tabs, safariTabs);
    }

    public void setSubDocuments(ArrayList<String[]> safariTabs, Document applicationDoc) {
        setSafariNodes(safariTabs);
        ArrayList<Document> lstAppolicationDoc = new ArrayList<>();
        lstAppolicationDoc.add(applicationDoc);
        setArrFocusApp_timeline(lstAppolicationDoc);
    }

    private void setSafariNodes(ArrayList<String[]> safariTabs) {
        for (String[] safariTab : safariTabs) {
            SafariNode safariNode =
                    new SafariNode(Integer.valueOf(safariTab[0]),
                            safariTab[2].toLowerCase(),
                            safariTab[1].toLowerCase(),
                            this.isNew
                    );
            this.arrSafariTabs.add(safariNode);
        }
    }

    private void setSafariNodes(Document statCollection) {
        for (Document tmpDoc : (ArrayList<Document>) statCollection.get(this.safari_tabs)) {
            SafariNode safariNode = new SafariNode(tmpDoc, isNew);
            arrSafariTabs.add(safariNode);
        }
    }

    private void setArrFocusApp_timeline(ArrayList<Document> inDocument) {
        for (Document tmpDoc : inDocument) {
            FocusAppNode focusAppNode = new FocusAppNode(tmpDoc);
            arrFocusApp_timeline.add(focusAppNode);
        }
    }

    private void setArrFocusApp_timeline(Document inDocument) {
        this.arrFocusApp_timeline = new ArrayList<>();
        for (Document tmpDoc : (ArrayList<Document>) inDocument.get(this.focusApp_timeline)) {
            logger.debug("Focus App" + tmpDoc);
            FocusAppNode focusAppNode = new FocusAppNode(tmpDoc);
            logger.debug(focusAppNode.getAppNodeDoc());
            arrFocusApp_timeline.add(focusAppNode);
        }
    }

    public ArrayList<Document> getSafariNodesDocuments() {
        ArrayList<Document> arrSafariDocuments = new ArrayList<>();
        for (SafariNode tmpSafariNode : this.arrSafariTabs) {
            arrSafariDocuments.add(tmpSafariNode.getDocSafariTab());
        }
        return logger.traceExit(arrSafariDocuments);
    }

    public ArrayList<SafariNode> getArrSafariTabs() {
        return arrSafariTabs;
    }

    public ArrayList<SafariNode> getArrClosedSafariTabs() {
        ArrayList<SafariNode> ArrClosedSafariNodes = new ArrayList<>();
        for (SafariNode tmpSafariNode : this.arrSafariTabs) {
            if (tmpSafariNode.isClosed())
                ArrClosedSafariNodes.add(tmpSafariNode);
        }
        return logger.traceExit(ArrClosedSafariNodes);
    }

    public ArrayList<SafariNode> getArrOpenSafariTabs() {
        ArrayList<SafariNode> ArrClosedSafariNodes = new ArrayList<>();
        for (SafariNode tmpSafariNode : this.arrSafariTabs) {
            if (!tmpSafariNode.isClosed())
                ArrClosedSafariNodes.add(tmpSafariNode);
        }
        return logger.traceExit(ArrClosedSafariNodes);
    }

    public ArrayList<Document> getFocusAppDocuments() {
        ArrayList<Document> arrFocusAppDocuments = new ArrayList<>();
        for (FocusAppNode tmpFocusAppNode : this.arrFocusApp_timeline) {
            arrFocusAppDocuments.add(tmpFocusAppNode.getAppNodeDoc());
        }
        return arrFocusAppDocuments;
    }

    public FocusAppNode getLastApp() {
        FocusAppNode retDoc = null;
        for (FocusAppNode tmpFocusAppNode : this.arrFocusApp_timeline) {
            if (retDoc == null)
                retDoc = tmpFocusAppNode;
            else if (tmpFocusAppNode.getApp_dt().after(retDoc.getApp_dt()))
                retDoc = tmpFocusAppNode;
        }
        return retDoc;
    }
    public FocusAppNode getNewApp(){
        return this.arrFocusApp_timeline.get(0);
    }

    public Document getFnlDocument(){
        return this.fnlDocument;
    }


}


