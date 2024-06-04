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

    static class Graph {
        static List<Stat> stats;
        static String packageName;
        static final int TODAY = 0;
        static final int PAST_30_DAYS = 1;
        
        static View make(Context mContext, String pkgName, int code){
            if(pkgName != null) populateVariables(mContext, pkgName);
            switch(code){
                case TODAY:
                    return graphOfToday(mContext, packageName, stats);
                case PAST_30_DAYS:
                    return graphOfPastDays(mContext, packageName, stats, 30);
            }
            return new TextView(mContext);
        }
        
        static void populateVariables(Context mContext, String pkgName){
            packageName = pkgName;
            File stats_log = getStatsLog(mContext, packageName);
            stats = StatsReader.getStats(mContext, stats_log);
            Collections.reverse(stats);
        }
        
        static View graphOfToday(Context mContext, String packageName, List<Stat> stats){
            List<Integer> dataForEachHour = new ArrayList<>();
            List<String> stringsForEachHour = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                dataForEachHour.add(0);
                
                SimpleDateFormat hour_string_sdf = new SimpleDateFormat("ha");
                Date date = new Date();
                date.setHours(i);
                stringsForEachHour.add(hour_string_sdf.format(date) + " : ");
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
            String today = sdf.format(new Date());
            boolean isLastStatsDateToday = today.equals(stats.get(0).DATE);
            if(!isLastStatsDateToday) return BarGraphView.create(mContext, dataForEachHour);
            
            for(Stat stat : stats){
                if(!today.equals(stat.DATE)) break;
                Date statToday = StatsReader.getDate(stat);
                SimpleDateFormat hour_sdf = new SimpleDateFormat("HH");
                int hour = Integer.parseInt(hour_sdf.format(statToday));
                
                int hourData = dataForEachHour.get(hour);
                hourData++;
                dataForEachHour.set(hour, hourData);
            }
            
            return BarGraphView.create(mContext, dataForEachHour, stringsForEachHour);
        }
        
        static View graphOfPastDays(Context mContext, String packageName, List<Stat> stats, int daysRange){
            List<Integer> dataForEachDay = new ArrayList<>();
            List<String> stringsForEachDay = new ArrayList<>();
            
            List<String> dates = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
            Calendar calendar = Calendar.getInstance();
            
            for (int i = 0; i < daysRange; i++) {
                dataForEachDay.add(0);
                String date = sdf.format(calendar.getTime());
                dates.add(date);
                stringsForEachDay.add(date + " : ");
                calendar.add(Calendar.DAY_OF_YEAR, -1);
            }
            
            boolean isLastStatsDateIncluded = false;
            for(String date : dates){
                if(stats.get(0).DATE.equals(date)){
                    isLastStatsDateIncluded = true;
                    break;
                }
            }
            if(!isLastStatsDateIncluded) return BarGraphView.create(mContext, dataForEachDay, stringsForEachDay);
            
            for(Stat stat : stats){
                int i = dates.indexOf(stat.DATE);
                if(i == -1) break;
                int dayData = dataForEachDay.get(i);
                dayData++;
                dataForEachDay.set(i, dayData);
            }
            
            Collections.reverse(dataForEachDay);
            Collections.reverse(stringsForEachDay);
            return BarGraphView.create(mContext, dataForEachDay, stringsForEachDay);
        }
    }
}
