package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/** পরিসংখ্যান — চার্ট, অগ্রগতি, মাইলফলক। */
public final class ScrStats {

    private ScrStats() {}

    public static View build(Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);
        root.addView(Ui.heroBar(c, "পরিসংখ্যান", "সংঘের অগ্রগতি এক নজরে", false, null, null));

        Data.User me = Data.me();
        List<Data.User> users = Data.visibleUsers();
        List<Data.Donation> dons = Data.visibleDonations();

        /* শীর্ষ টাইল */
        LinearLayout tiles = Ui.h(c);
        tiles.setPadding(D.dp(16), D.dp(14), D.dp(16), 0);
        tiles.addView(Ui.statTile(c, Bn.bn(users.size()), "মোট ডোনার", true));
        ((LinearLayout.LayoutParams) tiles.getChildAt(0).getLayoutParams()).rightMargin = D.dp(8);
        tiles.addView(Ui.statTile(c, Bn.bn(dons.size()), "মোট দান", true));
        ((LinearLayout.LayoutParams) tiles.getChildAt(1).getLayoutParams()).rightMargin = D.dp(8);
        tiles.addView(Ui.statTile(c, Bn.bn(Data.stockTotal()), "স্টক ব্যাগ", false));
        root.addView(tiles);

        /* আমার অগ্রগতি — পরবর্তী ব্যাজ */
        root.addView(Ui.sectionHeader(c, "আপনার অগ্রগতি"));
        LinearLayout prog = Ui.cardPad(c);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pp.setMargins(D.dp(16), 0, D.dp(16), 0);
        prog.setLayoutParams(pp);

        String[] badge = Data.badge(me);
        String[] next = nextBadge(me.donationCount);
        int need = next[2] != null ? Integer.parseInt(next[2]) - me.donationCount : 0;
        prog.addView(Ui.txt(c, "বর্তমান: " + badge[1], 13.5f, D.onSurface, 700));
        if (next[1] != null) {
            TextView pt = Ui.txt(c, "পরবর্তী ব্যাজ (" + next[1] + ") পেতে আর " + Bn.bn(need) + " বার দান করুন", 12f, D.onSurfaceVar, 500);
            pt.setPadding(0, D.dp(3), 0, D.dp(9));
            prog.addView(pt);

            float pct = me.donationCount / (float) Integer.parseInt(next[2]);
            prog.addView(progressBar(c, Math.max(0.02f, Math.min(1f, pct)),
                    Bn.bn(me.donationCount) + " / " + Bn.bn(Integer.parseInt(next[2]))));
        } else {
            TextView pt = Ui.txt(c, "আপনি সর্বোচ্চ স্তরে — প্লাটিনাম ডোনার! 🏆", 12f, D.onSurfaceVar, 500);
            pt.setPadding(0, D.dp(3), 0, 0);
            prog.addView(pt);
        }
        root.addView(prog);

