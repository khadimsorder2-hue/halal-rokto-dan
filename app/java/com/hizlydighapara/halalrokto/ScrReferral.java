package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** রেফারেল প্রোগ্রাম — কোড, পয়েন্ট, রেফারকৃত তালিকা। */
public final class ScrReferral {

    private ScrReferral() {}

    public static View build(final Context c) {
        final Data.User me = Data.me();
        final List<Data.User> referred = new ArrayList<>();
        for (Data.User u : Data.visibleUsers())
            if (me.referralCode.equals(u.referredBy)) referred.add(u);

        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);

        root.addView(Ui.heroBar(c, "রেফারেল প্রোগ্রাম", "বন্ধুদের আমন্ত্রণ জানান", true, null, new Runnable() {
            public void run() { Ui.host.back(); }
        }));

        /* hero code card */
        LinearLayout hero = Ui.v(c);
        hero.setGravity(Gravity.CENTER_HORIZONTAL);
        hero.setPadding(D.dp(22), D.dp(20), D.dp(22), D.dp(20));
        hero.setBackground(D.gradAngle(D.redGrad, 22, 315));
        hero.setElevation(D.dp(5));
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hp.setMargins(D.dp(16), D.dp(16), D.dp(16), 0);
        hero.setLayoutParams(hp);

        hero.addView(Ui.txt(c, "আপনার রেফারেল কোড", 11.5f, D.withAlpha(0xFFFFFFFF, 220), 600));
        TextView code = Ui.txt(c, me.referralCode, 32, 0xFFFFFFFF, 700);
        code.setGravity(Gravity.CENTER);
        code.setPadding(0, D.dp(6), 0, D.dp(2));
        code.setLetterSpacing(0.08f);
        hero.addView(code);

