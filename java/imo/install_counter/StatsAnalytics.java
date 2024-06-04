package imo.install_counter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatsAnalytics{
    static Map pkgNamesAndDirs = null;
    static Map setPkgNameAndDirsMap(Context mContext){
        if(pkgNamesAndDirs == null) 
            pkgNamesAndDirs = ProjectFinder.getActiveProjects(mContext);
        return pkgNamesAndDirs;
    }

    static String timeSinceLastLog(Context mContext,String packageName){
        setPkgNameAndDirsMap(mContext);
        StringBuilder sb = new StringBuilder();

        File stats_log = getStatsLog(mContext, packageName);
        if(!stats_log.exists()) return null;
        Date date = StatsReader.getDate(StatsReader.getLastStat(mContext, stats_log));
        Date currentDate = new Date();

        long timeDifferenceMillis = Math.abs(currentDate.getTime() - date.getTime());

        long seconds = timeDifferenceMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if(days > 0) sb.append(days + "d ");
        if(hours > 0) sb.append((hours % 24) + "h ");
        if(minutes > 0) sb.append((minutes % 60) + "m ");
        if(seconds > 0) sb.append((seconds % 60) + "s ago");
        return sb.toString();
    }

    static List<String> getPackageNames(Context mContext){
        setPkgNameAndDirsMap(mContext);
        List<String> packageNames = new ArrayList<>();
        for(Map.Entry<String, String> entry : pkgNamesAndDirs.entrySet()){
            packageNames.add(entry.getKey());
        }
        return packageNames;
    }

    static String getProjectDirPath(Context mContext,String packageName){
        setPkgNameAndDirsMap(mContext);
        return (String) pkgNamesAndDirs.get(packageName);
    }

    static File getStatsLog(Context mContext,String packageName){
        return new File(getProjectDirPath(mContext, packageName),  "stats.log");
    }

    static class Graph{
        static List<Stat> stats;
        static String packageName;
        static final int TODAY = 0;
        static final int PAST_30_DAYS = 1;
        
        static class Modes {
            static final String INSERTS = "Insertions";
            static final String DELETES = "Deletions";
            static final String INSERTS_AND_DELETES = "Combined";
            static final String LOGS = "Logs";
            static final String[] modes = {INSERTS, DELETES, INSERTS_AND_DELETES, LOGS};
            private static String current = INSERTS;
            
            static void set(String code){
                current = code;
            }
            
            static int addDataByMode(Stat stat){
                switch(current){
                    case INSERTS:
                        return stat.INSERTS;
                    case DELETES:
                        return stat.DELETES;
                    case INSERTS_AND_DELETES:
                        return stat.INSERTS + stat.DELETES;
                    case LOGS:
                        return 1;
                }
                return -1;
            }
        }
        
        static View make(Context mContext,String pkgName,int code){
            if(pkgName != null) populateVariables(mContext, pkgName);
            switch(code){
                case TODAY:
                    return graphOfToday(mContext, packageName, stats);
                case PAST_30_DAYS:
                    return graphOfPastDays(mContext, packageName, stats, 30);
            }
            return new TextView(mContext);
        }

        static void populateVariables(Context mContext,String pkgName){
            packageName = pkgName;
            File stats_log = getStatsLog(mContext, packageName);
            stats = StatsReader.getStats(mContext, stats_log);
            Collections.reverse(stats);
        }

        static View graphOfToday(Context mContext,String packageName,List<Stat> stats){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
            String todayDateString = sdf.format(new Date());
            return graphOfDay(mContext, packageName, stats, todayDateString)
            .setTitle("Today")
            .create();
        }

        static BarGraphView graphOfDay(Context mContext,String packageName,List<Stat> stats,String dateString){
            List<Integer> dataForEachHour = new ArrayList<>();
            List<String> stringsForEachHour = new ArrayList<>();
            for(int i = 0; i < 24; i++){
                dataForEachHour.add(0);

                SimpleDateFormat hour_string_sdf = new SimpleDateFormat("ha");
                Date date = new Date();
                date.setHours(i);
                stringsForEachHour.add(hour_string_sdf.format(date) + " : ");
            }

            for(Stat stat : stats){
                if(!dateString.equals(stat.DATE)) continue;
                Date statToday = StatsReader.getDate(stat);
                SimpleDateFormat hour_sdf = new SimpleDateFormat("HH");
                int hour = Integer.parseInt(hour_sdf.format(statToday));

                int hourData = dataForEachHour.get(hour);
                hourData += Modes.addDataByMode(stat);
                dataForEachHour.set(hour, hourData);
            }

            BarGraphView barGraph = new BarGraphView(mContext, dataForEachHour)
                .stringsForEachY(stringsForEachHour);
            return barGraph;
        }

        static View graphOfPastDays(final Context mContext,final String packageName,final List<Stat> stats,final int daysRange){
            List<Integer> dataForEachDay = new ArrayList<>();
            List<String> stringsForEachDay = new ArrayList<>();

            final List<String> dates = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
            Calendar calendar = Calendar.getInstance();

            for(int i = 0; i < daysRange; i++){
                dataForEachDay.add(0);
                String date = sdf.format(calendar.getTime());
                dates.add(date);
                stringsForEachDay.add(date + " : ");
                calendar.add(Calendar.DAY_OF_YEAR, -1);
            }

            for(Stat stat : stats){
                int i = dates.indexOf(stat.DATE);
                if(i == -1) break;
                int dayData = dataForEachDay.get(i);
                dayData += Modes.addDataByMode(stat);
                dataForEachDay.set(i, dayData);
            }

            Collections.reverse(dataForEachDay);
            Collections.reverse(stringsForEachDay);
            Collections.reverse(dates);

            final LinearLayout layout = new LinearLayout(mContext);
            final TextView text = new TextView(mContext);
            layout.setOrientation(LinearLayout.VERTICAL);

            final List<Integer> maxYOfGraphs = new ArrayList<>();
            for(String date : dates){
                maxYOfGraphs.add(0);
            }

            BarGraphView.OnProgressChange onProgressChange = new BarGraphView.OnProgressChange(){
                @Override
                public void run(int progress){
                    if(layout.getChildCount() > 2) layout.removeViewAt(2);
                    BarGraphView graphOfDay = graphOfDay(mContext, packageName, stats, dates.get(progress));

                    String date = dates.get(progress);
                    int maxY = Collections.max(maxYOfGraphs);
                    layout.addView(graphOfDay.setMaxY(maxY)
                    .setTitle(date)
                    .create());

                    maxYOfGraphs.set(progress, graphOfDay.getMaxY());
                    text.setText("max Y of graphs: " + Collections.max(maxYOfGraphs));
                }
            };

            BarGraphView barGraph = new BarGraphView(mContext, dataForEachDay)
                .stringsForEachY(stringsForEachDay)
                .setOnProgressChange(onProgressChange)
                .setTitle("Last 30 Days");

            layout.addView(barGraph.create());
            layout.addView(text);
            return layout;
        }
    }
}
