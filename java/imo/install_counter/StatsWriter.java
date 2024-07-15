package imo.install_counter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class StatsWriter{
    static void recordStat(Context mContext,File stats_log,int index){
        recordStat(mContext, stats_log, index, false);
    }

    static void recordStat(Context mContext,File stats_log,int index,boolean isAmmend){
        //will record e.g 44 2024-MAY-19 01:39pm files:3 +27 -6
        Calendar cal = Calendar.getInstance();
        String recordString = index + " " + getCurrentDate(cal) + " " + getCurrentTime(cal) + " ";
        String previousContent = StatsReader.read(stats_log);
        write(stats_log, previousContent + "\n" + recordString);
        addGitChanges(mContext, stats_log, isAmmend);
    }

    private static String getCurrentDate(Calendar calendar){
        //will return e.g 2024-MAY-19
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
        return dateFormat.format(calendar.getTime());
    }

    private static String getCurrentTime(Calendar calendar){
        //will return e.g 01:39pm
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma");
        return dateFormat.format(calendar.getTime());
    }

    private static void addGitChanges(Context mContext,File stats_log,boolean isAmmend){
        //will add e.g files:3 +27 -6
        String script = "cd \'" + StatsReader.getMainDirPath(stats_log) + "\' \n";

        String addChanges = StatsReader.readAsset(mContext, "recordChanges.sh") + " \n";
        addChanges += "echo $output >> '" + stats_log.getAbsolutePath() + "' \n";
        String commit = StatsReader.readAsset(mContext, "commit.sh") + " \n";
        String ammend = StatsReader.readAsset(mContext, "commitAmmend.sh") + " \n";

        script += addChanges + "\n";
        script += isAmmend ? ammend : commit;
        TermuxTools.runScript(mContext, script);
    }

    static void optimizeStatsLog(File stats_log){
        collapseDates(stats_log);
        updateStatsLog(stats_log);
    }

    static void collapseDates(File stats_log){
        String[] logs = StatsReader.read(stats_log).split("\n");
        HashSet<String> dateSet = new HashSet<>();
        String[] outputs = new String[logs.length];

        for(int i = 0; i < logs.length; i++){
            Stat stat = new Stat(logs[i]);
            String date = stat.DATE;

            if(dateSet.contains(date)){
                outputs[i] = logs[i].replace(date, "*");
            }else{
                dateSet.add(date);
                outputs[i] = logs[i];
            }
        }
        String newContent = "";
        for(String output : outputs){
            newContent += output + "\n";
        }

        write(stats_log, newContent);
    }

    static void updateStatsLog(File stats_log){
        String newContent = StatsReader.read(stats_log).replace("am", "AM");
        newContent = StatsReader.read(stats_log).replace("pm", "PM");
        write(stats_log, newContent);
    }

    private static void write(File file,String input){
        try{
            if(!file.exists()) file.createNewFile();
            if(file.isFile()){
                FileWriter fw = new FileWriter(file);
                fw.write(input);
                fw.close();
            }
        }catch(IOException e){}
    }

}
