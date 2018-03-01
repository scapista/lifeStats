package SystemCommandRun;

import DBConnections.CollectionBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

/**
 * Created by scapista on 2/24/18.
 * +
 * + This Class is Meant to illustrate the current state of a macbook
 */

public class CommandDataStructures {
    private List<String> cmds = new ArrayList<String>();
    private List<String> lstTabs = new ArrayList<String>();
    private String[] currTabApplication = new String[2];


    CommandDataStructures(){
        setSafariTabs();
        setCurrTabWindow();
    }
    public List<String> getSafariTabs(){return this.lstTabs;}
    public String getCurrWindow(){return this.currTabApplication[0];}
    public String getCurrSafariTabURL(){return this.currTabApplication[1];}

    public List<Document> getSafariTabDoc(){
        List<Document> docList = new ArrayList<Document>();
        for (String safariTab : this.lstTabs) {
            String[] tmpStr = safariTab.split("[|]",3);
            try {
                //System.out.println(tmpStr[0]);
                docList.add( new Document("windowNumber",Integer.valueOf(tmpStr[0]))
                        .append("tab", tmpStr[2].toLowerCase())
                        .append("url", tmpStr[1].toLowerCase())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return docList;
    }
    public Document getFocusApplicationDoc(){
        Document tmpDoc = new Document("App_dt", Instant.now().toEpochMilli())
                .append("Application", getCurrWindow());
        if (getCurrWindow().equals("Safari"))
            tmpDoc = tmpDoc.append("tabURL", getCurrSafariTabURL());
        return tmpDoc;
    }
    private void setSafariTabs(){
        setSafariCmds();
        for(String tmpStr :getCommandOutputText().split("\n")){
            this.lstTabs.add(tmpStr);
        }
    }
    private void setCurrTabWindow(){
        String[] tmpStr;
        setCurrTabCmds();
        tmpStr = getCommandOutputText().split("[|]");
        for(int i = 0; i < tmpStr.length; i++){
            this.currTabApplication[i] = tmpStr[i];
        }
    }
    private String getCommandOutputText(){
        SystemCommandExecutor exec = new SystemCommandExecutor(this.cmds);
        try {
            exec.executeCommand();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(exec.getStandardErrorFromCommand().length() != 0) {
            System.out.println(exec.getStandardErrorFromCommand());
            return "";
        } else {
            return exec.getStandardOutputFromCommand().toString();
        }
    }
    private void setSafariCmds(){
        cmds.clear();
        cmds.add("/usr/bin/osascript");
        cmds.add("src/main/resources/appleScript.scpt");
    }
    private void setCurrTabCmds(){
        cmds.clear();
        cmds.add("/usr/bin/osascript");
        cmds.add("src/main/resources/curr_tab.scpt");
    }
    public static void main(String args[]){
        CommandDataStructures cmd = new CommandDataStructures();
        CollectionBuilder SafariTabs = new CollectionBuilder("devStats","statCollection");
        //System.out.println(cmd.getFocusApplicationDoc());
        //System.out.println(cmd.getSafariTabDoc());
        SafariTabs.setDevStatsStatCollection(cmd.getSafariTabDoc(), cmd.getFocusApplicationDoc()).closeConnection();
    }

}
