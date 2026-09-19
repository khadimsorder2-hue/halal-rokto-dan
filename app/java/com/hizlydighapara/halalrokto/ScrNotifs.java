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

/** নোটিফিকেশন — তালিকা + সব পড়া। */
public final class ScrNotifs {

    private ScrNotifs() {}

    public static View build(final Context c) {
        final ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        final LinearLayout root = Ui.v(c);

        final Runnable[] render = new Runnable[1];
        render[0] = new Runnable() {
            public void run() {
                root.removeAllViews();
                List<Data.Notif> list = Data.myNotifs();
                boolean anyUnread = false;
                for (Data.Notif n : list) if (!n.read) { anyUnread = true; break; }

                LinearLayout bar = Ui.heroBar(c, "নোটিফিকেশন", "রক্ত দান সম্পর্কিত সকল আপডেট", true, null, new Runnable() {
                    public void run() { Ui.host.back(); }
                });
                if (anyUnread) {
                    TextView readAll = Ui.btnSmall(c, "সব পড়া হয়েছে", -1, new Runnable() {
                        public void run() {
                            for (Data.Notif n : Data.s.notifications)
                                if ("all".equals(n.userId) || (Data.s.session != null && n.userId.equals(Data.s.session))) n.read = true;
                            Data.save();
                            render[0].run();
                        }
                    });
                    readAll.setTextColor(0xFFFFFFFF);
                    readAll.setBackground(D.ripple(D.round(D.withAlpha(0xFFFFFFFF, 46), 50), D.rippleColorLight));
                    bar.addView(readAll);
                }
                root.addView(bar);

                if (list.isEmpty()) {
                    root.addView(Ui.emptyState(c, R.drawable.ic_bell,
                            "কোনো নোটিফিকেশন নেই",
                            "নতুন জরুরি আবেদন বা রক্তদানের আপডেট এখানে দেখা যাবে।"));
                } else {
                    LinearLayout wrap = Ui.v(c);
                    wrap.setPadding(D.dp(16), D.dp(16), D.dp(16), 0);
                    for (Data.Notif n : list) wrap.addView(notifRow(c, n));
                    root.addView(wrap);
                    View gap = new View(c);
                    gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(18)));
                    root.addView(gap);
                }
            }
        };
        render[0].run();
        sc.addView(root);
        return sc;
    }

    static LinearLayout notifRow(Context c, Data.Notif n) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.TOP);
        row.setPadding(D.dp(13), D.dp(12), D.dp(13), D.dp(12));
        row.setBackground(D.roundStroke(n.read ? D.surfaceCLo : D.primaryC, 16,
                n.read ? D.outlineVar : D.withAlpha(D.primary, 90), 1));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = D.dp(8);
        row.setLayoutParams(p);

        int iconRes = n.type.equals("emg") ? R.drawable.ic_sos : n.type.equals("don") ? R.drawable.ic_dropfill
                : n.type.equals("rank") ? R.drawable.ic_medal : R.drawable.ic_info;
        int iconBg = n.type.equals("emg") ? D.errorC : n.type.equals("don") ? D.primaryC
                : n.type.equals("rank") ? D.tertiaryC : D.surfaceCH;
        int iconFg = n.type.equals("emg") ? D.onErrorC : n.type.equals("don") ? D.onPrimaryC
                : n.type.equals("rank") ? D.onTertiaryC : D.onSurfaceVar;
        ImageView iv = Ui.icon(c, iconRes, 19, iconFg);
        iv.setBackground(D.round(iconBg, 13));
        iv.setPadding(D.dp(9), D.dp(9), D.dp(9), D.dp(9));
        row.addView(iv);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).rightMargin = D.dp(12);

        LinearLayout m = Ui.v(c);
        m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        LinearLayout titleRow = Ui.h(c);
        TextView t = Ui.txt(c, n.title, 13, D.onSurface, n.read ? 600 : 700);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        titleRow.addView(t);
        TextView time = Ui.txt(c, Bn.timeAgo(n.date), 9.5f, D.outline, 500);
        time.setPadding(D.dp(8), D.dp(2), 0, 0);
        titleRow.addView(time);
        m.addView(titleRow);
        TextView body = Ui.txt(c, n.body, 11.5f, D.onSurfaceVar, 400);
        body.setLineSpacing(0, 1.45f);
        body.setPadding(0, D.dp(3), 0, 0);
        m.addView(body);
        row.addView(m);
        return row;
    }
}
