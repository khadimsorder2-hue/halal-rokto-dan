package com.hizlydighapara.halalrokto;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * ফোরগ্রাউন্ড ব্যাকগ্রাউন্ড সার্ভিস — জরুরি রক্ত ও চ্যাটের আপডেট।
 * ৬০ সেকেন্ড পর পর সার্ভার পোল (সার্ভার সেট থাকলে), ১৫ মিনিট পর পর লোকাল সতর্কতা
 * (কুলডাউন সম্পূর্ণ, স্টক ঘাটতি)। ব্যাটারি-বান্ধব — কোনো লাইব্রেরি নেই।
 */
public class BgService extends Service {

    public static final int NOTIF_ID = 1001;

    private Thread worker;
    private volatile boolean running = true;
    private int tick = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;
        Notification n = NotifUtil.serviceNotification(this);
        if (Build.VERSION.SDK_INT >= 29)
            startForeground(NOTIF_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        else
            startForeground(NOTIF_ID, n);

        worker = new Thread(new Runnable() {
            public void run() { loop(); }
        }, "halal-bg-sync");
        worker.setDaemon(true);
        worker.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        running = false;
        if (worker != null) worker.interrupt();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    /* ── Poll loop ──────────────────────────────────────────── */
    void loop() {
        while (running) {
            try { Thread.sleep(60000L); } catch (InterruptedException e) { break; }
            if (!running) break;
            tick++;
            try {
                if (Data.s.notifOn) {
                    if (Data.Sync.enabled()) pollServer();
                    if (tick % 15 == 0) localChecks();
                }
            } catch (Exception ignored) { }
        }
    }

    /* ── Server poll (ঐচ্ছিক) ─────────────────────────────── */
    void pollServer() {
        try {
            String base = Data.s.serverUrl.trim();
            HttpURLConnection c = (HttpURLConnection) new URL(
                    base + (base.endsWith("/") ? "" : "/") + "api/pull?since=" + Data.s.syncLast).openConnection();
            c.setRequestMethod("GET");
            c.setConnectTimeout(10000);
            c.setReadTimeout(12000);
            int code = c.getResponseCode();
            if (code >= 200 && code < 300) {
                InputStream is = c.getInputStream();
                java.io.ByteArrayOutputStream bo = new java.io.ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int n;
                while ((n = is.read(buf)) > 0) bo.write(buf, 0, n);
                is.close();
                String json = new String(bo.toByteArray(), "UTF-8");
                Data.s.syncLast = System.currentTimeMillis();
                Data.s.syncStatus = "ok";
                Data.save();
                handlePull(json);
            } else {
                Data.s.syncStatus = "err";
                Data.save();
            }
            c.disconnect();
        } catch (Exception e) {
            Data.s.syncStatus = "err";
            Data.save();
        }
    }

    void handlePull(String json) {
        try {
            JSONObject o = new JSONObject(json);
            JSONArray ms = o.optJSONArray("messages");
            if (ms != null) {
                for (int i = 0; i < ms.length(); i++) {
                    JSONObject m = ms.getJSONObject(i);
                    String sid = m.optString("senderId", "");
                    if (sid.equals(Data.s.session)) continue; // নিজের মেসেজ
                    Data.User from = Data.userById(sid);
                    if (from == null) {
                        from = new Data.User();
                        from.id = sid;
                        from.name = m.optString("senderName", "সদস্য");
                    }
                    Chat.receive(m.optString("channel", "general"), from, m.optString("text", ""));
                }
            }
            JSONArray es = o.optJSONArray("emergencies");
            if (es != null) {
                for (int i = 0; i < es.length(); i++) {
                    JSONObject e = es.getJSONObject(i);
                    String id = e.optString("id", "");
                    boolean exists = false;
                    for (Data.Emergency x : Data.s.emergencies) if (x.id.equals(id)) { exists = true; break; }
                    if (!exists) {
                        Data.Emergency em = new Data.Emergency();
                        em.id = id;
                        em.patientName = e.optString("patientName", "");
                        em.bloodGroup = e.optString("bloodGroup", "O+");
                        em.units = e.optInt("units", 1);
                        em.hospital = e.optString("hospital", "");
                        em.location = e.optString("location", "");
                        em.contact = e.optString("contact", "");
                        em.notes = e.optString("notes", "");
                        em.createdAt = e.optLong("createdAt", System.currentTimeMillis());
                        em.createdBy = e.optString("createdBy", "");
                        em.fulfilled = e.optBoolean("fulfilled", false);
                        Data.s.emergencies.add(0, em);
                        Data.save();
                        NotifUtil.notify(this, NotifUtil.CH_ALERTS, ("emg_" + id).hashCode(),
                                "নতুন জরুরি আবেদন: " + em.bloodGroup,
                                em.patientName + "-এর জন্য " + Bn.bn(em.units) + " ব্যাগ রক্ত প্রয়োজন — " + em.hospital,
                                "emergency", null);
                    }
                }
            }
        } catch (Exception ignored) { }
    }

    /* ── Local alerts ───────────────────────────────────────── */
    void localChecks() {
        SharedPreferences flags = getSharedPreferences("halal_rokto_flags", MODE_PRIVATE);

        Data.User me = Data.me();
        if (me != null && me.lastDonationDate > 0) {
            Data.Cooldown cd = Data.cooldown(me);
            String key = "cd_done_" + me.id + "_" + me.lastDonationDate;
            if (cd.eligible && !flags.getBoolean(key, false)) {
                flags.edit().putBoolean(key, true).apply();
                NotifUtil.notify(this, NotifUtil.CH_ALERTS, key.hashCode(),
                        "আপনি এখন রক্ত দিতে পারেন",
                        "৯০ দিনের কুলডাউন পূর্ণ হয়েছে — কারও জীবন বাঁচাতে পারেন।",
                        "home", null);
            }
        }

        List<String> critical = new ArrayList<String>();
        for (int i = 0; i < Data.BLOOD_GROUPS.length; i++)
            if (Data.s.stock[i] <= 1) critical.add(Data.BLOOD_GROUPS[i]);
        if (!critical.isEmpty()) {
            long last = flags.getLong("stock_alert_at", 0);
            if (System.currentTimeMillis() - last > 12L * 3600000L) {
                flags.edit().putLong("stock_alert_at", System.currentTimeMillis()).apply();
                StringBuilder b = new StringBuilder();
                for (String g : critical) { if (b.length() > 0) b.append(", "); b.append(g); }
                NotifUtil.notify(this, NotifUtil.CH_ALERTS, "stock_alert".hashCode(),
                        "রক্তের স্টকে মারাত্মক ঘাটতি",
                        b.toString() + " গ্রুপের রক্ত প্রায় শেষ — ডোনার হিসেবে এগিয়ে আসুন।",
                        "stock", null);
            }
        }
    }
}