        /* মাসিক চার্ট */
        root.addView(Ui.sectionHeader(c, "মাসিক রক্তদান (শেষ ৬ মাস)"));
        LinearLayout chartCard = Ui.cardPad(c);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, D.dp(190));
        cp.setMargins(D.dp(16), 0, D.dp(16), 0);
        chartCard.setLayoutParams(cp);

        Views.BarChartView bar = new Views.BarChartView(c);
        bar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        int[] vals = Data.donationsLast6Months();
        String[] labels = new String[6];
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -5);
        for (int i = 0; i < 6; i++) {
            labels[i] = Bn.MONTHS[cal.get(Calendar.MONTH)].substring(0, Math.min(4, Bn.MONTHS[cal.get(Calendar.MONTH)].length()));
            cal.add(Calendar.MONTH, 1);
        }
        bar.setData(vals, labels);
        chartCard.addView(bar);
        root.addView(chartCard);

        int thisMonth = vals[5];
        int lastMonth = vals[4];
        TextView trend = Ui.txt(c, thisMonth > lastMonth
                ? "▲ এই মাসে গত মাসের চেয়ে " + Bn.bn(thisMonth - lastMonth) + " বার বেশি দান হয়েছে"
                : lastMonth > thisMonth
                ? "▼ এই মাসে গত মাসের চেয়ে " + Bn.bn(lastMonth - thisMonth) + " বার কম — উৎসাহ দিন!"
                : "গত মাসের সমান রক্তদান হয়েছে এই মাসেও",
                11.5f, thisMonth >= lastMonth ? D.success : D.onSurfaceVar, 600);
        trend.setPadding(D.dp(18), D.dp(8), D.dp(18), 0);
        root.addView(trend);

        /* গ্রুপ বিভাজন */
        root.addView(Ui.sectionHeader(c, "ডোনার গ্রুপ বিভাজন"));
        LinearLayout donutWrap = Ui.h(c);
        donutWrap.setPadding(D.dp(16), 0, D.dp(16), 0);

        Views.DonutView donut = new Views.DonutView(c);
        donut.setLayoutParams(new LinearLayout.LayoutParams(D.dp(150), D.dp(150)));
        donut.setData(Data.donorsByGroup());
        donutWrap.addView(donut);
        ((LinearLayout.LayoutParams) donut.getLayoutParams()).rightMargin = D.dp(14);

        LinearLayout legend = Ui.v(c);
        legend.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        int[] byGroup = Data.donorsByGroup();
        for (int i = 0; i < 8; i++) {
            if (byGroup[i] == 0) continue;
            LinearLayout lr = Ui.h(c);
            lr.setPadding(0, D.dp(3), 0, D.dp(3));
            View dot = new View(c);
            dot.setBackground(D.round(Views.DonutView.COLORS[i], 4));
            dot.setLayoutParams(Ui.lp(D.dp(9), D.dp(9)));
            lr.addView(dot);
            ((LinearLayout.LayoutParams) dot.getLayoutParams()).rightMargin = D.dp(8);
            lr.addView(Ui.txt(c, Data.BLOOD_GROUPS[i] + " — " + Bn.bn(byGroup[i]) + " জন", 12f, D.onSurface, 600));
            legend.addView(lr);
        }
        donutWrap.addView(legend);
        root.addView(donutWrap);

        /* মাইলফলক তালিকা */
        root.addView(Ui.sectionHeader(c, "সংঘের মাইলফলক"));
        LinearLayout miles = Ui.v(c);
        miles.setPadding(D.dp(16), 0, D.dp(16), 0);
        int totalDons = dons.size();
        miles.addView(mile(c, totalDons >= 1, "প্রথম রক্তদান সম্পন্ন", "সংঘের যাত্রা শুরু"));
        miles.addView(mile(c, totalDons >= 25, Bn.bn(25) + " বার দান", "কমিউনিটি গড়ে উঠছে"));
        miles.addView(mile(c, totalDons >= 50, Bn.bn(50) + " বার দান", "অর্ধশত মাইলফলক"));
        miles.addView(mile(c, totalDons >= 100, Bn.bn(100) + " বার দান", "শত পূর্ণ — কৃতিত্বের"));
        miles.addView(mile(c, users.size() >= 50, Bn.bn(50) + " জন ডোনার", "সংঘের বিশাল পরিবার"));
        root.addView(miles);

        View gap = new View(c);
        gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(24)));
        root.addView(gap);

        sc.addView(root);
        return sc;
    }

    static String[] nextBadge(int count) {
        if (count < 5) return new String[]{"friend", "রক্তবন্ধু", "5"};
        if (count < 10) return new String[]{"bronze", "ব্রোঞ্জ ডোনার", "10"};
        if (count < 20) return new String[]{"silver", "সিলভার ডোনার", "20"};
        if (count < 50) return new String[]{"gold", "গোল্ড ডোনার", "50"};
        return new String[]{null, null, null};
    }

    static LinearLayout progressBar(Context c, float pct, String label) {
        LinearLayout wrap = Ui.v(c);
        View fill = new View(c);
        fill.setBackground(D.gradAngle(D.redGrad, 8, 315));
        FrameWrap fw = new FrameWrap(c, fill, pct);
        fw.setBackground(D.round(D.surfaceCH, 8));
        fw.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(12)));
        wrap.addView(fw);

        TextView lt = Ui.txt(c, label, 10.5f, D.onSurfaceVar, 600);
        lt.setGravity(Gravity.END);
        lt.setPadding(0, D.dp(5), 0, 0);
        wrap.addView(lt);
        return wrap;
    }

    /** প্রগ্রেস ট্র্যাকের ভেতরে অনুপাত-অনুযায়ী পূরণ — লেআউটের পরে মাপ বসায়। */
    static final class FrameWrap extends android.widget.FrameLayout {
        final View fill; final float pct;
        FrameWrap(Context c, View fill, float pct) {
            super(c);
            this.fill = fill; this.pct = pct;
            setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(12)));
            addView(fill, new android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT));
        }
        @Override
        protected void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh);
            android.widget.FrameLayout.LayoutParams p =
                    (android.widget.FrameLayout.LayoutParams) fill.getLayoutParams();
            p.width = Math.max(D.dp(14), Math.round(w * Math.max(0f, Math.min(1f, pct))));
            fill.setLayoutParams(p);
        }
    }

    static LinearLayout mile(Context c, boolean done, String title, String sub) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(D.dp(13), D.dp(10), D.dp(13), D.dp(10));
        row.setBackground(D.roundStroke(done ? D.successC : D.surfaceCLo, 16, D.outlineVar, 1));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, D.dp(8));
        row.setLayoutParams(p);

        TextView mark = Ui.txt(c, done ? "✓" : "○", 15, done ? D.onSuccessC : D.outline, 700);
        mark.setGravity(Gravity.CENTER);
        mark.setLayoutParams(Ui.lp(D.dp(24), D.dp(24)));
        row.addView(mark);
        ((LinearLayout.LayoutParams) mark.getLayoutParams()).rightMargin = D.dp(10);

        LinearLayout t = Ui.v(c);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        t.addView(Ui.txt(c, title, 13f, done ? D.onSurface : D.onSurfaceVar, 600));
        TextView s = Ui.txt(c, sub, 11f, D.onSurfaceVar, 400);
        s.setPadding(0, D.dp(1), 0, 0);
        t.addView(s);
        row.addView(t);

        if (done) row.addView(Ui.pill(c, "অর্জিত", 0));
        return row;
    }
}
