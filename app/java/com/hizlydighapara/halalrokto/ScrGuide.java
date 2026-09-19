package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** রক্তদান গাইড — আগে/পরে, কারা পারবেন, হালাল দৃষ্টিভঙ্গি, প্রশ্নোত্তর। অ্যাকর্ডিয়ন UI। */
public final class ScrGuide {

    private ScrGuide() {}

    static final class Topic {
        final int icon; final String title; final String[][] items; // {head, body} বা {null, bullet}
        final boolean bulletOnly;
        Topic(int icon, String title, String[][] items, boolean bulletOnly) {
            this.icon = icon; this.title = title; this.items = items; this.bulletOnly = bulletOnly;
        }
    }

    static final Topic[] TOPICS = {
        new Topic(R.drawable.ic_drop, "রক্তদানের আগে করণীয়", new String[][]{
            {"সুস্থ থাকুন", "দানের আগের রাতে ভালো ঘুম (কমপক্ষে ৬–৮ ঘণ্টা) হতে হবে। জ্বর, ঠান্ডা বা পেটের সমস্যা থাকলে সেদিন দান করবেন না।"},
            {"হালকা খাবার খান", "খালি পেটে দান করবেন না, আবার ভারী তেল-মসলার খাবারও নয়। ঘণ্টা দুয়েক আগে ভাত-রুটি, ডিম বা খেজুরের মতো হালকা খাবার খান।"},
            {"প্রচুর পানি পান করুন", "দানের আগে ২–৩ গ্লাস পানি পান করুন — শিরা ফুলে রক্ত নেওয়া সহজ হয় এবং দুর্বল লাগা কমে।"},
            {"আইডি ও তথ্য সাথে রাখুন", "জাতীয় পরিচয়পত্র বা ডোনার কার্ড সাথে নিন। ওষুধ খাচ্ছেন কি না সৎভাবে জানান।"},
            {"ক্যাফেইন ও ধূমপান এড়িয়ে চলুন", "দানের আগের ২–৩ ঘণ্টায় চা-কফি কম খান, ধূমপান করবেন না — রক্তে অক্সিজেন মাত্রা ঠিক থাকে।"},
            {"মানসিকভাবে প্রস্তুত থাকুন", "ভয় পাওয়ার কিছু নেই — সবচেয়ে বেশি সময় লাগে ১০–১৫ মিনিট। হাসিমুখে দিন, মন হালকা থাকলে রক্তও সহজে বের হয়।"},
        }, false),
        new Topic(R.drawable.ic_heart, "রক্তদানের পরে করণীয়", new String[][]{
            {"১০–১৫ মিনিট বিশ্রাম", "শুয়ে বা বসে কিছুক্ষণ থাকুন। সাথে সাথে উঠে হাঁটতে যাবেন না — মাথা ঘুরতে পারে।"},
            {"পানি ও জুস পান করুন", "২৪ ঘণ্টায় ৮–১০ গ্লাস পানি পান করুন। কমলার জুস, নারকেল পানি বা খেজুর শরীরে শক্তি ফেরায়।"},
            {"ব্যান্ডেজ ৪–৫ ঘণ্টা রাখুন", "তারপর খুলে হালকা সাবান-পানিতে ধুয়ে নিন। নাড়ি বা ভেন থেকে রক্ত পড়লে হাত উঁচু করে চাপ দিন।"},
            {"ভারী পরিশ্রম এড়িয়ে চলুন", "দানের দিন ব্যায়াম, ভারী ওজন তোলা বা দৌড়ানো করবেন না। বাইক/রিকশা করে বাড়ি ফিরুন।"},
            {"আয়রনসমৃদ্ধ খাবার খান", "সপ্তাহখানেক লিভার, ডিমের কুসুম, ডাল, পালং শাক, খেজুর ও কিশমিশ খান — রক্ত দ্রুত তৈরি হয়।"},
            {"অস্বস্তি লাগলে ডাক্তার", "মাথা ঘোরা, বমি বা দুর্বল ভাব ২৪ ঘণ্টার বেশি থাকলে ক্যাম্পের ডাক্তার বা ১৬২৬৩-এ যোগাযোগ করুন।"},
        }, false),
        new Topic(R.drawable.ic_person, "কারা রক্ত দিতে পারবেন", new String[][]{
            {null, "বয়স ১৮–৬০ বছর (স্বাস্থ্যবান হলে ৬৫ পর্যন্ত সম্ভব)"},
            {null, "ওজন: পুরুষ ৫০ কেজি+, মহিলা ৪৫ কেজি+"},
            {null, "শরীরের তাপমাত্রা ও রক্তচাপ স্বাভাবিক"},
            {null, "হিমোগ্লোবিন: পুরুষ ১২.৫+, মহিলা ১২.০+ গ্রাম/ডেসিলিটার"},
            {null, "দুইবার দানের মাঝে কমপক্ষে ৯০ দিন (৩ মাস) বিরতি"},
            {null, "সর্বশেষ ৬ মাসে অপারেশন, রক্ত গ্রহণ বা ট্যাটু করাননি"},
            {null, "গর্ভবতী বা স্তন্যদানকারী মা নন"},
        }, true),
        new Topic(R.drawable.ic_warn, "যাদের রক্ত দেওয়া মানা", new String[][]{
            {null, "হৃদরোগ, উচ্চ রক্তচাপ বা ডায়াবেটিসের গুরুতর রোগী"},
            {null, "হেপাটাইটিস B/C, এইচআইভি বা যক্ষ্মার সক্রিয় রোগী"},
            {null, "ক্যান্সার বা রক্তের দীর্ঘমেয়াদি রোগ (হিমোফিলিয়া ইত্যাদি)"},
            {null, "মাদকাসক্ত বা শিরায় ওষুধ নেওয়া ব্যক্তি"},
            {null, "সাম্প্রতিক (৪ সপ্তাহের মধ্যে) টিকা নিয়েছেন যারা — ডাক্তারের পরামর্শ নিন"},
            {null, "শেষ ৬ মাসে ডেঙ্গু, ম্যালেরিয়া বা টাইফয়েড হয়েছে যারা"},
        }, true),
        new Topic(R.drawable.ic_shield, "রক্তদানের উপকারিতা", new String[][]{
            {"শরীরে নতুন রক্ত তৈরি হয়", "দানের ৪৮ ঘণ্টার মধ্যে প্লাজমা ফিরে আসে, ৩–৪ সপ্তাহে লোহিত কণিকা পুনরায় তৈরি হয় — শরীরের রক্ত তৈরির কারখানা সচল থাকে।"},
            {"হৃদরোগের ঝুঁকি কমে", "নিয়মিত দানে রক্তের ঘনত্ব নিয়ন্ত্রণে থাকে, লোহার মাত্রা ভারসাম্যে থাকে — গবেষণায় হৃদরোগ ও স্ট্রোকের ঝুঁকি কম পাওয়া গেছে।"},
            {"ফ্রি স্বাস্থ্য পরীক্ষা", "প্রতিবার দানের আগে রক্তের গ্রুপ, হিমোগ্লোবিন, বিপি ও সংক্রামক রোগ পরীক্ষা হয় — নিজের স্বাস্থ্যের হালহকিকত জানা যায়।"},
            {"মানসিক শান্তি ও সদকা", "একব্যাগ রক্ত (৪৫০ মিলি) দিয়ে ৩ জন রোগীর জীবন বাঁচানো সম্ভব — এর চেয়ে বড় পুণ্য কমই আছে।"},
        }, false),
        new Topic(R.drawable.ic_tag, "ইসলামী দৃষ্টিতে রক্তদান", new String[][]{
            {"জীবন বাঁচানো মহান ইবাদত", "পবিত্র কোরআনে বলা হয়েছে — একটি প্রাণ বাঁচানো সমগ্র মানবজাতি বাঁচানোর সমান (সূরা মায়িদা: ৩২)। রক্তদান সেই পথেরই একটি বাস্তব মাধ্যম।"},
            {"রক্ত বিক্রি নয়, দান", "স্বেচ্ছায় বিনামূল্যে রক্ত দেওয়া দান/সদকার অন্তর্ভুক্ত। সংঘ 'হালাল রক্ত দান' নাম রেখেছে এ কারণেই — অর্থের বিনিময়ে রক্ত নয়, কেবল আল্লাহর জন্য।"},
            {"রোজা অবস্থায় দেওয়া যায়", "ফিকহ পণ্ডিতদের মতে রক্ত দান রোজা ভঙ্গ করে না — যদিও দুর্বল শরীরে রোজা রেখে দান উচিত নয়। জরুরি প্রয়োজনে রোগীর জীবন আগে।"},
            {"রক্ত নাপবিত্র নয়", "সংক্রামক রোগ ছাড়া সুস্থ মানুষের রক্ত শুদ্ধ রক্ত — চিকিৎসা প্রয়োজনে গ্রহণ-দান জায়েয। এ বিষয়ে দেশের ইসলামী ফাউন্ডেশনও অনুমোদন দিয়েছে।"},
        }, false),
        new Topic(R.drawable.ic_info, "সাধারণ প্রশ্নোত্তর", new String[][]{
            {"দান করতে কত সময় লাগে?", "মোট প্রক্রিয়া ২০–২৫ মিনিট (নিবন্ধন, পরীক্ষা ও বিশ্রামসহ); শুধু রক্ত নিতে ৮–১০ মিনিট।"},
            {"একবারে কত রক্ত নেওয়া হয়?", "৪৫০ মিলিলিটার — শরীরের মোট রক্তের মাত্র ৮–১০%। শরীর নিজেই ২৪ ঘণ্টায় তরল অংশ পূরণ করে।"},
            {"কতদিন পর পর দিতে পারি?", "পুরুষ বছরে ৪ বার, মহিলা বছরে ২–৩ বার — মাঝে কমপক্ষে ৯০ দিন বিরতি।"},
            {"রক্তদানে কি মোটা হওয়া/রোগ হয়?", "না — বরং নিয়ন্ত্রিত খাদ্যাভ্যাস আর সক্রিয় রক্ত তৈরির কারণে শরীর ঝরঝরে থাকে। ব্যবহৃত সিরিঞ্জ সম্পূর্ণ নতুন ও স্বয়ংক্রিয়ভাবে নষ্ট হয়।"},
            {"থ্যালাসেমিয়া রোগীর জন্য?", "তাদের নিয়মিত (২–৩ সপ্তাহ পরপর) রক্ত লাগে — আপনার একবার দান একটি শিশুর পুরো মাস কাটায়। সংঘের মাধ্যমে নিয়মিত দাতা হোন।"},
        }, false),
    };

