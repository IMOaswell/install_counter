package imo.install_counter;

import android.content.Context;
import android.content.Intent;

public class TermuxTools
 {
    static void runScript(Context context, String script) {
        
        Intent intent = new Intent();
        intent.setClassName("com.termux", "com.termux.app.RunCommandService");
        intent.setAction("com.termux.RUN_COMMAND");
        intent.putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/sh");
        intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", new String[]{"-c", script});
        context.startService(intent);
    }
    
    
}
