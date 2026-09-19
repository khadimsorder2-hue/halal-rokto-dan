package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/** হোম ড্যাশবোর্ড — স্ট্যাটস, কুলডাউন রিং, কুইক অ্যাকশন, জরুরি কার্ড, র‍্যাংক। */
public final class ScrHome {

    private ScrHome() {}

    public static View build(final Context c) {
        final Data.User me = Data.me();
        Data.Cooldown cd = Data.cooldown(me);

        ScrollView sc = new ScrollView(c);
        sc.setFillViewport(true);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);

        /* ── hero header ── */
        LinearLayout hero = Ui.v(c);
        hero.setBackground(D.gradAngle(D.heroGrad, 0, 315));
        hero.setPadding(D.dp(18), D.dp(16), D.dp(18), D.dp(22));
        root.addView(hero);

        LinearLayout top = Ui.h(c);
        TextView av = Ui.avatar(c, Bn.initials(me.name), D.avatarColor(me.id), 46, 17);
        top.addView(av);
        LinearLayout hiWrap = Ui.v(c);
        hiWrap.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        ((LinearLayout.LayoutParams) hiWrap.getLayoutParams()).leftMargin = D.dp(12);
        TextView hi = Ui.txt(c, "আসসালামু আলাইকুম,", 11.5f, D.withAlpha(0xFFFFFFFF, 205), 500);
        hiWrap.addView(hi);
        TextView nm = Ui.txt(c, me.name, 17, 0xFFFFFFFF, 700);
        nm.setPadding(0, D.dp(1), 0, 0);
        hiWrap.addView(nm);
        top.addView(hiWrap);
        top.addView(navActions(c));
        hero.addView(top);

        LinearLayout stats = Ui.h(c);
        stats.setPadding(0, D.dp(16), 0, 0);
        LinearLayout.LayoutParams statLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stats.setLayoutParams(statLp);

        List<Data.User> users = Data.visibleUsers();
        int activeEmg = 0;
        for (Data.Emergency e : Data.s.emergencies) if (!e.fulfilled) activeEmg++;

        statTileOn(stats, "white", Bn.bn(users.size()), "মোট ডোনার", D.dark ? 0xFF312827 : 0x26FFFFFF);
        LinearLayout.LayoutParams sp1 = (LinearLayout.LayoutParams) stats.getChildAt(0).getLayoutParams();
        sp1.rightMargin = D.dp(6);
        statTileOn(stats, "white", Bn.bn(Data.visibleDonations().size()), "মোট রক্তদান", D.dark ? 0x26FFFFFF : 0x33FFFFFF);
        LinearLayout.LayoutParams sp2 = (LinearLayout.LayoutParams) stats.getChildAt(1).getLayoutParams();
        sp2.rightMargin = D.dp(6);
        statTileOn(stats, "white", Bn.bn(activeEmg), "সক্রিয় জরুরি", D.dark ? 0xFF312827 : 0x26FFFFFF);
        hero.addView(stats);

        /* ── cooldown card ── */
        root.addView(cooldownCard(c, me, cd));

        /* ── quick actions ── */
        root.addView(Ui.sectionHeader(c, "দ্রুত সেবা"));
        LinearLayout qg = Ui.h(c);
        qg.setPadding(D.dp(16), 0, D.dp(16), 0);
        qg.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        quickTile(c, qg, R.drawable.ic_bloodbank, "রক্তের\nস্টক", D.primaryC, D.onPrimaryC, "stock");
        quickTile(c, qg, R.drawable.ic_people, "ডোনার\nখুঁজুন", 0xFFDCEEEE, 0xFF0F3B3B, "donors");
        quickTile(c, qg, R.drawable.ic_add, "রক্তদান\nযোগ করুন", 0xFFFFF3CD, 0xFF5D4E00, "history");
        quickTile(c, qg, R.drawable.ic_card, "ডোনার\nকার্ড", 0xFFE8DEF8, 0xFF3A2A5E, "donorcard");
        root.addView(qg);

