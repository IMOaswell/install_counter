package imo.install_counter;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class StatsAnalytics
{
    static Map pkgNamesAndDirs = null;
    static Map setPkgNameAndDirsMap (Context mContext) {
        if (pkgNamesAndDirs == null) 
            pkgNamesAndDirs = ProjectFinder.getActiveProjects(mContext);
        return pkgNamesAndDirs;
    }

    static String timeSinceLastLog (Context mContext, String packageName) {
        setPkgNameAndDirsMap(mContext);
        StringBuilder sb = new StringBuilder();

        File stats_log = getStatsLog(mContext, packageName);
        if (!stats_log.exists()) return null;
        Date date = StatsReader.getDate(StatsReader.getLastStat(mContext, stats_log));
        Date currentDate = new Date();

        long timeDifferenceMillis = Math.abs(currentDate.getTime() - date.getTime());

        long seconds = timeDifferenceMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) sb.append(days + "d ");
        if (hours > 0) sb.append((hours % 24) + "h ");
        if (minutes > 0) sb.append((minutes % 60) + "m ");
        if (seconds > 0) sb.append((seconds % 60) + "s ago");
        return sb.toString();
    }

    static List<String> getPackageNames (Context mContext) {
        setPkgNameAndDirsMap(mContext);
        List<String> packageNames = new ArrayList<>();
        for (Map.Entry<String, String> entry : pkgNamesAndDirs.entrySet()) {
            packageNames.add(entry.getKey());
        }
        return packageNames;
    }

    static String getProjectDirPath (Context mContext, String packageName) {
        setPkgNameAndDirsMap(mContext);
        return (String) pkgNamesAndDirs.get(packageName);
    }

    static File getStatsLog (Context mContext, String packageName) {
        return new File(getProjectDirPath(mContext, packageName),  "stats.log");
    }

    static class GraphMaker {
        
        static View last24hours(Context mContext, String packageName){
            List<Integer> graphData = new ArrayList<>();
            File stats_log = getStatsLog(mContext, packageName);
            List<Stat> statsDescending = StatsReader.getStats(mContext, stats_log);
            Collections.reverse(statsDescending);
            
            final int HOURS = 24;
            Calendar calendar = Calendar.getInstance();
            int minutes = calendar.get(Calendar.MINUTE);
            if(minutes < 30){ //round to nearest hour
                calendar.set(Calendar.MINUTE, 0);
            }else{
                calendar.add(Calendar.HOUR_OF_DAY, 1);
            }
            Date currentDate = new Date();
            
            int insertsAndDeletes = 0;
            for (int i = 1; i < HOURS + 1; i++) {
                calendar.setTime(currentDate);
                calendar.add(Calendar.HOUR_OF_DAY, -i);
                Date date = calendar.getTime();
                
                List<Stat> usedStats = new ArrayList<>();
                for(Stat stat : statsDescending){
                    Date statDate = StatsReader.getDate(stat);
                    if(statDate.after(date) || statDate.equals(date)){
                        insertsAndDeletes += stat.INSERTS + stat.DELETES;
                        usedStats.add(stat);
                    }else{
                        break;
                    }
                }
                for(Stat usedStat : usedStats){
                    statsDescending.remove(usedStat);
                }
                graphData.add(insertsAndDeletes);
                insertsAndDeletes = 0;
            }
            Collections.reverse(graphData);
            
            return BarGraphView.create(mContext, graphData);
        }
//        static View last7days(Context mContext){
//            return BarGraphView.create(mContext);
//        }
//        static View last30days(Context mContext){
//            return BarGraphView.create(mContext);
//        }
        static View test (Context mContext) {
            List<Integer> i = new ArrayList<>();
            i.add(700);
            i.add(50);
            i.add(120);
            return BarGraphView.create(mContext, i);
        }
    }
}