    public static View build(Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);
        root.addView(Ui.heroBar(c, "রক্তদান গাইড", "সঠিক জ্ঞান — নিরাপদ দান", false, null, null));

        for (int i = 0; i < TOPICS.length; i++)
            root.addView(topicCard(c, TOPICS[i], i == 0));

        View gap = new View(c);
        gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(24)));
        root.addView(gap);

        sc.addView(root);
        return sc;
    }

    static LinearLayout topicCard(final Context c, final Topic t, boolean open) {
        LinearLayout card = Ui.card(c);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(D.dp(16), D.dp(0), D.dp(16), D.dp(10));
        card.setLayoutParams(p);

        /* হেডার */
        final LinearLayout head = Ui.h(c);
        head.setPadding(D.dp(14), D.dp(12), D.dp(12), D.dp(12));
        head.setBackground(D.ripple(D.round(Color.TRANSPARENT, 18), D.rippleColor));

        ImageView iv = Ui.icon(c, t.icon, 20, D.primary);
        iv.setBackground(D.round(D.primaryC, 13));
        iv.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
        head.addView(iv);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).rightMargin = D.dp(12);

        TextView title = Ui.txt(c, t.title, 14, D.onSurface, 700);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        head.addView(title);

        final ImageView chev = Ui.icon(c, R.drawable.ic_chevron, 15, D.outline);
        head.addView(chev);

        card.addView(head);

        /* বডি */
        final LinearLayout body = Ui.v(c);
        body.setPadding(D.dp(8), D.dp(0), D.dp(12), D.dp(12));
        for (String[] item : t.items) body.addView(t.bulletOnly ? bullet(c, item[1]) : qa(c, item[0], item[1]));
        body.setVisibility(open ? View.VISIBLE : View.GONE);
        if (open) chev.setRotation(180f);
        card.addView(body);

        head.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Ui.host.haptic(7);
                boolean show = body.getVisibility() != View.VISIBLE;
                body.setVisibility(show ? View.VISIBLE : View.GONE);
                chev.animate().rotation(show ? 180f : 0f).setDuration(220).start();
            }
        });
        return card;
    }

    static LinearLayout qa(Context c, String head, String text) {
        LinearLayout l = Ui.v(c);
        l.setPadding(D.dp(12), D.dp(9), D.dp(12), D.dp(10));
        l.setBackground(D.round(D.surfaceC, 12));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, D.dp(6));
        l.setLayoutParams(p);
        l.addView(Ui.txt(c, head, 12.5f, D.primary, 700));
        TextView b = Ui.txt(c, text, 12f, D.onSurfaceVar, 400);
        b.setLineSpacing(0, 1.38f);
        b.setPadding(0, D.dp(3), 0, 0);
        l.addView(b);
        return l;
    }

    static LinearLayout bullet(Context c, String text) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.TOP);
        row.setPadding(D.dp(12), D.dp(8), D.dp(12), D.dp(8));
        TextView dot = Ui.txt(c, "▪", 12, D.primary, 700);
        row.addView(dot);
        ((LinearLayout.LayoutParams) dot.getLayoutParams()).rightMargin = D.dp(8);
        TextView t = Ui.txt(c, text, 12f, D.onSurface, 500);
        t.setLineSpacing(0, 1.35f);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(t);
        return row;
    }
}
