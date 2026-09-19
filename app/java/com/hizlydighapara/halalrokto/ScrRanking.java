package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/** ডোনার র‌্যাংকিং — পডিয়াম + সম্পূর্ণ তালিকা + ব্যাজের নিয়ম। */
public final class ScrRanking {

    private ScrRanking() {}

    static final int[] MEDAL_COLORS = {0xFFD4AF37, 0xFFA8A9AD, 0xFFCD7F32};

    public static View build(final Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);

        root.addView(Ui.heroBar(c, "ডোনার র‌্যাংকিং", "সম্মাননা ও পদক তালিকা", true, null, new Runnable() {
            public void run() { Ui.host.back(); }
        }));

        final List<Data.User> list = Data.ranking();
        Data.User me = Data.me();
        int myRank = 0;
        for (int i = 0; i < list.size(); i++) if (list.get(i).id.equals(me.id)) { myRank = i + 1; break; }

        /* podium */
        if (list.size() >= 3) {
            LinearLayout podium = Ui.h(c);
            podium.setGravity(Gravity.BOTTOM);
            podium.setPadding(D.dp(16), D.dp(18), D.dp(16), 0);
            int[] order = {1, 0, 2}; // silver, gold, bronze visual order
            int[] heights = {D.dp(118), D.dp(136), D.dp(108)};
            for (int oi = 0; oi < 3; oi++) {
                final Data.User u = list.get(order[oi]);
                LinearLayout col = Ui.v(c);
                col.setGravity(Gravity.CENTER_HORIZONTAL);
                col.setPadding(D.dp(8), D.dp(12), D.dp(8), D.dp(12));
                col.setBackground(D.roundStroke(D.surfaceCLo, 18, D.outlineVar, 1));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, heights[oi], 1f);
                p.rightMargin = D.dp(7);
                col.setLayoutParams(p);

                ImageView medal = Ui.icon(c, R.drawable.ic_medal, 22, MEDAL_COLORS[order[oi]]);
                col.addView(medal);
                TextView av = Ui.avatar(c, Bn.initials(u.name), D.avatarColor(u.id), 44, 16);
                LinearLayout.LayoutParams avp = new LinearLayout.LayoutParams(D.dp(44), D.dp(44));
                avp.topMargin = D.dp(7);
                av.setLayoutParams(avp);
                col.addView(av);
                TextView nm = Ui.txt(c, u.name, 11.5f, D.onSurface, 700);
                nm.setGravity(Gravity.CENTER);
                nm.setLineSpacing(0, 1.2f);
                nm.setPadding(0, D.dp(6), 0, 0);
                col.addView(nm);
                TextView cnt = Ui.txt(c, Bn.bn(u.donationCount) + " বার দান", 10f, D.onSurfaceVar, 600);
                cnt.setGravity(Gravity.CENTER);
                cnt.setPadding(0, D.dp(3), 0, 0);
                col.addView(cnt);

                if (order[oi] == 0) col.setElevation(D.dp(4));
                col.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) { ScrDonors.donorSheet(c, u); }
                });
                podium.addView(col);
            }
            root.addView(podium);
        }

        /* full list from #4 */
        root.addView(Ui.sectionHeader(c, "সম্পূর্ণ তালিকা"));
        LinearLayout listWrap = Ui.v(c);
        listWrap.setPadding(D.dp(16), 0, D.dp(16), 0);
        for (int i = 3; i < list.size(); i++) {
            final Data.User u = list.get(i);
            LinearLayout row = Ui.h(c);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(D.dp(12), D.dp(10), D.dp(12), D.dp(10));
            boolean isMe = u.id.equals(me.id);
            row.setBackground(D.ripple(D.roundStroke(isMe ? D.primaryC : D.surfaceCLo, 16,
                    isMe ? D.primary : D.outlineVar, 1), D.rippleColor));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.bottomMargin = D.dp(7);
            row.setLayoutParams(p);

            TextView pos = Ui.txt(c, Bn.bn(i + 1), 13, D.onSurfaceVar, 700);
            pos.setMinWidth(D.dp(22));
            row.addView(pos);
            row.addView(Ui.avatar(c, Bn.initials(u.name), D.avatarColor(u.id), 36, 13));
            ((LinearLayout.LayoutParams) row.getChildAt(1).getLayoutParams()).leftMargin = D.dp(8);
            ((LinearLayout.LayoutParams) row.getChildAt(1).getLayoutParams()).rightMargin = D.dp(11);

            LinearLayout m = Ui.v(c);
            m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            LinearLayout nameRow = Ui.h(c);
            nameRow.addView(Ui.txt(c, u.name, 13, D.onSurface, 700));
            if (isMe) {
                TextView meTag = Ui.txt(c, " — আপনি", 10f, D.primary, 700);
                nameRow.addView(meTag);
            }
            m.addView(nameRow);
            TextView addr = Ui.txt(c, u.address, 10.5f, D.onSurfaceVar, 400);
            addr.setPadding(0, D.dp(1), 0, 0);
            m.addView(addr);
            row.addView(m);

            row.addView(Ui.rankBadge(c, Data.badge(u)[0]));
            ((LinearLayout.LayoutParams) row.getChildAt(3).getLayoutParams()).rightMargin = D.dp(10);

            TextView cnt = Ui.txt(c, Bn.bn(u.donationCount), 14, D.primary, 700);
            row.addView(cnt);

            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { ScrDonors.donorSheet(c, u); }
            });
            listWrap.addView(row);
        }
        root.addView(listWrap);

        /* my rank banner */
        LinearLayout bw = Ui.v(c);
        bw.setPadding(D.dp(16), D.dp(12), D.dp(16), 0);
        LinearLayout ban = Ui.h(c);
        ban.setGravity(Gravity.CENTER_VERTICAL);
        ban.setPadding(D.dp(14), D.dp(13), D.dp(14), D.dp(13));
        ban.setBackground(D.round(D.primaryC, 18));
        ImageView medal = Ui.icon(c, R.drawable.ic_medal, 22, D.onPrimaryC);
        medal.setBackground(D.round(D.withAlpha(0xFFFFFFFF, 130), 15));
        medal.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
        ban.addView(medal);
        ((LinearLayout.LayoutParams) medal.getLayoutParams()).rightMargin = D.dp(13);
        LinearLayout t = Ui.v(c);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        t.addView(Ui.txt(c, "🏆 আপনার র‌্যাংকিং: " + Bn.bn(myRank) + " নং", 14, D.onPrimaryC, 700));
        TextView sub = Ui.txt(c, "মোট " + Bn.bn(me.donationCount) + " বার দান • " + Bn.bn(list.size()) + " জনের মধ্যে", 11.5f, D.withAlpha(D.onPrimaryC, 220), 500);
        sub.setPadding(0, D.dp(2), 0, 0);
        t.addView(sub);
        ban.addView(t);
        bw.addView(ban);
        root.addView(bw);

        /* badge rules */
        root.addView(Ui.sectionHeader(c, "ব্যাজের নিয়মাবলি"));
        String[][] rules = {
                {"platinum", "প্লাটিনাম ডোনার", "৫০+ বার রক্তদান"},
                {"gold", "গোল্ড ডোনার", "২০+ বার রক্তদান"},
                {"silver", "সিলভার ডোনার", "১০+ বার রক্তদান"},
                {"bronze", "ব্রোঞ্জ ডোনার", "৫+ বার রক্তদান"},
                {"friend", "রক্তবন্ধু", "অন্তত ১ বার রক্তদান"},
                {"new", "নতুন ডোনার", "এখনো রক্ত দেননি"}};
        LinearLayout rulesWrap = Ui.v(c);
        rulesWrap.setPadding(D.dp(16), 0, D.dp(16), D.dp(18));
        for (String[] r : rules) {
            LinearLayout row = Ui.h(c);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(D.dp(13), D.dp(9), D.dp(13), D.dp(9));
            row.setBackground(D.roundStroke(D.surfaceCLo, 14, D.outlineVar, 1));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.bottomMargin = D.dp(7);
            row.setLayoutParams(p);
            row.addView(Ui.rankBadge(c, r[0]));
            ((LinearLayout.LayoutParams) row.getChildAt(0).getLayoutParams()).rightMargin = D.dp(12);
            TextView d = Ui.txt(c, r[2], 12f, D.onSurfaceVar, 500);
            d.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            row.addView(d);
            rulesWrap.addView(row);
        }
        root.addView(rulesWrap);

        sc.addView(root);
        return sc;
    }
}
