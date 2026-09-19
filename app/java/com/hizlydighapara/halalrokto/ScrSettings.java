package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

/** সেটিংস — ডার্ক মোড, ডেমো ডেটা, সার্ভার সিঙ্ক, ডেটা মুছুন। */
public final class ScrSettings {

    private ScrSettings() {}

    public static View build(final Context c) {
        final ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        final LinearLayout root = Ui.v(c);

        final Runnable[] render = new Runnable[1];
        render[0] = new Runnable() {
            public void run() {
                root.removeAllViews();
                root.addView(Ui.appBar(c, "সেটিংস", null, new Runnable() {
                    public void run() { Ui.host.back(); }
                }));

                /* উপস্থিতি */
                root.addView(Ui.sectionHeader(c, "উপস্থিতি"));
                LinearLayout m1 = Ui.v(c);
                m1.setPadding(D.dp(16), 0, D.dp(16), 0);

                LinearLayout darkRow = switchRow(c, R.drawable.ic_dark, D.surfaceCH, D.onSurfaceVar,
                        "ডার্ক মোড", "রাতে চোখের আরামের জন্য", Data.s.themeDark, new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton b, boolean checked) {
                                Data.s.themeDark = checked;
                                Data.save();
                                Ui.host.haptic(10);
                                Ui.host.applyTheme();
                            }
                        });
                m1.addView(darkRow);
                root.addView(m1);

                /* ডেটা ও সিঙ্ক */
                root.addView(Ui.sectionHeader(c, "ডেটা ও সিঙ্ক"));
                LinearLayout m2 = Ui.v(c);
                m2.setPadding(D.dp(16), 0, D.dp(16), 0);

                LinearLayout demoRow = switchRow(c, R.drawable.ic_people, D.tertiaryC, D.onTertiaryC,
                        "ডেমো ডেটা", "নমুনা ডোনার ও আবেদন দেখান", Data.s.demoData, new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton b, boolean checked) {
                                Data.s.demoData = checked;
                                Data.save();
                                Ui.toast(checked ? "ডেমো ডেটা চালু হয়েছে"
                                        : "ডেমো ডেটা বন্ধ — এখন শুধু আসল সদস্যরা দেখা যাবে", false);
                            }
                        });
                demoRow.setLayoutParams(padBottom(demoRow));
                m2.addView(demoRow);

                LinearLayout serverRow = Ui.menuRow(c, R.drawable.ic_sync, D.primaryC, D.onPrimaryC,
                        "সার্ভার সিঙ্ক URL",
                        Data.s.serverUrl.isEmpty() ? "ক্লাউড ব্যাকএন্ড নির্ধারণ করুন (ঐচ্ছিক)" : Data.s.serverUrl,
                        Ui.icon(c, R.drawable.ic_chevron, 15, D.outline), new Runnable() {
                            public void run() { serverSheet(c, render); }
                        });
                serverRow.setLayoutParams(padBottom(serverRow));
                m2.addView(serverRow);

                LinearLayout wipeRow = Ui.menuRow(c, R.drawable.ic_trash, D.errorC, D.onErrorC,
                        "সব ডেটা মুছুন", "অ্যাপটি নতুন অবস্থায় ফিরবে", null, new Runnable() {
                            public void run() {
                                Ui.confirm(c, "সব ডেটা মুছে ফেলবেন?",
                                        "আপনার অ্যাকাউন্ট, রক্তদানের রেকর্ড, জরুরি আবেদন — সবকিছু মুছে যাবে। এই কাজ ফেরানো যাবে না।",
                                        "মুছে ফেলুন", true, new Runnable() {
                                            public void run() {
                                                Data.wipe();
                                                D.setDark(false);
                                                Ui.host.applyTheme();
                                                Ui.host.go("onboarding", true);
                                                Ui.toast("সব ডেটা মুছে ফেলা হয়েছে", false);
                                            }
                                        });
                            }
                        });
                m2.addView(wipeRow);
                root.addView(m2);

                /* অ্যাপ */
                root.addView(Ui.sectionHeader(c, "অ্যাপ"));
                LinearLayout m3 = Ui.v(c);
                m3.setPadding(D.dp(16), 0, D.dp(16), D.dp(20));
                LinearLayout aboutRow = Ui.menuRow(c, R.drawable.ic_dropfill, D.primaryC, D.onPrimaryC,
                        "অ্যাপ ও সংগঠন সম্পর্কে", Data.APP_NAME + " v" + Data.VERSION,
                        Ui.icon(c, R.drawable.ic_chevron, 15, D.outline), new Runnable() {
                            public void run() { Ui.host.go("about", false); }
                        });
                m3.addView(aboutRow);
                root.addView(m3);
            }
        };
        render[0].run();
        sc.addView(root);
        return sc;
    }

    static LinearLayout.LayoutParams padBottom(View v) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = D.dp(9);
        return p;
    }

    static LinearLayout switchRow(Context c, int iconRes, int iconBg, int iconFg,
                                  String title, String sub, boolean checked,
                                  CompoundButton.OnCheckedChangeListener listener) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(D.dp(16), D.dp(12), D.dp(14), D.dp(12));
        row.setBackground(D.roundStroke(D.surfaceCLo, 18, D.outlineVar, 1));

        android.widget.ImageView iv = Ui.icon(c, iconRes, 20, iconFg);
        iv.setBackground(D.round(iconBg, 14));
        iv.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
        row.addView(iv);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).rightMargin = D.dp(13);

        LinearLayout m = Ui.v(c);
        m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        m.addView(Ui.txt(c, title, 14, D.onSurface, 600));
        TextView s = Ui.txt(c, sub, 11.5f, D.onSurfaceVar, 400);
        s.setPadding(0, D.dp(2), 0, 0);
        m.addView(s);
        row.addView(m);

        Switch sw = new Switch(c);
        sw.setChecked(checked);
        sw.setOnCheckedChangeListener(listener);
        sw.setThumbTintList(android.content.res.ColorStateList.valueOf(checked ? 0xFFFFFFFF : 0xFFB3A6A4));
        row.addView(sw);
        return row;
    }

    static void serverSheet(final Context c, final Runnable[] refresh) {
        Ui.sheet(c, "সার্ভার সিঙ্ক URL", R.drawable.ic_sync, false, new Ui.SheetCallback() {
            public void onSheet(final LinearLayout body, final Runnable close) {
                TextView intro = Ui.txt(c, "সংগঠনের REST ব্যাকএন্ড হোস্ট করা থাকলে এখানে URL দিন — রেজিস্ট্রেশন, লগইন ও জরুরি আবেদন সার্ভারেও সিঙ্ক হবে। না জানলে খালি রাখুন; অ্যাপ সম্পূর্ণ অফলাইনে কাজ করে।", 12f, D.onSurfaceVar, 400);
                intro.setLineSpacing(0, 1.55f);
                intro.setPadding(D.dp(20), D.dp(10), D.dp(20), D.dp(6));
                body.addView(intro);

                final Ui.Field fUrl = Ui.field(c, "ব্যাকএন্ড URL", Ui.F_TEXT, Data.s.serverUrl, "https://api.example.com");
                body.addView(fUrl.root);

                TextView hint = Ui.txt(c, "যেমন: https://halal-rokto.vercel.app", 10.5f, D.outline, 400);
                hint.setPadding(D.dp(24), 0, 0, D.dp(6));
                body.addView(hint);

                LinearLayout btnWrap = Ui.v(c);
                btnWrap.setPadding(D.dp(18), D.dp(12), D.dp(18), 0);
                TextView save = Ui.btn(c, "সংরক্ষণ করুন", Ui.BTN_GRAD, new Runnable() {
                    public void run() {
                        String v = fUrl.input.getText().toString().trim();
                        if (!v.isEmpty() && !v.matches("https?://.+\\..+")) {
                            Ui.toast("সঠিক URL দিন (https:// দিয়ে শুরু)", true);
                            return;
                        }
                        Data.s.serverUrl = v;
                        Data.s.syncLast = 0;
                        Data.s.syncStatus = v.isEmpty() ? "off" : "on";
                        Data.save();
                        close.run();
                        Ui.toast(v.isEmpty() ? "সার্ভার সিঙ্ক বন্ধ" : "সার্ভার সিঙ্ক চালু: " + v, false);
                        refresh[0].run();
                    }
                });
                save.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(50)));
                btnWrap.addView(save);
                body.addView(btnWrap);
            }
        });
    }
}
