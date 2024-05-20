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
        Runnable delayedRunnable = new Runnable() {
            @Override
            public void run () {
                String command = "~/.shortcuts/SHORTCUTS-UPDATE.sh";
                TermuxTools.runCommand(mContext, command);
            }
        };
        new Handler().postDelayed(delayedRunnable, 1000);
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
        String recordString = index + " " + getCurrentDate() + " | " + getCharChangesCount();
        String previousContent = FileTools.readTextFile(stats_log);
        FileTools.writeTextFile(stats_log, previousContent + "\n" + recordString);
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

    String getCharChangesCount () {
        //TODO: get how many char for each text files is changed
        return "";
    }

    void installApk (Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
