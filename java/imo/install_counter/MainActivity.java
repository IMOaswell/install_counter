package imo.install_counter;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
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
        setModeSetup();
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            setModeRecieveApk(intent);
        }
    }
    
    public void setModeSetup(){
        setContentView(R.layout.setup);
        recordStats(stats_log);
    }
    public void setModeRecieveApk(Intent intent){
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
                    btn.setText(j + "");
                    installApk(apkUri);
                }
            });
    }
    
    void recordStats(File stats_log){
        setTitle(getCurrentDate());
    }
    
    String getCurrentDate(){
        //will return e.g 2024-MAY-19 1:39PM
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String monthString = new SimpleDateFormat("MMM").format(calendar.getTime());
        final String DATE = year + "-" + monthString + "-" + day;

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int amPm = calendar.get(Calendar.AM_PM);
        String amPmString = (amPm == Calendar.AM) ? "AM" : "PM";
        final String TIME = hour + ":" + minute + " " + amPmString;
        return DATE + " " + TIME;
    }

    void installApk (Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    
}
