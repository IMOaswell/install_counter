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
    final File stats_log = new File("/storage/emulated/0/AppProjects/frog/stats.log");
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;
        if (!TermuxTools.hasPermission(mContext)) {
            TermuxTools.requestPermission(mContext);
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
        final int i = StatsReader.getLastLog(stats_log).INDEX;

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
                    StatsWriter.recordStats(mContext, stats_log, j);
                    btn.setText(j + "");
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
