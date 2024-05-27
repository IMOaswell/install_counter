package imo.install_counter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class StatsWriter
{
    static void recordStat (Context mContext, File stats_log, int index) {
        recordStat(mContext, stats_log, index, false);
    }

    static void recordStat (Context mContext, File stats_log, int index, boolean isAmmend) {
        //will record e.g 44 2024-MAY-19 01:39pm files:3 +27 -6
        String recordString = index + " " + getCurrentDate() + " " + getCurrentTime() + " ";
        String previousContent = StatsReader.read(stats_log);
        write(stats_log, previousContent + "\n" + recordString);
        addGitChanges(mContext, stats_log, isAmmend);
    }

    private static String getCurrentDate () {
        //will return e.g 2024-MAY-19
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String monthString = new SimpleDateFormat("MMM").format(calendar.getTime());
        final String DATE = year + "-" + monthString + "-" + day;
        return DATE;
    }

    private static String getCurrentTime () {
        //will return e.g 01:39pm
        Calendar calendar = Calendar.getInstance();
        String hour = String.format("%02d", calendar.get(Calendar.HOUR));
        String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
        int amPm = calendar.get(Calendar.AM_PM);
        String amPmString = (amPm == Calendar.AM) ? "am" : "pm";
        final String TIME = hour + ":" + minute + amPmString;
        return TIME;
    }

    private static void addGitChanges (Context mContext, File stats_log, boolean isAmmend) {
        //will add e.g files:3 +27 -6
        String script = "cd \'" + StatsReader.getMainDirPath(stats_log) + "\' \n";

        String addChanges = "input=$(git diff --shortstat) \n";
        addChanges += "untracked_files=$(git ls-files --others --exclude-standard) \n";
        addChanges += "if [ -z \"$input\" ]; then \n";
        addChanges += "input=\"$(echo \"$untracked_files\" | wc -l) files\" \n";
        addChanges += "fi \n";
        addChanges += "files=$(echo $input | sed -n -E 's/^([0-9]+) file.*/files:\\1/p') \n";
        addChanges += "insertions=$(echo $input | sed -n -E 's/.* ([0-9]+) insertion.*/+\\1/p') \n";
        addChanges += "deletions=$(echo $input | sed -n -E 's/.* ([0-9]+) deletion.*/-\\1/p') \n";
        addChanges += "if [ -z \"$insertions\" ]; then \n";
        addChanges += "insertions=\"0\" \n";
        addChanges += "fi \n";
        addChanges += "if [ -z \"$deletions\" ]; then \n";
        addChanges += "deletions=\"0\" \n";
        addChanges += "fi \n";
        addChanges += "output=\"$files $insertions $deletions\" \n";
        addChanges += "echo $output \n";
        addChanges += "echo $output >> '" + stats_log.getAbsolutePath() + "' \n";
        String commit = "echo Enter Commit Message \n";
        commit += "nothing=\"probably just testing:D\" \n";
        commit += "echo put nothing to set it to \\\"$nothing\\\" \n";
        commit += "echo commit message: \n";
        commit += "read userInput \n";
        commit += "git add . \n";
        commit += "if [ -z \"$userInput\" ]; then \n";
        commit += "\techo \"$nothing\" \n";
        commit += "\tgit commit -m \"$nothing\" \n";
        commit += "else \n";
        commit += "\tgit commit -m \"$userInput\"\n";
        commit += "fi \n";
        String ammend = "echo Commit Ammend \n";
        ammend += "echo 'are u sure? (press any key to confirm)' \n";
        ammend += "read userInput \n";
        ammend += "git add . \n";
        ammend += "git commit --amend --no-edit \n";

        script += addChanges + "\n";
        script += isAmmend ? ammend : commit;
        TermuxTools.runScript(mContext, script);
    }

    static void optimizeStatsLog (File stats_log) {
        collapseDates(stats_log);
    }

    static void collapseDates (File stats_log) {
        String[] logs = StatsReader.read(stats_log).split("\n");
        HashSet<String> dateSet = new HashSet<>();
        String[] outputs = new String[logs.length];

        for (int i = 0; i < logs.length; i++) {
            Stat stat = new Stat(logs[i]);
            String date = stat.DATE;

            if (dateSet.contains(date)) {
                outputs[i] = logs[i].replace(date, "*");
            } else {
                dateSet.add(date);
                outputs[i] = logs[i];
            }
        }
        String newContent = "";
        for (String output : outputs) {
            newContent += output + "\n";
        }

        write(stats_log, newContent);
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
