package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** হাসপাতাল, ব্লাড ব্যাংক ও জরুরি হটলাইন — নাটোর জেলা কেন্দ্রিক। */
public final class ScrHospitals {

    private ScrHospitals() {}

    static final class H {
        final String name, area, type, phone, mapQuery;
        final boolean hotline;
        H(String name, String area, String type, String phone, String mapQuery, boolean hotline) {
            this.name = name; this.area = area; this.type = type;
            this.phone = phone; this.mapQuery = mapQuery; this.hotline = hotline;
        }
    }

    static final List<H> HOTLINES = new ArrayList<H>();
    static final List<H> PLACES = new ArrayList<H>();
    static {
        HOTLINES.add(new H("জাতীয় জরুরি সেবা", "সারাদেশ, ২৪/৭", "হটলাইন", "999", null, true));
        HOTLINES.add(new H("স্বাস্থ্য বাতায়ন কল সেন্টার", "স্বাস্থ্য পরামর্শ, ২৪/৭", "হটলাইন", "16263", null, true));
        HOTLINES.add(new H("তথ্য ও সেবা (যেকোনো সরকারি সেবা)", "সারাদেশ", "হটলাইন", "333", null, true));
        HOTLINES.add(new H("নারী ও শিশু নির্যাতন প্রতিরোধ", "আইন ও সহায়তা", "হটলাইন", "109", null, true));
        HOTLINES.add(new H("অ্যাম্বুলেন্স (নাটোর)", "জেলা স্বাস্থ্য বাতায়ন", "অ্যাম্বুলেন্স", "999", null, true));

        PLACES.add(new H("বাগাতিপাড়া উপজেলা স্বাস্থ্য কমপ্লেক্স", "বাগাতিপাড়া উপজেলা, নাটোর", "সরকারি হাসপাতাল", "",
                "Bagatipara Upazila Health Complex, Natore", false));
        PLACES.add(new H("নাটোর জেনারেল (সদর) হাসপাতাল", "নাটোর সদর", "সরকারি হাসপাতাল", "",
                "Natore General Hospital, Natore Sadar", false));
        PLACES.add(new H("সিভিল সার্জন অফিস, নাটোর", "নাটোর সদর", "সরকারি দপ্তর", "",
                "Civil Surgeon Office Natore", false));
        PLACES.add(new H("রাজশাহী মেডিকেল কলেজ হাসপাতাল", "রাজশাহী (বৃহত্তর রেফারেল)", "মেডিকেল কলেজ", "",
                "Rajshahi Medical College Hospital", false));
        PLACES.add(new H("পপুলার ডায়াগনস্টিক সেন্টার", "নাটোর শাখা (ব্লাড ব্যাংকসহ)", "ব্লাড ব্যাংক", "",
                "Popular Diagnostic Centre Natore", false));
        PLACES.add(new H("ল্যাবএইড ডায়াগনস্টিকস", "নাটোর শাখা (ব্লাড ব্যাংকসহ)", "ব্লাড ব্যাংক", "",
                "LabAid Diagnostics Natore", false));
        PLACES.add(new H("বাগাতিপাড়া মাতৃসদন", "বাগাতিপাড়া", "মাতৃসদন", "",
                "Bagatipara Maternity Clinic Natore", false));
        PLACES.add(new H("দিঘাপাড়া কমিউনিটি ক্লিনিক", "দিঘাপাড়া, বাগাতিপাড়া", "কমিউনিটি ক্লিনিক", "",
                "Community Clinic Dighapara Bagatipara", false));
        PLACES.add(new H("হিজলি কমিউনিটি ক্লিনিক", "হিজলি, বাগাতিপাড়া", "কমিউনিটি ক্লিনিক", "",
                "Community Clinic Hizli Bagatipara", false));
    }

