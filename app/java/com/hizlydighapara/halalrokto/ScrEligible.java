package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** রক্তদান যোগ্যতা চেক — ইন্টারঅ্যাক্টিভ কুইজ, তাৎক্ষণিক ফলাফল। */
public final class ScrEligible {

    private ScrEligible() {}

    static final class Q {
        final String text;
        final boolean yesOk;   // হ্যাঁ = যোগ্যতার পক্ষে
        final String failReason; // ভুল উত্তর দিলে যোগ্যতা-বাধা কারণ
        final boolean naOk;    // প্রযোজ্য-নয় অপশন আছে
        int ans = 0;           // 0=উত্তর নেই 1=হ্যাঁ 2=না 3=প্রযোজ্য নয়

        Q(String text, boolean yesOk, String failReason, boolean naOk) {
            this.text = text; this.yesOk = yesOk; this.failReason = failReason; this.naOk = naOk;
        }
    }

    public static View build(final Context c) {
        final ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        final LinearLayout root = Ui.v(c);
        root.addView(Ui.heroBar(c, "যোগ্যতা চেক", "রক্ত দিতে পারবেন কি না — ২ মিনিটে জেনে নিন", false, null, null));

        /* কুলডাউন অটো-চেক */
        final Data.User me = Data.me();
        final Data.Cooldown cd = Data.cooldown(me);
        LinearLayout cdCard = Ui.cardPad(c);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cp.setMargins(D.dp(16), D.dp(14), D.dp(16), 0);
        cdCard.setLayoutParams(cp);
        LinearLayout cdRow = Ui.h(c);
        ImageView ic = Ui.icon(c, R.drawable.ic_clock, 20, cd.eligible ? D.onSuccessC : D.onTertiaryC);
        ic.setBackground(D.round(cd.eligible ? D.successC : D.tertiaryC, 14));
        ic.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
        cdRow.addView(ic);
        ((LinearLayout.LayoutParams) ic.getLayoutParams()).rightMargin = D.dp(12);
        LinearLayout ct = Ui.v(c);
        ct.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        ct.addView(Ui.txt(c, cd.eligible ? "কুলডাউন: আপনি প্রস্তুত ✓" : "কুলডাউন চলছে", 13.5f, D.onSurface, 700));
        TextView cs = Ui.txt(c, cd.first
                ? "আপনার এখনো কোনো দানের রেকর্ড নেই — প্রশ্নের উত্তর দিয়ে যোগ্যতা দেখুন।"
                : cd.eligible ? "৯০ দিনের অপেক্ষা শেষ — শরীর সুস্থ থাকলে দিতে পারবেন।"
                : "আর " + Bn.bn(cd.days) + " দিন অপেক্ষা করতে হবে ("
                    + Bn.fmtDateShort(me.lastDonationDate + 90L * 86400000L) + ")।",
                11.5f, D.onSurfaceVar, 400);
        cs.setLineSpacing(0, 1.3f);
        cs.setPadding(0, D.dp(2), 0, 0);
        ct.addView(cs);
        cdRow.addView(ct);
        cdCard.addView(cdRow);
        root.addView(cdCard);

        /* ফলাফল হোস্ট + চেক বোতাম আগে ঘোষণা (ইনার-ক্লাস রেফারেন্সের ক্রম) */
        final LinearLayout resultHost = Ui.v(c);
        resultHost.setPadding(D.dp(16), 0, D.dp(16), 0);

        /* প্রশ্ন তৈরি */
        final List<Q> qs = new ArrayList<Q>();
        qs.add(new Q("আপনার বয়স কি ১৮–৬০ বছরের মধ্যে?", true, "বয়স ১৮–৬০ এর মধ্যে হতে হবে", false));
        qs.add(new Q("ওজন কি কমপক্ষে ৫০ কেজি (পুরুষ) / ৪৫ কেজি (মহিলা)?", true, "ন্যূনতম ওজন পূরণ হয়নি", false));
        qs.add(new Q("গত ২৪ ঘণ্টায় কি কোনো ওষুধ/অ্যান্টিবায়োটিক খেয়েছেন?", false, "ওষুধ খাওয়ার ২৪–৪৮ ঘণ্টা পর দিতে হবে", false));
        qs.add(new Q("গত ৬ মাসে কি অপারেশন, রক্ত গ্রহণ বা ট্যাটু করিয়েছেন?", false, "অপারেশন/রক্ত গ্রহণের ৬ মাস পর দিতে হবে", false));
        qs.add(new Q("আপনার কি হৃদরোগ, ডায়াবেটিস, উচ্চ রক্তচাপ বা যক্ষ্মা আছে?", false, "দীর্ঘমেয়াদি রোগ থাকলে দিতে পারবেন না", false));
        qs.add(new Q("আপনি কি গর্ভবতী বা স্তন্যদানকারী?", false, "গর্ভবতী/স্তন্যদানকারী মায়েরা দিতে পারবেন না", true));
        qs.add(new Q("আজ কি সম্পূর্ণ সুস্থ আছেন (জ্বর, ঠান্ডা, মাথা ঘোরা নেই)?", true, "অসুস্থ অবস্থায় রক্ত দেওয়া যাবে না", false));
        qs.add(new Q("রাতে কি ভালো ঘুম হয়েছে এবং হালকা খাবার/পানি খেয়েছেন?", true, "খালি পেটে বা ঘুম না হলে দেওয়া উচিত নয়", false));

        /* চেক বোতাম */
        final TextView checkBtn = Ui.btn(c, "ফলাফল দেখুন", Ui.BTN_GRAD, new Runnable() {
            public void run() {
                for (Q q : qs) if (q.ans == 0) { Ui.toast("সব প্রশ্নের উত্তর দিন", true); return; }
                resultHost.removeAllViews();
                resultHost.addView(verdict(c, qs, cd));
                resultHost.post(new Runnable() {
                    public void run() { resultHost.requestRectangleOnScreen( // স্ক্রল করে ফলাফলে
                            new android.graphics.Rect(0, 0, resultHost.getWidth(), resultHost.getHeight())); }
                });
                Ui.host.haptic(16);
            }
        });
        checkBtn.setEnabled(false);
        checkBtn.setTextColor(D.withAlpha(0xFFFFFFFF, 160));
        checkBtn.setBackground(D.round(D.surfaceCH, 50));

        /* প্রশ্নের তালিকা */
        root.addView(Ui.sectionHeader(c, "প্রশ্ন (" + Bn.bn(qs.size()) + "টি)"));
        LinearLayout list = Ui.v(c);
        list.setPadding(D.dp(16), 0, D.dp(16), 0);

        for (int i = 0; i < qs.size(); i++) {
            final Q q = qs.get(i);
            LinearLayout card = Ui.card(c);
            card.setPadding(D.dp(14), D.dp(13), D.dp(14), D.dp(12));
            LinearLayout.LayoutParams pcl = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pcl.setMargins(0, 0, 0, D.dp(10));
            card.setLayoutParams(pcl);

            LinearLayout qRow = Ui.h(c);
            TextView num = Ui.txt(c, Bn.bn(i + 1), 11f, 0xFFFFFFFF, 700);
            num.setGravity(Gravity.CENTER);
            num.setBackground(D.round(D.primary, 50));
            num.setPadding(D.dp(8), D.dp(3), D.dp(8), D.dp(4));
            qRow.addView(num);
            ((LinearLayout.LayoutParams) num.getLayoutParams()).rightMargin = D.dp(10);
            TextView qt = Ui.txt(c, q.text, 13f, D.onSurface, 600);
            qt.setLineSpacing(0, 1.25f);
            qt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            qRow.addView(qt);
            card.addView(qRow);

            LinearLayout aRow = Ui.h(c);
            aRow.setPadding(0, D.dp(11), 0, 0);
            final TextView[] btns = new TextView[q.naOk ? 3 : 2];

            View.OnClickListener l = new View.OnClickListener() {
                public void onClick(View v) {
                    Ui.host.haptic(6);
                    for (int j = 0; j < btns.length; j++) {
                        boolean sel = btns[j] == v;
                        btns[j].setTextColor(sel ? 0xFFFFFFFF : D.onSurfaceVar);
                        btns[j].setBackground(sel
                                ? D.ripple(D.gradAngle(D.redGrad, 50, 315), D.rippleColorLight)
                                : D.ripple(D.roundStroke(D.surfaceC, 50, D.outlineVar, 1.2f), D.rippleColor));
                    }
                    q.ans = v.getId();
                    if (!checkBtn.isEnabled()) {
                        checkBtn.setEnabled(true);
                        checkBtn.setTextColor(0xFFFFFFFF);
                        checkBtn.setBackground(D.ripple(D.gradAngle(D.redGrad, 50, 315), D.rippleColorLight));
                    }
                }
            };

            String[] labels = q.naOk ? new String[]{"হ্যাঁ", "না", "প্রযোজ্য নয়"} : new String[]{"হ্যাঁ", "না"};
            for (int j = 0; j < labels.length; j++) {
                TextView b = Ui.txt(c, labels[j], 12f, D.onSurfaceVar, 600);
                b.setGravity(Gravity.CENTER);
                b.setId(j + 1);
                b.setMinHeight(D.dp(36));
                b.setBackground(D.ripple(D.roundStroke(D.surfaceC, 50, D.outlineVar, 1.2f), D.rippleColor));
                LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                blp.setMargins(0, 0, j < labels.length - 1 ? D.dp(8) : 0, 0);
                b.setLayoutParams(blp);
                b.setOnClickListener(l);
                btns[j] = b;
                aRow.addView(b);
            }
            card.addView(aRow);
            list.addView(card);
        }
        root.addView(list);

        LinearLayout btnWrap = Ui.v(c);
        btnWrap.setPadding(D.dp(16), D.dp(6), D.dp(16), D.dp(4));
        checkBtn.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(50)));
        btnWrap.addView(checkBtn);
        root.addView(btnWrap);

        root.addView(resultHost);

        View gap = new View(c);
        gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(24)));
        root.addView(gap);

        sc.addView(root);
        return sc;
    }

    static LinearLayout verdict(Context c, List<Q> qs, Data.Cooldown cd) {
        List<String> reasons = new ArrayList<String>();
        for (Q q : qs) {
            if (q.ans == 1 && !q.yesOk) reasons.add(q.failReason);
            if (q.ans == 2 && q.yesOk) reasons.add(q.failReason);
        }
        if (!cd.eligible) reasons.add("শেষ দানের পর ৯০ দিন পূর্ণ হয়নি — আর " + Bn.bn(cd.days) + " দিন");

        final boolean ok = reasons.isEmpty();

        LinearLayout card = Ui.v(c);
        card.setPadding(D.dp(18), D.dp(18), D.dp(18), D.dp(18));
        card.setBackground(D.gradAngle(ok ? new int[]{0xFF2E7D32, 0xFF1E6B33, 0xFF0F5124} : D.heroGrad, 22, 315));
        card.setElevation(D.dp(5));

        TextView emoji = Ui.txt(c, ok ? "🎉" : "⚠️", 34, 0xFFFFFFFF, 700);
        emoji.setGravity(Gravity.CENTER);
        card.addView(emoji);
        TextView title = Ui.txt(c, ok ? "অভিনন্দন — আপনি যোগ্য!" : "এখন রক্ত দেওয়া যাবে না", 18, 0xFFFFFFFF, 700);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, D.dp(8), 0, 0);
        card.addView(title);
        TextView sub = Ui.txt(c, ok
                ? "আপনি সুস্থ শরীরে নিরাপদে রক্ত দিতে পারবেন। কারও জীবন বাঁচান — আজই সিদ্ধান্ত নিন।"
                : "নিচের কারণগুলো ঠিক হলে আপনি যোগ্য হয়ে যাবেন।", 12f, D.withAlpha(0xFFFFFFFF, 228), 500);
        sub.setGravity(Gravity.CENTER);
        sub.setLineSpacing(0, 1.35f);
        sub.setPadding(D.dp(6), D.dp(6), D.dp(6), 0);
        card.addView(sub);

        if (!ok) {
            LinearLayout rs = Ui.v(c);
            rs.setPadding(0, D.dp(12), 0, 0);
            for (String r : reasons) {
                LinearLayout rr = Ui.h(c);
                rr.setGravity(Gravity.CENTER_VERTICAL);
                rr.setPadding(D.dp(12), D.dp(9), D.dp(12), D.dp(10));
                rr.setBackground(D.round(D.withAlpha(0xFFFFFFFF, D.dark ? 32 : 58), 12));
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlp.bottomMargin = D.dp(7);
                rr.setLayoutParams(rlp);
                TextView dot = Ui.txt(c, "•", 14, 0xFFFFFFFF, 700);
                rr.addView(dot);
                ((LinearLayout.LayoutParams) dot.getLayoutParams()).rightMargin = D.dp(9);
                TextView rt = Ui.txt(c, r, 12f, 0xFFFFFFFF, 500);
                rt.setLineSpacing(0, 1.3f);
                rt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                rr.addView(rt);
                rs.addView(rr);
            }
            card.addView(rs);
        }

        LinearLayout acts = Ui.h(c);
        acts.setGravity(Gravity.CENTER);
        acts.setPadding(0, D.dp(14), 0, 0);
        if (ok) {
            acts.addView(whiteBtn(c, "জরুরি ডোনার খুঁজুন", new Runnable() {
                public void run() { Ui.host.go("emergency", false); }
            }));
            acts.addView(whiteBtn(c, "দান রেকর্ড করুন", new Runnable() {
                public void run() { Ui.host.go("history", false); }
            }));
        } else {
            acts.addView(whiteBtn(c, "নির্দেশনা দেখুন", new Runnable() {
                public void run() { Ui.host.go("guide", false); }
            }));
        }
        card.addView(acts);
        return card;
    }

    static TextView whiteBtn(Context c, String label, final Runnable action) {
        TextView t = Ui.txt(c, label, 12.5f, 0xFFFFFFFF, 700);
        t.setGravity(Gravity.CENTER);
        t.setMinHeight(D.dp(38));
        t.setPadding(D.dp(16), 0, D.dp(16), D.dp(1));
        t.setBackground(D.ripple(D.round(D.withAlpha(0xFFFFFFFF, D.dark ? 36 : 66), 50), D.rippleColorLight));
        t.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.haptic(8); action.run(); }
        });
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, D.dp(38));
        p.rightMargin = D.dp(8);
        t.setLayoutParams(p);
        return t;
    }
}
