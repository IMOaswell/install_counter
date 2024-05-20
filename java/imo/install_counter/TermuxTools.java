package imo.install_counter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class TermuxTools
 {
     static boolean hasPermission(Activity activity){
         return activity.checkSelfPermission("com.termux.permission.RUN_COMMAND") == PackageManager.PERMISSION_GRANTED;
     }
     
     static void requestPermission(Activity activity){
         activity.requestPermissions(new String[]{"com.termux.permission.RUN_COMMAND"}, 69);
         
     }
     
    static void runScript(Context context, String script) {
        
        Intent intent = new Intent();
        intent.setClassName("com.termux", "com.termux.app.RunCommandService");
        intent.setAction("com.termux.RUN_COMMAND");
        intent.putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/sh");
        intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", new String[]{"-c", script});
        context.startService(intent);
    }
    
    
}
