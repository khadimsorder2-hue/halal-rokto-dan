package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** সংগঠন সম্পর্কে। */
public final class ScrAbout {

    private ScrAbout() {}

    public static View build(final Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);

        root.addView(Ui.appBar(c, "সংগঠন সম্পর্কে", null, new Runnable() {
            public void run() { Ui.host.back(); }
        }));

        /* hero */
        LinearLayout hero = Ui.v(c);
        hero.setGravity(Gravity.CENTER_HORIZONTAL);
        hero.setPadding(D.dp(24), D.dp(30), D.dp(24), D.dp(28));
        hero.setBackground(D.gradAngle(D.heroGrad, 0, 315));
        root.addView(hero);

        ImageView logo = Ui.icon(c, R.drawable.ic_dropfill, 42, 0xFFFFFFFF);
        LinearLayout logoWrap = new LinearLayout(c);
        logoWrap.setGravity(Gravity.CENTER);
        logoWrap.setLayoutParams(Ui.lp(D.dp(78), D.dp(78)));
        logoWrap.setBackground(D.gradAngle(D.redGrad, 24, 315));
        logoWrap.setElevation(D.dp(6));
        logoWrap.addView(logo);
        hero.addView(logoWrap);

        TextView h1 = Ui.txt(c, Data.ORG, 20, 0xFFFFFFFF, 700);
        h1.setGravity(Gravity.CENTER);
        h1.setPadding(0, D.dp(14), 0, 0);
        hero.addView(h1);
        TextView h2 = Ui.txt(c, Data.ORG_EN + " — " + Data.APP_NAME, 11.5f, D.withAlpha(0xFFFFFFFF, 215), 600);
        h2.setGravity(Gravity.CENTER);
        h2.setPadding(0, D.dp(4), 0, 0);
        hero.addView(h2);
        TextView h3 = Ui.txt(c, "বাগাতিপাড়া, নাটোর • " + Data.SINCE, 11f, D.withAlpha(0xFFFFFFFF, 200), 500);
        h3.setGravity(Gravity.CENTER);
        h3.setPadding(0, D.dp(5), 0, 0);
        hero.addView(h3);

        /* sections */
        root.addView(section(c, R.drawable.ic_info, "আমাদের কথা",
                Data.ORG + " বাগাতিপাড়া, নাটোরের তরুণ সমাজের একটি স্বেচ্ছাসেবী সংগঠন। ২০২৬ সাল থেকে সংগঠনটি এলাকার মানুষের সেবায় নিয়োজিত। হালাল রক্ত দান অ্যাপের মাধ্যমে আমরা রক্তদাতাদের একটি সুসংগঠিত ডেটাবেস গড়ে তুলছি — যাতে জরুরি মুহূর্তে কেউ রক্তের জন্য দরদর না ঘুরে বেড়ায়।"));
        root.addView(section(c, R.drawable.ic_dropfill, "আমাদের লক্ষ্য",
                "এলাকার প্রতিটি যোগ্য ব্যক্তিকে নিয়মিত রক্তদাতা হিসেবে সংগঠিত করা, রোগী ও দাতার মধ্যে দ্রুত যোগাযোগ নিশ্চিত করা এবং হালাল ও নিরাপদ পথে রক্ত সংগ্রহ ও বিতরণ ব্যবস্থাপনা করা। প্রতিটি ব্যাগ রক্ত বাঁচায় তিনটি প্রাণ — আমাদের এই বিশ্বাস নিয়েই আমরা কাজ করি।"));
        root.addView(section(c, R.drawable.ic_hosp, "রক্তদানের নিয়ম",
                "১৮-৬০ বছর বয়সী সুস্থ প্রাপ্তবয়স্করা রক্ত দিতে পারেন। দুইবার রক্তদানের মধ্যে কমপক্ষে ৯০ দিন (৩ মাস) বিরতি রাখতে হয়। রক্ত দেওার আগে হালকা খাবার খাওয়া জরুরি; খালি পাকস্থলীতে রক্ত দেওয়া উচিত নয়। দানের পর প্রচুর পানি পান করুন এবং দিনটি বিশ্রামে কাটান।"));

        /* version card */
        LinearLayout vWrap = Ui.v(c);
        vWrap.setPadding(D.dp(16), D.dp(10), D.dp(16), D.dp(24));
        LinearLayout vCard = Ui.v(c);
        vCard.setGravity(Gravity.CENTER_HORIZONTAL);
        vCard.setPadding(D.dp(16), D.dp(14), D.dp(16), D.dp(14));
        vCard.setBackground(D.round(D.primaryC, 18));
        vCard.addView(Ui.txt(c, Data.APP_NAME + " v" + Data.VERSION, 14, D.onPrimaryC, 700));
        TextView vs = Ui.txt(c, Data.ORG + " • " + Data.SINCE + " • " + Data.ADDRESS + ", " + Data.DISTRICT, 10.5f, D.withAlpha(D.onPrimaryC, 230), 500);
        vs.setGravity(Gravity.CENTER);
        vs.setPadding(0, D.dp(4), 0, 0);
        vs.setLineSpacing(0, 1.4f);
        vCard.addView(vs);
        vWrap.addView(vCard);
        root.addView(vWrap);

        sc.addView(root);
        return sc;
    }

    static LinearLayout section(Context c, int iconRes, String title, String body) {
        LinearLayout l = Ui.v(c);
        l.setPadding(D.dp(18), D.dp(14), D.dp(18), D.dp(14));
        l.setBackground(D.roundStroke(D.surfaceCLo, 20, D.outlineVar, 1));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(D.dp(16), D.dp(14), D.dp(16), 0);
        l.setLayoutParams(p);

        LinearLayout head = Ui.h(c);
        head.setGravity(Gravity.CENTER_VERTICAL);
        head.addView(Ui.icon(c, iconRes, 18, D.primary));
        ((LinearLayout.LayoutParams) head.getChildAt(0).getLayoutParams()).rightMargin = D.dp(9);
        head.addView(Ui.txt(c, title, 15, D.onSurface, 700));
        l.addView(head);

        TextView b = Ui.txt(c, body, 12.5f, D.onSurfaceVar, 400);
        b.setLineSpacing(0, 1.6f);
        b.setPadding(0, D.dp(8), 0, 0);
        l.addView(b);
        return l;
    }
}
