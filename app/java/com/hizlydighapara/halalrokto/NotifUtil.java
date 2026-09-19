package com.hizlydighapara.halalrokto;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

/** সিস্টেম নোটিফিকেশন — চ্যানেল, পারমিশন, ফোরগ্রাউন্ড সার্ভিস, ডিপ-লিংক। */
public final class NotifUtil {

    private NotifUtil() {}

    public static final String CH_SERVICE = "halal_service";
    public static final String CH_ALERTS = "halal_alerts";
    public static final String CH_CHAT = "halal_chat";

    /** নোটিফিকেশন পাঠানো যাবে কি? (সেটিংস + পারমিশন + সিস্টেম অন) */
    public static boolean enabled(Context c) {
        if (!Data.s.notifOn || Data.s.session == null) return false;
        try {
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null && !nm.areNotificationsEnabled()) return false;
            if (Build.VERSION.SDK_INT >= 33 &&
                    c.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) return false;
        } catch (Exception ignored) { }
        return true;
    }

    public static void ensureChannels(Context c) {
        try {
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return;

            NotificationChannel svc = new NotificationChannel(CH_SERVICE,
                    "ব্যাকগ্রাউন্ড সার্ভিস", NotificationManager.IMPORTANCE_LOW);
            svc.setDescription("অ্যাপের ব্যাকগ্রাউন্ড আপডেট সার্ভিস — নীরব, স্থায়ী");
            svc.setShowBadge(false);
            nm.createNotificationChannel(svc);

            NotificationChannel alerts = new NotificationChannel(CH_ALERTS,
                    "জরুরি সতর্কতা", NotificationManager.IMPORTANCE_HIGH);
            alerts.setDescription("জরুরি রক্তের আবেদন, স্টক ঘাটতি ও কুলডাউন সতর্কতা");
            nm.createNotificationChannel(alerts);

            NotificationChannel chat = new NotificationChannel(CH_CHAT,
                    "চ্যাট বার্তা", NotificationManager.IMPORTANCE_DEFAULT);
            chat.setDescription("নতুন চ্যাট বার্তার নোটিফিকেশন");
            nm.createNotificationChannel(chat);
        } catch (Exception ignored) { }
    }

    /** লগইন থাকলে + চালু থাকলে ফোরগ্রাউন্ড সার্ভিস চালু করে। */
    public static void ensureService(Context c) {
        if (!enabled(c)) return;
        try {
            Intent i = new Intent(c, BgService.class);
            if (Build.VERSION.SDK_INT >= 26) c.startForegroundService(i);
            else c.startService(i);
        } catch (Exception ignored) { }
    }

    public static void stopService(Context c) {
        try { c.stopService(new Intent(c, BgService.class)); } catch (Exception ignored) { }
    }

    /** Android 13+ এ নোটিফিকেশন পারমিশন চাওয়া (একবার)। */
    public static void requestPermission(MainActivity a) {
        try {
            if (Build.VERSION.SDK_INT >= 33 &&
                    a.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED)
                a.requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 4001);
        } catch (Exception ignored) { }
    }

    static PendingIntent pi(Context c, String screen, String channel) {
        Intent i = new Intent(c, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("screen", screen);
        if (channel != null) i.putExtra("channel", channel);
        int req = (screen + ":" + (channel == null ? "" : channel)).hashCode();
        return PendingIntent.getActivity(c, req, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    /** চ্যানেল অনুযায়ী নোটিফিকেশন — ট্যাপ করলে নির্দিষ্ট স্ক্রিনে যাবে। */
    public static void notify(Context c, String channel, int id, String title, String body,
                              String screen, String deepChannel) {
        if (!enabled(c)) return;
        ensureChannels(c);
        try {
            Notification.Builder b = new Notification.Builder(c.getApplicationContext(), channel)
                    .setSmallIcon(R.drawable.ic_dropfill)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new Notification.BigTextStyle().bigText(body))
                    .setContentIntent(pi(c, screen, deepChannel))
                    .setAutoCancel(true)
                    .setColor(D.primary);
            if (CH_ALERTS.equals(channel)) b.setPriority(Notification.PRIORITY_HIGH);
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.notify(id, b.build());
        } catch (Exception ignored) { }
    }

    /** ফোরগ্রাউন্ড সার্ভিসের স্থায়ী নোটিফিকেশন। */
    static Notification serviceNotification(Context c) {
        ensureChannels(c);
        return new Notification.Builder(c.getApplicationContext(), CH_SERVICE)
                .setSmallIcon(R.drawable.ic_dropfill)
                .setContentTitle(Data.APP_NAME)
                .setContentText("সক্রিয় — জরুরি রক্ত ও নতুন বার্তার আপডেট পাচ্ছেন")
                .setOngoing(true)
                .setShowWhen(false)
                .setColor(D.primary)
                .setContentIntent(pi(c, "home", null))
                .build();
    }
}
