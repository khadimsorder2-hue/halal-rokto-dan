package com.hizlydighapara.halalrokto;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.OutputStream;

/** সম্মাননা সনদ — Canvas-এ আঁকা সুন্দর সার্টিফিকেট, ছবি হিসেবে শেয়ার। */
public final class ScrCert {

    private ScrCert() {}

    public static View build(final Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        LinearLayout root = Ui.v(c);
        root.addView(Ui.heroBar(c, "সম্মাননা সনদ", "আপনার রক্তদানের স্বীকৃতি", false, null, null));

        final Data.User me = Data.me();

        /* প্রিভিউ */
        LinearLayout previewWrap = Ui.v(c);
        previewWrap.setPadding(D.dp(16), D.dp(14), D.dp(16), 0);
        View preview = new View(c) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                drawCert(canvas, getWidth(), getHeight(), me, false);
            }
        };
        preview.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, D.dp(430)));
        preview.setElevation(D.dp(3));
        previewWrap.addView(preview);
        root.addView(previewWrap);

        /* ব্যাখ্যা */
        TextView desc = Ui.txt(c, "এই সনদে আছে আপনার নাম, রক্তের গ্রুপ, মোট দানের সংখ্যা ও সংঘের সীলমোহর। \"সনদ শেয়ার করুন\" চাপলে ছবি হিসেবে গ্যালারিতে সেভ হবে এবং শেয়ার করা যাবে।",
                11.5f, D.onSurfaceVar, 400);
        desc.setLineSpacing(0, 1.4f);
        desc.setPadding(D.dp(18), D.dp(10), D.dp(18), D.dp(12));
        root.addView(desc);

        /* বোতাম */
        LinearLayout btns = Ui.h(c);
        btns.setPadding(D.dp(16), D.dp(2), D.dp(16), 0);

        TextView share = Ui.btn(c, "সনদ শেয়ার করুন", Ui.BTN_GRAD, new Runnable() {
            public void run() {
                MainActivity host = Ui.host;
                if (me.donationCount == 0) {
                    Ui.toast("দান রেকর্ড হলে সনদ আরও অর্থবহ হবে — তবু শেয়ার করা যাবে", false);
                }
                Bitmap bmp = Bitmap.createBitmap(1080, 1536, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmp);
                drawCert(canvas, 1080, 1536, me, true);
                host.shareImage(bmp, "sonod_" + me.name.replaceAll("\\s+", "_"));
            }
        });
        share.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(50), 1f));
        btns.addView(share);
        ((LinearLayout.LayoutParams) share.getLayoutParams()).rightMargin = D.dp(9);

        TextView card = Ui.btn(c, "ডোনার কার্ড", Ui.BTN_TONAL, new Runnable() {
            public void run() { Ui.host.go("donorcard", false); }
        });
        card.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(50), 1f));
        btns.addView(card);
        root.addView(btns);

        View gap = new View(c);
        gap.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(24)));
        root.addView(gap);

        sc.addView(root);
        return sc;
    }

    /* ══ সনদ আঁকা (প্রিভিউ + ফুল রেজোলিউশন উভয়ই) ═════════════ */
    static void drawCert(Canvas canvas, int w, int h, Data.User me, boolean full) {
        float k = w / 1080f; // স্কেল ফ্যাক্টর — সব সমন্বয় 1080-বেসে

        Paint bgP = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgP.setColor(0xFFFDFAF5);
        canvas.drawRect(0, 0, w, h, bgP);

        /* হালকা কোণার অলংকরণ */
        Paint deco = new Paint(Paint.ANTI_ALIAS_FLAG);
        deco.setStyle(Paint.Style.STROKE);
        deco.setStrokeWidth(10 * k);
        deco.setColor(0x26B3261E);
        Path corner = new Path();
        float cl = 190 * k;
        corner.moveTo(56 * k, 56 * k + cl); corner.lineTo(56 * k, 56 * k);
        corner.quadTo(56 * k, 56 * k, 56 * k + cl, 56 * k);
        canvas.drawPath(corner, deco);
        corner.reset();
        corner.moveTo(w - 56 * k - cl, h - 56 * k); corner.lineTo(w - 56 * k, h - 56 * k);
        corner.quadTo(w - 56 * k, h - 56 * k, w - 56 * k, h - 56 * k - cl);
        canvas.drawPath(corner, deco);

        /* ডাবল বর্ডার */
        Paint brd = new Paint(Paint.ANTI_ALIAS_FLAG);
        brd.setStyle(Paint.Style.STROKE);
        brd.setColor(0xFFB3261E);
        brd.setStrokeWidth(9 * k);
        canvas.drawRoundRect(new RectF(36 * k, 36 * k, w - 36 * k, h - 36 * k), 26 * k, 26 * k, brd);
        brd.setStrokeWidth(3 * k);
        brd.setColor(0xFFD0453C);
        canvas.drawRoundRect(new RectF(54 * k, 54 * k, w - 54 * k, h - 54 * k), 18 * k, 18 * k, brd);

        /* হেডার ব্যান্ড */
        Paint band = new Paint(Paint.ANTI_ALIAS_FLAG);
        band.setColor(0xFFB3261E);
        canvas.drawRoundRect(new RectF(80 * k, 80 * k, w - 80 * k, 250 * k), 14 * k, 14 * k, band);

        /* ফোঁটা আইকন (পথ দিয়ে আঁকা) */
        Paint drop = new Paint(Paint.ANTI_ALIAS_FLAG);
        drop.setColor(0xFFFFFFFF);
        Path p = new Path();
        float dx = w / 2f, dy = 128 * k, ds = 44 * k;
        p.moveTo(dx, dy - ds);
        p.cubicTo(dx + ds * 0.95f, dy - ds * 0.15f, dx + ds * 0.72f, dy + ds * 0.62f, dx, dy + ds * 0.8f);
        p.cubicTo(dx - ds * 0.72f, dy + ds * 0.62f, dx - ds * 0.95f, dy - ds * 0.15f, dx, dy - ds);
        p.close();
        canvas.drawPath(p, drop);

        text(canvas, Data.ORG, w / 2f, 205 * k, 33 * k, 0xFFFFFFFF, D.tfBold, true);
        text(canvas, Data.SINCE + " • বাগাতিপাড়া, নাটোর", w / 2f, 238 * k, 20 * k, 0xCCFFFFFF, D.tfMedium, true);

        /* শিরোনাম */
        text(canvas, "সম্মাননা সনদ", w / 2f, 330 * k, 58 * k, 0xFFB3261E, D.tfBold, true);
        text(canvas, "স্বেচ্ছায় রক্তদানে অসামান্য অবদানের স্বীকৃতিপত্র", w / 2f, 380 * k, 25 * k, 0xFF8A6D6A, D.tfMedium, true);

        /* বিভাজক */
        Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
        line.setStrokeWidth(2.5f * k);
        line.setColor(0xFFD8C7C5);
        canvas.drawLine(190 * k, 420 * k, w - 190 * k, 420 * k, line);
        canvas.drawCircle(w / 2f, 420 * k, 7 * k, line);

        /* প্রাপক */
        text(canvas, "এই সনদটি প্রদান করা হলো", w / 2f, 480 * k, 24 * k, 0xFF7A6A67, D.tfMedium, true);
        text(canvas, me.name, w / 2f, 560 * k, 56 * k, 0xFF3A2E2C, D.tfBold, true);

        /* নামের নিচে আঁকিয়া রেখা */
        Paint ul = new Paint(Paint.ANTI_ALIAS_FLAG);
        ul.setStrokeWidth(2 * k);
        ul.setColor(0xFFC9B8B5);
        float nameW = textWidth(me.name, 56 * k, D.tfBold);
        canvas.drawLine(w / 2f - nameW / 2f, 590 * k, w / 2f + nameW / 2f, 590 * k, ul);

        /* রক্তের গ্রুপ চিপ */
        Paint chip = new Paint(Paint.ANTI_ALIAS_FLAG);
        chip.setColor(0xFFB3261E);
        float chipW = 250 * k, chipH = 86 * k;
        canvas.drawRoundRect(new RectF(w / 2f - chipW / 2f, 640 * k, w / 2f + chipW / 2f, 640 * k + chipH), 16 * k, 16 * k, chip);
        text(canvas, me.bloodType, w / 2f, 640 * k + 58 * k, 46 * k, 0xFFFFFFFF, D.tfBold, true);

        /* দানের তথ্য */
        text(canvas, "যিনি এ পর্যন্ত " + toBn(me.donationCount) + " বার নিঃস্বার্থভাবে রক্ত দান করেছেন এবং", w / 2f, 790 * k, 26 * k, 0xFF5D4F4D, D.tfRegular, true);
        text(canvas, "মানবতার সেবায় হিজলি দিঘাপাড়া যুব সংঘের গর্বিত সদস্য হিসেবে", w / 2f, 830 * k, 26 * k, 0xFF5D4F4D, D.tfRegular, true);
        text(canvas, "সকলের প্রশংসা ও কৃতজ্ঞতা অর্জন করেছেন।", w / 2f, 870 * k, 26 * k, 0xFF5D4F4D, D.tfRegular, true);

        /* দানের রেকর্ড বাক্স */
        Paint box = new Paint(Paint.ANTI_ALIAS_FLAG);
        box.setColor(0xFFF6EDED);
        box.setStyle(Paint.Style.STROKE);
        box.setStrokeWidth(2.5f * k);
        box.setColor(0xFFE5DEDE);
        RectF brect = new RectF(150 * k, 930 * k, w - 150 * k, 1170 * k);
        canvas.drawRoundRect(brect, 18 * k, 18 * k, box);
        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setColor(0xFFFCF8F8);
        canvas.drawRoundRect(new RectF(153 * k, 933 * k, w - 153 * k, 1167 * k), 16 * k, 16 * k, fill);

        String[] rows = {
                "মোট রক্তদান: " + toBn(me.donationCount) + " বার",
                "সদস্য নম্বর: " + (me.referralCode.isEmpty() ? "—" : me.referralCode),
                "যাচাইকৃত: " + (me.verified ? "✓ সংঘ কর্তৃক অনুমোদিত" : "প্রক্রিয়াধীন"),
                "রক্তের গ্রুপ: " + me.bloodType,
        };
        float ry = 990 * k;
        for (String r : rows) {
            text(canvas, r, w / 2f, ry, 26 * k, 0xFF3A2E2C, D.tfMedium, true);
            ry += 55 * k;
        }

        /* তারিখ */
        text(canvas, "দেয় তারিখ: " + Bn.fmtDate(System.currentTimeMillis()), w / 2f, 1240 * k, 24 * k, 0xFF8A6D6A, D.tfMedium, true);

        /* স্বাক্ষর ও সীল */
        Paint sig = new Paint(Paint.ANTI_ALIAS_FLAG);
        sig.setStrokeWidth(2 * k);
        sig.setColor(0xFF9C8885);
        canvas.drawLine(150 * k, 1390 * k, 420 * k, 1390 * k, sig);
        canvas.drawLine(w - 420 * k, 1390 * k, w - 150 * k, 1390 * k, sig);
        text(canvas, "সভাপতি", 285 * k, 1425 * k, 22 * k, 0xFF7A6A67, D.tfMedium, true);
        text(canvas, "সাধারণ সম্পাদক", w - 285 * k, 1425 * k, 22 * k, 0xFF7A6A67, D.tfMedium, true);

        /* সীলমোহর (ডাবল রিং + ফোঁটা) */
        Paint seal = new Paint(Paint.ANTI_ALIAS_FLAG);
        seal.setStyle(Paint.Style.STROKE);
        seal.setStrokeWidth(5 * k);
        seal.setColor(0xFFB3261E);
        canvas.drawCircle(w / 2f, 1360 * k, 78 * k, seal);
        seal.setStrokeWidth(2 * k);
        seal.setColor(0xFFD0453C);
        canvas.drawCircle(w / 2f, 1360 * k, 64 * k, seal);
        Paint sealFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        sealFill.setColor(0x14B3261E);
        canvas.drawCircle(w / 2f, 1360 * k, 64 * k, sealFill);
        Paint sealDrop = new Paint(Paint.ANTI_ALIAS_FLAG);
        sealDrop.setColor(0xFFB3261E);
        Path sp = new Path();
        float sdx = w / 2f, sdy = 1352 * k, sds = 26 * k;
        sp.moveTo(sdx, sdy - sds);
        sp.cubicTo(sdx + sds * 0.95f, sdy - sds * 0.15f, sdx + sds * 0.72f, sdy + sds * 0.62f, sdx, sdy + sds * 0.8f);
        sp.cubicTo(sdx - sds * 0.72f, sdy + sds * 0.62f, sdx - sds * 0.95f, sdy - sds * 0.15f, sdx, sdy - sds);
        sp.close();
        canvas.drawPath(sp, sealDrop);
        text(canvas, "সত্যায়িত", w / 2f, 1408 * k, 17 * k, 0xFFB3261E, D.tfBold, true);

        /* ফুটার */
        text(canvas, Data.APP_NAME + " • " + Data.ORG_EN, w / 2f, 1480 * k, 19 * k, 0xFFA08D8A, D.tfMedium, true);
    }

    static String toBn(long n) { return Bn.toBn(String.valueOf(n)); }

    static void text(Canvas canvas, String s, float cx, float baseline, float px, int color, Typeface tf, boolean center) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setTypeface(tf);
        p.setTextSize(px);
        float x = center ? cx - p.measureText(s) / 2f : cx;
        canvas.drawText(s, x, baseline, p);
    }

    static float textWidth(String s, float px, Typeface tf) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(tf);
        p.setTextSize(px);
        return p.measureText(s);
    }
}
