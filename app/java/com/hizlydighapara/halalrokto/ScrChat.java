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

/** চ্যাট তালিকা — গ্রুপ চ্যানেল + ব্যক্তিগত বার্তা + নতুন চ্যাট পিকার। */
public final class ScrChat {

    private ScrChat() {}

    public static View build(final Context c) {
        Chat.init(c);

        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);

        root.addView(Ui.heroBar(c, "চ্যাট", "সদস্যদের সাথে যোগাযোগ রাখুন", false, null, null));

        /* ── group channels ── */
        root.addView(sectionTitle(c, "গ্রুপ চ্যানেল"));
        LinearLayout groups = Ui.v(c);
        groups.setPadding(D.dp(16), 0, D.dp(16), 0);
        for (final Chat.Channel g : Chat.GROUPS)
            groups.addView(channelCard(c, g.icon, D.gradAngle(D.redGrad, 16, 315),
                    g.name, g.desc, Chat.lastOf(g.id), Chat.unreadOf(g.id), g.id, null));
        root.addView(groups);

        /* ── direct messages ── */
        LinearLayout dmHead = Ui.h(c);
        dmHead.setGravity(Gravity.CENTER_VERTICAL);
        dmHead.setPadding(D.dp(18), D.dp(18), D.dp(10), D.dp(8));
        TextView dmTitle = Ui.txt(c, "ব্যক্তিগত বার্তা", 14.5f, D.onSurface, 700);
        dmTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        dmHead.addView(dmTitle);
        dmHead.addView(Ui.btnSmall(c, "নতুন চ্যাট", Ui.BTN_TONAL, new Runnable() {
            public void run() { memberPicker(c); }
        }));
        root.addView(dmHead);

        LinearLayout dms = Ui.v(c);
        dms.setPadding(D.dp(16), 0, D.dp(16), 0);
        List<Data.User> partners = Chat.dmPartners();
        if (partners.isEmpty()) {
            LinearLayout empty = Ui.cardPad(c);
            LinearLayout er = Ui.h(c);
            ImageView ii = Ui.icon(c, R.drawable.ic_chat, 18, D.onSurfaceVar);
            er.addView(ii);
            ((LinearLayout.LayoutParams) ii.getLayoutParams()).rightMargin = D.dp(10);
            TextView t = Ui.txt(c, "কোনো ব্যক্তিগত বার্তা নেই — উপরের \"নতুন চ্যাট\" বোতামে ডোনারদের সাথে কথা শুরু করুন।", 12f, D.onSurfaceVar, 400);
            t.setLineSpacing(0, 1.45f);
            t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            er.addView(t);
            empty.addView(er);
            dms.addView(empty);
        } else {
            for (final Data.User u : partners) {
                Chat.Msg last = Chat.lastOf(Chat.dmId(u));
                dms.addView(channelCard(c, 0, D.round(D.avatarColor(u.id), 16),
                        u.name, u.bloodType + " • " + Data.badge(u)[1], last,
                        Chat.unreadOf(Chat.dmId(u)), Chat.dmId(u), u));
            }
        }
        root.addView(dms);

