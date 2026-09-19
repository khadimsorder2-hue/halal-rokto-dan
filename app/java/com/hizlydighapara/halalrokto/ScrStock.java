package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** রক্তের স্টক — ৮ গ্রুপ কার্ড, সামঞ্জস্য তালিকা, টিপস। */
public final class ScrStock {

    private ScrStock() {}

    public static View build(final Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);

        root.addView(Ui.heroBar(c, "রক্তের স্টক", "রিয়েল-টাইম ব্লাড ব্যাংক অবস্থা", false, null, null));

        /* total banner */
        LinearLayout total = Ui.h(c);
        total.setGravity(Gravity.CENTER_VERTICAL);
        total.setPadding(D.dp(20), D.dp(16), D.dp(20), D.dp(16));
        total.setBackground(D.gradAngle(D.redGrad, 20, 315));
        total.setElevation(D.dp(4));
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tp.setMargins(D.dp(16), D.dp(16), D.dp(16), 0);
        total.setLayoutParams(tp);

        LinearLayout tw = Ui.v(c);
        tw.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView tn = Ui.txt(c, Bn.bn(Data.stockTotal()), 30, 0xFFFFFFFF, 700);
        tw.addView(tn);
        TextView tl = Ui.txt(c, "ব্যাগ রক্ত মোট স্টকে আছে", 12.5f, D.withAlpha(0xFFFFFFFF, 220), 600);
        tl.setPadding(0, D.dp(2), 0, 0);
        tw.addView(tl);
        TextView tl2 = Ui.txt(c, "প্রতি গ্রুপে " + Bn.bn(Data.TARGET_STOCK) + " ব্যাগ লক্ষ্যমাত্রা", 11f, D.withAlpha(0xFFFFFFFF, 190), 500);
        tl2.setPadding(0, D.dp(2), 0, 0);
        tw.addView(tl2);
        total.addView(tw);
        ImageView bank = Ui.icon(c, R.drawable.ic_bloodbank, 52, D.withAlpha(0xFFFFFFFF, 235));
        total.addView(bank);
        root.addView(total);

        /* grid: 2 columns × 4 rows */
        LinearLayout gridWrap = Ui.v(c);
        gridWrap.setPadding(D.dp(16), D.dp(12), D.dp(16), 0);
        root.addView(gridWrap);
        for (int r = 0; r < 4; r++) {
            LinearLayout row = Ui.h(c);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            for (int i = r * 2; i < r * 2 + 2; i++) {
                row.addView(stockCard(c, Data.BLOOD_GROUPS[i], Data.s.stock[i]));
            }
            gridWrap.addView(row);
        }

