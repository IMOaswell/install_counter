package imo.install_counter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.text.ParseException;

public class StatsAnalytics
 {
    static Map pkgNamesAndDirs = null;
    static String recordedProjects(Context mContext){
        StringBuilder sb = new StringBuilder();
        if(pkgNamesAndDirs == null) 
            pkgNamesAndDirs = ProjectFinder.getActiveProjects(mContext);
        for (Map.Entry<String, String> entry : pkgNamesAndDirs.entrySet()) {
            sb.append("\n" + entry.getKey().replace("/", "."));
        }
        return sb.toString();
    }
    
    static String timeSinceLastLog(Context mContext){
        StringBuilder sb = new StringBuilder(); 
        if(pkgNamesAndDirs == null) 
            pkgNamesAndDirs = ProjectFinder.getActiveProjects(mContext);
        
        for (Map.Entry<String, String> entry : pkgNamesAndDirs.entrySet()) {
            String projectDirPath = entry.getValue();
            File stats_log = new File(projectDirPath,  "stats.log");
            if(!stats_log.exists()) continue;
            Stat stat = StatsReader.getLastStat(mContext, stats_log);
            String dateAndTimeString = stat.DATE + " " + stat.TIME;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd hh:mma");
            try {
                Date date = format.parse(dateAndTimeString);
                Date currentDate = new Date();

                long timeDifferenceMillis = Math.abs(currentDate.getTime() - date.getTime());

                long seconds = timeDifferenceMillis / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                
                if(days > 0) sb.append(days + " D ");
                if(hours > 0) sb.append((hours % 24) + " H ");
                if(minutes > 0) sb.append((minutes % 60) + " Min ");
                if(seconds > 0) sb.append((seconds % 60) + " Sec ago");
                return sb.toString();
            } catch (ParseException e) {
                System.out.println(e);
            }

        }
        return "--";
    }
}
