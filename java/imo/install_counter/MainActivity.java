package imo.install_counter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends Activity 
{
    final File compiled_to_apk_count = new File("/storage/emulated/0/AppProjects/frog/compiled_to_apk_count.txt");

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
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
    }

    private void installApk (Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    static class FileTools
    {
        static String readTextFile (File file) {
            String filePath = file.getAbsolutePath();
            return readTextFile(filePath);
        }

        static String readTextFile (String filePath) {
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

        static void writeTextFile (File file, String input) {
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
}
