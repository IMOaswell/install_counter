package imo.install_counter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatsReader{
    static List<Stat> getStats(Context mContext,File stats_log){
        if(!stats_log.exists()){
            StatsWriter.recordStat(mContext, stats_log, 0);
        }
        List<Stat> STATS = new ArrayList<>();
        String[] logs = read(stats_log).split("\n");
        for(String log : logs){
            STATS.add(new Stat(log));
        }
        String DATE = "";
        for(Stat stat : STATS){
            if(stat.DATE.equals("*")){
                stat.DATE = DATE;
            }else{
                DATE = stat.DATE;
            }
        }
        return STATS;
    }

    static Stat getLastStat(Context mContext,File stats_log){
        List<Stat> Stats = getStats(mContext, stats_log);
        Stat lastStat = Stats.get(Stats.size() - 1);
        return lastStat;
    }

    static Date getDate(Stat stat){
        String dateAndTimeString = stat.DATE + " " + stat.TIME;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd hh:mma");
        try{
            return format.parse(dateAndTimeString);
        }catch(ParseException e){
            System.out.println(e);
        }
        return null;
    }

    static String getMainDirPath(File stats_log){
        File mainDir = new File(stats_log.getParent(), "/app/src/main/");
        System.out.println(mainDir.getAbsolutePath());
        return mainDir.exists() ? mainDir.getAbsolutePath() : null;
    }

    static String read(File file){
        String filePath = file.getAbsolutePath();
        return read(filePath);
    }

    static String read(String filePath){
        StringBuilder content = new StringBuilder();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            while((line = reader.readLine()) != null){
                content.append(line).append("\n");
            }
            reader.close();
        }catch(IOException e){}
        return content.toString().trim();
    }
    
    public static String readAsset(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
