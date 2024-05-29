package imo.install_counter;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StatsReader
{
    static List<Stat> getStats (Context mContext, File stats_log) {
        if (!stats_log.exists()) {
            StatsWriter.recordStat(mContext, stats_log, 0);
        }
        List<Stat> STATS = new ArrayList<>();
        String[] logs = read(stats_log).split("\n");
        for (String log : logs) {
            STATS.add(new Stat(log));
        }
        return STATS;
    }

    static Stat getLastStat (Context mContext, File stats_log) {
        List<Stat> Stats = getStats(mContext, stats_log);
        Stat lastStat = Stats.get(Stats.size() - 1);
        if (lastStat.DATE.equals("*")) {
            for (int i = Stats.indexOf(lastStat); i >= 0; i--) {
                String dateAtPosition = Stats.get(i).DATE;
                if (!(dateAtPosition.equals("*"))) 
                    lastStat.DATE = dateAtPosition;
            }
        }
        return lastStat;
    }

    static String getMainDirPath (File stats_log) {
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
