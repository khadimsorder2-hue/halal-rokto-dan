package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** আরও সেবা — প্রোফাইল হাব + মেনু + লগআউট। */
public final class ScrMore {

    private ScrMore() {}

    public static View build(final Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);

        root.addView(Ui.heroBar(c, "আরও সেবা", Data.ORG, false, null, null));

        final Data.User me = Data.me();
        String[] badge = Data.badge(me);
        int rank = Data.myRank();

        /* profile header */
        LinearLayout head = Ui.v(c);
        head.setGravity(Gravity.CENTER_HORIZONTAL);
        head.setPadding(D.dp(24), D.dp(22), D.dp(24), D.dp(20));
        head.setBackground(D.gradAngle(D.heroGrad, 0, 315));
        root.addView(head);

        TextView av = Ui.avatar(c, Bn.initials(me.name), D.avatarColor(me.id), 74, 27);
        av.setElevation(D.dp(4));
        head.addView(av);

        LinearLayout nameRow = Ui.h(c);
        nameRow.setGravity(Gravity.CENTER_VERTICAL);
        nameRow.setPadding(0, D.dp(10), 0, 0);
        nameRow.addView(Ui.txt(c, me.name, 19, 0xFFFFFFFF, 700));
        if (me.verified) {
            ImageView vi = Ui.icon(c, R.drawable.ic_verified, 17, 0xFF8BD89E);
            nameRow.addView(vi);
            ((LinearLayout.LayoutParams) vi.getLayoutParams()).leftMargin = D.dp(7);
        }
        head.addView(nameRow);