    public static View build(final Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        final LinearLayout root = Ui.v(c);
        root.addView(Ui.heroBar(c, "হাসপাতাল ও হটলাইন", "নাটোর জেলার নির্ভরযোগ্য ঠিকানা", false, null, null));

        /* জরুরি হটলাইন */
        root.addView(Ui.sectionHeader(c, "🚨 জরুরি হটলাইন — এক চাপে কল"));
        LinearLayout hl = Ui.v(c);
        hl.setPadding(D.dp(16), 0, D.dp(16), 0);
        for (final H h : HOTLINES) hl.addView(hotlineRow(c, h));
        root.addView(hl);

        /* সার্চ */
        root.addView(Ui.sectionHeader(c, "🏥 নিকটস্থ হাসপাতাল ও ব্লাড ব্যাংক"));
        EditText search = new EditText(c);
        search.setTypeface(D.tfRegular);
        search.setTextSize(13.5f);
        search.setTextColor(D.onSurface);
        search.setHintTextColor(D.outline);
        search.setHint("হাসপাতালের নাম বা এলাকা লিখুন…");
        search.setBackground(D.roundStroke(D.surfaceC, 14, D.outlineVar, 1.2f));
        search.setPadding(D.dp(14), D.dp(11), D.dp(14), D.dp(11));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sp.setMargins(D.dp(16), 0, D.dp(16), D.dp(10));
        search.setLayoutParams(sp);
        root.addView(search);

        final LinearLayout[] listHost = {Ui.v(c)};
        listHost[0].setPadding(D.dp(16), 0, D.dp(16), 0);
        fillPlaces(c, listHost[0], "");
        root.addView(listHost[0]);

        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int d) {}
            public void onTextChanged(CharSequence s, int a, int b, int d) {}
            public void afterTextChanged(Editable s) {
                listHost[0].removeAllViews();
                fillPlaces(c, listHost[0], s.toString().trim());
            }
        });

        TextView note = Ui.txt(c, "☎ ফোন নম্বর বদলাতে পারে — যাওয়ার আগে ৯৯৯ বা সংশ্লিষ্ট হাসপাতাল থেকে নিশ্চিত করে নিন। নির্দেশনা বোতামে চাপ দিলে গুগল ম্যাপে ঠিকানা খোলা যাবে।",
                10.5f, D.outline, 400);
        note.setLineSpacing(0, 1.4f);
        note.setPadding(D.dp(18), D.dp(10), D.dp(18), D.dp(20));
        root.addView(note);

        sc.addView(root);
        return sc;
    }

    static void fillPlaces(Context c, LinearLayout host, String q) {
        String ql = q.toLowerCase();
        int shown = 0;
        for (final H h : PLACES) {
            if (!ql.isEmpty() && !h.name.toLowerCase().contains(ql) && !h.area.toLowerCase().contains(ql)) continue;
            host.addView(placeRow(c, h));
            shown++;
        }
        if (shown == 0) {
            host.addView(Ui.emptyState(c, R.drawable.ic_hosp, "কিছু পাওয়া যায়নি", "\"" + q + "\" দিয়ে কোনো হাসপাতাল মেলেনি — বানান দেখে আবার চেষ্টা করুন।"));
        }
    }

    static LinearLayout hotlineRow(final Context c, final H h) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(D.dp(14), D.dp(11), D.dp(10), D.dp(11));
        row.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 18, D.outlineVar, 1), D.rippleColor));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, D.dp(8));
        row.setLayoutParams(p);

        LinearLayout m = Ui.v(c);
        m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        m.addView(Ui.txt(c, h.name, 13.5f, D.onSurface, 600));
        TextView sub = Ui.txt(c, h.area, 11f, D.onSurfaceVar, 400);
        sub.setPadding(0, D.dp(2), 0, 0);
        m.addView(sub);
        row.addView(m);

        TextView num = Ui.txt(c, Bn.toBn(h.phone), 15, 0xFFFFFFFF, 700);
        num.setGravity(Gravity.CENTER);
        num.setPadding(D.dp(14), D.dp(9), D.dp(14), D.dp(10));
        num.setBackground(D.ripple(D.gradAngle(D.redGrad, 14, 315), D.rippleColorLight));
        num.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.haptic(10); Ui.host.dial(h.phone); }
        });
        row.addView(num);
        return row;
    }

    static LinearLayout placeRow(final Context c, final H h) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(D.dp(13), D.dp(11), D.dp(9), D.dp(11));
        row.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 18, D.outlineVar, 1), D.rippleColor));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, D.dp(8));
        row.setLayoutParams(p);

        ImageView iv = Ui.icon(c, R.drawable.ic_hosp, 19, D.onPrimaryC);
        iv.setBackground(D.round(D.primaryC, 13));
        iv.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
        row.addView(iv);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).rightMargin = D.dp(11);

        LinearLayout m = Ui.v(c);
        m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        m.addView(Ui.txt(c, h.name, 13f, D.onSurface, 600));
        TextView sub = Ui.txt(c, h.area + " • " + h.type, 11f, D.onSurfaceVar, 400);
        sub.setPadding(0, D.dp(2), 0, 0);
        m.addView(sub);
        row.addView(m);

        /* নির্দেশনা */
        ImageView dir = Ui.icon(c, R.drawable.ic_location, 19, D.onTertiaryC);
        dir.setBackground(D.ripple(D.round(D.tertiaryC, 13), D.withAlpha(D.onTertiaryC, 46)));
        dir.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
        dir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Ui.host.haptic(9);
                try {
                    Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(h.mapQuery));
                    c.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception e) {
                    Ui.toast("ম্যাপ অ্যাপ পাওয়া যায়নি", true);
                }
            }
        });
        row.addView(dir);
        ((LinearLayout.LayoutParams) dir.getLayoutParams()).rightMargin = D.dp(7);

        /* কল */
        ImageView call = Ui.icon(c, R.drawable.ic_phone, 19, 0xFFFFFFFF);
        call.setBackground(D.ripple(D.gradAngle(D.redGrad, 13, 315), D.rippleColorLight));
        call.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.haptic(9); Ui.host.dial("999"); }
        });
        row.addView(call);
        return row;
    }
}
