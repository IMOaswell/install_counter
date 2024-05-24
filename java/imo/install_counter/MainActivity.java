package imo.install_counter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.io.File;

public class MainActivity extends Activity 
{
    Activity mContext;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;
        if (!TermuxTools.hasPermission(mContext)) {
            TermuxTools.requestPermission(mContext);
            return;
        }

        Intent intent = getIntent();
        boolean recieveApk = Intent.ACTION_VIEW.equals(intent.getAction());
        
        if (recieveApk){
            setModeRecieveApk(intent.getData());
        }else{
            setModeSetup();
        } 
    }

    public void setModeSetup () {
        setContentView(R.layout.setup);

    }
    public void setModeRecieveApk (final Uri apkUri) {
        setContentView(R.layout.recieve_apk);
        
        String apkPackageName = ProjectFinder.getApkPackageName(mContext, apkUri);
        String foundProjectDirPath = ProjectFinder.findProjectDirByPackageName(mContext, apkPackageName);
        if(foundProjectDirPath == null) return;
        final File stats_log = new File(foundProjectDirPath,  "stats.log");
        final int currentStatIndex = StatsReader.getLastStat(mContext, stats_log).INDEX;

        StatsWriter.optimizeStatsLog(stats_log);
        
        final Button btn = findViewById(R.id.btn);
        btn.setText(currentStatIndex + "");
        btn.setOnClickListener(new View.OnClickListener() {
                public void onClick (View v) {
                    if (apkUri == null) {
                        finishAffinity();
                        return;
                    }
                    btn.setEnabled(false);
                    int newStatIndex = currentStatIndex + 1;
                    StatsWriter.recordStat(mContext, stats_log, newStatIndex);
                    btn.setText(newStatIndex + "");
                    installApk(apkUri);
                }
            });
    }

    void installApk (Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
