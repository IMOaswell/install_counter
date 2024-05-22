package imo.install_counter;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StatsReader
{
    static Stats getLastStat (Context mContext, File stats_log) {
        if(!stats_log.exists()){
            StatsWriter.recordStat(mContext, stats_log, 0);
        }
        String[] logs = read(stats_log).split("\n");
        String lastLog = logs[logs.length - 1];
        return new Stats(lastLog);
    }
    
    static String getMainDirPath(File stats_log){
        File mainDir = new File(stats_log.getParent(), "/app/src/main/");
        System.out.println(mainDir.getAbsolutePath());
        return mainDir.exists() ? mainDir.getAbsolutePath() : null;
    }

    static String read (File file) {
        String filePath = file.getAbsolutePath();
        return read(filePath);
    }

    static String read (String filePath) {
        StringBuilder content = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {}
        return content.toString().trim();
    }
}