        LinearLayout btns = Ui.h(c);
        btns.setPadding(0, D.dp(12), 0, 0);
        TextView copy = Ui.btnSmall(c, "কপি", -1, new Runnable() {
            public void run() {
                android.content.ClipboardManager cm = (android.content.ClipboardManager)
                        c.getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) cm.setText(me.referralCode);
                Ui.toast("কোড কপি হয়েছে: " + me.referralCode, false);
            }
        });
        copy.setTextColor(0xFFFFFFFF);
        copy.setBackground(D.ripple(D.round(D.withAlpha(0xFFFFFFFF, 55), 50), D.rippleColorLight));
        copy.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(38), 1f));
        btns.addView(copy);
        ((LinearLayout.LayoutParams) copy.getLayoutParams()).rightMargin = D.dp(9);

        TextView share = Ui.btnSmall(c, "শেয়ার করুন", -1, new Runnable() {
            public void run() {
                Ui.host.share("🩸 " + Data.APP_NAME + " অ্যাপে যোগ দিন!\nরক্ত দিন, জীবন বাঁচান — " + Data.ORG
                        + "\n\nরেজিস্ট্রেশনের সময় আমার রেফারেল কোড দিন: " + me.referralCode, "রেফারেল শেয়ার");
            }
        });
        share.setTextColor(D.onPrimaryC);
        share.setBackground(D.ripple(D.round(0xFFFFFFFF, 50), D.rippleColor));
        share.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(38), 1f));
        btns.addView(share);
        hero.addView(btns);
        root.addView(hero);

        /* stats */
        LinearLayout stats = Ui.h(c);
        stats.setPadding(D.dp(16), D.dp(12), D.dp(16), 0);
        stats.addView(statBox(c, R.drawable.ic_people, "মোট রেফার", Bn.bn(referred.size()) + " জন", D.primaryC, D.onPrimaryC));
        ((LinearLayout.LayoutParams) stats.getChildAt(0).getLayoutParams()).rightMargin = D.dp(9);
        stats.addView(statBox(c, R.drawable.ic_medal, "রেফারেল পয়েন্ট", Bn.bn(referred.size() * 10), D.tertiaryC, D.onTertiaryC));
        root.addView(stats);

        /* referred list */
        root.addView(Ui.sectionHeader(c, "আপনার রেফারকৃত সদস্য"));
        if (referred.isEmpty()) {
            root.addView(Ui.emptyState(c, R.drawable.ic_people,
                    "এখনো কেউ যোগ দেননি",
                    "আপনার কোড শেয়ার করুন — যারা রেজিস্ট্রেশনের সময় কোডটি দেবেন, তারা এখানে তালিকাভুক্ত হবেন।"));
        } else {
            LinearLayout wrap = Ui.v(c);
            wrap.setPadding(D.dp(16), 0, D.dp(16), 0);
            for (final Data.User u : referred) {
                LinearLayout row = Ui.h(c);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(D.dp(12), D.dp(10), D.dp(12), D.dp(10));
                row.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 16, D.outlineVar, 1), D.rippleColor));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                p.bottomMargin = D.dp(8);
                row.setLayoutParams(p);
                row.addView(Ui.avatar(c, Bn.initials(u.name), D.avatarColor(u.id), 38, 14));
                ((LinearLayout.LayoutParams) row.getChildAt(0).getLayoutParams()).rightMargin = D.dp(12);
                LinearLayout m = Ui.v(c);
                m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                m.addView(Ui.txt(c, u.name, 13, D.onSurface, 700));
                TextView sub = Ui.txt(c, "যোগদান: " + Bn.fmtDateShort(u.createdAt) + " • " + Bn.bn(u.donationCount) + " বার দান", 10.5f, D.onSurfaceVar, 400);
                sub.setPadding(0, D.dp(2), 0, 0);
                m.addView(sub);
                row.addView(m);
                row.addView(Ui.bgroup(c, u.bloodType, false, 11));
                row.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) { ScrDonors.donorSheet(c, u); }
                });
                wrap.addView(row);
            }
            root.addView(wrap);
        }

        /* how it works */
        root.addView(Ui.sectionHeader(c, "কীভাবে কাজ করে?"));
        LinearLayout how = Ui.cardPad(c);
        LinearLayout.LayoutParams hwp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hwp.setMargins(D.dp(16), 0, D.dp(16), D.dp(20));
        how.setLayoutParams(hwp);
        String[][] steps = {
                {"১.", "আপনার ইউনিক কোড বন্ধুদের শেয়ার করুন"},
                {"২.", "তিনি রেজিস্ট্রেশনের সময় \"রেফারেল কোড\" ঘরে কোডটি দেবেন"},
                {"৩.", "প্রতিটি সফল রেফারেলে আপনি ১০ পয়েন্ট পাবেন এবং তালিকায় যুক্ত হবেন"}};
        for (String[] s : steps) {
            LinearLayout r = Ui.h(c);
            r.setGravity(Gravity.TOP);
            TextView num = Ui.txt(c, s[0], 14, D.primary, 700);
            r.addView(num);
            ((LinearLayout.LayoutParams) num.getLayoutParams()).rightMargin = D.dp(10);
            TextView t = Ui.txt(c, s[1], 12.5f, D.onSurfaceVar, 400);
            t.setLineSpacing(0, 1.5f);
            t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            r.addView(t);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rp.bottomMargin = D.dp(9);
            r.setLayoutParams(rp);
            how.addView(r);
        }
        root.addView(how);

        sc.addView(root);
        return sc;
    }

    static LinearLayout statBox(Context c, int iconRes, String label, String value, int bg, int fg) {
        LinearLayout l = Ui.h(c);
        l.setGravity(Gravity.CENTER_VERTICAL);
        l.setPadding(D.dp(14), D.dp(14), D.dp(14), D.dp(14));
        l.setBackground(D.round(bg, 18));
        l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        ImageView iv = Ui.icon(c, iconRes, 20, fg);
        iv.setBackground(D.round(D.withAlpha(0xFFFFFFFF, 120), 13));
        iv.setPadding(D.dp(9), D.dp(9), D.dp(9), D.dp(9));
        l.addView(iv);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).rightMargin = D.dp(12);
        LinearLayout t = Ui.v(c);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        t.addView(Ui.txt(c, label, 10.5f, D.withAlpha(fg, 230), 600));
        TextView v = Ui.txt(c, value, 17, fg, 700);
        v.setPadding(0, D.dp(2), 0, 0);
        t.addView(v);
        l.addView(t);
        return l;
    }
}
