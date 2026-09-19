package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** প্রোফাইল সম্পাদনা। */
public final class ScrProfile {

    private ScrProfile() {}

    public static View build(final Context c) {
        final ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        final LinearLayout root = Ui.v(c);

        final Runnable[] render = new Runnable[1];
        render[0] = new Runnable() {
            public void run() {
                root.removeAllViews();
                final Data.User me = Data.me();

                root.addView(Ui.heroBar(c, "প্রোফাইল সম্পাদনা", "আপনার তথ্য হালনাগাদ করুন", true, null, new Runnable() {
                    public void run() { Ui.host.back(); }
                }));

                /* avatar header card */
                LinearLayout card = Ui.v(c);
                card.setPadding(D.dp(16), D.dp(16), D.dp(16), D.dp(18));
                card.setBackground(D.roundStroke(D.surfaceCLo, 20, D.outlineVar, 1));
                LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cp.setMargins(D.dp(16), D.dp(16), D.dp(16), 0);
                card.setLayoutParams(cp);

                LinearLayout avWrap = Ui.v(c);
                avWrap.setGravity(Gravity.CENTER_HORIZONTAL);
                avWrap.addView(Ui.avatar(c, Bn.initials(me.name), D.avatarColor(me.id), 72, 26));
                LinearLayout badges = Ui.h(c);
                badges.setGravity(Gravity.CENTER);
                badges.setPadding(0, D.dp(10), 0, 0);
                badges.addView(Ui.bgroup(c, me.bloodType, true, 12));
                ((LinearLayout.LayoutParams) badges.getChildAt(0).getLayoutParams()).rightMargin = D.dp(8);
                if (me.verified) badges.addView(Ui.pill(c, "ভেরিফায়েড ✓", 0));
                else badges.addView(Ui.pill(c, "ভেরিফিকেশন অপেক্ষমান", 3));
                avWrap.addView(badges);
                card.addView(avWrap);

                final Ui.Field fName = Ui.field(c, "পূর্ণ নাম *", Ui.F_TEXT, me.name, null);
                final Ui.Field fPhone = Ui.field(c, "ফোন নম্বর *", Ui.F_PHONE, me.phone, null);
                final Ui.Field fEmail = Ui.field(c, "ইমেইল *", Ui.F_EMAIL, me.email, null);
                final Ui.Field fAddr = Ui.field(c, "ঠিকানা / এলাকা *", Ui.F_TEXT, me.address, null);
                final Ui.Field fNid = Ui.field(c, "জাতীয় পরিচয়পত্র নম্বর (ঐচ্ছিক)", Ui.F_TEXT, me.nid, "ভেরিফিকেশনের জন্য পরে ব্যবহৃত হতে পারে");

                card.addView(fName.root);
                card.addView(fPhone.root);
                card.addView(fEmail.root);
                card.addView(fAddr.root);
                card.addView(fNid.root);

                LinearLayout btnWrap = Ui.v(c);
                btnWrap.setPadding(D.dp(4), D.dp(14), D.dp(4), 0);
                TextView save = Ui.btn(c, "সংরক্ষণ করুন", Ui.BTN_GRAD, new Runnable() {
                    public void run() {
                        if (!Ui.validate(new Ui.Field[]{fName, fPhone, fEmail, fAddr})) return;
                        me.name = fName.input.getText().toString().trim();
                        me.phone = fPhone.input.getText().toString().trim();
                        me.email = fEmail.input.getText().toString().trim();
                        me.address = fAddr.input.getText().toString().trim();
                        me.nid = fNid.input.getText().toString().trim();
                        Data.save();
                        Ui.toast("প্রোফাইল আপডেট হয়েছে", false);
                        Ui.host.back();
                    }
                });
                save.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, D.dp(50)));
                btnWrap.addView(save);
                card.addView(btnWrap);
                root.addView(card);

                /* referral code card */
                LinearLayout refWrap = Ui.v(c);
                refWrap.setPadding(D.dp(16), D.dp(14), D.dp(16), D.dp(20));
                LinearLayout refCard = Ui.h(c);
                refCard.setGravity(Gravity.CENTER_VERTICAL);
                refCard.setPadding(D.dp(16), D.dp(13), D.dp(12), D.dp(13));
                refCard.setBackground(D.round(D.surfaceC, 16));
                LinearLayout t = Ui.v(c);
                t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                t.addView(Ui.txt(c, "রেফারেল কোড", 13, D.onSurface, 700));
                TextView cv = Ui.txt(c, me.referralCode, 11.5f, D.onSurfaceVar, 500);
                cv.setPadding(0, D.dp(2), 0, 0);
                t.addView(cv);
                refCard.addView(t);
                refCard.addView(Ui.btnSmall(c, "কপি", Ui.BTN_TONAL, new Runnable() {
                    public void run() {
                        android.content.ClipboardManager cm = (android.content.ClipboardManager)
                                c.getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cm != null) cm.setText(me.referralCode);
                        Ui.toast("কোড কপি হয়েছে", false);
                    }
                }));
                refWrap.addView(refCard);
                root.addView(refWrap);
            }
        };
        render[0].run();
        sc.addView(root);
        return sc;
    }
}