        /* compatibility */
        root.addView(Ui.sectionHeader(c, "রক্তের সামঞ্জস্য তালিকা"));
        LinearLayout compat = Ui.v(c);
        compat.setPadding(D.dp(16), 0, D.dp(16), 0);
        for (final String g : Data.BLOOD_GROUPS) {
            LinearLayout row = Ui.h(c);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(D.dp(13), D.dp(10), D.dp(13), D.dp(10));
            row.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 14, D.outlineVar, 1), D.rippleColor));
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rp.bottomMargin = D.dp(7);
            row.setLayoutParams(rp);
            row.addView(Ui.bgroup(c, g, false, 12));
            ((LinearLayout.LayoutParams) row.getChildAt(0).getLayoutParams()).rightMargin = D.dp(12);
            ImageView arr = Ui.icon(c, R.drawable.ic_chevron, 14, D.outline);
            row.addView(arr);
            ((LinearLayout.LayoutParams) arr.getLayoutParams()).rightMargin = D.dp(12);
            TextView t = Ui.txt(c, Data.compatOf(g) + " রক্ত নিতে পারে", 12f, D.onSurfaceVar, 500);
            t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            row.addView(t);
            compat.addView(row);
        }
        root.addView(compat);

        /* tips card */
        LinearLayout tipWrap = Ui.v(c);
        tipWrap.setPadding(D.dp(16), D.dp(10), D.dp(16), D.dp(20));
        LinearLayout tip = Ui.cardPad(c);
        tip.setBackground(D.round(D.tertiaryC, 20));
        LinearLayout tr = Ui.h(c);
        tr.setGravity(Gravity.TOP);
        ImageView ii = Ui.icon(c, R.drawable.ic_info, 18, D.onTertiaryC);
        tr.addView(ii);
        ((LinearLayout.LayoutParams) ii.getLayoutParams()).rightMargin = D.dp(10);
        ((LinearLayout.LayoutParams) ii.getLayoutParams()).topMargin = D.dp(2);
        TextView tt = Ui.txt(c, "টিপস: রক্তদানের পর প্রতিটি ব্যাগ স্বয়ংক্রিয়ভাবে এই স্টকে যোগ হয়। জরুরি আবেদন পূরণ হলে সংশ্লিষ্ট গ্রুপের স্টক থেকে বাদ যায়। O− গ্রুপের ডোনার \"সর্বজনীন দাতা\" — সবাই তার রক্ত নিতে পারে।", 12f, D.onTertiaryC, 400);
        tt.setLineSpacing(0, 1.55f);
        tt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tr.addView(tt);
        tip.addView(tr);
        tipWrap.addView(tip);
        root.addView(tipWrap);

        View gap = new View(c);
        gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(16)));
        root.addView(gap);

        sc.addView(root);
        return sc;
    }

    static LinearLayout stockCard(Context c, String g, int n) {
        LinearLayout card = Ui.v(c);
        card.setPadding(D.dp(14), D.dp(13), D.dp(14), D.dp(13));
        card.setBackground(D.roundStroke(D.surfaceCLo, 20, D.outlineVar, 1));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        p.rightMargin = D.dp(8);
        p.bottomMargin = D.dp(8);
        card.setLayoutParams(p);

        LinearLayout top = Ui.h(c);
        top.addView(Ui.bgroup(c, g, n <= 1, 12.5f));
        View spacer = new View(c);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        top.addView(spacer);
        int tone = n <= 1 ? 2 : n <= 3 ? 1 : n >= 6 ? 0 : 3;
        String lbl = n <= 1 ? "মারাত্মক ঘাটতি" : n <= 3 ? "কম আছে" : n >= 6 ? "পর্যাপ্ত" : "সচল";
        top.addView(Ui.pill(c, lbl, tone));
        card.addView(top);

        LinearLayout numRow = Ui.h(c);
        numRow.setGravity(Gravity.BOTTOM);
        TextView num = Ui.txt(c, Bn.bn(n), 24, D.onSurface, 700);
        numRow.addView(num);
        TextView unit = Ui.txt(c, " ব্যাগ", 11f, D.onSurfaceVar, 500);
        unit.setPadding(0, 0, 0, D.dp(3));
        numRow.addView(unit);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        np.topMargin = D.dp(9);
        numRow.setLayoutParams(np);
        card.addView(numRow);

        // progress
        int pct = Math.min(100, Math.round(n * 100f / Data.TARGET_STOCK));
        LinearLayout progTrack = new LinearLayout(c);
        progTrack.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, D.dp(6)));
        progTrack.setBackground(D.round(D.surfaceCH, 50));
        View fill = new View(c);
        int color = tone == 2 ? D.error : tone == 1 ? 0xFFB8860B : tone == 0 ? D.success : D.primary;
        fill.setBackground(D.round(color, 50));
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, Math.max(0.02f, pct / 100f));
        fill.setLayoutParams(fp);
        progTrack.addView(fill);
        card.addView(progTrack);

        TextView lbl2 = Ui.txt(c, Data.rarityOf(g) + (n <= 1 ? " — জরুরি ডোনার প্রয়োজন!" : ""), 10.5f,
                n <= 1 ? D.error : D.onSurfaceVar, 600);
        lbl2.setPadding(0, D.dp(7), 0, 0);
        card.addView(lbl2);
        return card;
    }
}
