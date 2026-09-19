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

import java.util.List;

/** ইভেন্ট ও ক্যাম্প — আসছে কী, RSVP, নতুন ইভেন্ট যোগ, শেয়ার। */
public final class ScrEvents {

    private ScrEvents() {}

    static String typeLabel(String t) {
        switch (t) {
            case "camp": return "রক্তদান ক্যাম্প";
            case "meeting": return "কমিটি বৈঠক";
            case "awareness": return "সচেতনতা কর্মসূচি";
            default: return "সামাজিক অনুষ্ঠান";
        }
    }

    static int[] typeColors(String t) {
        switch (t) {
            case "camp": return new int[]{D.primaryC, D.onPrimaryC};
            case "meeting": return new int[]{0xFFDCEEEE, 0xFF0F3B3B};
            case "awareness": return new int[]{0xFFFFF3CD, 0xFF5D4E00};
            default: return new int[]{0xFFE8DEF8, 0xFF3A2A5E};
        }
    }

    public static View build(final Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        final LinearLayout root = Ui.v(c);

        root.addView(Ui.heroBar(c, "ইভেন্ট ও ক্যাম্প", "সংঘের সব কর্মসূচি এক জায়গায়", false, null, null));

        List<Data.Event> up = Data.upcomingEvents();
        List<Data.Event> all = Data.visibleEvents();

        /* summary strip */
        LinearLayout sum = Ui.h(c);
        sum.setPadding(D.dp(16), D.dp(14), D.dp(16), D.dp(4));
        int camps = 0, rsvpd = 0;
        for (Data.Event e : up) {
            if ("camp".equals(e.type)) camps++;
            if (e.mine) rsvpd++;
        }
        sum.addView(tile(c, Bn.bn(up.size()), "আসছে"));
        ((LinearLayout.LayoutParams) sum.getChildAt(0).getLayoutParams()).rightMargin = D.dp(8);
        sum.addView(tile(c, Bn.bn(camps), "ক্যাম্প"));
        ((LinearLayout.LayoutParams) sum.getChildAt(1).getLayoutParams()).rightMargin = D.dp(8);
        sum.addView(tile(c, Bn.bn(rsvpd), "আমার RSVP"));
        root.addView(sum);

        /* upcoming */
        root.addView(Ui.sectionHeader(c, "আসন্ন কর্মসূচি"));
        LinearLayout upList = Ui.v(c);
        upList.setPadding(D.dp(16), 0, D.dp(16), 0);
        if (up.isEmpty()) {
            upList.addView(Ui.emptyState(c, R.drawable.ic_calendar, "কোনো আসন্ন ইভেন্ট নেই",
                    "নতুন ইভেন্ট যোগ করতে নিচের \"+ নতুন ইভেন্ট\" বোতামে চাপ দিন।"));
        } else {
            for (final Data.Event e : up) upList.addView(eventCard(c, e, root));
        }
        root.addView(upList);

        /* past events */
        List<Data.Event> past = new java.util.ArrayList<Data.Event>();
        for (Data.Event e : all) if (e.date < Data.todayStart()) past.add(e);
        if (!past.isEmpty()) {
            root.addView(Ui.sectionHeader(c, "সম্পন্ন কর্মসূচি"));
            LinearLayout pastList = Ui.v(c);
            pastList.setPadding(D.dp(16), 0, D.dp(16), 0);
            for (final Data.Event e : past) pastList.addView(pastRow(c, e));
            root.addView(pastList);
        }

        View gap = new View(c);
        gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(90)));
        root.addView(gap);

        sc.addView(root);

        /* FAB — নতুন ইভেন্ট */
        FrameLayout wrap = new FrameLayout(c);
        wrap.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView fab = Ui.fab(c, "+  নতুন ইভেন্ট", new Runnable() {
            public void run() { addSheet(c, root); }
        });
        fab.setLayoutParams(Ui.flp(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.END, 18));
        wrap.addView(sc);
        wrap.addView(fab);
        return wrap;
    }

    static LinearLayout tile(Context c, String num, String label) {
        LinearLayout l = Ui.v(c);
        l.setGravity(Gravity.CENTER);
        l.setPadding(D.dp(6), D.dp(12), D.dp(6), D.dp(12));
        l.setBackground(D.round(D.surfaceC, 18));
        l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        l.addView(Ui.txt(c, num, 20, D.primary, 700));
        TextView lb = Ui.txt(c, label, 10.5f, D.onSurfaceVar, 600);
        lb.setGravity(Gravity.CENTER);
        lb.setPadding(0, D.dp(2), 0, 0);
        l.addView(lb);
        return l;
    }

    static LinearLayout eventCard(final Context c, final Data.Event e, final LinearLayout root) {
        LinearLayout card = Ui.card(c);
        card.setPadding(D.dp(0), D.dp(0), D.dp(0), D.dp(2));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, D.dp(11));
        card.setLayoutParams(p);

        /* তারিখ-ব্যান্ড + কাউন্টডাউন */
        LinearLayout band = Ui.h(c);
        band.setGravity(Gravity.CENTER_VERTICAL);
        band.setPadding(D.dp(14), D.dp(12), D.dp(14), D.dp(11));
        int[] tc = typeColors(e.type);
        band.setBackground(D.gradAngle(D.heroGrad, 18, 315));
        card.addView(band);

        FrameLayout dateBox = new FrameLayout(c);
        dateBox.setLayoutParams(Ui.lp(D.dp(52), D.dp(56)));
        LinearLayout dl = Ui.v(c);
        dl.setGravity(Gravity.CENTER);
        dl.setBackground(D.round(D.withAlpha(0xFFFFFFFF, D.dark ? 34 : 64), 14));
        dl.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTimeInMillis(e.date);
        TextView d1 = Ui.txt(c, Bn.bn(cal.get(java.util.Calendar.DAY_OF_MONTH)), 19, 0xFFFFFFFF, 700);
        d1.setGravity(Gravity.CENTER);
        dl.addView(d1);
        TextView d2 = Ui.txt(c, Bn.MONTHS[cal.get(java.util.Calendar.MONTH)], 10f, D.withAlpha(0xFFFFFFFF, 225), 600);
        d2.setGravity(Gravity.CENTER);
        dl.addView(d2);
        dateBox.addView(dl);
        band.addView(dateBox);
        ((LinearLayout.LayoutParams) dateBox.getLayoutParams()).rightMargin = D.dp(13);

        LinearLayout tv = Ui.v(c);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView title = Ui.txt(c, e.title, 15, 0xFFFFFFFF, 700);
        title.setLineSpacing(0, 1.15f);
        tv.addView(title);
        String sub = typeLabel(e.type) + (e.timeText.isEmpty() ? "" : " • " + e.timeText);
        TextView st = Ui.txt(c, sub, 11f, D.withAlpha(0xFFFFFFFF, 215), 500);
        st.setPadding(0, D.dp(3), 0, 0);
        tv.addView(st);
        band.addView(tv);

        int days = Data.daysUntil(e.date);
        String cd = days == 0 ? "আজ" : days == 1 ? "কাল" : Bn.bn(days) + " দিন";
        TextView cdPill = Ui.txt(c, cd, 11f, D.onPrimaryC, 700);
        cdPill.setPadding(D.dp(10), D.dp(4), D.dp(10), D.dp(5));
        cdPill.setBackground(D.round(D.withAlpha(0xFFFFFFFF, D.dark ? 34 : 70), 50));
        band.addView(cdPill);

        /* বডি */
        LinearLayout body = Ui.v(c);
        body.setPadding(D.dp(14), D.dp(11), D.dp(14), D.dp(13));

        LinearLayout vRow = Ui.h(c);
        ImageView pin = Ui.icon(c, R.drawable.ic_location, 16, D.onSurfaceVar);
        vRow.addView(pin);
        ((LinearLayout.LayoutParams) pin.getLayoutParams()).rightMargin = D.dp(7);
        TextView vt = Ui.txt(c, e.venue, 12.5f, D.onSurface, 500);
        vt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        vRow.addView(vt);
        body.addView(vRow);

        if (e.details != null && !e.details.isEmpty()) {
            TextView dt = Ui.txt(c, e.details, 11.5f, D.onSurfaceVar, 400);
            dt.setLineSpacing(0, 1.35f);
            dt.setPadding(0, D.dp(7), 0, D.dp(4));
            body.addView(dt);
        }

        /* অ্যাকশন রো */
        LinearLayout acts = Ui.h(c);
        acts.setPadding(0, D.dp(6), 0, 0);

        LinearLayout rsvpWrap = Ui.v(c);
        rsvpWrap.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView rsvpBtn = Ui.btnSmall(c, e.mine ? "✓ অংশ নিচ্ছি" : "অংশ নিব", e.mine ? Ui.BTN_SUCCESS : Ui.BTN_TONAL,
                new Runnable() {
                    public void run() {
                        Data.toggleRsvp(e);
                        if (e.mine) {
                            Ui.toast("ধন্যবাদ! \u201C" + e.title + "\u201D-এ আপনার নাম নিশ্চিত হলো", false);
                            Data.pushNotif(Data.s.session, "sys", "RSVP নিশ্চিত",
                                    e.title + " — " + Bn.fmtDate(e.date) + ", " + e.venue + "। সময়মতো উপস্থিত থাকুন।");
                        }
                        Ui.host.refresh();
                    }
                });
        rsvpWrap.addView(rsvpBtn);

        LinearLayout cntRow = Ui.h(c);
        cntRow.setPadding(D.dp(2), D.dp(6), 0, 0);
        ImageView ppl = Ui.icon(c, R.drawable.ic_people, 13, D.onSurfaceVar);
        cntRow.addView(ppl);
        ((LinearLayout.LayoutParams) ppl.getLayoutParams()).rightMargin = D.dp(4);
        cntRow.addView(Ui.txt(c, Bn.bn(Data.rsvpCount(e)) + " জন অংশ নিচ্ছে", 11f, D.onSurfaceVar, 600));
        rsvpWrap.addView(cntRow);
        acts.addView(rsvpWrap);

        TextView share = Ui.btnSmall(c, "শেয়ার", Ui.BTN_OUTLINE, new Runnable() {
            public void run() {
                Ui.host.share("📅 " + e.title + "\n" + typeLabel(e.type) + " — " + Data.ORG + "\n"
                        + "🗓 " + Bn.dayName(e.date) + ", " + Bn.fmtDate(e.date)
                        + (e.timeText.isEmpty() ? "" : "\n🕑 " + e.timeText)
                        + "\n📍 " + e.venue + "\n\n" + e.details
                        + "\n\n— " + Data.APP_NAME + " অ্যাপ থেকে শেয়ার করা", "ইভেন্ট শেয়ার");
            }
        });
        acts.addView(share);
        ((LinearLayout.LayoutParams) share.getLayoutParams()).leftMargin = D.dp(9);

        body.addView(acts);
        card.addView(body);
        return card;
    }

    static LinearLayout pastRow(final Context c, final Data.Event e) {
        int[] tc = typeColors(e.type);
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(D.dp(14), D.dp(11), D.dp(14), D.dp(11));
        row.setBackground(D.ripple(D.roundStroke(D.surfaceC, 16, D.outlineVar, 1), D.rippleColor));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, D.dp(8));
        row.setLayoutParams(p);

        ImageView iv = Ui.icon(c, R.drawable.ic_calendar, 18, tc[1]);
        iv.setBackground(D.round(tc[0], 12));
        iv.setPadding(D.dp(9), D.dp(9), D.dp(9), D.dp(9));
        row.addView(iv);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).rightMargin = D.dp(11);

        LinearLayout tv = Ui.v(c);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tv.addView(Ui.txt(c, e.title, 13f, D.onSurface, 600));
        TextView sub = Ui.txt(c, Bn.fmtDateShort(e.date) + " • " + typeLabel(e.type), 11f, D.onSurfaceVar, 400);
        sub.setPadding(0, D.dp(2), 0, 0);
        tv.addView(sub);
        row.addView(tv);

        row.addView(Ui.pill(c, "সম্পন্ন", 3));
        return row;
    }

    /* ── নতুন ইভেন্ট শিট ──────────────────────────────────── */
    static void addSheet(final Context c, final LinearLayout root) {
        Ui.sheet(c, "নতুন ইভেন্ট যোগ করুন", R.drawable.ic_calendar, true, new Ui.SheetCallback() {
            public void onSheet(LinearLayout body, final Runnable close) {
                final Ui.Field fTitle = Ui.field(c, "ইভেন্টের নাম *", Ui.F_TEXT, "", "যেমন: স্বেচ্ছায় রক্তদান ক্যাম্প");
                final Ui.Field fDate = Ui.field(c, "তারিখ *", Ui.F_DATE, null, null);
                final Ui.Field fTime = Ui.field(c, "সময়", Ui.F_TEXT, "", "যেমন: সকাল ৯টা – দুপুর ২টা");
                final Ui.Field fVenue = Ui.field(c, "স্থান *", Ui.F_TEXT, "", "যেমন: বাগাতিপাড়া ইউনিয়ন পরিষদ মাঠ");
                final Ui.Field fDetails = Ui.field(c, "বিবরণ", Ui.F_AREA, "", "কী কী হবে, কারা যোগ দিবে…");

                body.addView(fTitle.root);
                body.addView(fDate.root);
                body.addView(fTime.root);
                body.addView(fVenue.root);
                body.addView(fDetails.root);

                /* ইভেন্টের ধরন */
                final String[] types = {"camp", "meeting", "awareness", "social"};
                LinearLayout typeRow = Ui.h(c);
                typeRow.setPadding(D.dp(18), D.dp(8), D.dp(18), D.dp(4));
                for (int i = 0; i < types.length; i++) {
                    final String t = types[i];
                    TextView tp = Ui.txt(c, typeLabel(t), 12f, D.onSurfaceVar, 600);
                    tp.setGravity(Gravity.CENTER);
                    tp.setPadding(D.dp(10), D.dp(8), D.dp(10), D.dp(9));
                    tp.setBackground(D.ripple(D.roundStroke(D.surfaceC, 50, D.outlineVar, 1.2f), D.rippleColor));
                    tp.setOnClickListener(new View.OnClickListener() {
                        boolean on = false;
                        public void onClick(View v) {
                            Ui.host.haptic(6);
                            on = true;
                            typeSel[0] = t;
                            ViewGroup par = (ViewGroup) v.getParent();
                            for (int j = 0; j < par.getChildCount(); j++) {
                                TextView ch = (TextView) par.getChildAt(j);
                                boolean sel = ch == v;
                                ch.setTextColor(sel ? 0xFFFFFFFF : D.onSurfaceVar);
                                ch.setBackground(sel
                                        ? D.ripple(D.gradAngle(D.redGrad, 50, 315), D.rippleColorLight)
                                        : D.ripple(D.roundStroke(D.surfaceC, 50, D.outlineVar, 1.2f), D.rippleColor));
                            }
                        }
                    });
                    typeRow.addView(tp);
                    LinearLayout.LayoutParams tlp = (LinearLayout.LayoutParams) tp.getLayoutParams();
                    tlp.weight = 1f; tlp.width = 0;
                    tlp.rightMargin = D.dp(6);
                    tp.setLayoutParams(tlp);
                }
                body.addView(typeRow);

                LinearLayout btnWrap = Ui.v(c);
                btnWrap.setPadding(D.dp(18), D.dp(12), D.dp(18), 0);
                TextView save = Ui.btn(c, "ইভেন্ট যোগ করুন", Ui.BTN_GRAD, new Runnable() {
                    public void run() {
                        if (!Ui.validate(new Ui.Field[]{fTitle, fDate, fVenue})) return;
                        Data.Event e = new Data.Event();
                        e.id = Bn.uid("ev");
                        e.title = fTitle.input.getText().toString().trim();
                        e.type = typeSel[0] == null ? "social" : typeSel[0];
                        e.date = Bn.parseYmd((String) fDate.input.getTag());
                        e.timeText = fTime.input.getText().toString().trim();
                        e.venue = fVenue.input.getText().toString().trim();
                        e.details = fDetails.input.getText().toString().trim();
                        e.rsvpBase = 0; e.mine = false;
                        e.createdAt = System.currentTimeMillis();
                        e.createdBy = Data.s.session == null ? "" : Data.s.session;
                        Data.s.events.add(e);
                        Data.pushNotif("all", "sys", "নতুন ইভেন্ট: " + e.title,
                                Bn.fmtDate(e.date) + " — " + e.venue + "। বিস্তারিত ইভেন্ট সেকশনে দেখুন।");
                        Data.save();
                        close.run();
                        Ui.toast("ইভেন্ট যোগ হয়েছে", false);
                        Ui.host.refresh();
                    }
                });
                save.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(50)));
                btnWrap.addView(save);
                body.addView(btnWrap);
            }

            final String[] typeSel = {null};
        });
    }
}
