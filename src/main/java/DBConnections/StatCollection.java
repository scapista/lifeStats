package DBConnections;

import DBCollections.statCollectionSubDocuments.SafariNode;
import DBCollections.statCollectionBuilder;

import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

public class StatCollection extends CollectionBuilder{
    private static Logger logger = LogManager.getLogger(StatCollection.class.getName());
    private static final Marker updateRecord = MarkerManager.getMarker("Update");
    private static final Marker newRecord = MarkerManager.getMarker("New");

    public StatCollection(){
        super("devStats","statCollection");
    }

    public StatCollection setDevStatsStatCollection(ArrayList<String[]> safariTabs, Document focusApplication) throws IOException{
        logger.debug(new Object(){}.getClass().getEnclosingMethod().getName());

        statCollectionBuilder statCollectionBuilder = new statCollectionBuilder(true);
        statCollectionBuilder.setSubDocuments(safariTabs, focusApplication);
        Document currDoc = getCurrDocument(statCollectionBuilder.insert_dt);

        if (currDoc == null) {
            logger.debug(newRecord, new Object(){}.getClass().getEnclosingMethod().getName() + "--> New Document for the day");
            setNewDayStatsCollection(statCollectionBuilder);
        }
        else {
            logger.debug(updateRecord, new Object(){}.getClass().getEnclosingMethod().getName() + "--> Existing Document for the day");
            setUpdateDayStatsCollection(statCollectionBuilder, currDoc);
        }
        return this;
    }
    public Document getCurrDocument(String queryFilterDate){
        logger.traceEntry();
        return (Document) this.collection
                .find(getTodayDateRange(queryFilterDate))
                .sort(Sorts.descending(queryFilterDate))
                .first();
    }
    private void setNewDayStatsCollection(statCollectionBuilder statCollectionBuilder){
        logger.debug(newRecord, new Object(){}.getClass().getEnclosingMethod().getName());

        Document coll = statCollectionBuilder.buildDocument();

        //TODO: test for missing database
        this.collection.insertOne(coll);
        logger.info("inserted new field: _id field: " + coll.get("_id"));
    }
    private void setUpdateDayStatsCollection(statCollectionBuilder newStatCollection, Document queryResult){
        logger.debug(updateRecord, new Object(){}.getClass().getEnclosingMethod().getName());
        statCollectionBuilder DBStatCollection = new statCollectionBuilder(queryResult, false);
        ArrayList<Document> lstFocusApplication = new ArrayList<>();
        ArrayList<Document> lstSafariTabs = new ArrayList<>();

        lstFocusApplication.addAll(getFinalFocusAppList(DBStatCollection, getLastApp(DBStatCollection, newStatCollection)));
        lstSafariTabs.addAll(
                getFinalSafariList(
                    getNewClosedSafariTabs(newStatCollection, DBStatCollection, getClosedSafariTabs(newStatCollection, DBStatCollection)),
                    getOpenedSafariTabs(newStatCollection, DBStatCollection),
                    DBStatCollection.getArrOpenSafariTabs()
                )
        );

        statCollectionBuilder updateColl = new statCollectionBuilder();
        updateColl.buildDocument(queryResult.getDate(updateColl.insert_dt),lstFocusApplication, lstSafariTabs);

        UpdateResult updateResult = this.collection.updateOne(eq("_id",queryResult.getObjectId("_id")),
                new Document("$set",updateColl.buildDocument(queryResult.getDate(updateColl.insert_dt),lstFocusApplication, lstSafariTabs)));
        logger.trace(updateResult);
    }

