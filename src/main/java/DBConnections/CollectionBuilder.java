package DBConnections;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;

import java.util.Calendar;
import java.util.Date;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;

/**
 * Created by scapista on 3/1/18.
 * +
 * + Priority:
 * + 1. build failing
 * + 2. function not working
 * + 3. nice to have but would optimize
 * + 4. nice to have no benefit
 * + 5. future state
 */

public class CollectionBuilder {
    protected MongoClient client;
    protected MongoDatabase database;
    protected MongoCollection collection;

    public CollectionBuilder(String database, String Collection){
        this.client = new MongoClient();
        this.database = this.client.getDatabase(database);
        this.collection = this.database.getCollection(Collection);
    }

    public void closeConnection(){ this.client.close();}

    protected Bson getTodayDateRange(String dateAttribute){
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR, -cal.getTime().getHours());
        cal.add(Calendar.MINUTE, -cal.getTime().getMinutes());
        cal.add(Calendar.SECOND, -cal.getTime().getSeconds());
        cal.add(Calendar.MILLISECOND,  -(int)(cal.getTimeInMillis()%1000) );
        Date yest = cal.getTime();

        return and(lt(dateAttribute, now),gt(dateAttribute, yest));
    }

}
