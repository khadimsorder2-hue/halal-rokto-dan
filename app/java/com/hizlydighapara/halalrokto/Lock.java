package com.hizlydighapara.halalrokto;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** অ্যাপ লক — ৪ সংখ্যার পিন কীপ্যাড (MainActivity-র ওভারলে হিসেবে দেখানো হয়)। */
public final class Lock {

    private Lock() {}

    public static View build(final Context c, int statusBarInset, final Runnable hideLock) {
        final StringBuilder entered = new StringBuilder();
        final LinearLayout[] dotsRef = new LinearLayout[1];
        final TextView[] errRef = new TextView[1];

        LinearLayout root = Ui.v(c);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(D.bg);
        root.setPadding(0, statusBarInset + D.dp(60), 0, D.dp(30));

        /* লোগো ফোঁটা */
        ImageView drop = Ui.icon(c, R.drawable.ic_dropfill, 46, D.primary);
        drop.setBackground(D.round(D.primaryC, 60));
        drop.setPadding(D.dp(18), D.dp(18), D.dp(18), D.dp(18));
        drop.setElevation(D.dp(4));
        LinearLayout.LayoutParams dp = (LinearLayout.LayoutParams) drop.getLayoutParams();
        dp.gravity = Gravity.CENTER_HORIZONTAL;
        drop.setLayoutParams(dp);
        root.addView(drop);

        TextView title = Ui.txt(c, Data.APP_NAME, 19, D.onSurface, 700);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, D.dp(14), 0, 0);
        root.addView(title);
        TextView sub = Ui.txt(c, "গোপনীয়তার জন্য পিন দিন", 12f, D.onSurfaceVar, 500);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, D.dp(4), 0, 0);
        root.addView(sub);

        /* ডট */
        LinearLayout dots = Ui.h(c);
        dots.setGravity(Gravity.CENTER);
        dots.setPadding(0, D.dp(30), 0, D.dp(8));
        for (int i = 0; i < 4; i++) {
            View d = new View(c);
            d.setBackground(D.roundStroke(D.outlineVar, 50, D.outline, 1.4f));
            d.setLayoutParams(Ui.lp(D.dp(15), D.dp(15)));
            ((LinearLayout.LayoutParams) d.getLayoutParams()).rightMargin = i < 3 ? D.dp(13) : 0;
            dots.addView(d);
        }
        dotsRef[0] = dots;
        root.addView(dots);

        TextView errText = Ui.txt(c, " ", 11.5f, D.error, 600);
        errText.setGravity(Gravity.CENTER);
        errText.setPadding(0, D.dp(4), 0, D.dp(6));
        errRef[0] = errText;
        root.addView(errText);

        /* কীপ্যাড */
        final Runnable[] tryUnlock = new Runnable[1];

        LinearLayout pad = new LinearLayout(c);
        pad.setOrientation(LinearLayout.VERTICAL);
        pad.setPadding(D.dp(38), D.dp(8), D.dp(38), 0);

        String[][] rows = {{"1", "2", "3"}, {"4", "5", "6"}, {"7", "8", "9"}, {"", "0", "⌫"}};
        for (String[] row : rows) {
            LinearLayout r = Ui.h(c);
            r.setGravity(Gravity.CENTER);
            r.setPadding(0, D.dp(6), 0, D.dp(6));
            for (final String k : row) {
                TextView b = new TextView(c);
                b.setTypeface(D.tfSemi);
                b.setTextSize(21);
                b.setGravity(Gravity.CENTER);
                if (k.equals("⌫")) {
                    b.setTextColor(D.onSurfaceVar);
                    b.setBackground(D.ripple(D.round(D.surfaceC, 50), D.rippleColor));
                } else if (k.isEmpty()) {
                    b.setClickable(false);
                } else {
                    b.setTextColor(D.onSurface);
                    b.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 50, D.outlineVar, 1), D.rippleColor));
                }
                LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0, D.dp(58), 1f);
                bp.setMargins(D.dp(7), 0, D.dp(7), 0);
                b.setLayoutParams(bp);
                b.setMinHeight(D.dp(58));
                if (!k.isEmpty()) b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Ui.host.haptic(6);
                        if (k.equals("⌫")) {
                            if (entered.length() > 0) entered.deleteCharAt(entered.length() - 1);
                        } else if (entered.length() < 4) {
                            entered.append(k);
                        }
                        // ডট আপডেট
                        for (int i = 0; i < 4; i++) {
                            View d = dotsRef[0].getChildAt(i);
                            boolean on = i < entered.length();
                            d.setBackground(on ? D.round(D.primary, 50)
                                    : D.roundStroke(D.outlineVar, 50, D.outline, 1.4f));
                        }
                        if (entered.length() == 4) tryUnlock[0].run();
                    }
                });
                r.addView(b);
            }
            pad.addView(r);
        }
        root.addView(pad);

        View gap = new View(c);
        gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(20)));
        root.addView(gap);

        /* ভেরিফাই রানাবল */
        tryUnlock[0] = new Runnable() {
            public void run() {
                if (Data.checkPin(entered.toString())) {
                    Ui.host.haptic(14);
                    hideLock.run();
                } else {
                    errRef[0].setText("ভুল পিন — আবার চেষ্টা করুন");
                    Ui.host.haptic(30);
                    ObjectAnimator sh = ObjectAnimator.ofFloat(dotsRef[0], "translationX",
                            0, D.dp(9), -D.dp(9), D.dp(6), -D.dp(6), 0);
                    sh.setDuration(380);
                    sh.start();
                    dotsRef[0].postDelayed(new Runnable() {
                        public void run() {
                            entered.setLength(0);
                            for (int i = 0; i < 4; i++)
                                dotsRef[0].getChildAt(i).setBackground(
                                        D.roundStroke(D.outlineVar, 50, D.outline, 1.4f));
                            errRef[0].setText(" ");
                        }
                    }, 650);
                }
            }
        };

        return root;
    }
}
