package imo.install_counter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;

public class MainActivity extends Activity 
{
    Activity mContext;
    final File compiled_to_apk_count = new File("/storage/emulated/0/AppProjects/frog/compiled_to_apk_count.txt");
    final File stats_log = new File("/storage/emulated/0/AppProjects/frog/stats.log");
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;
        if (checkSelfPermission("com.termux.permission.RUN_COMMAND") != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{"com.termux.permission.RUN_COMMAND"}, 69);
            return;
        }
        setModeSetup();
        
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            setModeRecieveApk(intent);
        }
    }

    public void setModeSetup () {
        setContentView(R.layout.setup);
        
    }
    public void setModeRecieveApk (Intent intent) {
        setContentView(R.layout.recieve_apk);
        final Uri apkUri = intent.getData();
        String savedCountInFile = FileTools.readTextFile(compiled_to_apk_count);
        final int i = Integer.parseInt(savedCountInFile);

        final Button btn = findViewById(R.id.btn);
        btn.setText(i + "");
        btn.setOnClickListener(new View.OnClickListener() {
                public void onClick (View v) {
                    if (apkUri == null) {
                        finishAffinity();
                        return;
                    }
                    btn.setEnabled(false);
                    int j = i + 1;
                    FileTools.writeTextFile(compiled_to_apk_count, j + "");
                    recordStats(stats_log, j);
                    btn.setText(j + "");
                    installApk(apkUri);
                }
            });
    }

    void recordStats (File stats_log, int index) {
        String recordString = index + " " + getCurrentDate() + " | ";
        String previousContent = FileTools.readTextFile(stats_log);
        FileTools.writeTextFile(stats_log, previousContent + "\n" + recordString);
        addGitChanges(stats_log);
    }

    String getCurrentDate () {
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
        final String TIME = hour + ":" + minute + " " + amPmString;
        return DATE + " " + TIME;
    }

    void addGitChanges (File stats_log) {
        //will add e.g 3 files +27 -6
        String script = "cd /storage/emulated/0/AppProjects/frog/app/src/main \n";
        script += "input=$(git diff --shortstat) \n";
        script += "files=$(echo $input | sed -E 's/^([0-9]+) files.*/\\1/') \n";
        script += "insertions=$(echo $input | sed -E 's/.* ([0-9]+) insertions.*/+\\1/') \n";
        script += "deletions=$(echo $input | sed -E 's/.* ([0-9]+) deletions.*/-\\1/') \n";
        script += "output=\"$files files $insertions $deletions\" \n";
        script += "echo $output >> "+stats_log.getAbsolutePath();
        TermuxTools.runScript(mContext, script);
    }

    void installApk (Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