        /* ── emergencies ── */
        List<Data.Emergency> emgs = Data.visibleEmergencies();
        if (!emgs.isEmpty()) {
            LinearLayout sec = Ui.h(c);
            sec.setGravity(Gravity.CENTER_VERTICAL);
            sec.setPadding(D.dp(18), D.dp(18), D.dp(10), D.dp(8));
            TextView st = Ui.txt(c, "জরুরি রক্তের প্রয়োজন", 14.5f, D.onSurface, 700);
            sec.addView(st);
            TextView all = Ui.btnSmall(c, "সব দেখুন", Ui.BTN_TEXT, new Runnable() {
                public void run() { Ui.host.go("emergency", false); }
            });
            sec.addView(all);
            root.addView(sec);

            int shown = 0;
            for (final Data.Emergency e : emgs) {
                if (shown++ >= 2) break;
                root.addView(emgMini(c, e));
            }
        }

        /* ── my rank card ── */
        root.addView(Ui.sectionHeader(c, "আপনার অবস্থান"));
        LinearLayout rankCard = Ui.cardPad(c);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rp.setMargins(D.dp(16), 0, D.dp(16), 0);
        rankCard.setLayoutParams(rp);

        LinearLayout rankRow = Ui.h(c);
        ImageView medal = Ui.icon(c, R.drawable.ic_medal, 22, D.onTertiaryC);
        medal.setBackground(D.round(D.tertiaryC, 16));
        medal.setPadding(D.dp(11), D.dp(11), D.dp(11), D.dp(11));
        rankRow.addView(medal);
        ((LinearLayout.LayoutParams) medal.getLayoutParams()).rightMargin = D.dp(13);

        int myRank = Data.myRank();
        LinearLayout rText = Ui.v(c);
        rText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        rText.addView(Ui.txt(c, "র‌্যাংকিং: " + Bn.bn(myRank) + " নং", 14.5f, D.onSurface, 700));
        TextView rSub = Ui.txt(c, "মোট " + Bn.bn(me.donationCount) + " বার রক্তদান • " + Data.badge(me)[1], 12f, D.onSurfaceVar, 400);
        rSub.setPadding(0, D.dp(2), 0, 0);
        rText.addView(rSub);
        rankRow.addView(rText);
        rankRow.addView(Ui.btnSmall(c, "র‌্যাংকিং", Ui.BTN_TONAL, new Runnable() {
            public void run() { Ui.host.go("ranking", false); }
        }));
        rankCard.addView(rankRow);
        root.addView(rankCard);

        /* ── org banner ── */
        LinearLayout org = Ui.h(c);
        org.setPadding(D.dp(16), D.dp(14), D.dp(16), 0);
        LinearLayout banner = Ui.h(c);
        banner.setPadding(D.dp(14), D.dp(13), D.dp(8), D.dp(13));
        banner.setBackground(D.round(D.tertiaryC, 18));
        banner.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ImageView shield = Ui.icon(c, R.drawable.ic_shield, 20, D.onTertiaryC);
        banner.addView(shield);
        ((LinearLayout.LayoutParams) shield.getLayoutParams()).rightMargin = D.dp(12);
        LinearLayout orgT = Ui.v(c);
        orgT.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        orgT.addView(Ui.txt(c, Data.ORG, 13, D.onTertiaryC, 700));
        TextView orgS = Ui.txt(c, Data.SINCE + " • " + Data.ADDRESS, 11f, D.withAlpha(D.onTertiaryC, 220), 500);
        orgS.setPadding(0, D.dp(2), 0, 0);
        orgT.addView(orgS);
        banner.addView(orgT);
        banner.addView(Ui.btnSmall(c, "বিস্তারিত", Ui.BTN_TEXT, new Runnable() {
            public void run() { Ui.host.go("about", false); }
        }));
        org.addView(banner);
        root.addView(org);

