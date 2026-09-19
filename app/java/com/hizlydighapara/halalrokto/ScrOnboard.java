package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** অনবোর্ডিং — ৩টি স্লাইড। */
public final class ScrOnboard {

    private ScrOnboard() {}

    static final String[][] SLIDES = {
            {"dropfill", "রক্ত দিন, জীবন বাঁচান",
                    "প্রতিটি ব্যাগ রক্ত একটি প্রাণ বাঁচায়। হালাল পথে সঠিক নিয়মে রক্তদান করুন এবং মানুষের জীবনে হাসি ফোটান।"},
            {"sos", "জরুরি মুহূর্তে ডোনার খুঁজুন",
                    "মুহূর্তের প্রয়োজনে রক্তের গ্রুপ অনুযায়ী যোগ্য ডোনার খুঁজুন, সরাসরি কল করুন — সময় নষ্ট না হয়।"},
            {"medal", "ডোনার কার্ড ও র‌্যাংকিং",
                    "নিজের ডিজিটাল ডোনার কার্ড পান, রক্তদানের ইতিহাস রাখুন এবং র‌্যাংকিংয়ে নিজের অবস্থান দেখুন।"}
    };

    public static View build(final Context c) {
        final LinearLayout root = Ui.v(c);
        root.setBackgroundColor(D.bg);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final FrameLayout media = new FrameLayout(c);
        media.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.15f));

        final LinearLayout body = Ui.v(c);
        body.setPadding(D.dp(34), D.dp(10), D.dp(34), 0);
        body.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final LinearLayout foot = Ui.v(c);
        foot.setPadding(D.dp(24), D.dp(12), D.dp(24), D.dp(18));

        final int[] idx = {0};
        final Runnable[] render = new Runnable[1];

        render[0] = new Runnable() {
            public void run() {
                int i = idx[0];
                String[] s = SLIDES[i];
                int resId = s[0].equals("dropfill") ? R.drawable.ic_dropfill : s[0].equals("sos") ? R.drawable.ic_sos : R.drawable.ic_medal;

                media.removeAllViews();
                // concentric rings
                float[] radii = {86, 62, 40};
                for (int r = 0; r < 3; r++) {
                    View ring = new View(c);
                    int size = D.dp(radii[r] * 2);
                    ring.setBackground(D.roundStroke(Color.TRANSPARENT, radii[r],
                            D.dark ? D.withAlpha(0xFFFFB4AB, 26 + r * 12) : D.withAlpha(0xFFB3261E, 20 + r * 16), 1.6f));
                    ring.setLayoutParams(Ui.flp(size, size, Gravity.CENTER));
                    ring.setScaleX(0.6f + r * 0.1f);
                    ring.setScaleY(0.6f + r * 0.1f);
                    media.addView(ring);
                    ring.animate().scaleX(1f).scaleY(1f).setDuration(420 + r * 90)
                            .setInterpolator(new DecelerateInterpolator()).start();
                }
                ImageView ico = Ui.icon(c, resId, 54, 0xFFFFFFFF);
                ico.setLayoutParams(Ui.flp(D.dp(54), D.dp(54), Gravity.CENTER));
                ico.setScaleX(0.4f);
                ico.setScaleY(0.4f);
                media.addView(ico);
                ico.animate().scaleX(1f).scaleY(1f).setDuration(420)
                        .setInterpolator(new DecelerateInterpolator()).start();

                body.removeAllViews();
                body.setAlpha(0f);
                TextView t = Ui.txt(c, s[1], 24, D.onSurface, 700);
                t.setGravity(Gravity.CENTER);
                body.addView(t);
                TextView p = Ui.txt(c, s[2], 13f, D.onSurfaceVar, 400);
                p.setGravity(Gravity.CENTER);
                p.setLineSpacing(0, 1.55f);
                p.setPadding(0, D.dp(12), 0, 0);
                body.addView(p);

                LinearLayout dots = Ui.h(c);
                dots.setGravity(Gravity.CENTER);
                dots.setPadding(0, D.dp(22), 0, 0);
                for (int d = 0; d < SLIDES.length; d++) {
                    View dot = new View(c);
                    int w = d == i ? D.dp(22) : D.dp(7);
                    dot.setLayoutParams(lpDot(w));
                    dot.setBackground(D.round(d == i ? D.primary : D.outlineVar, 50));
                    dots.addView(dot);
                }
                body.addView(dots);
                body.animate().alpha(1f).setDuration(300).start();

                foot.removeAllViews();
                LinearLayout btns = Ui.h(c);
                if (i > 0) {
                    TextView prev = Ui.btn(c, "পেছনে", Ui.BTN_TEXT, new Runnable() {
                        public void run() { idx[0]--; render[0].run(); }
                    });
                    prev.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, D.dp(52)));
                    ((LinearLayout.LayoutParams) prev.getLayoutParams()).rightMargin = D.dp(10);
                    btns.addView(prev);
                }
                TextView next = Ui.btn(c, i == SLIDES.length - 1 ? "শুরু করুন" : "পরবর্তী", Ui.BTN_GRAD, new Runnable() {
                    public void run() {
                        if (idx[0] < SLIDES.length - 1) { idx[0]++; render[0].run(); }
                        else {
                            Data.s.onboardingDone = true;
                            Data.save();
                            Ui.host.go("auth", true);
                        }
                    }
                });
                next.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(52), 1f));
                btns.addView(next);
                foot.addView(btns);

                TextView org = Ui.txt(c, Data.ORG + " • " + Data.SINCE + " • " + Data.ADDRESS, 10.5f, D.onSurfaceVar, 600);
                org.setGravity(Gravity.CENTER);
                org.setPadding(0, D.dp(14), 0, D.dp(20));
                foot.addView(org);
            }
        };

        root.addView(media);
        root.addView(body);
        root.addView(foot);
        render[0].run();
        return root;
    }

    static LinearLayout.LayoutParams lpDot(int w) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, D.dp(7));
        p.rightMargin = D.dp(5);
        return p;
    }
}
