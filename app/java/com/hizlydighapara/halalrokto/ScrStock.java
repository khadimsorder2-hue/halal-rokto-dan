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

/** রক্তের স্টক — ৮ গ্রুপ কার্ড, কার্ডে চাপ দিলে কারা কারা এখন উপলব্ধ দেখা যায়। */
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

        /* grid: 2 columns × 4 rows — কার্ডে ক্লিকে উপলব্ধ ডোনার */
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

        TextView hint = Ui.txt(c, "যেকোনো গ্রুপের কার্ডে চাপ দিন — কারা কারা এখন রক্ত দিতে পারবেন, তাদের ফোন ও চ্যাটের তথ্যসহ দেখা যাবে।", 11f, D.onSurfaceVar, 400);
        hint.setLineSpacing(0, 1.4f);
        hint.setPadding(D.dp(20), D.dp(2), D.dp(20), 0);
        root.addView(hint);

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
            final String group = g;
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { openDetail(c, group); }
            });
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
        final Context ctx = c;
        final String group = g;

        LinearLayout card = Ui.v(c);
        card.setPadding(D.dp(14), D.dp(13), D.dp(14), D.dp(13));
        card.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 20, D.outlineVar, 1), D.rippleColor));
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

        int avail = Data.availableSame(g).size();
        TextView lbl2 = Ui.txt(c, avail > 0
                        ? Bn.bn(avail) + " জন এখন উপলব্ধ — বিস্তারিত দেখুন"
                        : Data.rarityOf(g) + (n <= 1 ? " — জরুরি ডোনার প্রয়োজন!" : ""),
                10.5f, avail > 0 ? D.success : (n <= 1 ? D.error : D.onSurfaceVar), 600);
        lbl2.setPadding(0, D.dp(7), 0, 0);
        card.addView(lbl2);

        card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { openDetail(ctx, group); }
        });
        return card;
    }

    /* ══ স্টক বিস্তারিত — কারা কারা এখন available ═══════════ */

    static void openDetail(final Context c, final String g) {
        Ui.host.haptic(8);
        Ui.sheet(c, g + " গ্রুপ — বিস্তারিত", R.drawable.ic_drop, true, new Ui.SheetCallback() {
            public void onSheet(LinearLayout body, Runnable close) {

                final int n = Data.s.stock[Data.stockIdx(g)];
                int tone = n <= 1 ? 2 : n <= 3 ? 1 : n >= 6 ? 0 : 3;
                List<Data.User> same = Data.availableSame(g);
                List<Data.User> compat = Data.availableCompat(g);
                List<Data.User> cooling = Data.coolingSame(g);

                /* ── হেডার ব্যানার ── */
                LinearLayout head = Ui.h(c);
                head.setGravity(Gravity.CENTER_VERTICAL);
                head.setPadding(D.dp(16), D.dp(14), D.dp(16), D.dp(14));
                head.setBackground(tone == 2
                        ? D.gradAngle(D.redGrad, 18, 315)
                        : D.round(D.primaryC, 18));
                LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                hp.bottomMargin = D.dp(12);
                head.setLayoutParams(hp);

                TextView badge = Ui.txt(c, g, 20, 0xFFFFFFFF, 700);
                badge.setGravity(Gravity.CENTER);
                badge.setPadding(D.dp(14), D.dp(8), D.dp(14), D.dp(9));
                badge.setBackground(D.round(D.withAlpha(0xFFFFFFFF, 40), 16));
                badge.setElevation(D.dp(2));
                head.addView(badge);
                ((LinearLayout.LayoutParams) badge.getLayoutParams()).rightMargin = D.dp(13);

                LinearLayout hw = Ui.v(c);
                hw.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                hw.addView(Ui.txt(c, Bn.bn(n) + " ব্যাগ স্টকে আছে", 16, tone == 2 ? 0xFFFFFFFF : D.onPrimaryC, 700));
                TextView hs = Ui.txt(c, Data.rarityOf(g) + " গ্রুপ • " + (n <= 1 ? "মারাত্মক ঘাটতি" : n <= 3 ? "কম আছে" : n >= 6 ? "পর্যাপ্ত" : "সচল"),
                        11.5f, tone == 2 ? D.withAlpha(0xFFFFFFFF, 215) : D.withAlpha(D.onPrimaryC, 220), 500);
                hs.setPadding(0, D.dp(2), 0, 0);
                hw.addView(hs);
                head.addView(hw);
                body.addView(head);

                /* ── তথ্য কার্ড ── */
                LinearLayout info = Ui.cardPad(c);
                LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ip.bottomMargin = D.dp(10);
                info.setLayoutParams(ip);
                info.addView(infoRow(c, R.drawable.ic_people, "যারা এই রক্ত নিতে পারবে: " + Data.compatOf(g)));
                info.addView(infoRow(c, R.drawable.ic_dropfill, "৯০ দিন কুলডাউন শেষ হলেই ডোনার উপলব্ধ হয়"));
                body.addView(info);

                /* ── এখন উপলব্ধ ডোনার ── */
                LinearLayout sh = Ui.h(c);
                sh.setGravity(Gravity.CENTER_VERTICAL);
                sh.setPadding(D.dp(2), 0, D.dp(2), D.dp(6));
                TextView st = Ui.txt(c, "এখনই রক্ত দিতে পারবেন (" + Bn.bn(same.size()) + " জন)", 13.5f, D.onSurface, 700);
                st.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                sh.addView(st);
                sh.addView(Ui.pill(c, "এখন উপলব্ধ", 0));
                body.addView(sh);

                if (same.isEmpty()) {
                    LinearLayout e = Ui.cardPad(c);
                    e.addView(Ui.txt(c, "এই মুহূর্তে এই গ্রুপের কোনো ডোনার উপলব্ধ নেই — সবাই ৯০ দিনের কুলডাউনে আছেন বা তালিকায় নেই। জরুরি প্রয়োজন হলে নিচের বোতামে আবেদন করুন।", 12f, D.onSurfaceVar, 400));
                    ((LinearLayout.LayoutParams) e.getLayoutParams()).bottomMargin = D.dp(10);
                    body.addView(e);
                } else {
                    for (final Data.User u : same)
                        body.addView(donorRow(c, u, g, false));
                }

                /* ── সামঞ্জস্যপূর্ণ গ্রুপের দাতা ── */
                if (!compat.isEmpty()) {
                    LinearLayout ch2 = Ui.h(c);
                    ch2.setGravity(Gravity.CENTER_VERTICAL);
                    ch2.setPadding(D.dp(2), D.dp(10), D.dp(2), D.dp(6));
                    TextView ct = Ui.txt(c, "সামঞ্জস্যপূর্ণ গ্রুপের দাতাও দিতে পারবে (" + Bn.bn(compat.size()) + " জন)", 12.5f, D.onSurfaceVar, 700);
                    ct.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    ch2.addView(ct);
                    body.addView(ch2);
                    for (final Data.User u : compat)
                        body.addView(donorRow(c, u, g, true));
                }

                /* ── কুলডাউনে আছেন ── */
                if (!cooling.isEmpty()) {
                    LinearLayout ch3 = Ui.h(c);
                    ch3.setGravity(Gravity.CENTER_VERTICAL);
                    ch3.setPadding(D.dp(2), D.dp(12), D.dp(2), D.dp(6));
                    TextView ct3 = Ui.txt(c, "কুলডাউনে আছেন (" + Bn.bn(cooling.size()) + " জন)", 12.5f, D.onSurfaceVar, 700);
                    ct3.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    ch3.addView(ct3);
                    body.addView(ch3);
                    for (Data.User u : cooling) {
                        Data.Cooldown cd = Data.cooldown(u);
                        LinearLayout row = Ui.h(c);
                        row.setGravity(Gravity.CENTER_VERTICAL);
                        row.setPadding(D.dp(12), D.dp(9), D.dp(12), D.dp(9));
                        row.setBackground(D.roundStroke(D.surfaceC, 14, D.outlineVar, 1));
                        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        rp.bottomMargin = D.dp(6);
                        row.setLayoutParams(rp);
                        TextView av = Ui.avatar(c, Bn.initials(u.name), D.avatarColor(u.id), 30, 11);
                        row.addView(av);
                        ((LinearLayout.LayoutParams) av.getLayoutParams()).rightMargin = D.dp(10);
                        TextView nm = Ui.txt(c, u.name, 12.5f, D.onSurface, 600);
                        nm.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                        row.addView(nm);
                        row.addView(Ui.pill(c, Bn.bn(cd.days) + " দিন পর", 3));
                        body.addView(row);
                    }
                }

                /* ── অ্যাকশন ── */
                LinearLayout btnWrap = Ui.v(c);
                btnWrap.setPadding(D.dp(2), D.dp(14), D.dp(2), D.dp(6));
                LinearLayout btnRow = Ui.h(c);

                TextView emg = Ui.btn(c, g + " এর জন্য জরুরি আবেদন", Ui.BTN_GRAD, new Runnable() {
                    public void run() {
                        close.run();
                        ScrEmergency.newEmergencySheet(c, new Runnable[]{new Runnable() {
                            public void run() { Ui.host.refresh(); }
                        }}, g);
                    }
                });
                emg.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(48), 1f));
                ((LinearLayout.LayoutParams) emg.getLayoutParams()).rightMargin = D.dp(8);
                btnRow.addView(emg);

                TextView all = Ui.btn(c, "সব ডোনার", Ui.BTN_OUTLINE, new Runnable() {
                    public void run() {
                        close.run();
                        Ui.host.go("donors", false);
                    }
                });
                all.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(48), 1.2f));
                btnRow.addView(all);

                btnWrap.addView(btnRow);
                body.addView(btnWrap);
            }
        });
    }

    static LinearLayout infoRow(Context c, int iconRes, String text) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.TOP);
        ImageView iv = Ui.icon(c, iconRes, 15, D.onSurfaceVar);
        row.addView(iv);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).rightMargin = D.dp(8);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).topMargin = D.dp(2);
        TextView t = Ui.txt(c, text, 11.5f, D.onSurfaceVar, 500);
        t.setLineSpacing(0, 1.35f);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(t);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = D.dp(4);
        row.setLayoutParams(p);
        return row;
    }

    /** উপলব্ধ ডোনারের সারি — ফোন + চ্যাট বোতামসহ। */
    static LinearLayout donorRow(final Context c, final Data.User u, String forGroup, boolean compatTag) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(D.dp(12), D.dp(10), D.dp(12), D.dp(10));
        row.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 16, D.outlineVar, 1), D.rippleColor));
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rp.bottomMargin = D.dp(7);
        row.setLayoutParams(rp);

        row.addView(Ui.avatar(c, Bn.initials(u.name), D.avatarColor(u.id), 42, 15));
        ((LinearLayout.LayoutParams) row.getChildAt(0).getLayoutParams()).rightMargin = D.dp(11);

        LinearLayout m = Ui.v(c);
        m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout nameRow = Ui.h(c);
        nameRow.setGravity(Gravity.CENTER_VERTICAL);
        nameRow.addView(Ui.txt(c, u.name, 13.5f, D.onSurface, 600));
        if (u.verified) {
            ImageView vi = Ui.icon(c, R.drawable.ic_verified, 13, D.success);
            nameRow.addView(vi);
            ((LinearLayout.LayoutParams) vi.getLayoutParams()).leftMargin = D.dp(5);
        }
        m.addView(nameRow);

        String line = Bn.bn(u.donationCount) + " বার দান • ট্রাস্ট " + Bn.bn(u.trustScore) + "%";
        Data.Cooldown cd = Data.cooldown(u);
        if (cd.first) line += " • নতুন ডোনার";
        else line += " • শেষ দান " + Bn.bn(cd.lastDiff) + " দিন আগে";
        TextView sub = Ui.txt(c, line, 10.5f, D.onSurfaceVar, 400);
        sub.setPadding(0, D.dp(2), 0, 0);
        m.addView(sub);

        if (compatTag) {
            LinearLayout tagRow = Ui.h(c);
            tagRow.setPadding(0, D.dp(4), 0, 0);
            tagRow.addView(Ui.bgroup(c, u.bloodType, false, 10));
            ((LinearLayout.LayoutParams) tagRow.getChildAt(0).getLayoutParams()).rightMargin = D.dp(6);
            TextView tg = Ui.txt(c, "আপনার " + forGroup + " রোগীর জন্য দিতে পারবেন", 10f, D.onSurfaceVar, 500);
            tg.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            tagRow.addView(tg);
            m.addView(tagRow);
        }
        row.addView(m);

        ImageView chat = Ui.icon(c, R.drawable.ic_chat, 17, D.onPrimaryC);
        chat.setBackground(D.ripple(D.round(D.primaryC, 50), D.rippleColor));
        chat.setPadding(D.dp(9), D.dp(9), D.dp(9), D.dp(9));
        chat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Ui.host.haptic(8);
                Ui.host.openChat(Chat.dmId(u));
            }
        });
        row.addView(chat);
        ((LinearLayout.LayoutParams) chat.getLayoutParams()).rightMargin = D.dp(7);

        ImageView call = Ui.icon(c, R.drawable.ic_phone, 17, 0xFFFFFFFF);
        call.setBackground(D.ripple(D.gradAngle(new int[]{0xFF2E9E4F, 0xFF1E6B33}, 50, 315), D.rippleColorLight));
        call.setElevation(D.dp(2));
        call.setPadding(D.dp(9), D.dp(9), D.dp(9), D.dp(9));
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Ui.host.haptic(10);
                Ui.host.dial(u.phone);
            }
        });
        row.addView(call);

        return row;
    }
}