        View gap = new View(c);
        gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(24)));
        root.addView(gap);

        sc.addView(root);
        return sc;
    }

    static LinearLayout navActions(final Context c) {
        LinearLayout l = Ui.h(c);
        int unread = Data.unreadCount();
        FrameLayout bellWrap = new FrameLayout(c);
        ImageView bell = Ui.icon(c, R.drawable.ic_bell, 22, 0xFFFFFFFF);
        bell.setBackground(D.ripple(D.round(D.withAlpha(0xFFFFFFFF, 44), 50), D.rippleColorLight));
        bell.setPadding(D.dp(9), D.dp(9), D.dp(9), D.dp(9));
        bell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.go("notifs", false); }
        });
        bellWrap.addView(bell);
        if (unread > 0) {
            TextView dot = Ui.txt(c, Bn.bn(unread), 9.5f, 0xFFFFFFFF, 700);
            dot.setGravity(Gravity.CENTER);
            dot.setPadding(D.dp(5), D.dp(1), D.dp(5), D.dp(2));
            dot.setBackground(D.round(0xFF1E6B33, 50));
            dot.setElevation(D.dp(2));
            dot.setLayoutParams(Ui.flp(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.END));
            bellWrap.addView(dot);
        }
        l.addView(bellWrap);
        ((LinearLayout.LayoutParams) bellWrap.getLayoutParams()).rightMargin = D.dp(6);

        ImageView person = Ui.icon(c, R.drawable.ic_person, 22, 0xFFFFFFFF);
        person.setBackground(D.ripple(D.round(D.withAlpha(0xFFFFFFFF, 44), 50), D.rippleColorLight));
        person.setPadding(D.dp(9), D.dp(9), D.dp(9), D.dp(9));
        person.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.go("profile", false); }
        });
        l.addView(person);
        return l;
    }

    static LinearLayout cooldownCard(Context c, Data.User me, Data.Cooldown cd) {
        LinearLayout card = Ui.h(c);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(D.dp(18), D.dp(16), D.dp(18), D.dp(16));
        card.setBackground(D.gradAngle(D.redGrad, 22, 315));
        card.setElevation(D.dp(5));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(D.dp(16), D.dp(18), D.dp(16), 0);
        card.setLayoutParams(p);

        FrameLayout ringWrap = new FrameLayout(c);
        ringWrap.setLayoutParams(Ui.lp(D.dp(76), D.dp(76)));
        Views.RingView ring = new Views.RingView(c);
        ring.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ringWrap.addView(ring);

        TextView center = new TextView(c);
        center.setGravity(Gravity.CENTER);
        center.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        center.setTypeface(D.tfBold);
        center.setTextSize(20);

        if (cd.first) {
            ring.setProgress(0f);
            center.setText("❤");
            center.setTextSize(22);
        } else if (cd.eligible) {
            ring.setProgress(1f);
            center.setText("✓");
            center.setTextSize(24);
        } else {
            float pct = cd.elapsed / (float) Data.COOLDOWN_DAYS;
            ring.setProgress(pct);
            center.setText(Bn.bn(cd.days));
        }
        center.setTextColor(0xFFFFFFFF);
        ringWrap.addView(center);
        card.addView(ringWrap);
        ((LinearLayout.LayoutParams) ringWrap.getLayoutParams()).rightMargin = D.dp(16);

        LinearLayout t = Ui.v(c);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        if (cd.first) {
            t.addView(Ui.txt(c, "আপনি এখনো রক্ত দেননি", 15.5f, 0xFFFFFFFF, 700));
            TextView s = Ui.txt(c, "প্রথমবার রক্ত দিয়ে রক্তবন্ধু হয়ে যান — জীবন বাঁচানোর মহান পথ শুরু করুন।", 12f, D.withAlpha(0xFFFFFFFF, 215), 400);
            s.setLineSpacing(0, 1.4f);
            s.setPadding(0, D.dp(4), 0, 0);
            t.addView(s);
        } else if (cd.eligible) {
            t.addView(Ui.txt(c, "আপনি রক্ত দিতে পারেন ✓", 15.5f, 0xFFFFFFFF, 700));
            TextView s = Ui.txt(c, "শেষ দান " + Bn.bn(cd.lastDiff) + " দিন আগে — ৯০ দিনের কুলডাউন পূর্ণ। এখনই দান করতে পারেন।", 12f, D.withAlpha(0xFFFFFFFF, 215), 400);
            s.setLineSpacing(0, 1.4f);
            s.setPadding(0, D.dp(4), 0, 0);
            t.addView(s);
        } else {
            int pctShown = Math.round(cd.elapsed * 100f / Data.COOLDOWN_DAYS);
            t.addView(Ui.txt(c, Bn.bn(cd.days) + " দিন পর রক্ত দিতে পারবেন", 15.5f, 0xFFFFFFFF, 700));
            TextView s = Ui.txt(c, "শেষ দান: " + Bn.fmtDate(me.lastDonationDate) + " — ৯০ দিনের কুলডাউন চলছে (" + Bn.bn(pctShown) + "% পূর্ণ)।", 12f, D.withAlpha(0xFFFFFFFF, 215), 400);
            s.setLineSpacing(0, 1.4f);
            s.setPadding(0, D.dp(4), 0, 0);
            t.addView(s);
        }
        card.addView(t);
        return card;
    }

    static void statTileOn(LinearLayout parent, String ignore, String num, String label, int bg) {
        Context c = parent.getContext();
        LinearLayout l = Ui.v(c);
        l.setGravity(Gravity.CENTER);
        l.setPadding(D.dp(6), D.dp(12), D.dp(6), D.dp(12));
        l.setBackground(D.round(bg, 16));
        l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView n = Ui.txt(c, num, 21, 0xFFFFFFFF, 700);
        n.setGravity(Gravity.CENTER);
        l.addView(n);
        TextView lb = Ui.txt(c, label, 10.5f, D.withAlpha(0xFFFFFFFF, 220), 500);
        lb.setGravity(Gravity.CENTER);
        lb.setPadding(0, D.dp(3), 0, 0);
        l.addView(lb);
        parent.addView(l);
    }

    static void quickTile(Context c, LinearLayout parent, int iconRes, String label, int bg, int fg, final String target) {
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

    static LinearLayout emgMini(final Context c, final Data.Emergency e) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(D.dp(14), D.dp(12), D.dp(12), D.dp(12));
        row.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 18, D.outlineVar, 1), D.rippleColor));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(D.dp(16), 0, D.dp(16), D.dp(9));
        row.setLayoutParams(p);

        Views.PulseView pulse = new Views.PulseView(c);
        pulse.setLayoutParams(Ui.lp(D.dp(12), D.dp(12)));
        row.addView(pulse);
        ((LinearLayout.LayoutParams) pulse.getLayoutParams()).rightMargin = D.dp(11);

        row.addView(Ui.bgroup(c, e.bloodGroup, true, 13));
        ((LinearLayout.LayoutParams) row.getChildAt(1).getLayoutParams()).rightMargin = D.dp(11);

        LinearLayout m = Ui.v(c);
        m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        m.addView(Ui.txt(c, e.patientName, 13.5f, D.onSurface, 700));
        TextView sub = Ui.txt(c, e.hospital + " • " + Bn.bn(e.units) + " ব্যাগ • " + Bn.timeAgo(e.createdAt), 11f, D.onSurfaceVar, 400);
        sub.setPadding(0, D.dp(2), 0, 0);
        m.addView(sub);
        row.addView(m);

        ImageView call = Ui.icon(c, R.drawable.ic_phone, 18, 0xFFFFFFFF);
        call.setBackground(D.ripple(D.gradAngle(new int[]{0xFF2E9E4F, 0xFF1E6B33}, 50, 315), D.rippleColorLight));
        call.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
        call.setElevation(D.dp(3));
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.haptic(12); Ui.host.dial(e.contact); }
        });
        row.addView(call);

        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.go("emergency", false); }
        });
        return row;
    }
}