        TextView sub = Ui.txt(c, me.bloodType + "  •  " + badge[1] + "  •  র‌্যাংক " + Bn.bn(rank), 11.5f, D.withAlpha(0xFFFFFFFF, 225), 600);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, D.dp(4), 0, 0);
        head.addView(sub);

        LinearLayout stats = Ui.h(c);
        stats.setPadding(0, D.dp(16), 0, 0);
        pstat(stats, Bn.bn(me.donationCount), "মোট দান");
        ((LinearLayout.LayoutParams) stats.getChildAt(0).getLayoutParams()).rightMargin = D.dp(7);
        pstat(stats, Bn.bn(me.referralCount), "রেফারেল");
        ((LinearLayout.LayoutParams) stats.getChildAt(1).getLayoutParams()).rightMargin = D.dp(7);
        pstat(stats, Bn.bn(me.trustScore) + "%", "ট্রাস্ট স্কোর");
        head.addView(stats);

        /* quick grid */
        LinearLayout qg = Ui.h(c);
        qg.setPadding(D.dp(16), D.dp(16), D.dp(16), 0);
        quick(c, qg, R.drawable.ic_card, "ডোনার\nকার্ড", D.primaryC, D.onPrimaryC, "donorcard");
        quick(c, qg, R.drawable.ic_medal, "র‌্যাংকিং", 0xFFFFF3CD, 0xFF5D4E00, "ranking");
        quick(c, qg, R.drawable.ic_people, "রেফারেল", 0xFFDCEEEE, 0xFF0F3B3B, "referral");
        quick(c, qg, R.drawable.ic_person, "প্রোফাইল\nসম্পাদনা", 0xFFE8DEF8, 0xFF3A2A5E, "profile");
        root.addView(qg);

        /* menu */
        root.addView(Ui.sectionHeader(c, "সব সেবা"));
        LinearLayout menu1 = Ui.v(c);
        menu1.setPadding(D.dp(16), 0, D.dp(16), 0);

        LinearLayout r1 = Ui.menuRow(c, R.drawable.ic_history, D.primaryC, D.onPrimaryC,
                "রক্তদানের ইতিহাস", "আপনার সকল দানের রেকর্ড ও টাইমলাইন",
                chevron(c), new Runnable() {
                    public void run() { Ui.host.go("history", false); }
                });
        menu1.addView(r1);
        ((LinearLayout.LayoutParams) r1.getLayoutParams()).bottomMargin = D.dp(9);

        LinearLayout r2 = Ui.menuRow(c, R.drawable.ic_bell, D.tertiaryC, D.onTertiaryC,
                "নোটিফিকেশন", "পড়া হয়নি " + Bn.bn(Data.unreadCount()) + " টি",
                chevron(c), new Runnable() {
                    public void run() { Ui.host.go("notifs", false); }
                });
        menu1.addView(r2);
        ((LinearLayout.LayoutParams) r2.getLayoutParams()).bottomMargin = D.dp(9);

        LinearLayout r3 = Ui.menuRow(c, R.drawable.ic_settings, D.surfaceCH, D.onSurfaceVar,
                "সেটিংস", "থিম, ডেটা ও সার্ভার সিঙ্ক",
                chevron(c), new Runnable() {
                    public void run() { Ui.host.go("settings", false); }
                });
        menu1.addView(r3);
        ((LinearLayout.LayoutParams) r3.getLayoutParams()).bottomMargin = D.dp(9);

        LinearLayout r4 = Ui.menuRow(c, R.drawable.ic_info, D.surfaceCH, D.onSurfaceVar,
                "অ্যাপ ও সংগঠন সম্পর্কে", Data.ORG,
                chevron(c), new Runnable() {
                    public void run() { Ui.host.go("about", false); }
                });
        menu1.addView(r4);
        root.addView(menu1);

        /* logout */
        LinearLayout menu2 = Ui.v(c);
        menu2.setPadding(D.dp(16), D.dp(14), D.dp(16), 0);
        LinearLayout lo = Ui.menuRow(c, R.drawable.ic_logout, D.errorC, D.onErrorC,
                "লগআউট", "অ্যাকাউন্ট থেকে বেরিয়ে যান", null, new Runnable() {
                    public void run() {
                        Ui.confirm(c, "লগআউট করবেন?",
                                "আপনার অ্যাকাউন্ট থেকে বেরিয়ে যাবেন। ডেটা সংরক্ষিত থাকবে, আবার লগইন করে ফিরে আসতে পারবেন।",
                                "লগআউট", false, new Runnable() {
                                    public void run() {
                                        Data.logout();
                                        Ui.host.go("auth", true);
                                    }
                                });
                    }
                });
        menu2.addView(lo);
        root.addView(menu2);

        /* version footer */
        TextView ver = Ui.txt(c, Data.APP_NAME + " v" + Data.VERSION + " • " + Data.ORG, 10.5f, D.outline, 600);
        ver.setGravity(Gravity.CENTER);
        ver.setPadding(0, D.dp(18), 0, D.dp(22));
        root.addView(ver);

        sc.addView(root);
        return sc;
    }

    static void pstat(LinearLayout parent, String num, String label) {
        Context c = parent.getContext();
        LinearLayout l = Ui.v(c);
        l.setGravity(Gravity.CENTER);
        l.setPadding(D.dp(10), D.dp(11), D.dp(10), D.dp(11));
        l.setBackground(D.round(D.withAlpha(0xFFFFFFFF, D.dark ? 30 : 60), 16));
        l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView n = Ui.txt(c, num, 18, 0xFFFFFFFF, 700);
        n.setGravity(Gravity.CENTER);
        l.addView(n);
        TextView lb = Ui.txt(c, label, 10f, D.withAlpha(0xFFFFFFFF, 220), 600);
        lb.setGravity(Gravity.CENTER);
        lb.setPadding(0, D.dp(2), 0, 0);
        l.addView(lb);
        parent.addView(l);
    }

    static void quick(Context c, LinearLayout parent, int iconRes, String label, int bg, int fg, final String target) {
        LinearLayout l = Ui.v(c);
        l.setGravity(Gravity.CENTER_HORIZONTAL);
        l.setPadding(D.dp(6), D.dp(13), D.dp(6), D.dp(13));
        l.setBackground(D.ripple(D.round(bg, 20), D.withAlpha(fg, 46)));
        l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        ((LinearLayout.LayoutParams) l.getLayoutParams()).rightMargin = D.dp(7);
        ImageView iv = Ui.icon(c, iconRes, 23, fg);
        iv.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
        iv.setBackground(D.round(D.withAlpha(0xFFFFFFFF, 120), 15));
        l.addView(iv);
        TextView t = Ui.txt(c, label, 10.5f, fg, 700);
        t.setGravity(Gravity.CENTER);
        t.setLineSpacing(0, 1.25f);
        t.setPadding(0, D.dp(8), 0, 0);
        l.addView(t);
        l.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.go(target, false); }
        });
        parent.addView(l);
    }

    static ImageView chevron(Context c) {
        return Ui.icon(c, R.drawable.ic_chevron, 15, D.outline);
    }
}
