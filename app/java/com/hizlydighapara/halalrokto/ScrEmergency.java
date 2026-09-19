package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/** জরুরি রক্তের আবেদন — তালিকা, নতুন আবেদন, কল/শেয়ার/পূর্ণ। */
public final class ScrEmergency {

    private ScrEmergency() {}

    public static View build(final Context c) {
        final ScrollView sc = new ScrollView(c);
        sc.setBackgroundColor(D.bg);
        final LinearLayout root = Ui.v(c);

        final Runnable[] render = new Runnable[1];
        render[0] = new Runnable() {
            public void run() {
                root.removeAllViews();
                root.addView(Ui.heroBar(c, "জরুরি রক্তের আবেদন", "মুহূর্তের প্রয়োজনে ডোনার খুঁজুন", true, null, new Runnable() {
                    public void run() { Ui.host.back(); }
                }));

                List<Data.Emergency> list = Data.visibleEmergencies();
                if (list.isEmpty()) {
                    root.addView(Ui.emptyState(c, R.drawable.ic_sos,
                            "এখন কোনো জরুরি আবেদন নেই",
                            "কারও জরুরি রক্তের প্রয়োজন হলে এখানে আবেদন করুন — সদস্যরা দ্রুত সাড়া দিতে পারবেন।"));
                } else {
                    LinearLayout listWrap = Ui.v(c);
                    listWrap.setPadding(D.dp(16), D.dp(16), D.dp(16), 0);
                    for (final Data.Emergency e : list) listWrap.addView(emgCard(c, e, render));
                    root.addView(listWrap);
                }

                // FAB
                FrameLayout fabWrap = new FrameLayout(c);
                LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, D.dp(84));
                fabWrap.setLayoutParams(fp);
                TextView fab = Ui.fab(c, "+  নতুন আবেদন", new Runnable() {
                    public void run() { newEmergencySheet(c, render); }
                });
                fab.setLayoutParams(Ui.flp(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM | Gravity.END, 16));
                fabWrap.addView(fab);
                root.addView(fabWrap);
                sc.requestLayout();
            }
        };
        render[0].run();
        sc.addView(root);
        return sc;
    }

    static LinearLayout emgCard(final Context c, final Data.Emergency e, final Runnable[] refresh) {
        LinearLayout card = Ui.v(c);
        card.setPadding(D.dp(16), D.dp(14), D.dp(16), D.dp(14));
        card.setBackground(D.roundStroke(D.surfaceCLo, 20, D.outlineVar, 1));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = D.dp(10);
        card.setLayoutParams(p);
        if (e.fulfilled) card.setAlpha(0.62f);

        // top row: pulse + group + time
        LinearLayout top = Ui.h(c);
        if (!e.fulfilled) {
            Views.PulseView pulse = new Views.PulseView(c);
            pulse.setLayoutParams(Ui.lp(D.dp(11), D.dp(11)));
            top.addView(pulse);
            ((LinearLayout.LayoutParams) pulse.getLayoutParams()).rightMargin = D.dp(10);
        }
        top.addView(Ui.bgroup(c, e.bloodGroup, true, 14));
        ((LinearLayout.LayoutParams) top.getChildAt(top.getChildCount() - 1).getLayoutParams()).rightMargin = D.dp(10);
        TextView units = Ui.txt(c, Bn.bn(e.units) + " ব্যাগ প্রয়োজন", 12.5f, D.onSurface, 700);
        top.addView(units);
        View sp = new View(c);
        sp.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        top.addView(sp);
        top.addView(Ui.pill(c, e.fulfilled ? "পূর্ণ হয়েছে" : "সক্রিয়", e.fulfilled ? 0 : 2));
        card.addView(top);

        // patient + hospital
        TextView name = Ui.txt(c, e.patientName, 16, D.onSurface, 700);
        name.setPadding(0, D.dp(10), 0, 0);
        card.addView(name);

        LinearLayout metaWrap = Ui.v(c);
        metaWrap.setPadding(0, D.dp(4), 0, 0);
        metaWrap.addView(metaRow(c, R.drawable.ic_hosp, e.hospital + (e.location.isEmpty() ? "" : " • " + e.location)));
        metaWrap.addView(metaRow(c, R.drawable.ic_clock, Bn.timeAgo(e.createdAt)));
        card.addView(metaWrap);

        if (!e.notes.isEmpty()) {
            TextView notes = Ui.txt(c, e.notes, 12f, D.onSurfaceVar, 400);
            notes.setLineSpacing(0, 1.45f);
            notes.setPadding(0, D.dp(8), 0, 0);
            card.addView(notes);
        }

        // actions
        LinearLayout actions = Ui.h(c);
        actions.setPadding(0, D.dp(12), 0, 0);
        TextView call = Ui.btnSmall(c, "কল করুন", Ui.BTN_SUCCESS, new Runnable() {
            public void run() { Ui.host.haptic(12); Ui.host.dial(e.contact); }
        });
        call.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(38), 1f));
        actions.addView(call);
        ((LinearLayout.LayoutParams) call.getLayoutParams()).rightMargin = D.dp(8);

        TextView share = Ui.btnSmall(c, "শেয়ার", Ui.BTN_TONAL, new Runnable() {
            public void run() {
                Ui.host.share("🚨 জরুরি " + e.bloodGroup + " রক্তের প্রয়োজন!\nরোগী: " + e.patientName
                        + "\nহাসপাতাল: " + e.hospital + "\nযোগাযোগ: " + e.contact
                        + "\n— " + Data.ORG, "জরুরি রক্ত শেয়ার");
            }
        });
        share.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(38), 1f));
        actions.addView(share);
        ((LinearLayout.LayoutParams) share.getLayoutParams()).rightMargin = D.dp(8);

        if (!e.fulfilled) {
            TextView done = Ui.btnSmall(c, "পূর্ণ হয়েছে ✓", Ui.BTN_OUTLINE, new Runnable() {
                public void run() {
                    Ui.confirm(c, "আবেদন পূর্ণ হিসেবে চিহ্নিত করবেন?",
                            "\"" + e.patientName + "\"-এর আবেদনটি পূর্ণ হয়েছে হিসেবে চিহ্নিত হবে এবং তালিকা থেকে সরে যাবে।",
                            "হ্যাঁ, পূর্ণ", false, new Runnable() {
                                public void run() {
                                    e.fulfilled = true;
                                    int idx = Data.stockIdx(e.bloodGroup);
                                    Data.s.stock[idx] = Math.max(0, Data.s.stock[idx] - e.units);
                                    Data.pushNotif("all", "emg", "জরুরি আবেদন পূর্ণ হয়েছে",
                                            e.patientName + "-এর " + e.bloodGroup + " আবেদন পূর্ণ হয়েছে। ধন্যবাদ সবাইকে!");
                                    Data.save();
                                    Ui.toast("আবেদনটি পূর্ণ হিসেবে চিহ্নিত হয়েছে", false);
                                    refresh[0].run();
                                }
                            });
                }
            });
            done.setLayoutParams(new LinearLayout.LayoutParams(0, D.dp(38), 1.15f));
            actions.addView(done);
        }
        card.addView(actions);

        /* ── SOS ব্রডকাস্ট — সামঞ্জস্যপূর্ণ সব ডোনারকে জানান ── */
        if (!e.fulfilled) {
            LinearLayout sos = Ui.h(c);
            sos.setPadding(0, D.dp(10), 0, 0);
            TextView sosBtn = Ui.btnSmall(c, "📡 সব ডোনারকে জানান (SOS)", Ui.BTN_GRAD, new Runnable() {
                public void run() {
                    Ui.host.haptic(24);
                    int same = Data.availableSame(e.bloodGroup).size();
                    int compat = Data.availableCompat(e.bloodGroup).size();
                    String msg = "🚨 SOS: " + e.patientName + "-এর জন্য " + Bn.bn(e.units) + " ব্যাগ "
                            + e.bloodGroup + " রক্ত জরুরি প্রয়োজন — " + e.hospital
                            + "। যোগাযোগ: " + e.contact + "। এখনই যে দিতে পারবেন, এগিয়ে আসুন!";
                    Chat.send("emergency", msg);
                    Ui.host.share(msg, "SOS ব্রডকাস্ট");
                    Data.pushNotif("all", "emg", "SOS ব্রডকাস্ট পাঠানো হয়েছে",
                            Bn.bn(same + compat) + " জন উপলব্ধ ডোনার (" + e.bloodGroup + " ও সামঞ্জস্যপূর্ণ) জানানো হয়েছে — জরুরি চ্যানেলে দেখুন।");
                    Ui.toast("জরুরি চ্যানেলে ব্রডকাস্ট হয়েছে — " + Bn.bn(same + compat) + " জন উপলব্ধ ডোনার", false);
                }
            });
            sosBtn.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, D.dp(42)));
            sos.addView(sosBtn);
            card.addView(sos);
        }
        return card;
    }

    static LinearLayout metaRow(Context c, int iconRes, String text) {
        LinearLayout row = Ui.h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        ImageView iv = Ui.icon(c, iconRes, 13, D.onSurfaceVar);
        row.addView(iv);
        ((LinearLayout.LayoutParams) iv.getLayoutParams()).rightMargin = D.dp(6);
        TextView t = Ui.txt(c, text, 11.5f, D.onSurfaceVar, 500);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(t);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.topMargin = D.dp(3);
        row.setLayoutParams(p);
        return row;
    }

    static void newEmergencySheet(final Context c, final Runnable[] refresh) {
        newEmergencySheet(c, refresh, null);
    }

    /** পূর্বনির্ধারিত গ্রুপসহ নতুন জরুরি আবেদন (স্টক বিস্তারিত থেকে)। */
    static void newEmergencySheet(final Context c, final Runnable[] refresh, final String presetGroup) {
        Ui.sheet(c, "নতুন জরুরি আবেদন", R.drawable.ic_sos, true, new Ui.SheetCallback() {
            public void onSheet(final LinearLayout body, final Runnable close) {
                final Ui.Field fPatient = Ui.field(c, "রোগীর নাম *", Ui.F_TEXT, null, "যেমন: জনাব আব্দুল জলিল (৬২)");
                final AtomicReference<String> bgRef = new AtomicReference<>(presetGroup);
                LinearLayout bgGrid = Ui.bloodGroupGrid(c, presetGroup, bgRef);
                final Ui.Field fUnits = Ui.field(c, "কত ব্যাগ প্রয়োজন *", Ui.F_NUMBER, "1", "যেমন: 2");
                final Ui.Field fHospital = Ui.field(c, "হাসপাতালের নাম *", Ui.F_TEXT, null, "যেমন: নাটোর সদর হাসপাতাল");
                final Ui.Field fLocation = Ui.field(c, "লোকেশন (ঐচ্ছিক)", Ui.F_TEXT, null, "যেমন: নাটোর সদর");
                final Ui.Field fContact = Ui.field(c, "যোগাযোগের ফোন *", Ui.F_PHONE, null, "01XXXXXXXXX");
                final Ui.Field fNotes = Ui.field(c, "বিবরণ (ঐচ্ছিক)", Ui.F_AREA, null, "অতিরিক্ত তথ্য থাকলে লিখুন...");
                body.addView(fPatient.root);
                body.addView(bgGrid);
                body.addView(fUnits.root);
                body.addView(fHospital.root);
                body.addView(fLocation.root);
                body.addView(fContact.root);
                body.addView(fNotes.root);

                LinearLayout btnWrap = Ui.v(c);
                btnWrap.setPadding(D.dp(18), D.dp(14), D.dp(18), 0);
                TextView save = Ui.btn(c, "আবেদন জানান", Ui.BTN_GRAD, new Runnable() {
                    public void run() {
                        if (bgRef.get() == null) { Ui.toast("রক্তের গ্রুপ নির্বাচন করুন", true); return; }
                        if (!Ui.validate(new Ui.Field[]{fPatient, fUnits, fHospital, fContact})) return;
                        Data.Emergency e = new Data.Emergency();
                        e.id = Bn.uid("em");
                        e.patientName = fPatient.input.getText().toString().trim();
                        e.bloodGroup = bgRef.get();
                        int units = 1;
                        try { units = Math.max(1, Math.min(10, Integer.parseInt(fUnits.input.getText().toString().trim()))); } catch (Exception ignored) {}
                        e.units = units;
                        e.hospital = fHospital.input.getText().toString().trim();
                        e.location = fLocation.input.getText().toString().trim();
                        e.contact = fContact.input.getText().toString().trim();
                        e.notes = fNotes.input.getText().toString().trim();
                        e.createdAt = System.currentTimeMillis();
                        e.createdBy = Data.s.session == null ? "" : Data.s.session;
                        e.fulfilled = false;
                        Data.s.emergencies.add(0, e);
                        Data.pushNotif("all", "emg", "নতুন জরুরি আবেদন: " + e.bloodGroup,
                                e.patientName + "-এর জন্য " + Bn.bn(e.units) + " ব্যাগ " + e.bloodGroup + " রক্ত প্রয়োজন — " + e.hospital + "। যোগাযোগ: " + e.contact);
                        Data.save();
                        Data.Sync.pushEmergency(e);
                        close.run();
                        Ui.toast("জরুরি আবেদন প্রকাশিত হয়েছে", false);
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
