package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** রক্তদানের ইতিহাস — টাইমলাইন + রেকর্ড যোগ। */
public final class ScrHistory {

    private ScrHistory() {}

    public static View build(final Context c) {
        final ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        final LinearLayout root = Ui.v(c);

        final Runnable[] render = new Runnable[1];
        render[0] = new Runnable() {
            public void run() {
                root.removeAllViews();
                root.addView(Ui.heroBar(c, "রক্তদানের ইতিহাস", "আপনার সকল দানের রেকর্ড", true, null, new Runnable() {
                    public void run() { Ui.host.back(); }
                }));

                Data.User me = Data.me();
                List<Data.Donation> mine = new ArrayList<>();
                for (Data.Donation d : Data.visibleDonations())
                    if (d.userId.equals(me.id)) mine.add(d);

                if (!mine.isEmpty()) {
                    long totalMl = 0;
                    for (Data.Donation d : mine) totalMl += d.amountMl;

                    LinearLayout ban = Ui.h(c);
                    ban.setGravity(Gravity.CENTER_VERTICAL);
                    ban.setPadding(D.dp(20), D.dp(16), D.dp(20), D.dp(16));
                    ban.setBackground(D.gradAngle(D.redGrad, 20, 315));
                    ban.setElevation(D.dp(4));
                    LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    bp.setMargins(D.dp(16), D.dp(16), D.dp(16), 0);
                    ban.setLayoutParams(bp);
                    LinearLayout tw = Ui.v(c);
                    tw.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    tw.addView(Ui.txt(c, Bn.bn(mine.size()), 30, 0xFFFFFFFF, 700));
                    TextView tl = Ui.txt(c, "বার রক্তদান করেছেন", 12.5f, D.withAlpha(0xFFFFFFFF, 220), 600);
                    tl.setPadding(0, D.dp(2), 0, 0);
                    tw.addView(tl);
                    TextView tl2 = Ui.txt(c, "মোট প্রায় " + Bn.bn(Math.round(totalMl / 1000f)) + " লিটার রক্ত — " + Bn.bn(mine.size() * 3) + "+ প্রাণ স্পর্শ", 11f, D.withAlpha(0xFFFFFFFF, 190), 500);
                    tl2.setPadding(0, D.dp(2), 0, 0);
                    tw.addView(tl2);
                    ban.addView(tw);
                    ban.addView(Ui.icon(c, R.drawable.ic_dropfill, 50, D.withAlpha(0xFFFFFFFF, 235)));
                    root.addView(ban);

                    // timeline
                    LinearLayout tlWrap = Ui.v(c);
                    tlWrap.setPadding(D.dp(22), D.dp(16), D.dp(16), 0);
                    for (Data.Donation d : mine) tlWrap.addView(timelineItem(c, d));
                    root.addView(tlWrap);
                } else {
                    root.addView(Ui.emptyState(c, R.drawable.ic_history,
                            "আপনি এখনো রক্ত দেননি",
                            "রক্ত দিতে পারেন ✓ — প্রথম দানের রেকর্ড যোগ করুন এবং রক্তবন্ধু ব্যাজ অর্জন করুন।"));
                }

                FrameLayout fabWrap = new FrameLayout(c);
                fabWrap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(88)));
                TextView fab = Ui.fab(c, "+  রক্তদান যোগ করুন", new Runnable() {
                    public void run() { addDonationSheet(c, render); }
                });
                fab.setLayoutParams(Ui.flp(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM | Gravity.END, 16));
                fabWrap.addView(fab);
                root.addView(fabWrap);
            }
        };
        render[0].run();
        sc.addView(root);
        return sc;
    }

    static LinearLayout timelineItem(Context c, Data.Donation d) {
        LinearLayout item = Ui.h(c);
        item.setPadding(0, 0, 0, 0);

        // left rail: dot + line
        LinearLayout rail = Ui.v(c);
        rail.setGravity(Gravity.CENTER_HORIZONTAL);
        ImageView dot = Ui.icon(c, R.drawable.ic_dropfill, 16, D.primary);
        dot.setBackground(D.round(D.primaryC, 50));
        dot.setPadding(D.dp(4), D.dp(4), D.dp(4), D.dp(4));
        rail.addView(dot);
        View line = new View(c);
        line.setLayoutParams(Ui.lp(Math.max(1, D.dp(1.6f)), ViewGroup.LayoutParams.MATCH_PARENT));
        line.setBackgroundColor(D.outlineVar);
        rail.addView(line);
        LinearLayout.LayoutParams railP = new LinearLayout.LayoutParams(D.dp(26), ViewGroup.LayoutParams.MATCH_PARENT);
        rail.setLayoutParams(railP);
        item.addView(rail);

        // content card
        LinearLayout card = Ui.v(c);
        card.setPadding(D.dp(14), D.dp(12), D.dp(14), D.dp(12));
        card.setBackground(D.roundStroke(D.surfaceCLo, 16, D.outlineVar, 1));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        cp.bottomMargin = D.dp(11);
        cp.leftMargin = D.dp(6);
        card.setLayoutParams(cp);

        card.addView(Ui.txt(c, d.recipientName, 14, D.onSurface, 700));

        LinearLayout metas = Ui.v(c);
        metas.setPadding(0, D.dp(5), 0, 0);
        metas.addView(meta(c, R.drawable.ic_calendar, Bn.fmtDate(d.date)));
        metas.addView(meta(c, R.drawable.ic_hosp, d.hospital));
        LinearLayout amtRow = Ui.h(c);
        ImageView di = Ui.icon(c, R.drawable.ic_drop, 12, D.primary);
        amtRow.addView(di);
        ((LinearLayout.LayoutParams) di.getLayoutParams()).rightMargin = D.dp(6);
        ((LinearLayout.LayoutParams) di.getLayoutParams()).topMargin = D.dp(2);
        TextView amt = Ui.txt(c, Bn.bn(d.amountMl) + " ml", 11.5f, D.primary, 700);
        amtRow.addView(amt);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mp.topMargin = D.dp(3);
        amtRow.setLayoutParams(mp);
        metas.addView(amtRow);
        if (!d.location.isEmpty()) metas.addView(meta(c, R.drawable.ic_location, d.location));
        card.addView(metas);

        if (!d.notes.isEmpty()) {
            TextView n = Ui.txt(c, d.notes, 11.5f, D.onSurfaceVar, 400);
            n.setLineSpacing(0, 1.4f);
            n.setPadding(0, D.dp(7), 0, 0);
            card.addView(n);
        }
        item.addView(card);
        return item;
    }

    static LinearLayout meta(Context c, int iconRes, String text) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.TOP);
        ImageView iv = Ui.icon(c, iconRes, 12, D.onSurfaceVar);
        row.addView(iv);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).rightMargin = D.dp(6);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).topMargin = D.dp(2);
        TextView t = Ui.txt(c, text, 11.5f, D.onSurfaceVar, 500);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(t);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.topMargin = D.dp(3);
        row.setLayoutParams(p);
        return row;
    }

    static void addDonationSheet(final Context c, final Runnable[] refresh) {
        Ui.sheet(c, "রক্তদানের রেকর্ড যোগ করুন", R.drawable.ic_dropfill, true, new Ui.SheetCallback() {
            public void onSheet(final LinearLayout body, final Runnable close) {
                Data.User me = Data.me();
                Data.Cooldown cd = Data.cooldown(me);

                if (!cd.eligible) {
                    LinearLayout warn = Ui.cardPad(c);
                    warn.setBackground(D.round(D.tertiaryC, 16));
                    LinearLayout.LayoutParams wp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    wp.setMargins(D.dp(18), D.dp(10), D.dp(18), 0);
                    warn.setLayoutParams(wp);
                    warn.addView(Ui.txt(c, "⚠ কুলডাউন চলছে: শেষ দানের " + Bn.bn(cd.lastDiff) + " দিন হয়েছে — স্বাস্থ্যগত কারণে ৯০ দিন অপেক্ষা করা উচিত। তবুও রেকর্ড করতে চাইলে সঠিক তারিখ দিন।", 11.5f, D.onTertiaryC, 500));
                    body.addView(warn);
                }

                final Ui.Field fDate = Ui.field(c, "রক্তদানের তারিখ *", Ui.F_DATE, Bn.toYmd(System.currentTimeMillis()), null);
                final Ui.Field fRecip = Ui.field(c, "রিসিপিয়েন্টের নাম / রোগীর নাম *", Ui.F_TEXT, null, "যার জন্য রক্ত দিয়েছেন");
                final Ui.Field fPhone = Ui.field(c, "রিসিপিয়েন্টের ফোন (ঐচ্ছিক)", Ui.F_PHONE, null, "01XXXXXXXXX");
                final Ui.Field fHosp = Ui.field(c, "হাসপাতালের নাম *", Ui.F_TEXT, null, "যেমন: নাটোর সদর হাসপাতাল");
                final Ui.Field fLoc = Ui.field(c, "লোকেশন (ঐচ্ছিক)", Ui.F_TEXT, null, "যেমন: নাটোর সদর");
                final Ui.Field fAmount = Ui.field(c, "রক্তের পরিমাণ (ml) *", Ui.F_NUMBER, "450", "সাধারণত ৪৫০ ml");
                final Ui.Field fNotes = Ui.field(c, "নোট (ঐচ্ছিক)", Ui.F_AREA, null, "কোনো বিশেষ তথ্য থাকলে লিখুন...");

                body.addView(fDate.root);
                body.addView(fRecip.root);
                body.addView(fPhone.root);
                body.addView(fHosp.root);
                body.addView(fLoc.root);
                body.addView(fAmount.root);
                body.addView(fNotes.root);

                LinearLayout btnWrap = Ui.v(c);
                btnWrap.setPadding(D.dp(18), D.dp(14), D.dp(18), 0);
                TextView save = Ui.btn(c, "সংরক্ষণ করুন", Ui.BTN_GRAD, new Runnable() {
                    public void run() {
                        String ymd = fDate.input.getTag() != null ? (String) fDate.input.getTag() : Bn.toYmd(System.currentTimeMillis());
                        if (fRecip.input.getText().toString().trim().isEmpty()
                                || fHosp.input.getText().toString().trim().isEmpty()) {
                            Ui.toast("রিসিপিয়েন্ট ও হাসপাতালের নাম দিন", true);
                            return;
                        }
                        int amount = 450;
                        try { amount = Math.max(100, Math.min(1000, Integer.parseInt(fAmount.input.getText().toString().trim()))); } catch (Exception ignored) {}
                        Data.Donation d = new Data.Donation();
                        d.date = Bn.parseYmd(ymd);
                        d.recipientName = fRecip.input.getText().toString().trim();
                        d.phone = fPhone.input.getText().toString().trim();
                        d.hospital = fHosp.input.getText().toString().trim();
                        d.location = fLoc.input.getText().toString().trim();
                        d.amountMl = amount;
                        d.notes = fNotes.input.getText().toString().trim();
                        Data.addDonation(d);
                        Data.User me = Data.me();
                        Data.pushNotif(me.id, "don", "রক্তদান সফলভাবে সংরক্ষিত!",
                                "মোট দান: " + Bn.bn(me.donationCount) + " বার • পরবর্তী দানের সময়: ৯০ দিন পর। আপনার ব্যাজ: " + Data.badge(me)[1] + "।");
                        close.run();
                        Ui.toast("রক্তদান সফলভাবে সংরক্ষিত হয়েছে!", false);
                        refresh[0].run();
                    }
                });
                save.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(50)));
                btnWrap.addView(save);

                TextView note = Ui.txt(c, "✓ সংরক্ষণের পর ৯০ দিনের কুলডাউন নতুন করে শুরু হবে", 10.5f, D.outline, 500);
                note.setGravity(Gravity.CENTER);
                note.setPadding(0, D.dp(10), 0, D.dp(6));
                btnWrap.addView(note);
                body.addView(btnWrap);
            }
        });
    }
}
