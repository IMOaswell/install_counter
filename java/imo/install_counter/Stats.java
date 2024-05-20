package imo.install_counter;

public class Stats
{

    //e.g 44 2024-MAY-19 01:39PM files:3 +27 -6
    int INDEX;
    String DATE;
    String TIME;
    int FILES_CHANGED;
    int INSERTS;
    int DELETES;

    Stats (String log) {
        String[] parts = log.split(" ");
        if (parts.length < 3) return;
        INDEX = Integer.parseInt(parts[0]);
        DATE = parts[1];
        TIME = parts[2];
        if (parts.length < 6) return;
        FILES_CHANGED = Integer.parseInt(parts[3].replace("files:", ""));
        INSERTS = Integer.parseInt(parts[4].replace("+", ""));
        DELETES = Integer.parseInt(parts[5].replace("-", ""));
    }
}