        View gap = new View(c);
        gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(20)));
        root.addView(gap);

        sc.addView(root);
        return sc;
    }

    static TextView sectionTitle(Context c, String s) {
        return Ui.sectionHeader(c, s);
    }

    /** চ্যাট তালিকার কার্ড — আইকন/অ্যাভাটার, নাম, শেষ মেসেজ, সময়, আনরিড ব্যাজ। */
    static LinearLayout channelCard(final Context c, int iconRes, Object iconBg,
                                    String title, String sub, Chat.Msg last, int unread,
                                    final String chId, final Data.User dmUser) {
        LinearLayout card = Ui.h(c);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(D.dp(12), D.dp(11), D.dp(12), D.dp(11));
        card.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 18, D.outlineVar, 1), D.rippleColor));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = D.dp(8);
        card.setLayoutParams(p);

        View lead;
        if (iconRes != 0) {
            lead = Ui.icon(c, iconRes, 21, 0xFFFFFFFF);
            lead.setPadding(D.dp(11), D.dp(11), D.dp(11), D.dp(11));
        } else {
            lead = Ui.avatar(c, Bn.initials(title), D.avatarColor(dmUser == null ? title : dmUser.id), 44, 16);
        }
        if (iconBg instanceof android.graphics.drawable.Drawable)
            lead.setBackground((android.graphics.drawable.Drawable) iconBg);
        card.addView(lead);
        ((LinearLayout.LayoutParams) lead.getLayoutParams()).rightMargin = D.dp(12);

        LinearLayout m = Ui.v(c);
        m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        m.addView(Ui.txt(c, title, 14, D.onSurface, 600));
        TextView s = Ui.txt(c, last != null ? last.text : sub, 11.5f, D.onSurfaceVar, 400);
        s.setSingleLine(true);
        s.setEllipsize(android.text.TextUtils.TruncateAt.END);
        s.setPadding(0, D.dp(2), 0, 0);
        m.addView(s);
        card.addView(m);

        LinearLayout right = Ui.v(c);
        right.setGravity(Gravity.END);
        if (last != null) {
            TextView t = Ui.txt(c, Bn.timeAgo(last.ts), 9.5f, D.outline, 500);
            right.addView(t);
        }
        if (unread > 0) {
            TextView b = Ui.txt(c, Bn.bn(unread), 10.5f, 0xFFFFFFFF, 700);
            b.setGravity(Gravity.CENTER);
            b.setPadding(D.dp(7), D.dp(2), D.dp(7), D.dp(3));
            b.setBackground(D.gradAngle(D.redGrad, 50, 315));
            b.setElevation(D.dp(2));
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bp.topMargin = D.dp(5);
            b.setLayoutParams(bp);
            right.addView(b);
        }
        card.addView(right);

        card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Ui.host.haptic(8);
                Ui.host.openChat(chId);
            }
        });
        return card;
    }

    /** নতুন ব্যক্তিগত চ্যাট — সদস্য বাছাই শিট। */
    static void memberPicker(final Context c) {
        Ui.sheet(c, "নতুন ব্যক্তিগত চ্যাট", R.drawable.ic_people, true, new Ui.SheetCallback() {
            public void onSheet(LinearLayout body, Runnable close) {
                List<Data.User> users = Data.visibleUsers();
                boolean any = false;
                for (final Data.User u : users) {
                    if (u.id.equals(Data.s.session)) continue;
                    any = true;
                    LinearLayout row = Ui.h(c);
                    row.setGravity(Gravity.CENTER_VERTICAL);
                    row.setPadding(D.dp(14), D.dp(11), D.dp(14), D.dp(11));
                    row.setBackground(D.ripple(D.roundStroke(D.surfaceC, 16, D.outlineVar, 1), D.rippleColor));
                    LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    rp.bottomMargin = D.dp(7);
                    row.setLayoutParams(rp);

                    row.addView(Ui.avatar(c, Bn.initials(u.name), D.avatarColor(u.id), 40, 15));
                    ((LinearLayout.LayoutParams) row.getChildAt(0).getLayoutParams()).rightMargin = D.dp(12);

                    LinearLayout m = Ui.v(c);
                    m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    m.addView(Ui.txt(c, u.name, 13.5f, D.onSurface, 600));
                    TextView s = Ui.txt(c, u.bloodType + " • " + Bn.bn(u.donationCount) + " বার দান" + (u.verified ? " • ভেরিফায়েড" : ""), 11f, D.onSurfaceVar, 400);
                    s.setPadding(0, D.dp(2), 0, 0);
                    m.addView(s);
                    row.addView(m);

                    row.addView(Ui.bgroup(c, u.bloodType, false, 11.5f));

                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Ui.host.haptic(8);
                            close.run();
                            Ui.host.openChat(Chat.dmId(u));
                        }
                    });
                    body.addView(row);
                }
                if (!any) {
                    LinearLayout e = Ui.emptyState(c, R.drawable.ic_people,
                            "কোনো ডোনার নেই", "ডোনার যোগ হলে এখানে তালিকায় দেখা যাবে।");
                    body.addView(e);
                }
                View pad = new View(c);
                pad.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(10)));
                body.addView(pad);
            }
        });
    }
}
