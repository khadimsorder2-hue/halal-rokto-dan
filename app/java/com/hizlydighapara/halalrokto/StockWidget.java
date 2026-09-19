package com.hizlydighapara.halalrokto;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/** হোম-স্ক্রিন উইজেট — লাইভ রক্তের স্টক (৮ গ্রুপ + মোট ব্যাগ)। */
public class StockWidget extends AppWidgetProvider {

    static void updateAppWidget(Context c, int appWidgetId) {
        RemoteViews views = build(c);
        AppWidgetManager.getInstance(c).updateAppWidget(appWidgetId, views);
    }

    static RemoteViews build(Context c) {
        RemoteViews views = new RemoteViews(c.getPackageName(), R.layout.widget_stock);

        // মোট ব্যাগ
        views.setTextViewText(R.id.w_total, Bn.bn(Data.stockTotal()) + " ব্যাগ");

        int[] ids = {R.id.w_ap, R.id.w_an, R.id.w_bp, R.id.w_bn,
                R.id.w_abp, R.id.w_abn, R.id.w_op, R.id.w_on};
        for (int i = 0; i < 8; i++) {
            int n = Data.s.stock[i];
            views.setTextViewText(ids[i], Data.BLOOD_GROUPS[i] + "  " + Bn.bn(n));
            views.setTextColor(ids[i], n <= 1 ? 0xFFFF8A80 : n <= 3 ? 0xFFFFE082 : 0xFFE8F5E9);
        }

        // ট্যাপ করলে অ্যাপ খুলবে
        Intent open = new Intent(c, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        open.putExtra("screen", "stock");
        PendingIntent pi = PendingIntent.getActivity(c, 31, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.w_root, pi);
        return views;
    }

    /** ডেটা বদলালে সব উইজেট রিফ্রেশ — অ্যাপ থেকে ডাকা হয়। */
    public static void push(Context c) {
        try {
            AppWidgetManager m = AppWidgetManager.getInstance(c);
            ComponentName name = new ComponentName(c, StockWidget.class);
            int[] ids = m.getAppWidgetIds(name);
            if (ids == null || ids.length == 0) return;
            RemoteViews views = build(c);
            m.updateAppWidget(name, views);
        } catch (Exception ignored) { }
    }

    @Override
    public void onUpdate(Context c, AppWidgetManager m, int[] appWidgetIds) {
        try {
            D.init(c);
            Data.init(c);
        } catch (Exception ignored) { }
        for (int id : appWidgetIds) updateAppWidget(c, id);
    }

    @Override
    public void onEnabled(Context c) { push(c); }
}
