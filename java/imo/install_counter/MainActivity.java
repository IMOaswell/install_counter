package imo.install_counter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            final Uri apkUri = intent.getData();
            
            final SharedPreferences sp = getSharedPreferences("uwu", MODE_PRIVATE);
            final int i = sp.getInt("wawa", 0);
            final Button btn = findViewById(R.id.btn);
//            sp.edit().putInt("wawa", 0).apply();//reset
//            btn.setText(0+"");
//            if(true) return;
            btn.setText(i+"");
            btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){
                        if (apkUri == null){
                            finishAffinity();
                            return;
                        }
                        btn.setEnabled(false);
                        int j = i + 1;
                        writeIntToFile(j, "/storage/emulated/0/AppProjects/frog/compiled_to_apk_count.txt");
                        btn.setText(j+"");
                        installApk(apkUri);
                        sp.edit().putInt("wawa", j).apply();
                    }
                });
        }
    }

    private void installApk(Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
    private void writeIntToFile(int value, String path) {
        File file = new File(path);
        
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(Integer.toString(value).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {}
        
    }
}
