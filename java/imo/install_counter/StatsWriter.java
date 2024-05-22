package imo.install_counter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StatsWriter
{
    static void recordStats (Context mContext, File stats_log, int index) {
        //will record e.g 44 2024-MAY-19 01:39PM files:3 +27 -6
        String recordString = index + " " + getCurrentDate() + " ";
        String previousContent = StatsReader.read(stats_log);
        write(stats_log, previousContent + "\n" + recordString);
        addGitChanges(mContext, stats_log);
    }

    private static String getCurrentDate () {
        //will return e.g 2024-MAY-19 01:39PM
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String monthString = new SimpleDateFormat("MMM").format(calendar.getTime());
        final String DATE = year + "-" + monthString + "-" + day;

        String hour = String.format("%02d", calendar.get(Calendar.HOUR));
        String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
        int amPm = calendar.get(Calendar.AM_PM);
        String amPmString = (amPm == Calendar.AM) ? "AM" : "PM";
        final String TIME = hour + ":" + minute + amPmString;
        return DATE + " " + TIME;
    }

    private static void addGitChanges (Context mContext, File stats_log) {
        //will add e.g files:3 +27 -6
        String script = "cd \'" + StatsReader.getMainDirPath(stats_log) + "\' \n";

        String addChanges = "input=$(git diff --shortstat) \n";
        addChanges += "files=$(echo $input | sed -E 's/^([0-9]+) file.*/files:\\1/') \n";
        addChanges += "insertions=$(echo $input | sed -E 's/.* ([0-9]+) insertion.*/+\\1/') \n";
        addChanges += "deletions=$(echo $input | sed -E 's/.* ([0-9]+) deletion.*/-\\1/') \n";
        addChanges += "output=\"$files $insertions $deletions\" \n";
        addChanges += "echo $output \n";
        addChanges += "echo $output >> " + stats_log.getAbsolutePath();
        String commit = "echo Enter Commit Message \n";
        commit += "nothing=\"probably just testing:D\" \n";
        commit += "echo put nothing to set it to \\\"$nothing\\\" \n";
        commit += "echo just put \\* to amend \n";
        commit += "echo commit message: \n";
        commit += "read userInput \n";
        commit += "git add . \n";
        commit += "if echo \"$userInput\" | grep -q '*'; then";
        commit += "\tgit commit --amend \n";
        commit += "else \n";
        commit += "\tif [ -z \"$userInput\" ]; then\n";
        commit += "\t\techo \"$nothing\" \n";
        commit += "\t\tgit commit -m \"$nothing\" \n";
        commit += "\telse \n";
        commit += "\t\tgit commit -m \"$userInput\"\n";
        commit += "\tfi \n";
        commit += "fi \n";

        script += addChanges + "\n" + commit;
        TermuxTools.runScript(mContext, script);
    }

    private static void write (File file, String input) {
        try {
            if (!file.exists()) file.createNewFile();
            if (file.isFile()) {
                FileWriter fw = new FileWriter(file);
                fw.write(input);
                fw.close();
            }
        } catch (IOException e) {}
    }

}
