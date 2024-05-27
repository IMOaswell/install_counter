package imo.install_counter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class ProjectFinder
{
    final static String INTERNAL_STORAGE = Environment.getExternalStorageDirectory().getPath();
    final static String AIDE_PROJECTS_DIR =  INTERNAL_STORAGE + "/AppProjects/";
    final static String SHARED_PREFS_KEY = "banana";

    static String getApkPackageName (Context mContext, Uri apkUri) {
        String filePath = getFilePathFromUri(mContext, apkUri);
        PackageManager pm = mContext.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(filePath, 0);
        return packageInfo.packageName;
    }

    static String findProjectDir (Context mContext, String packageName) {
        String packageNameAsDir = packageName.replace(".", "/");
        SharedPreferences sp = mContext.getSharedPreferences(SHARED_PREFS_KEY, mContext.MODE_PRIVATE);
        final String KEY = packageNameAsDir;

        if (sp.contains(KEY)) {
            final String VALUE = sp.getString(KEY, "");
            if (new File(VALUE).exists()) return VALUE;
        }
        String projectDir = findProjectDirRecursively(packageNameAsDir, AIDE_PROJECTS_DIR);
        if (projectDir != null) sp.edit().putString(KEY, projectDir).apply();
        return projectDir;
    }

    private static String findProjectDirRecursively (String packageNameAsDir, String path) {
        File folder = new File(path);
        File[] files = folder.listFiles();

        if (files == null) return null;
        for (File file : files) {
            if (file.isDirectory()) {
                String dir = file.getAbsolutePath();
                String fullProjectJavaPath = dir + "/app/src/main/java/" + packageNameAsDir;

                if (new File(fullProjectJavaPath).exists()) {
                    return dir;
                } else {
                    if (new File(dir + "/app/src/main/java/").exists()) continue;
                    String result = findProjectDirRecursively(packageNameAsDir, file.getAbsolutePath());
                    if (result != null) return result;
                }
            }
        }
        return null;
    }
    
    static Map getActiveProjects(Context mContext){
        SharedPreferences sp = mContext.getSharedPreferences(SHARED_PREFS_KEY, mContext.MODE_PRIVATE);
        Map projectPkgNameAndFolder = sp.getAll();
        return projectPkgNameAndFolder;
    }




    private static String getFilePathFromUri (Context mContext, Uri apkUri) {
        String filePath = null;
        if ("file".equals(apkUri.getScheme())) {
            filePath = apkUri.getPath();
        } else if ("content".equals(apkUri.getScheme())) {
            try {
                filePath = copyToTempFile(mContext, apkUri).getAbsolutePath();
            } catch (Exception e) {}
        }
        return filePath;
    }

    private static File copyToTempFile (Context mContext, Uri uri) throws Exception {
        InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new Exception("Failed to open input stream from URI");
        }

        File tempFile = new File(INTERNAL_STORAGE, "temp_apk.apk");
        OutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        inputStream.close();
        outputStream.close();

        return tempFile;
    }
}
