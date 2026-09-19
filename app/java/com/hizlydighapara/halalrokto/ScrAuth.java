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

import java.util.concurrent.atomic.AtomicReference;

/** লগইন / রেজিস্ট্রেশন। */
public final class ScrAuth {

    private ScrAuth() {}

    public static View build(final Context c) {
        ScrollView sc = new ScrollView(c);
        sc.setFillViewport(true);
        sc.setBackgroundColor(D.bg);

        LinearLayout root = Ui.v(c);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // hero
        LinearLayout hero = Ui.v(c);
        hero.setBackground(D.gradAngle(D.heroGrad, 0, 315));
        hero.setGravity(Gravity.CENTER_HORIZONTAL);
        hero.setPadding(D.dp(24), D.dp(34), D.dp(24), D.dp(44));
        root.addView(hero);

        ImageView logo = Ui.icon(c, R.drawable.ic_dropfill, 46, 0xFFFFFFFF);
        logo.setElevation(D.dp(4));
        LinearLayout logoWrap = new LinearLayout(c);
        logoWrap.setGravity(Gravity.CENTER);
        logoWrap.setLayoutParams(Ui.lp(D.dp(84), D.dp(84)));
        logoWrap.setBackground(D.gradAngle(D.redGrad, 26, 315));
        logoWrap.setElevation(D.dp(6));
        logoWrap.addView(logo);
        hero.addView(logoWrap);

        TextView h1 = Ui.txt(c, Data.APP_NAME, 26, 0xFFFFFFFF, 700);
        h1.setGravity(Gravity.CENTER);
        h1.setPadding(0, D.dp(14), 0, 0);
        hero.addView(h1);
        TextView h2 = Ui.txt(c, "রক্ত দিন, জীবন বাঁচান — হালাল পথে", 12.5f, D.withAlpha(0xFFFFFFFF, 210), 500);
        h2.setGravity(Gravity.CENTER);
        h2.setPadding(0, D.dp(4), 0, 0);
        hero.addView(h2);

        // card
        LinearLayout card = Ui.v(c);
        card.setPadding(D.dp(4), D.dp(4), D.dp(4), D.dp(10));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cp.setMargins(D.dp(18), D.dp(-34), D.dp(18), 0);
        card.setLayoutParams(cp);
        card.setBackground(D.roundStroke(D.surfaceCLo, 26, D.outlineVar, 1));
        card.setElevation(D.dp(8));
        root.addView(card);

        // tab switch
        LinearLayout tabs = Ui.h(c);
        tabs.setPadding(D.dp(10), D.dp(10), D.dp(10), 0);
        card.addView(tabs);

        final TextView tabL = Ui.btnSmall(c, "লগইন", Ui.BTN_GRAD, null);
        tabL.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(38), 1f));
        final TextView tabR = Ui.btnSmall(c, "রেজিস্ট্রেশন", Ui.BTN_TONAL, null);
        tabR.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(38), 1f));
        ((LinearLayout.LayoutParams) tabR.getLayoutParams()).leftMargin = D.dp(8);
        tabs.addView(tabL);
        tabs.addView(tabR);

        final LinearLayout body = Ui.v(c);
        body.setPadding(0, D.dp(6), 0, D.dp(14));
        card.addView(body);

        final boolean[] loginMode = {true};
        final Runnable[] renderBody = new Runnable[1];

        final Runnable setTabs = new Runnable() {
            public void run() {
                tabL.setBackgroundResource(0);
                tabR.setBackgroundResource(0);
                if (loginMode[0]) {
                    tabL.setTextColor(0xFFFFFFFF);
                    tabL.setBackground(D.ripple(D.gradAngle(D.redGrad, 50, 315), D.rippleColorLight));
                    tabR.setTextColor(D.onSurfaceVar);
                    tabR.setBackground(D.rippleSolid(D.surfaceC, 50, D.rippleColor));
                } else {
                    tabR.setTextColor(0xFFFFFFFF);
                    tabR.setBackground(D.ripple(D.gradAngle(D.redGrad, 50, 315), D.rippleColorLight));
                    tabL.setTextColor(D.onSurfaceVar);
                    tabL.setBackground(D.rippleSolid(D.surfaceC, 50, D.rippleColor));
                }
                renderBody[0].run();
            }
        };

        tabL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { if (!loginMode[0]) { loginMode[0] = true; setTabs.run(); } }
        });
        tabR.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { if (loginMode[0]) { loginMode[0] = false; setTabs.run(); } }
        });

        renderBody[0] = new Runnable() {
            public void run() {
                body.removeAllViews();
                if (loginMode[0]) renderLogin(c, body, new Runnable() {
                    public void run() { loginMode[0] = false; setTabs.run(); }
                });
                else renderRegister(c, body);
            }
        };

        // org footer
        LinearLayout orgFoot = Ui.v(c);
        orgFoot.setGravity(Gravity.CENTER);
        orgFoot.setPadding(0, D.dp(30), 0, D.dp(26));
        TextView o1 = Ui.txt(c, Data.ORG, 13, D.onSurface, 700);
        o1.setGravity(Gravity.CENTER);
        orgFoot.addView(o1);
        TextView o2 = Ui.txt(c, Data.SINCE + " • " + Data.ADDRESS, 11f, D.onSurfaceVar, 500);
        o2.setGravity(Gravity.CENTER);
        o2.setPadding(0, D.dp(3), 0, 0);
        orgFoot.addView(o2);
        root.addView(orgFoot);

        setTabs.run();
        sc.addView(root);
        return sc;
    }

    static void renderLogin(final Context c, final LinearLayout body, final Runnable toRegister) {
        final Ui.Field fEmail = Ui.field(c, "ইমেইল", Ui.F_EMAIL, null, "you@example.com");
        final Ui.Field fPass = Ui.field(c, "পাসওয়ার্ড", Ui.F_PASS, null, "••••••");
        body.addView(fEmail.root);
        body.addView(fPass.root);

        LinearLayout btnWrap = Ui.v(c);
        btnWrap.setPadding(D.dp(14), D.dp(16), D.dp(14), 0);
        TextView go = Ui.btn(c, "লগইন করুন", Ui.BTN_GRAD, new Runnable() {
            public void run() {
                if (!Ui.validate(new Ui.Field[]{fEmail, fPass})) return;
                Data.AuthResult r = Data.login(fEmail.input.getText().toString().trim(),
                        fPass.input.getText().toString());
                if (!r.ok) { Ui.toast(r.msg, true); return; }
                Ui.toast("স্বাগতম, " + r.user.name.split(" ")[0] + "!", false);
                Ui.host.go("home", true);
            }
        });
        go.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(50)));
        btnWrap.addView(go);
        body.addView(btnWrap);

        addSwitch(c, body, "অ্যাকাউন্ট নেই?", "রেজিস্ট্রেশন করুন", toRegister);
    }

    static void renderRegister(final Context c, final LinearLayout body) {
        final Ui.Field fName = Ui.field(c, "পূর্ণ নাম *", Ui.F_TEXT, null, "যেমন: মোঃ রহিম মিয়া");
        final Ui.Field fPhone = Ui.field(c, "ফোন নম্বর *", Ui.F_PHONE, null, "01XXXXXXXXX");
        final AtomicReference<String> bgRef = new AtomicReference<>(null);
        LinearLayout bgGrid = Ui.bloodGroupGrid(c, null, bgRef);
        final Ui.Field fAddr = Ui.field(c, "ঠিকানা / এলাকা *", Ui.F_TEXT, null, "যেমন: হিজলি, বাগাতিপাড়া");
        final Ui.Field fEmail = Ui.field(c, "ইমেইল *", Ui.F_EMAIL, null, "you@example.com");
        final Ui.Field fPass = Ui.field(c, "পাসওয়ার্ড *", Ui.F_PASS, null, "কমপক্ষে ৬ অক্ষর");
        final Ui.Field fRef = Ui.field(c, "রেফারেল কোড (ঐচ্ছিক)", Ui.F_TEXT, null, "DJS-XXXXX");

        body.addView(fName.root);
        body.addView(fPhone.root);
        body.addView(bgGrid);
        body.addView(fAddr.root);
        body.addView(fEmail.root);
        body.addView(fPass.root);
        body.addView(fRef.root);

        TextView hint = Ui.txt(c, "বন্ধুর কোড থাকলে দিন — না থাকলে খালি রাখুন", 10.5f, D.outline, 400);
        hint.setPadding(D.dp(24), 0, D.dp(0), D.dp(4));
        body.addView(hint);

        LinearLayout btnWrap = Ui.v(c);
        btnWrap.setPadding(D.dp(14), D.dp(12), D.dp(14), 0);
        TextView go = Ui.btn(c, "রেজিস্ট্রেশন করুন", Ui.BTN_GRAD, new Runnable() {
            public void run() {
                if (bgRef.get() == null) { Ui.toast("রক্তের গ্রুপ নির্বাচন করুন", true); return; }
                if (!Ui.validate(new Ui.Field[]{fName, fPhone, fAddr, fEmail, fPass})) return;
                Data.AuthResult r = Data.register(
                        fName.input.getText().toString().trim(),
                        fEmail.input.getText().toString().trim(),
                        fPhone.input.getText().toString().trim(),
                        bgRef.get(),
                        fAddr.input.getText().toString().trim(),
                        "", fPass.input.getText().toString(),
                        fRef.input.getText().toString().trim());
                if (!r.ok) { Ui.toast(r.msg, true); return; }
                Ui.toast("রেজিস্ট্রেশন সফল! স্বাগতম", false);
                Ui.host.go("home", true);
            }
        });
        go.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(50)));
        btnWrap.addView(go);
        body.addView(btnWrap);
    }

    static void addSwitch(Context c, LinearLayout body, String prefix, String link, final Runnable onLink) {
        LinearLayout l = Ui.h(c);
        l.setGravity(Gravity.CENTER);
        l.setPadding(0, D.dp(14), 0, 0);
        TextView p = Ui.txt(c, prefix + "  ", 12.5f, D.onSurfaceVar, 400);
        l.addView(p);
        TextView lk = Ui.txt(c, link, 12.5f, D.primary, 700);
        lk.setPadding(0, 0, 0, D.dp(2));
        lk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { if (onLink != null) onLink.run(); }
        });
        l.addView(lk);
        body.addView(l);
    }
}