    private ArrayList<SafariNode> getClosedSafariTabs(statCollectionBuilder newStatCollection,statCollectionBuilder DBStatCollection) {
        logger.debug(new Object() {}.getClass().getEnclosingMethod().getName());
        Collection<SafariNode> currentDayCollection = DBStatCollection.getArrClosedSafariTabs();
        ArrayList<SafariNode> retList = new ArrayList<>();

        for (SafariNode coll: currentDayCollection){
            retList.add(coll);
            logger.debug("closed tab ==> " + coll.getDocSafariTab());
        }
        logger.trace(StatCollection.class.getClass().getEnclosingMethod());
        return logger.traceExit(retList);
    }
    private ArrayList<SafariNode> getNewClosedSafariTabs(statCollectionBuilder newStatCollection,statCollectionBuilder DBStatCollection, ArrayList<SafariNode> DBClosedTabs) {
        logger.debug(new Object() {}.getClass().getEnclosingMethod().getName());
        Collection<SafariNode> currentDayCollection = DBStatCollection.getArrOpenSafariTabs();
        Collection<SafariNode> newCollection = newStatCollection.getArrSafariTabs();
        ArrayList<SafariNode> retList = new ArrayList<>();

        currentDayCollection.removeAll(newCollection);
        currentDayCollection.removeAll(DBClosedTabs);

        for (SafariNode coll: currentDayCollection){
            coll.setClose_dt();
            retList.add(coll);
            logger.debug("new closed tab ==> " + coll.getDocSafariTab());
        }
        retList.addAll(DBClosedTabs);
        logger.trace(StatCollection.class.getClass().getEnclosingMethod());
        return logger.traceExit(retList);
    }
    private ArrayList<SafariNode> getOpenedSafariTabs(statCollectionBuilder newStatCollection, statCollectionBuilder DBStatCollection) {
        logger.debug(new Object() {}.getClass().getEnclosingMethod().getName());
        Collection<SafariNode> currentDayCollection = DBStatCollection.getArrOpenSafariTabs();
        Collection<SafariNode> newCollection = newStatCollection.getArrSafariTabs();
        ArrayList<SafariNode> retList = new ArrayList<>();

        newCollection.removeAll(currentDayCollection);

        for (SafariNode coll: newCollection){
            retList.add(coll);
            logger.debug("open tab ==> " + coll.getDocSafariTab());
        }
        return logger.traceExit(retList);
    }
    private Document getLastApp(statCollectionBuilder dbTabs, statCollectionBuilder newTab) {
        //Document retDoc = new Document();
        if(!dbTabs.getLastApp().getApplication().equals(newTab.getNewApp().getApplication())) {
            return newTab.getNewApp().getAppNodeDoc();
        }
        return null;
    }
    private ArrayList<Document> getFinalSafariList(ArrayList<SafariNode> closedTabs, ArrayList<SafariNode> newTabs , ArrayList<SafariNode> currentDBTabs){
        logger.debug(new Object() {}.getClass().getEnclosingMethod().getName());
        ArrayList<Document> retList = new ArrayList<>();

        for (SafariNode coll : closedTabs){
            logger.debug("closed tabs --> " + coll.getDocSafariTab());
            retList.add(coll.getDocSafariTab());
        }
        for (SafariNode coll: newTabs){
            logger.debug("newTabs tabs --> " + coll.getDocSafariTab());
            retList.add(coll.getDocSafariTab());
        }
        for (SafariNode coll: currentDBTabs){
            logger.debug("currentTabs tabs --> " + coll.getDocSafariTab());
            retList.add(coll.getDocSafariTab());
        }
        return logger.traceExit(retList);
    }
    private ArrayList<Document> getFinalFocusAppList(statCollectionBuilder dbTabs, Document newTab){
        logger.debug(new Object() {}.getClass().getEnclosingMethod().getName());
        ArrayList<Document> retList = new ArrayList<>();

        for (Document tmpDoc: dbTabs.getFocusAppDocuments()){
            logger.debug("oldTabs tabs --> " + tmpDoc);
            retList.add(tmpDoc);
        }
        if (newTab != null)
            retList.add(newTab);

        return logger.traceExit(retList);
    }
    public static void main(String args[]){
        StatCollection cb = new StatCollection();
    }


}
