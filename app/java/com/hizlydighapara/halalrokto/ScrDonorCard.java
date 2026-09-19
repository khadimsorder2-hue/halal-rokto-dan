package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** ডোনার কার্ড — ব্র্যান্ডেড ডিজিটাল পরিচয়পত্র + যাচাই প্যাটার্ন। */
public final class ScrDonorCard {

    private ScrDonorCard() {}

    public static View build(final Context c) {
        final Data.User me = Data.me();
        String[] badge = Data.badge(me);
        int rank = Data.myRank();
        final String memberId = "DJS-" + String.valueOf(10000 + Math.abs(Bn.hash(me.id)) % 89999);
        final String verCode = memberId.substring(4) + String.valueOf(Math.abs(Bn.hash(me.phone)) % 900 + 100);

        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);

        root.addView(Ui.heroBar(c, "ডোনার কার্ড", "আপনার ডিজিটাল পরিচয়পত্র", true, null, new Runnable() {
            public void run() { Ui.host.back(); }
        }));

        /* ── the card ── */
        LinearLayout cardWrap = Ui.v(c);
        cardWrap.setPadding(D.dp(18), D.dp(18), D.dp(18), 0);

        LinearLayout card = Ui.v(c);
        card.setPadding(D.dp(20), D.dp(18), D.dp(20), D.dp(18));
        card.setBackground(D.gradAngle(D.heroGrad, 24, 315));
        card.setElevation(D.dp(10));

        // org header
        LinearLayout orgRow = Ui.h(c);
        ImageView logo = Ui.icon(c, R.drawable.ic_dropfill, 26, 0xFFFFFFFF);
        logo.setBackground(D.round(D.withAlpha(0xFFFFFFFF, 56), 50));
        logo.setPadding(D.dp(7), D.dp(7), D.dp(7), D.dp(7));
        orgRow.addView(logo);
        ((LinearLayout.LayoutParams) logo.getLayoutParams()).rightMargin = D.dp(11);
        LinearLayout orgT = Ui.v(c);
        orgT.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        orgT.addView(Ui.txt(c, Data.ORG, 13.5f, 0xFFFFFFFF, 700));
        TextView orgS = Ui.txt(c, Data.SINCE + " • " + Data.ADDRESS, 10.5f, D.withAlpha(0xFFFFFFFF, 210), 500);
        orgS.setPadding(0, D.dp(2), 0, 0);
        orgT.addView(orgS);
        orgRow.addView(orgT);
        card.addView(orgRow);

        // blood group block
        LinearLayout bloodRow = Ui.h(c);
        bloodRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams brp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        brp.topMargin = D.dp(18);
        bloodRow.setLayoutParams(brp);

        LinearLayout bloodBox = Ui.v(c);
        bloodBox.setGravity(Gravity.CENTER);
        bloodBox.setPadding(D.dp(18), D.dp(10), D.dp(18), D.dp(12));
        bloodBox.setBackground(D.round(D.withAlpha(0xFFFFFFFF, 42), 18));
        TextView bgT = Ui.txt(c, me.bloodType, 30, 0xFFFFFFFF, 700);
        bloodBox.addView(bgT);
        TextView bgL = Ui.txt(c, "রক্তের গ্রুপ", 9.5f, D.withAlpha(0xFFFFFFFF, 220), 600);
        bgL.setPadding(0, D.dp(2), 0, 0);
        bloodBox.addView(bgL);
        bloodRow.addView(bloodBox);

        LinearLayout nameBox = Ui.v(c);
        nameBox.setPadding(D.dp(14), 0, 0, 0);
        nameBox.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        nameBox.addView(Ui.txt(c, me.name, 18, 0xFFFFFFFF, 700));
        TextView memId = Ui.txt(c, "সদস্য নং: " + memberId, 11f, D.withAlpha(0xFFFFFFFF, 225), 600);
        memId.setPadding(0, D.dp(4), 0, 0);
        nameBox.addView(memId);
        TextView badgeT = Ui.txt(c, badge[1] + (me.verified ? "  •  ভেরিফায়েড ডোনার ✓" : "  •  ভেরিফিকেশন অপেক্ষমান"), 11f, D.withAlpha(0xFFFFFFFF, 225), 500);
        badgeT.setPadding(0, D.dp(3), 0, 0);
        nameBox.addView(badgeT);
        bloodRow.addView(nameBox);
        card.addView(bloodRow);

