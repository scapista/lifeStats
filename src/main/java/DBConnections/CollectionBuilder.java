package DBConnections;


import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Time;
import java.util.ArrayList;
import java.time.*;

import java.util.List;

import static javafx.scene.input.KeyCode.T;


public class CollectionBuilder {
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection collection;
    private List<String> anchorSafariList = new ArrayList<String>();



    public CollectionBuilder(String database, String Collection){
        this.client = new MongoClient();
        this.database = this.client.getDatabase(database);
        this.collection = this.database.getCollection(Collection);
    }

    public CollectionBuilder setDevStatsStatCollection(List<Document> safariTabs, Document focusApplication){
        setNewDayStatsCollection(safariTabs, focusApplication);

        return this;
    }
    public void closeConnection(){ this.client.close();}
    private void setUpdateDayStatsCollection(List<Document> safariTabs, Document focusApplication){
        Document coll = new Document("date",Instant.now().toEpochMilli())
                .append("focusApp", focusApplication)
                .append("safari tabs", safariTabs);
    }
    private void setNewDayStatsCollection(List<Document> safariTabs, Document focusApplication){
        Document coll = new Document("date",Instant.now().toEpochMilli())
                .append("focusApp", focusApplication)
                .append("safari tabs", safariTabs);
        //TODO: test for missing database
        this.collection.insertOne(coll);
    }
    public FindIterable getQuery(){
        return this.collection.find();
    }
    public static void main(String args[]){
        CollectionBuilder cb = new CollectionBuilder("devStats","statCollection");
        for (Object tmpDoc : cb.getQuery()){
            System.out.println(tmpDoc);
        }
    }
}
