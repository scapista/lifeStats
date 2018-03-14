package SystemCommandRun;

import DBConnections.StatCollection;

import java.io.IOException;
import java.util.Date;
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
    //public List<String> getSafariTabs(){return this.lstTabs;}
    public String getCurrWindow(){return this.currTabApplication[0];}
    public String getCurrSafariTabURL(){return this.currTabApplication[1].replace("\n", "");}

    public ArrayList<String[]> getSafariTabArray(){
        ArrayList<String[]> safariTabs = new ArrayList<>();
        for (String safariTab : this.lstTabs) {
            String[] tmpStr = safariTab.split("[|]", 3);
            safariTabs.add(tmpStr);
        }
        return safariTabs;
    }
    public Document getFocusApplicationDoc(){
        Document tmpDoc = new Document("App_dt", new Date())
                .append("Application", getCurrWindow());
        if (getCurrWindow().equals("Safari"))
            tmpDoc = tmpDoc.append("tabURL", getCurrSafariTabURL());
        //System.out.println(tmpDoc);
        return tmpDoc;
    }
    private void setSafariTabs(){
        setSafariCmds();
        for(String tmpStr :getCommandOutputText().split("\n")){
            this.lstTabs.add(tmpStr);
            System.out.println("setSafariTabs " + tmpStr);
        }
    }
    private void setCurrTabWindow(){
        String[] tmpStr;
        setCurrTabCmds();
        tmpStr = getCommandOutputText().split("[|]");
        for(int i = 0; i < tmpStr.length; i++){
            this.currTabApplication[i] = tmpStr[i];
            System.out.println("setCurrTabWindow " + tmpStr[i]);
        }
    }
    private String getCommandOutputText(){
        SystemCommandExecutor exec = new SystemCommandExecutor(this.cmds);
        try {
            exec.executeCommand();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        boolean loopBool = true;
        while(loopBool) {
            CommandDataStructures cmd = new CommandDataStructures();
            StatCollection SafariTabs = new StatCollection();
            //System.out.println(cmd.getFocusApplicationDoc());
            try {
                SafariTabs.setDevStatsStatCollection(cmd.getSafariTabArray(), cmd.getFocusApplicationDoc()).closeConnection();
                Thread.sleep(10000);
            } catch (Exception e){
                e.printStackTrace();
                loopBool = false;
            }
        }
    }

}
