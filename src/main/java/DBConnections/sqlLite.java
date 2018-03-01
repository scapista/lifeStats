package DBConnections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static javax.swing.UIManager.getString;

/**
 * Created by scapista on 2/28/18.
 * +
 * + Priority:
 * + 1. build failing
 * + 2. function not working
 * + 3. nice to have but would optimize
 * + 4. nice to have no benefit
 * + 5. future state
 */

public class sqlLite {
    Connection connection = null;

    sqlLite(){
        setConnection();
    }

    private void setConnection(){
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/scapista/Library/Safari/History.db");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.print(e.getMessage());
        }
    }

    public ArrayList<String[]> getHistory () {
        ArrayList<String[]> siteList = new ArrayList<String[]>();
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery(
                    "SELECT h.visit_time, " +
                            "datetime( h.visit_time,'unixepoch','+31 years','-5 hours') as insert_dt, " +
                            "i.domain_expansion, " +
                            "i.url " +
                    "FROM history_visits h " +
                    "   INNER JOIN history_items i " +
                    "       ON h.history_item = i.id " +
                    "order by h.visit_time;");
            while(rs.next()) {
                String [] elements = new String[4];
                elements[0] = rs.getString(1);
                elements[1] = rs.getString(2);
                elements[2] = rs.getString(3);
                elements[3] = rs.getString(4);
                siteList.add(elements);
            }
        } catch(SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if(connection != null)
                    connection.close();
            } catch(SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
        return siteList;
    }
    public static void main(String args[]){
        sqlLite db = new sqlLite();

        for (String[] strArray : db.getHistory()){
            for (int i = 0; i < strArray.length; i++){
                System.out.print(strArray[i] + "|");
            }
            System.out.print("\n");
        }
    }
}
