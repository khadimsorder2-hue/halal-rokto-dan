package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/** ডোনার ডিরেক্টরি — সার্চ, গ্রুপ ফিল্টার, বিস্তারিত, নতুন ডোনার। */
public final class ScrDonors {

    private ScrDonors() {}

    static String query = "";
    static String groupFilter = "";

    public static View build(final Context c) {
        final ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        final LinearLayout root = Ui.v(c);

        root.addView(Ui.heroBar(c, "ডোনার খুঁজুন", "রক্তের গ্রুপ ও এলাকা অনুযায়ী", true, null, new Runnable() {
            public void run() { Ui.host.back(); }
        }));

        /* search box */
        LinearLayout searchWrap = Ui.h(c);
        searchWrap.setPadding(D.dp(16), D.dp(14), D.dp(16), 0);
        EditText q = new EditText(c);
        q.setTypeface(D.tfRegular);
        q.setTextSize(13.5f);
        q.setTextColor(D.onSurface);
        q.setHintTextColor(D.outline);
        q.setHint("নাম, এলাকা বা গ্রুপ দিয়ে খুঁজুন...");
        q.setBackground(D.roundStroke(D.surfaceCLo, 14, D.outlineVar, 1.2f));
        q.setPadding(D.dp(14), D.dp(11), D.dp(14), D.dp(12));
        q.setSingleLine(true);
        q.setText(query);
        searchWrap.addView(q);
        root.addView(searchWrap);

        /* group filter chips (horizontally scrollable) */
        android.widget.HorizontalScrollView chipScroll = new android.widget.HorizontalScrollView(c);
        chipScroll.setHorizontalScrollBarEnabled(false);
        chipScroll.setPadding(D.dp(16), D.dp(10), D.dp(16), 0);
        chipScroll.setFillViewport(false);
        LinearLayout chips = Ui.h(c);
        chips.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView allChip = filterChip(c, "সব", groupFilter.isEmpty(), new Runnable() {
            public void run() { groupFilter = ""; Ui.host.refresh(); }
        });
        chips.addView(allChip);
        ((LinearLayout.LayoutParams) allChip.getLayoutParams()).rightMargin = D.dp(7);
        for (final String g : Data.BLOOD_GROUPS) {
            TextView chip = filterChip(c, g, groupFilter.equals(g), new Runnable() {
                public void run() { groupFilter = g; Ui.host.refresh(); }
            });
            chips.addView(chip);
            ((LinearLayout.LayoutParams) chip.getLayoutParams()).rightMargin = D.dp(7);
        }
        chipScroll.addView(chips);
        root.addView(chipScroll);

        /* list */
        final LinearLayout listWrap = Ui.v(c);
        listWrap.setPadding(D.dp(16), D.dp(12), D.dp(16), 0);
        root.addView(listWrap);

        final Runnable[] render = new Runnable[1];
        render[0] = new Runnable() {
            public void run() {
                listWrap.removeAllViews();
                List<Data.User> users = filtered();
                if (users.isEmpty()) {
                    listWrap.addView(Ui.emptyState(c, R.drawable.ic_search,
                            "কোনো ডোনার পাওয়া যায়নি",
                            "অন্য নাম, এলাকা বা গ্রুপ দিয়ে খুঁজুন অথবা নতুন ডোনার যোগ করুন।"));
                } else {
                    for (final Data.User u : users) listWrap.addView(donorRow(c, u));
                }
            }
        };
        render[0].run();

        // note: live search re-render on typing
        q.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int d) {}
            public void onTextChanged(CharSequence s, int a, int b, int d) {}
            public void afterTextChanged(Editable s) { query = s.toString(); render[0].run(); }
        });

        // FAB
        FrameLayout fabWrap = new FrameLayout(c);
        fabWrap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(84)));
        TextView fab = Ui.fab(c, "+  নতুন ডোনার", new Runnable() {
            public void run() { addDonorSheet(c, render); }
        });
        fab.setLayoutParams(Ui.flp(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.END, 16));
        fabWrap.addView(fab);
        root.addView(fabWrap);

        sc.addView(root);
        return sc;
    }

    static List<Data.User> filtered() {
        List<Data.User> out = new ArrayList<>();
        String qq = query.trim().toLowerCase();
        for (Data.User u : Data.visibleUsers()) {
            if (!groupFilter.isEmpty() && !u.bloodType.equals(groupFilter)) continue;
            if (!qq.isEmpty() && !(u.name.toLowerCase().contains(qq)
                    || u.address.toLowerCase().contains(qq)
                    || u.bloodType.toLowerCase().contains(qq))) continue;
            out.add(u);
        }
        return out;
    }

    static TextView filterChip(Context c, String label, boolean active, final Runnable action) {
        TextView t = new TextView(c);
        t.setText(label);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(active ? D.tfBold : D.tfMedium);
        t.setTextSize(12);
        t.setMinHeight(D.dp(34));
        t.setPadding(D.dp(14), 0, D.dp(14), D.dp(1));
        t.setTextColor(active ? 0xFFFFFFFF : D.onSurfaceVar);
        t.setBackground(active
                ? D.ripple(D.gradAngle(D.redGrad, 50, 315), D.rippleColorLight)
                : D.ripple(D.roundStroke(D.surfaceC, 50, D.outlineVar, 1), D.rippleColor));
        t.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.haptic(6); action.run(); }
        });
        return t;
    }

    static LinearLayout donorRow(final Context c, final Data.User u) {
        Data.Cooldown cd = Data.cooldown(u);
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(D.dp(12), D.dp(11), D.dp(12), D.dp(11));
        row.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 18, D.outlineVar, 1), D.rippleColor));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = D.dp(9);
        row.setLayoutParams(p);

        row.addView(Ui.avatar(c, Bn.initials(u.name), D.avatarColor(u.id), 44, 16));
        ((LinearLayout.LayoutParams) row.getChildAt(0).getLayoutParams()).rightMargin = D.dp(12);

        LinearLayout m = Ui.v(c);
        m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        LinearLayout nameRow = Ui.h(c);
        nameRow.setGravity(Gravity.CENTER_VERTICAL);
        nameRow.addView(Ui.txt(c, u.name, 13.5f, D.onSurface, 700));
        if (u.verified) {
            ImageView vi = Ui.icon(c, R.drawable.ic_verified, 14, 0xFF1E6B33);
            nameRow.addView(vi);
            ((LinearLayout.LayoutParams) vi.getLayoutParams()).leftMargin = D.dp(5);
        }
        m.addView(nameRow);
        TextView addr = Ui.txt(c, u.address, 11f, D.onSurfaceVar, 400);
        addr.setPadding(0, D.dp(2), 0, 0);
        m.addView(addr);
        LinearLayout stats = Ui.h(c);
        stats.setPadding(0, D.dp(4), 0, 0);
        stats.addView(Ui.txt(c, "🩸 " + Bn.bn(u.donationCount) + " বার", 10.5f, D.onSurfaceVar, 500));
        TextView b = Ui.txt(c, "  •  " + Data.badge(u)[1], 10.5f, D.onSurfaceVar, 500);
        stats.addView(b);
        m.addView(stats);
        row.addView(m);

        LinearLayout right = Ui.v(c);
        right.setGravity(Gravity.END);
        right.addView(Ui.bgroup(c, u.bloodType, !cd.eligible, 12));
        TextView st = Ui.txt(c, cd.eligible ? "দিতে পারেন" : Bn.bn(cd.days) + " দিন", 9.5f, cd.eligible ? D.success : D.onSurfaceVar, 600);
        st.setPadding(0, D.dp(5), 0, 0);
        right.addView(st);
        row.addView(right);
        ((LinearLayout.LayoutParams) right.getLayoutParams()).rightMargin = D.dp(10);

        ImageView call = Ui.icon(c, R.drawable.ic_phone, 16, 0xFFFFFFFF);
        call.setBackground(D.ripple(D.gradAngle(new int[]{0xFF2E9E4F, 0xFF1E6B33}, 50, 315), D.rippleColorLight));
        call.setPadding(D.dp(9), D.dp(9), D.dp(9), D.dp(9));
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.haptic(12); Ui.host.dial(u.phone); }
        });
        row.addView(call);

        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.haptic(6); donorSheet(c, u); }
        });
        return row;
    }

    public static void donorSheet(final Context c, final Data.User u) {
        Ui.sheet(c, "", 0, true, new Ui.SheetCallback() {
            public void onSheet(final LinearLayout body, final Runnable close) {
                Data.Cooldown cd = Data.cooldown(u);
                int rank = 0;
                List<Data.User> ranking = Data.ranking();
                for (int i = 0; i < ranking.size(); i++)
                    if (ranking.get(i).id.equals(u.id)) { rank = i + 1; break; }

                LinearLayout head = Ui.h(c);
                head.setPadding(D.dp(20), D.dp(10), D.dp(20), 0);
                head.addView(Ui.avatar(c, Bn.initials(u.name), D.avatarColor(u.id), 62, 23));
                ((LinearLayout.LayoutParams) head.getChildAt(0).getLayoutParams()).rightMargin = D.dp(14);
                LinearLayout hw = Ui.v(c);
                hw.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                LinearLayout nameRow = Ui.h(c);
                nameRow.setGravity(Gravity.CENTER_VERTICAL);
                nameRow.addView(Ui.txt(c, u.name, 18, D.onSurface, 700));
                if (u.verified) {
                    ImageView vi = Ui.icon(c, R.drawable.ic_verified, 17, 0xFF1E6B33);
                    nameRow.addView(vi);
                    ((LinearLayout.LayoutParams) vi.getLayoutParams()).leftMargin = D.dp(6);
                }
                hw.addView(nameRow);
                LinearLayout badges = Ui.h(c);
                badges.setPadding(0, D.dp(8), 0, 0);
                badges.addView(Ui.bgroup(c, u.bloodType, true, 12));
                ((LinearLayout.LayoutParams) badges.getChildAt(0).getLayoutParams()).rightMargin = D.dp(8);
                badges.addView(Ui.rankBadge(c, Data.badge(u)[0]));
                hw.addView(badges);
                head.addView(hw);
                body.addView(head);

                LinearLayout rows = Ui.v(c);
                rows.setPadding(D.dp(16), D.dp(16), D.dp(16), 0);
                rows.addView(detailRow(c, R.drawable.ic_phone, "ফোন নম্বর", u.phone));
                rows.addView(detailRow(c, R.drawable.ic_location, "ঠিকানা", u.address));
                rows.addView(detailRow(c, R.drawable.ic_drop, "মোট রক্তদান", Bn.bn(u.donationCount) + " বার"));
                rows.addView(detailRow(c, R.drawable.ic_clock, "শেষ রক্তদান",
                        u.lastDonationDate == 0 ? "এখনো দেননি" : Bn.fmtDate(u.lastDonationDate)));
                rows.addView(detailRow(c, R.drawable.ic_medal, "র‌্যাংকিং", Bn.bn(rank) + " নং"));
                if (u.createdAt > 0) rows.addView(detailRow(c, R.drawable.ic_calendar, "সদস্য হয়েছেন", Bn.fmtDate(u.createdAt)));
                body.addView(rows);

                // eligibility banner
                LinearLayout ban = Ui.cardPad(c);
                LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                bp.setMargins(D.dp(16), D.dp(14), D.dp(16), 0);
                ban.setLayoutParams(bp);
                if (cd.first) {
                    ban.setBackground(D.round(D.surfaceC, 16));
                    ban.addView(Ui.txt(c, "এই ডোনার এখনো কোনো রক্ত দেননি", 13.5f, D.onSurface, 700));
                } else if (cd.eligible) {
                    ban.setBackground(D.round(D.successC, 16));
                    ban.addView(Ui.txt(c, "✓ এই ডোনার রক্ত দিতে পারেন", 13.5f, D.onSuccessC, 700));
                    TextView s = Ui.txt(c, "শেষ দান " + Bn.bn(cd.lastDiff) + " দিন আগে", 11.5f, D.onSuccessC, 500);
                    s.setPadding(0, D.dp(2), 0, 0);
                    ban.addView(s);
                } else {
                    ban.setBackground(D.round(D.surfaceC, 16));
                    ban.addView(Ui.txt(c, "⏳ " + Bn.bn(cd.days) + " দিন পর রক্ত দিতে পারবেন", 13.5f, D.onSurface, 700));
                    TextView s = Ui.txt(c, "শেষ দান: " + Bn.fmtDate(u.lastDonationDate), 11.5f, D.onSurfaceVar, 500);
                    s.setPadding(0, D.dp(2), 0, 0);
                    ban.addView(s);
                }
                body.addView(ban);

                // actions
                LinearLayout acts = Ui.h(c);
                acts.setPadding(D.dp(16), D.dp(14), D.dp(16), D.dp(4));
                TextView call = Ui.btn(c, "কল করুন", Ui.BTN_SUCCESS, new Runnable() {
                    public void run() { Ui.host.dial(u.phone); }
                });
                call.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(46), 1f));
                acts.addView(call);
                ((LinearLayout.LayoutParams) call.getLayoutParams()).rightMargin = D.dp(9);
                TextView share = Ui.btn(c, "↗", Ui.BTN_TONAL, new Runnable() {
                    public void run() {
                        Ui.host.share("🩸 রক্তদাতার তথ্য\nনাম: " + u.name + "\nগ্রুপ: " + u.bloodType
                                + "\nফোন: " + u.phone + "\nএলাকা: " + u.address + "\n— " + Data.ORG, "ডোনার তথ্য শেয়ার");
                    }
                });
                share.setLayoutParams(new LinearLayout.LayoutParams(D.dp(52), D.dp(46)));
                acts.addView(share);
                body.addView(acts);
            }
        });
    }

    static LinearLayout detailRow(Context c, int iconRes, String label, String value) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(D.dp(13), D.dp(11), D.dp(13), D.dp(11));
        row.setBackground(D.roundStroke(D.surfaceCLo, 14, D.outlineVar, 1));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = D.dp(7);
        row.setLayoutParams(p);
        ImageView iv = Ui.icon(c, iconRes, 16, D.onSurfaceVar);
        iv.setBackground(D.round(D.surfaceC, 11));
        iv.setPadding(D.dp(8), D.dp(8), D.dp(8), D.dp(8));
        row.addView(iv);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).rightMargin = D.dp(12);
        TextView l = Ui.txt(c, label, 12.5f, D.onSurfaceVar, 500);
        row.addView(l);
        View sp = new View(c);
        sp.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        row.addView(sp);
        TextView v = Ui.txt(c, value, 12.5f, D.onSurface, 700);
        row.addView(v);
        return row;
    }

    static void addDonorSheet(final Context c, final Runnable[] refresh) {
        Ui.sheet(c, "নতুন ডোনার যোগ করুন", R.drawable.ic_add, true, new Ui.SheetCallback() {
            public void onSheet(final LinearLayout body, final Runnable close) {
                TextView intro = Ui.txt(c, "পরিচিত কাউকে ডোনার হিসেবে যোগ করুন — পরে তিনি নিজে রেজিস্ট্রেশন করে অ্যাকাউন্ট নিতে পারবেন।", 12f, D.onSurfaceVar, 400);
                intro.setLineSpacing(0, 1.5f);
                intro.setPadding(D.dp(20), D.dp(8), D.dp(20), D.dp(4));
                body.addView(intro);

                final Ui.Field fName = Ui.field(c, "ডোনারের নাম *", Ui.F_TEXT, null, "পূর্ণ নাম লিখুন");
                final Ui.Field fPhone = Ui.field(c, "ফোন নম্বর *", Ui.F_PHONE, null, "01XXXXXXXXX");
                final AtomicReference<String> bgRef = new AtomicReference<>(null);
                LinearLayout bgGrid = Ui.bloodGroupGrid(c, null, bgRef);
                final Ui.Field fAddr = Ui.field(c, "ঠিকানা / এলাকা *", Ui.F_TEXT, null, "যেমন: হিজলি, বাগাতিপাড়া");
                body.addView(fName.root);
                body.addView(fPhone.root);
                body.addView(bgGrid);
                body.addView(fAddr.root);

                LinearLayout btnWrap = Ui.v(c);
                btnWrap.setPadding(D.dp(18), D.dp(14), D.dp(18), 0);
                TextView save = Ui.btn(c, "ডোনার যোগ করুন", Ui.BTN_GRAD, new Runnable() {
                    public void run() {
                        if (bgRef.get() == null) { Ui.toast("রক্তের গ্রুপ নির্বাচন করুন", true); return; }
                        if (!Ui.validate(new Ui.Field[]{fName, fPhone, fAddr})) return;
                        String phone = fPhone.input.getText().toString().trim();
                        for (Data.User x : Data.s.users)
                            if (x.phone.equals(phone)) { Ui.toast("এই ফোন নম্বর ইতিমধ্যে তালিকায় আছে", true); return; }
                        Data.User u = new Data.User();
                        u.id = Bn.uid("u");
                        u.name = fName.input.getText().toString().trim();
                        u.phone = phone;
                        u.bloodType = bgRef.get();
                        u.address = fAddr.input.getText().toString().trim();
                        u.email = ""; u.nid = ""; u.password = null;
                        u.donationCount = 0; u.trustScore = 40;
                        u.referralCode = "DJS-" + String.valueOf(100000 + Math.abs(Bn.hash(u.bloodType + System.currentTimeMillis())) % 900000).substring(0, 5);
                        u.referredBy = ""; u.referralCount = 0; u.verified = false;
                        u.lastDonationDate = 0; u.createdAt = System.currentTimeMillis();
                        Data.User me = Data.me();
                        u.addedBy = me != null ? me.name : "";
                        Data.s.users.add(u);
                        Data.save();
                        close.run();
                        Ui.toast("নতুন ডোনার যোগ হয়েছে", false);
                        refresh[0].run();
                    }
                });
                save.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(50)));
                btnWrap.addView(save);
                body.addView(btnWrap);
            }
        });
    }
}
