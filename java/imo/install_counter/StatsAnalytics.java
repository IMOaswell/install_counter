package imo.install_counter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class StatsAnalytics
 {
    static Map pkgNamesAndDirs = null;
    static Map setPkgNameAndDirsMap(Context mContext){
        if(pkgNamesAndDirs == null) 
            pkgNamesAndDirs = ProjectFinder.getActiveProjects(mContext);
        return pkgNamesAndDirs;
    }
    
    static String timeSinceLastLog(Context mContext, String packageName){
        setPkgNameAndDirsMap(mContext);
        StringBuilder sb = new StringBuilder();
        String projectDirPath = (String) pkgNamesAndDirs.get(packageName);
        File stats_log = new File(projectDirPath,  "stats.log");
        if(!stats_log.exists()) return null;
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
        return "--";
    }
    
    static List<String> getPackageNames(Context mContext){
        setPkgNameAndDirsMap(mContext);
        List<String> packageNames = new ArrayList<>();
        for (Map.Entry<String, String> entry : pkgNamesAndDirs.entrySet()) {
            packageNames.add(entry.getKey());
        }
        return packageNames;
    }
}