        // divider
        View div = new View(c);
        div.setBackground(D.round(D.withAlpha(0xFFFFFFFF, 60), 1));
        div.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, D.dp(0.8f))));
        LinearLayout.LayoutParams dvp = (LinearLayout.LayoutParams) div.getLayoutParams();
        dvp.topMargin = D.dp(16);
        div.setLayoutParams(dvp);
        card.addView(div);

        // bottom: info + pattern
        LinearLayout bottom = Ui.h(c);
        bottom.setGravity(Gravity.BOTTOM);
        LinearLayout.LayoutParams btp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btp.topMargin = D.dp(14);
        bottom.setLayoutParams(btp);

        LinearLayout info = Ui.v(c);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        info.addView(Ui.txt(c, "মোট দান: " + Bn.bn(me.donationCount) + " বার • র‌্যাংক: " + Bn.bn(rank), 11.5f, D.withAlpha(0xFFFFFFFF, 235), 600));
        TextView j2 = Ui.txt(c, "যোগদান: " + Bn.fmtDate(me.createdAt), 11.5f, D.withAlpha(0xFFFFFFFF, 235), 600);
        j2.setPadding(0, D.dp(4), 0, 0);
        info.addView(j2);
        TextView j3 = Ui.txt(c, "যাচাই কোড: " + verCode, 11.5f, 0xFFFFFFFF, 700);
        j3.setPadding(0, D.dp(4), 0, 0);
        info.addView(j3);
        bottom.addView(info);

        Views.PatternView pattern = new Views.PatternView(c);
        pattern.setLayoutParams(Ui.lp(D.dp(74), D.dp(74)));
        pattern.seed(me.id + me.phone, 0xFFFFFFFF);
        bottom.addView(pattern);
        card.addView(bottom);
        cardWrap.addView(card);
        root.addView(cardWrap);

        /* actions */
        LinearLayout acts = Ui.h(c);
        acts.setPadding(D.dp(18), D.dp(16), D.dp(18), 0);
        TextView shareBtn = Ui.btn(c, "কার্ড শেয়ার করুন", Ui.BTN_GRAD, new Runnable() {
            public void run() {
                Ui.host.share("🪸 " + Data.APP_NAME + " — ডোনার কার্ড\nসংগঠন: " + Data.ORG + " (" + Data.SINCE
                                + ")\nনাম: " + me.name + "\nরক্তের গ্রুপ: " + me.bloodType + "\nসদস্য নং: " + memberId
                                + "\nমোট দান: " + Bn.bn(me.donationCount) + " বার\nযোগদান: " + Bn.fmtDate(me.createdAt),
                        "ডোনার কার্ড শেয়ার");
            }
        });
        shareBtn.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(48), 1f));
        acts.addView(shareBtn);
        ((LinearLayout.LayoutParams) shareBtn.getLayoutParams()).rightMargin = D.dp(9);
        TextView call = Ui.btn(c, "☎", Ui.BTN_TONAL, new Runnable() {
            public void run() { Ui.host.dial(me.phone); }
        });
        call.setLayoutParams(Ui.lp(D.dp(52), D.dp(48)));
        acts.addView(call);
        root.addView(acts);

        /* info note */
        LinearLayout noteWrap = Ui.v(c);
        noteWrap.setPadding(D.dp(18), D.dp(14), D.dp(18), D.dp(20));
        LinearLayout note = Ui.cardPad(c);
        LinearLayout nr = Ui.h(c);
        nr.setGravity(Gravity.TOP);
        ImageView ii = Ui.icon(c, R.drawable.ic_info, 17, D.primary);
        nr.addView(ii);
        ((LinearLayout.LayoutParams) ii.getLayoutParams()).rightMargin = D.dp(10);
        ((LinearLayout.LayoutParams) ii.getLayoutParams()).topMargin = D.dp(2);
        TextView tt = Ui.txt(c, "জরুরি মুহূর্তে এই কার্ড দেখিয়ে দ্রুত পরিচয় যাচাই করুন। কার্ডের যাচাই কোড (" + verCode + ") সংগঠনের রেকর্ডের সাথে মিলিয়ে ডোনারের সত্যতা নিশ্চিত হওয়া যায়। প্রতিটি রক্তদানের পর কার্ড স্বয়ংক্রিয়ভাবে আপডেট হয়।", 12f, D.onSurfaceVar, 400);
        tt.setLineSpacing(0, 1.55f);
        tt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        nr.addView(tt);
        note.addView(nr);
        noteWrap.addView(note);
        root.addView(noteWrap);

        sc.addView(root);
        return sc;
    }
}
