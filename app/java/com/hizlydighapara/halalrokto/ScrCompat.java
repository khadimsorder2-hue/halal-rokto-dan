package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** রক্তের সামঞ্জস্য — কে কাকে দিতে পারে, কে কার কাছ থেকে নিতে পারে। */
public final class ScrCompat {

    private ScrCompat() {}

    static String sel = null; // শেষ নির্বাচিত গ্রুপ (স্ক্রিন রিবিল্ডে ধরে রাখতে)

    public static View build(final Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);
        root.addView(Ui.heroBar(c, "রক্তের সামঞ্জস্য", "কে কাকে রক্ত দিতে পারে — সহজে বুঝুন", false, null, null));

        if (sel == null) sel = "O+";

        /* গ্রুপ গ্রিড */
        LinearLayout grid = new LinearLayout(c);
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setPadding(D.dp(16), D.dp(14), D.dp(16), 0);
        for (int r = 0; r < 2; r++) {
            LinearLayout row = Ui.h(c);
            row.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            for (int i = r * 4; i < r * 4 + 4; i++) {
                final String g = Data.BLOOD_GROUPS[i];
                final boolean on = g.equals(sel);
                TextView t = Ui.txt(c, g, 15, on ? 0xFFFFFFFF : D.onSurface, 700);
                t.setGravity(Gravity.CENTER);
                t.setMinHeight(D.dp(46));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                p.setMargins(D.dp(3), D.dp(3), D.dp(3), D.dp(3));
                t.setLayoutParams(p);
                t.setBackground(on
                        ? D.ripple(D.gradAngle(D.redGrad, 16, 315), D.rippleColorLight)
                        : D.ripple(D.roundStroke(D.surfaceCLo, 16, D.outlineVar, 1), D.rippleColor));
                t.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Ui.host.haptic(7);
                        sel = g;
                        Ui.host.refresh();
                    }
                });
                row.addView(t);
            }
            grid.addView(row);
        }
        root.addView(grid);

        /* নির্বাচিত গ্রুপের প্যানেল */
        String g = sel;
        LinearLayout panel = Ui.card(c);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pp.setMargins(D.dp(16), D.dp(14), D.dp(16), 0);
        panel.setLayoutParams(pp);
        panel.setPadding(D.dp(16), D.dp(15), D.dp(16), D.dp(15));

        /* হেডার */
        LinearLayout head = Ui.h(c);
        TextView bigG = Ui.txt(c, g, 26, 0xFFFFFFFF, 700);
        bigG.setGravity(Gravity.CENTER);
        bigG.setBackground(D.gradAngle(D.redGrad, 18, 315));
        bigG.setPadding(D.dp(16), D.dp(8), D.dp(16), D.dp(10));
        head.addView(bigG);
        ((LinearLayout.LayoutParams) bigG.getLayoutParams()).rightMargin = D.dp(13);

        LinearLayout ht = Ui.v(c);
        ht.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        ht.addView(Ui.txt(c, g + " গ্রুপ", 17, D.onSurface, 700));
        TextView hs = Ui.txt(c, Data.rarityOf(g) + " • জনসংখ্যার প্রায় " + Bn.bn(Data.populationPct(g)) + "%", 11.5f, D.onSurfaceVar, 500);
        hs.setPadding(0, D.dp(3), 0, 0);
        ht.addView(hs);
        head.addView(ht);
        panel.addView(head);

        /* দিতে পারবে */
        String recv = Data.reverseCompatOf(g);   // g দাতা হিসেবে যাদের দিতে পারে
        String give = Data.compatOf(g);          // g রোগী হিসেবে যাদের থেকে নিতে পারে
        panel.addView(section(c, "🩸 " + g + " দাতা এই রোগীদের দিতে পারবে", recv.split(" "), D.primaryC, D.onPrimaryC));
        panel.addView(section(c, "📥 " + g + " রোগী এই দাতাদের থেকে নিতে পারবে", give.split(" "), D.successC, D.onSuccessC));

        /* স্টক + উপলব্ধ ডোনার */
        LinearLayout infoRow = Ui.h(c);
        infoRow.setPadding(0, D.dp(4), 0, 0);

        LinearLayout stockTile = Ui.v(c);
        stockTile.setGravity(Gravity.CENTER);
        stockTile.setPadding(D.dp(8), D.dp(12), D.dp(8), D.dp(12));
        stockTile.setBackground(D.round(D.surfaceC, 16));
        stockTile.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        stockTile.addView(Ui.txt(c, Bn.bn(Data.stockFor(g)), 20, D.primary, 700));
        TextView stl = Ui.txt(c, "বর্তমান স্টক (ব্যাগ)", 10f, D.onSurfaceVar, 600);
        stl.setGravity(Gravity.CENTER);
        stl.setPadding(0, D.dp(2), 0, 0);
        stockTile.addView(stl);
        infoRow.addView(stockTile);
        ((LinearLayout.LayoutParams) stockTile.getLayoutParams()).rightMargin = D.dp(8);

        int avail = Data.availableSame(g).size() + Data.availableCompat(g).size();
        LinearLayout availTile = Ui.v(c);
        availTile.setGravity(Gravity.CENTER);
        availTile.setPadding(D.dp(8), D.dp(12), D.dp(8), D.dp(12));
        availTile.setBackground(D.round(D.surfaceC, 16));
        availTile.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        availTile.addView(Ui.txt(c, Bn.bn(avail), 20, D.success, 700));
        TextView atl = Ui.txt(c, "এখনই উপলব্ধ ডোনার", 10f, D.onSurfaceVar, 600);
        atl.setGravity(Gravity.CENTER);
        atl.setPadding(0, D.dp(2), 0, 0);
        availTile.addView(atl);
        infoRow.addView(availTile);
        panel.addView(infoRow);

        LinearLayout seeBtn = Ui.h(c);
        seeBtn.setPadding(0, D.dp(12), 0, 0);
        TextView b = Ui.btn(c, "এই গ্রুপের উপলব্ধ ডোনার দেখুন", Ui.BTN_TONAL, new Runnable() {
            public void run() {
                Ui.host.go("stock", false);
                Ui.toast("স্টক স্ক্রিনে " + sel + " কার্ডে চাপ দিন", false);
            }
        });
        b.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(46)));
        seeBtn.addView(b);
        panel.addView(seeBtn);
        root.addView(panel);

        /* স্মার্ট নোট */
        root.addView(Ui.sectionHeader(c, "জরুরি মনে রাখুন"));
        LinearLayout note = Ui.cardPad(c);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        np.setMargins(D.dp(16), 0, D.dp(16), 0);
        note.setLayoutParams(np);
        LinearLayout nRow = Ui.h(c);
        ImageView ic = Ui.icon(c, R.drawable.ic_info, 19, D.onTertiaryC);
        ic.setBackground(D.round(D.tertiaryC, 13));
        ic.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
        nRow.addView(ic);
        ((LinearLayout.LayoutParams) ic.getLayoutParams()).rightMargin = D.dp(12);
        LinearLayout nt = Ui.v(c);
        nt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        nt.addView(Ui.txt(c, "O- হলো সর্বজনীন দাতা", 13, D.onSurface, 700));
        TextView nb = Ui.txt(c, "O- রক্ত সব গ্রুপের রোগীকে দেওয়া যায় — তাই সবচেয়ে দামি। আর AB+ হলো সর্বজনীন গ্রহীতা: সব গ্রুপের রক্ত নিতে পারে। জরুরি মুহূর্তে নিজ গ্রুপ না পেলে সামঞ্জস্যপূর্ণ গ্রুপ দেখে দাতা ডাকা যায়।",
                11.5f, D.onSurfaceVar, 400);
        nb.setLineSpacing(0, 1.38f);
        nb.setPadding(0, D.dp(3), 0, 0);
        nt.addView(nb);
        nRow.addView(nt);
        note.addView(nRow);
        root.addView(note);

        View gap = new View(c);
        gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(24)));
        root.addView(gap);

        sc.addView(root);
        return sc;
    }

    static LinearLayout section(Context c, String title, String[] groups, int bg, int fg) {
        LinearLayout sec = Ui.v(c);
        sec.setPadding(0, D.dp(14), 0, 0);
        sec.addView(Ui.txt(c, title, 12.5f, D.onSurface, 700));
        LinearLayout chips = Ui.h(c);
        chips.setPadding(0, D.dp(8), 0, 0);
        for (int i = 0; i < groups.length; i++) {
            TextView chip = Ui.txt(c, groups[i], 13.5f, fg, 700);
            chip.setGravity(Gravity.CENTER);
            chip.setMinWidth(D.dp(48));
            chip.setPadding(D.dp(10), D.dp(8), D.dp(10), D.dp(9));
            chip.setBackground(D.round(bg, 12));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.rightMargin = i < groups.length - 1 ? D.dp(7) : 0;
            chip.setLayoutParams(p);
            chips.addView(chip);
        }
        sec.addView(chips);
        return sec;
    }
}
