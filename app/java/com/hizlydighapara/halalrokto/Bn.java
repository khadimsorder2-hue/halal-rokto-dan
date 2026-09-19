package com.hizlydighapara.halalrokto;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/** বাংলা সংখ্যা / তারিখ ফরম্যাটিং হেল্পার। */
public final class Bn {

    private Bn() {}

    public static final String[] DIGITS = {"০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯"};
    public static final String[] MONTHS = {"জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"};
    public static final String[] DAYS = {"রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার"};

    public static String toBn(String s) {
        StringBuilder b = new StringBuilder();
        for (char ch : s.toCharArray()) {
            if (ch >= '0' && ch <= '9') b.append(DIGITS[ch - '0']);
            else b.append(ch);
        }
        return b.toString();
    }

    public static String bn(long n) { return toBn(String.valueOf(n)); }

    public static String bn(long n, int pad) {
        String s = String.valueOf(Math.abs(n));
        while (s.length() < pad) s = "0" + s;
        return (n < 0 ? "-" : "") + toBn(s);
    }

    private static Calendar cal(long ts) {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(ts);
        return c;
    }

    public static String fmtDate(long ts) {
        Calendar c = cal(ts);
        return bn(c.get(Calendar.DAY_OF_MONTH)) + " " + MONTHS[c.get(Calendar.MONTH)] + " " + bn(c.get(Calendar.YEAR));
    }

    public static String fmtDateShort(long ts) {
        Calendar c = cal(ts);
        return bn(c.get(Calendar.DAY_OF_MONTH)) + "/" + bn(c.get(Calendar.MONTH) + 1) + "/" + bn(c.get(Calendar.YEAR));
    }

    public static String dayName(long ts) { return DAYS[cal(ts).get(Calendar.DAY_OF_WEEK) - 1]; }

    public static String timeAgo(long ts) {
        long s = (System.currentTimeMillis() - ts) / 1000L;
        if (s < 60) return "এখনই";
        if (s < 3600) return bn(s / 60) + " মিনিট আগে";
        if (s < 86400) return bn(s / 3600) + " ঘণ্টা আগে";
        long d = s / 86400;
        if (d < 7) return bn(d) + " দিন আগে";
        if (d < 30) return bn(d / 7) + " সপ্তাহ আগে";
        return fmtDateShort(ts);
    }

    public static long parseYmd(String ymd) { // "2026-09-19"
        try {
            String[] p = ymd.split("-");
            Calendar c = new GregorianCalendar(
                    Integer.parseInt(p[0]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]));
            return c.getTimeInMillis();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    public static String toYmd(long ts) {
        Calendar c = cal(ts);
        return String.format(Locale.US, "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    public static String initials(String name) {
        if (name == null || name.isEmpty()) return "?";
        return name.substring(0, 1);
    }

    public static String maskPhone(String p) {
        if (p == null) return "";
        return p.length() >= 4 ? "••••••" + p.substring(p.length() - 4) : p;
    }

    public static int hash(String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) { h = (h << 5) - h + s.charAt(i); h |= 0; }
        return h;
    }

    public static String uid(String p) {
        return p + "_" + Long.toString(System.currentTimeMillis(), 36)
                + Long.toString((long) (Math.random() * 2176782336L), 36);
    }
}
