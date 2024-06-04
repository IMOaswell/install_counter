package imo.install_counter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import imo.install_counter.MainActivity;
import java.io.File;
import java.util.List;

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

        if (recieveApk) {
            setModeRecieveApk(intent.getData());
        } else {
            setModeInsights();
        } 
    }

    public void setModeInsights () {
        setContentView(R.layout.insights);
        final Spinner spinner = findViewById(R.id.spinner);
        final TextView directoryTxt = findViewById(R.id.directory_txt);
        final TextView timeTxt = findViewById(R.id.time_txt);
        final LinearLayout scrollLayout = findViewById(R.id.scroll);

        List<String> packageNames = StatsAnalytics.getPackageNames(mContext);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, packageNames);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onNothingSelected (AdapterView<?> parent) {}
                @Override                     
                public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
                    String packageName = ( String) parent.getItemAtPosition(position);

                    directoryTxt.setText("Project's Folder: \n" +
                                         StatsAnalytics.getProjectDirPath(mContext, packageName));
                    timeTxt.setText("Time Since Last Log: \n" +
                                    StatsAnalytics.timeSinceLastLog(mContext, packageName));
                    scrollLayout.removeAllViews();
                    scrollLayout.addView(StatsAnalytics.GraphMaker.makeGraph(mContext, packageName, StatsAnalytics.GraphMaker.TODAY));
                    scrollLayout.addView(StatsAnalytics.GraphMaker.makeGraph(mContext, null, StatsAnalytics.GraphMaker.PAST_30_DAYS));
                }
            });
    }
    
    public void setModeRecieveApk (final Uri apkUri) {
        setContentView(R.layout.recieve_apk);

        String apkPackageName = ProjectFinder.getApkPackageName(mContext, apkUri);
        String foundProjectDirPath = ProjectFinder.findProjectDir(mContext, apkPackageName);
        if (foundProjectDirPath == null) return;
        final File stats_log = new File(foundProjectDirPath,  "stats.log");
        final int currentStatIndex = StatsReader.getLastStat(mContext, stats_log).INDEX;

        StatsWriter.optimizeStatsLog(stats_log);

        final Button btn = findViewById(R.id.btn);
        final CheckBox box = findViewById(R.id.box);
        btn.setText(currentStatIndex + "");
        btn.setOnClickListener(new View.OnClickListener() {
                public void onClick (View v) {
                    if (apkUri == null) {
                        finishAffinity();
                        return;
                    }
                    btn.setEnabled(false);
                    int newStatIndex = currentStatIndex + 1;
                    boolean isAmmend = box.isChecked();
                    StatsWriter.recordStat(mContext, stats_log, newStatIndex, isAmmend);
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
