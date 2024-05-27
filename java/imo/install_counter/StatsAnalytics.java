package imo.install_counter;

import android.content.Context;
import java.util.Map;

public class StatsAnalytics
 {
    
    static String getActiveProjects(Context mContext){
        StringBuilder sb = new StringBuilder();
        Map pkgNamesAndDirs = ProjectFinder.getActiveProjects(mContext);
        for (Map.Entry<String, String> entry : pkgNamesAndDirs.entrySet()) {
            sb.append("\n" + entry.getKey().toUpperCase() + " " + entry.getValue());
        }
        return sb.toString();
    }
    
}
