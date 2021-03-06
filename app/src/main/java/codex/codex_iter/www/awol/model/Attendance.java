package codex.codex_iter.www.awol.model;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
import java.util.Scanner;

public class Attendance {
    private String sub, code, upd, theory, lab, percent, that = "", labt = "", old = "", bunk_text_str = "";
    private double thT;
    private double thp;
    private double lat;
    private double lap;
    private long lastAttendanceUpdateTime;

    public String getClasses() {
        return Integer.toString((int) (thT + lat));
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getOld() {
        return old;
    }

    public void setOld(String old) {
        this.old = old;
    }

    public String getCode() {
        return code;
    }

    public String getThat() {
        return that;
    }

    public void setThat(String that) {
        this.that = that;
    }

    public String getLabt() {
        return labt;
    }

    public void setLabt(String labt) {
        this.labt = labt;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUpd() {
        return upd;
    }

    public void setUpd(String upd) {
        this.upd = upd;
    }

    public String getTheory() {
        return theory;
    }

    public void setTheory(String theory) {
        if (theory.equals("Not Applicable")) {
            this.theory = theory;
            thp = 0;
            thT = 0;
        } else {
            Scanner in = new Scanner(theory);
            thp = in.nextInt();
            char c = in.next().charAt(0);
            thT = in.nextInt();
            String res = " (" + String.format(Locale.US, "%.0f", ((thp / thT) * 100)) + "%)";
            this.theory = theory;
            setThat(res);
        }
    }

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        if (lab.equals("Not Applicable")) {
            this.lab = lab;
            lap = 0;
            lat = 0;
        } else {
            Scanner in = new Scanner(lab);
            lap = in.nextInt();
            char c = in.next().charAt(0);
            lat = in.nextInt();
            this.lab = lab;
            setLabt(" (" + String.format(Locale.US, "%.0f", ((lap / lat) * 100)) + "%)");
        }
    }

    public String getBunk_text_str() {
        return bunk_text_str;
    }


    public int getAbsent() {
        return (int) Math.floor(lat + thT - thp - lap);
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = String.format(Locale.US, "%.1f", new Scanner(percent).nextDouble());
    }

    public void setBunk(int pref_min_attendance, boolean attendance_stats, boolean show_attendance_stats) {
        StringBuilder bunkStats = new StringBuilder();
        ArrayList<String> bunk = new ArrayList<>();
        ArrayList<String> need = new ArrayList<>();
        int attendance = (int) Double.parseDouble(getPercent());
        int classes = (int) Double.parseDouble(getClasses());
        int classesAbsent = getAbsent();
        int classesPresent = classes - classesAbsent;
        int lastDays;
        int approxTotalClasses = 55;

        if (classes != 0 && attendance <= pref_min_attendance) {
            bunk.add("DO NOT BUNK ANY MORE CLASSES\n");
        } else {
            lastDays = -1;
            for (int a = pref_min_attendance; a < attendance; a += 5) {
                int daysBunk = (int) ((100 * classesPresent / (double) a) - (double) classes);
                if (daysBunk == lastDays) continue;
                else lastDays = daysBunk;
                if (daysBunk > 0) {
                    bunk.add(new Formatter().format("Bunk %d%s %s for %d%% attendance\n",
                            daysBunk, classesAbsent == 0 ? "" : " more",
                            daysBunk == 1 ? "class" : "classes", a).toString());
                }
            }
        }

        if (classes != 0) {
            int nextAttendance = (attendance + 4) / 5 * 5;
            if (nextAttendance == attendance) nextAttendance = attendance + 5;
            if (nextAttendance < pref_min_attendance) nextAttendance = pref_min_attendance;
            lastDays = -1;
            for (int a = nextAttendance; a <= 95; a += 5) {
                int daysNeed = (int) ((a * classes - 100 * classesPresent) / (double) (100 - a));
                if (daysNeed == lastDays) continue;
                else lastDays = daysNeed;
                if (daysNeed > 0 && (daysNeed + classes <= approxTotalClasses)) {
                    need.add(new Formatter().format("Attend %d more %s for %d%% attendance\n",
                            daysNeed, daysNeed == 1 ? "class" : "classes", a).toString());
                }
            }
        }

        if (show_attendance_stats) {
            if (attendance_stats) {
                for (int i = 0; i < bunk.size(); i++) {
                    bunkStats.append(bunk.get(i));
                }
                for (int i = 0; i < need.size(); i++) {
                    bunkStats.append(need.get(i));
                }
            } else {
                if (!bunk.isEmpty()) bunkStats.append(bunk.get(bunk.size() - 1));
                if (!need.isEmpty()) bunkStats.append(need.get(0));
            }
            if (bunkStats.length() != 0) bunkStats.setLength(bunkStats.length() - 1);
        }
        this.bunk_text_str = bunkStats.toString();
    }

    public void setLastAttendanceUpdateTime(long lastAttendanceUpdateTime) {
        this.lastAttendanceUpdateTime = lastAttendanceUpdateTime;
    }

    public long getLastAttendanceUpdateTime() {
        return lastAttendanceUpdateTime;
    }
}


