package com.hizlydighapara.halalrokto;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/** রিইউজেবল নেটিভ UI কম্পোনেন্ট — M3 ডিজাইন। */
public final class Ui {

    private Ui() {}

    public static MainActivity host;

    /* ── Layout params helpers ──────────────────────────────── */
    public static LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w, h); }
    public static LinearLayout.LayoutParams lp(int w, int h, int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, h);
        p.setMargins(D.dp(l), D.dp(t), D.dp(r), D.dp(b));
        return p;
    }
    public static FrameLayout.LayoutParams flp(int w, int h, int gravity) {
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(w, h);
        p.gravity = gravity;
        return p;
    }
    public static FrameLayout.LayoutParams flp(int w, int h, int gravity, int marginDp) {
        FrameLayout.LayoutParams p = flp(w, h, gravity);
        int m = D.dp(marginDp);
        p.setMargins(m, m, m, m);
        return p;
    }
    public static FrameLayout.LayoutParams flpMargin(int w, int h, int gravity, int m) {
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(w, h);
        p.gravity = gravity;
        p.setMargins(D.dp(m), D.dp(m), D.dp(m), D.dp(m));
        return p;
    }

    public static LinearLayout v(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }
    public static LinearLayout h(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    /* ── Text & icons ───────────────────────────────────────── */
    public static TextView txt(Context c, String s, float sp, int color, Typeface tf) {
        TextView t = D.text(c, s, sp, color, tf);
        t.setLineSpacing(0, 1.22f);
        return t;
    }
    public static TextView txt(Context c, String s, float sp, int color, int weight) {
        return txt(c, s, sp, color, weight == 700 ? D.tfBold : weight == 600 ? D.tfSemi : weight == 500 ? D.tfMedium : D.tfRegular);
    }

    public static ImageView icon(Context c, int resId, int sizeDp, int tintColor) {
        ImageView iv = new ImageView(c);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(D.dp(sizeDp), D.dp(sizeDp));
        iv.setLayoutParams(p);
        iv.setImageResource(resId);
        if (tintColor != 0) iv.setColorFilter(tintColor);
        return iv;
    }

    public static ImageView iconBtn(Context c, int resId, int tint, final Runnable action) {
        ImageView iv = new ImageView(c);
        iv.setImageResource(resId);
        iv.setColorFilter(tint);
        int pad = D.dp(9);
        iv.setPadding(pad, pad, pad, pad);
        iv.setBackground(D.ripple(D.round(Color.TRANSPARENT, 40), D.withAlpha(tint, 60)));
        iv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { if (action != null) action.run(); }
        });
        return iv;
    }

    /* ── App bars ───────────────────────────────────────────── */
    /** Hero app bar — red gradient, big title, watermark drop. */
    public static LinearLayout heroBar(final Context c, String title, String subtitle, boolean back,
                                       View[] actions, final Runnable onBack) {
        LinearLayout bar = h(c);
        bar.setBackground(D.gradAngle(D.heroGrad, 0, 315));
        bar.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
        bar.setPadding(D.dp(16), D.dp(18), D.dp(8), D.dp(18));

        if (back) {
            ImageView bk = icon(c, R.drawable.ic_back, 22, 0xFFFFFFFF);
            bk.setBackground(D.ripple(D.round(D.withAlpha(0xFFFFFFFF, 46), 50), D.rippleColorLight));
            LinearLayout.LayoutParams bp = lp(D.dp(38), D.dp(38));
            bp.rightMargin = D.dp(10);
            bp.gravity = Gravity.CENTER_VERTICAL;
            bk.setLayoutParams(bp);
            bk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { if (onBack != null) onBack.run(); }
            });
            bar.addView(bk);
        }

        LinearLayout tv = v(c);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView t = txt(c, title, 21, 0xFFFFFFFF, 700);
        tv.addView(t);
        if (subtitle != null) {
            TextView st = txt(c, subtitle, 11.5f, D.withAlpha(0xFFFFFFFF, 205), 500);
            st.setPadding(0, D.dp(2), 0, 0);
            tv.addView(st);
        }
        bar.addView(tv);

        if (actions != null)
            for (View a : actions) {
                LinearLayout.LayoutParams ap = lp(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ap.rightMargin = D.dp(6);
                if (a.getLayoutParams() != null) {
                    ap.width = a.getLayoutParams().width;
                    ap.height = a.getLayoutParams().height;
                }
                a.setLayoutParams(ap);
                bar.addView(a);
            }
        return bar;
    }

    /** Plain surface app bar with back button. */
    public static LinearLayout appBar(final Context c, String title, String subtitle, final Runnable onBack) {
        LinearLayout bar = h(c);
        bar.setBackground(D.round(D.bg, 0));
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(D.dp(8), D.dp(10), D.dp(16), D.dp(10));

        if (onBack != null) {
            ImageView bk = icon(c, R.drawable.ic_back, 20, D.onSurface);
            bk.setBackground(D.ripple(D.round(D.surfaceCH, 50), D.rippleColor));
            LinearLayout.LayoutParams bp = lp(D.dp(38), D.dp(38));
            bp.rightMargin = D.dp(12);
            bk.setLayoutParams(bp);
            bk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { onBack.run(); }
            });
            bar.addView(bk);
        } else bar.setPadding(D.dp(18), D.dp(10), D.dp(16), D.dp(10));

        LinearLayout tv = v(c);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tv.addView(txt(c, title, 18, D.onSurface, 700));
        if (subtitle != null) {
            TextView st = txt(c, subtitle, 11.5f, D.onSurfaceVar, 500);
            st.setPadding(0, D.dp(1), 0, 0);
            tv.addView(st);
        }
        bar.addView(tv);
        return bar;
    }

    /* ── Buttons ────────────────────────────────────────────── */
    public static final int BTN_GRAD = 0, BTN_TONAL = 1, BTN_SUCCESS = 2, BTN_TEXT = 3, BTN_OUTLINE = 4;

    public static TextView btn(final Context c, String label, int style) {
        TextView t = new TextView(c);
        t.setText(label);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(D.tfSemi);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        int h = D.dp(48);
        t.setMinHeight(h);
        t.setPadding(D.dp(22), 0, D.dp(22), D.dp(2));
        switch (style) {
            case BTN_GRAD:
                t.setTextColor(0xFFFFFFFF);
                t.setBackground(D.ripple(D.gradAngle(D.redGrad, 50, 315), D.rippleColorLight));
                t.setElevation(D.dp(3));
                break;
            case BTN_TONAL:
                t.setTextColor(D.onPrimaryC);
                t.setBackground(D.rippleSolid(D.primaryC, 50, D.rippleColor));
                break;
            case BTN_SUCCESS:
                t.setTextColor(D.onSuccessC);
                t.setBackground(D.rippleSolid(D.successC, 50, D.withAlpha(D.success, 60)));
                break;
            case BTN_TEXT:
                t.setTextColor(D.primary);
                t.setBackground(D.ripple(D.round(Color.TRANSPARENT, 50), D.rippleColor));
                break;
            case BTN_OUTLINE:
                t.setTextColor(D.onSurface);
                t.setBackground(D.ripple(D.roundStroke(Color.TRANSPARENT, 50, D.outlineVar, 1), D.rippleColor));
                break;
        }
        t.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { host.haptic(8); }
        });
        return t;
    }

    public static TextView btn(Context c, String label, int style, final Runnable action) {
        TextView t = btn(c, label, style);
        t.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { host.haptic(8); if (action != null) action.run(); }
        });
        return t;
    }

    public static TextView btnSmall(Context c, String label, int style, final Runnable action) {
        TextView t = btn(c, label, style, action);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        t.setMinHeight(D.dp(36));
        t.setPadding(D.dp(16), 0, D.dp(16), D.dp(1));
        return t;
    }

    /* ── Badges & chips ─────────────────────────────────────── */
    public static TextView bgroup(Context c, String g, boolean solid, float sp) {
        TextView t = txt(c, g, sp, solid ? 0xFFFFFFFF : D.primary, 700);
        int ph = D.dp(7), pv = D.dp(3);
        t.setPadding(ph, pv, ph, pv + D.dp(1));
        t.setBackground(solid
                ? D.gradAngle(D.redGrad, 12, 315)
                : D.roundStroke(D.primaryC, 12, D.primary, 1.4f));
        t.setElevation(solid ? D.dp(2) : 0);
        LinearLayout.LayoutParams p = lp(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        t.setLayoutParams(p);
        return t;
    }

    /** tone: 0=ok 1=warn 2=err 3=info */
    public static TextView pill(Context c, String label, int tone) {
        int bg = tone == 0 ? D.successC : tone == 1 ? 0xFFFFF3CD : tone == 2 ? D.errorC : D.surfaceCH;
        int fg = tone == 0 ? D.onSuccessC : tone == 1 ? 0xFF6B4E00 : tone == 2 ? D.onErrorC : D.onSurfaceVar;
        TextView t = txt(c, label, 10.5f, fg, 600);
        int ph = D.dp(9), pv = D.dp(3);
        t.setPadding(ph, pv, ph, pv + D.dp(1));
        t.setBackground(D.round(bg, 50));
        return t;
    }

    public static TextView rankBadge(Context c, String style) {
        String label;
        int bg, fg;
        switch (style) {
            case "platinum": label = "প্লাটিনাম ডোনার"; bg = 0xFFE5E4E2; fg = 0xFF3E4A52; break;
            case "gold": label = "গোল্ড ডোনার"; bg = 0xFFFFE082; fg = 0xFF5D4200; break;
            case "silver": label = "সিলভার ডোনার"; bg = 0xFFE0E0E0; fg = 0xFF444444; break;
            case "bronze": label = "ব্রোঞ্জ ডোনার"; bg = 0xFFEDBB99; fg = 0xFF4E2607; break;
            case "friend": label = "রক্তবন্ধু"; bg = D.primaryC; fg = D.onPrimaryC; break;
            default: label = "নতুন ডোনার"; bg = D.surfaceCH; fg = D.onSurfaceVar; break;
        }
        if (D.dark && (style.equals("platinum") || style.equals("gold") || style.equals("silver") || style.equals("bronze"))) {
            bg = D.withAlpha(bg, 200);
        }
        TextView t = txt(c, label, 10.5f, fg, 600);
        t.setPadding(D.dp(10), D.dp(3), D.dp(10), D.dp(4));
        t.setBackground(D.round(bg, 50));
        return t;
    }

    public static TextView avatar(Context c, String initial, int bgColor, float sizeDp, float sp) {
        TextView t = txt(c, initial, sp, 0xFFFFFFFF, 700);
        t.setGravity(Gravity.CENTER);
        t.setBackground(D.round(bgColor, sizeDp / 2f));
        t.setLayoutParams(lp(D.dp(sizeDp), D.dp(sizeDp)));
        return t;
    }

    /* ── Structural components ──────────────────────────────── */
    public static TextView sectionHeader(Context c, String title) {
        TextView t = txt(c, title, 14.5f, D.onSurface, 700);
        t.setPadding(D.dp(18), D.dp(14), D.dp(18), D.dp(8));
        return t;
    }

    public static LinearLayout card(Context c) {
        LinearLayout l = v(c);
        l.setBackground(D.roundStroke(D.surfaceCLo, 20, D.outlineVar, 1));
        return l;
    }

    public static LinearLayout cardPad(Context c) {
        LinearLayout l = card(c);
        l.setPadding(D.dp(16), D.dp(14), D.dp(16), D.dp(14));
        return l;
    }

    /** FAB — bottom-right anchored */
    public static TextView fab(final Context c, String label, final Runnable action) {
        TextView t = new TextView(c);
        t.setText(label);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(D.tfSemi);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        t.setTextColor(0xFFFFFFFF);
        t.setPadding(D.dp(20), D.dp(14), D.dp(20), D.dp(16));
        t.setBackground(D.ripple(D.gradAngle(D.redGrad, 50, 315), D.rippleColorLight));
        t.setElevation(D.dp(6));
        t.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { host.haptic(10); action.run(); }
        });
        return t;
    }

    public static LinearLayout statTile(Context c, String num, String label, boolean hot) {
        LinearLayout l = v(c);
        l.setGravity(Gravity.CENTER);
        l.setPadding(D.dp(6), D.dp(12), D.dp(6), D.dp(12));
        l.setBackground(D.round(D.surfaceC, 18));
        TextView n = txt(c, num, 22, hot ? D.primary : D.onSurface, 700);
        n.setGravity(Gravity.CENTER);
        l.addView(n);
        TextView lb = txt(c, label, 10.5f, D.onSurfaceVar, 500);
        lb.setGravity(Gravity.CENTER);
        lb.setPadding(0, D.dp(3), 0, 0);
        l.addView(lb);
        l.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        return l;
    }

    public static LinearLayout menuRow(final Context c, int iconRes, int iconBg, int iconFg,
                                       String title, String sub, View end, final Runnable action) {
        LinearLayout row = h(c);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(D.dp(16), D.dp(12), D.dp(14), D.dp(12));
        row.setBackground(D.ripple(D.roundStroke(D.surfaceCLo, 18, D.outlineVar, 1), D.rippleColor));

        ImageView iv = icon(c, iconRes, 20, iconFg);
        iv.setBackground(D.round(iconBg, 14));
        int p = D.dp(10);
        iv.setPadding(p, p, p, p);
        row.addView(iv);
        LinearLayout.LayoutParams ip = (LinearLayout.LayoutParams) iv.getLayoutParams();
        ip.rightMargin = D.dp(13);
        iv.setLayoutParams(ip);

        LinearLayout m = v(c);
        m.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        m.addView(txt(c, title, 14, D.onSurface, 600));
        if (sub != null) {
            TextView s = txt(c, sub, 11.5f, D.onSurfaceVar, 400);
            s.setPadding(0, D.dp(2), 0, 0);
            m.addView(s);
        }
        row.addView(m);
        if (end != null) row.addView(end);

        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { host.haptic(8); if (action != null) action.run(); }
        });
        return row;
    }

    public static LinearLayout emptyState(Context c, int iconRes, String title, String sub) {
        LinearLayout l = v(c);
        l.setGravity(Gravity.CENTER_HORIZONTAL);
        l.setPadding(D.dp(30), D.dp(40), D.dp(30), D.dp(40));
        ImageView iv = icon(c, iconRes, 44, D.outline);
        iv.setPadding(0, 0, 0, D.dp(10));
        l.addView(iv);
        TextView t = txt(c, title, 15, D.onSurface, 700);
        t.setGravity(Gravity.CENTER);
        l.addView(t);
        TextView s = txt(c, sub, 12, D.onSurfaceVar, 400);
        s.setGravity(Gravity.CENTER);
        s.setPadding(0, D.dp(6), 0, 0);
        s.setLineSpacing(0, 1.4f);
        l.addView(s);
        return l;
    }

    /* ── Input fields ───────────────────────────────────────── */
    public static final int F_TEXT = 0, F_EMAIL = 1, F_PASS = 2, F_PHONE = 3, F_NUMBER = 4, F_DATE = 5, F_AREA = 6;

    public static final class Field {
        public LinearLayout root;
        public EditText input;
        public TextView error;
        public TextView label;
        Runnable revalidate;
    }

    public static Field field(final Context c, String labelText, int type, String value, String hint) {
        final Field f = new Field();
        LinearLayout root = v(c);
        root.setPadding(D.dp(18), D.dp(8), D.dp(18), D.dp(4));

        TextView label = txt(c, labelText, 12.5f, D.onSurfaceVar, 600);
        label.setPadding(D.dp(4), 0, 0, D.dp(6));
        root.addView(label);

        EditText et = new EditText(c);
        et.setTypeface(D.tfRegular);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        et.setTextColor(D.onSurface);
        et.setHintTextColor(D.outline);
        et.setHint(hint == null ? "" : hint);
        et.setBackground(D.roundStroke(D.surfaceC, 14, D.outlineVar, 1.2f));
        et.setPadding(D.dp(14), D.dp(12), D.dp(14), D.dp(12));
        et.setSingleLine(type != F_AREA);
        if (type == F_AREA) et.setMinLines(2);
        if (type == F_EMAIL) et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        else if (type == F_PASS) et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        else if (type == F_PHONE) { et.setInputType(InputType.TYPE_CLASS_PHONE); et.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(11)}); }
        else if (type == F_NUMBER) et.setInputType(InputType.TYPE_CLASS_NUMBER);
        else if (type == F_DATE) et.setInputType(InputType.TYPE_NULL); // pick only
        else if (type == F_AREA) et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        else et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        if (value != null && type != F_DATE) et.setText(value);
        if (type == F_DATE) {
            if (value != null && value.length() == 10) {
                et.setText(Bn.fmtDate(Bn.parseYmd(value)));
                et.setTag(value);
            }
            et.setClickable(true);
            et.setFocusable(false);
            et.setCursorVisible(false);
            final EditText fet = et;
            et.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    long init = value != null && value.length() == 10 ? Bn.parseYmd(value) : System.currentTimeMillis();
                    Calendar cc = new GregorianCalendar();
                    cc.setTimeInMillis(init);
                    DatePickerDialog dlg = new DatePickerDialog(c, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker dp, int y, int m, int d) {
                            String ymd = String.format(java.util.Locale.US, "%04d-%02d-%02d", y, m + 1, d);
                            fet.setText(Bn.fmtDate(Bn.parseYmd(ymd)));
                            fet.setTag(ymd);
                            if (f.error != null) {
                                f.error.setVisibility(View.GONE);
                                fet.setBackground(D.roundStroke(D.surfaceC, 14, D.outlineVar, 1.2f));
                            }
                        }
                    }, cc.get(Calendar.YEAR), cc.get(Calendar.MONTH), cc.get(Calendar.DAY_OF_MONTH));
                    dlg.getDatePicker().setMaxDate(System.currentTimeMillis());
                    dlg.show();
                }
            });
        }
        root.addView(et);

        TextView err = txt(c, " ", 11, D.error, 500);
        err.setPadding(D.dp(6), D.dp(3), 0, 0);
        err.setVisibility(View.GONE);
        root.addView(err);

        f.root = root; f.input = et; f.error = err; f.label = label;
        return f;
    }

    public static boolean validate(Field[] fields) {
        boolean ok = true;
        for (Field f : fields) {
            String v = f.input.getText().toString().trim();
            String errMsg = null;
            if (f.input.getInputType() == 0) { /* date */ }
            boolean isEmail = (f.input.getInputType() & InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) != 0;
            boolean isPhone = (f.input.getInputType() & InputType.TYPE_CLASS_PHONE) != 0;
            boolean isPass = (f.input.getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0;
            if (v.isEmpty()) errMsg = "এই ঘরটি পূরণ করুন";
            else if (isEmail && !v.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) errMsg = "সঠিক ইমেইল দিন";
            else if (isPhone && !v.matches("01\\d{9}")) errMsg = "সঠিক ফোন নম্বর দিন (01XXXXXXXXX)";
            else if (isPass && v.length() < 6) errMsg = "কমপক্ষে ৬ অক্ষর হতে হবে";
            if (errMsg != null) {
                ok = false;
                f.error.setText(errMsg);
                f.error.setVisibility(View.VISIBLE);
                f.input.setBackground(D.roundStroke(D.surfaceC, 14, D.error, 1.4f));
            }
        }
        return ok;
    }

    /* ── Blood group selector ───────────────────────────────── */
    public static LinearLayout bloodGroupGrid(final Context c, String selected, final java.util.concurrent.atomic.AtomicReference<String> ref) {
        LinearLayout wrap = v(c);
        wrap.setPadding(D.dp(18), D.dp(6), D.dp(18), D.dp(4));
        TextView label = txt(c, "রক্তের গ্রুপ *", 12.5f, D.onSurfaceVar, 600);
        label.setPadding(D.dp(4), 0, 0, D.dp(6));
        wrap.addView(label);

        LinearLayout grid = new LinearLayout(c);
        grid.setOrientation(LinearLayout.VERTICAL);
        for (int r = 0; r < 2; r++) {
            LinearLayout row = h(c);
            row.setLayoutParams(lp(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            for (int i = r * 4; i < r * 4 + 4; i++) {
                final String g = Data.BLOOD_GROUPS[i];
                final TextView t = new TextView(c);
                t.setText(g);
                t.setGravity(Gravity.CENTER);
                t.setTypeface(D.tfBold);
                t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                p.setMargins(D.dp(3), D.dp(3), D.dp(3), D.dp(3));
                t.setLayoutParams(p);
                t.setMinHeight(D.dp(42));
                final boolean sel = g.equals(selected);
                t.setTextColor(sel ? 0xFFFFFFFF : D.onSurface);
                t.setBackground(sel
                        ? D.ripple(D.gradAngle(D.redGrad, 14, 315), D.rippleColorLight)
                        : D.ripple(D.roundStroke(D.surfaceC, 14, D.outlineVar, 1.2f), D.rippleColor));
                t.setOnClickListener(new View.OnClickListener() {
                    boolean on = sel;
                    public void onClick(View v) {
                        host.haptic(6);
                        on = true; ref.set(g);
                        ViewGroup parent = (ViewGroup) v.getParent();
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            TextView ch = (TextView) parent.getChildAt(i);
                            boolean isSel = ch == v;
                            ch.setTextColor(isSel ? 0xFFFFFFFF : D.onSurface);
                            ch.setBackground(isSel
                                    ? D.ripple(D.gradAngle(D.redGrad, 14, 315), D.rippleColorLight)
                                    : D.ripple(D.roundStroke(D.surfaceC, 14, D.outlineVar, 1.2f), D.rippleColor));
                        }
                    }
                });
                row.addView(t);
            }
            grid.addView(row);
        }
        LinearLayout.LayoutParams gp = lp(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        grid.setLayoutParams(gp);
        wrap.addView(grid);
        return wrap;
    }

    /* ── Bottom sheet ───────────────────────────────────────── */
    public interface SheetCallback { void onSheet(LinearLayout body, Runnable close); }

    public static void sheet(final Context c, String title, int iconRes, boolean scrollable, final SheetCallback cb) {
        final FrameLayout overlay = new FrameLayout(c);
        overlay.setBackgroundColor(0x99000000);

        final LinearLayout panel = v(c);
        panel.setBackground(D.roundStroke(D.surfaceCLo, 26, D.outlineVar, 1));
        panel.setElevation(D.dp(12));
        panel.setTag("panel");

        // drag handle
        View handle = new View(c);
        handle.setBackground(D.round(D.outlineVar, 4));
        handle.setLayoutParams(lp(D.dp(40), D.dp(4)));
        LinearLayout.LayoutParams hp = (LinearLayout.LayoutParams) handle.getLayoutParams();
        hp.gravity = Gravity.CENTER_HORIZONTAL;
        hp.topMargin = D.dp(10); hp.bottomMargin = D.dp(4);
        handle.setLayoutParams(hp);
        panel.addView(handle);

        // header
        LinearLayout head = h(c);
        head.setPadding(D.dp(20), D.dp(6), D.dp(12), D.dp(10));
        if (iconRes != 0) head.addView(icon(c, iconRes, 22, D.primary));
        TextView t = txt(c, title, 16.5f, D.onSurface, 700);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tp.leftMargin = iconRes != 0 ? D.dp(10) : 0;
        t.setLayoutParams(tp);
        head.addView(t);
        head.addView(iconBtn(c, R.drawable.ic_close, D.onSurfaceVar, new Runnable() {
            public void run() { closeSheet(overlay, panel); }
        }));
        panel.addView(head);

        View hr = new View(c);
        hr.setBackground(D.round(D.outlineVar, 1));
        hr.setLayoutParams(lp(ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, D.dp(0.8f))));
        panel.addView(hr);

        LinearLayout bodyWrap;
        if (scrollable) {
            ScrollView sc = new ScrollView(c);
            sc.setFillViewport(true);
            bodyWrap = v(c);
            sc.addView(bodyWrap);
            panel.addView(sc, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        } else {
            bodyWrap = v(c);
            panel.addView(bodyWrap, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        FrameLayout.LayoutParams pp = flp(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        panel.setLayoutParams(pp);

        overlay.addView(panel);
        overlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { closeSheet(overlay, panel); }
        });

        host.openSheet(overlay, panel);

        // cap sheet height to ~72% of screen
        panel.post(new Runnable() {
            public void run() {
                int screenH = host.getResources().getDisplayMetrics().heightPixels;
                int cap = Math.round(screenH * 0.72f);
                if (panel.getHeight() > cap) {
                    FrameLayout.LayoutParams p2 = (FrameLayout.LayoutParams) panel.getLayoutParams();
                    p2.height = cap;
                    panel.setLayoutParams(p2);
                }
            }
        });

        // default bottom padding for body content
        bodyWrap.setPadding(0, 0, 0, D.dp(16));
        cb.onSheet(bodyWrap, new Runnable() {
            public void run() { closeSheet(overlay, panel); }
        });
    }

    static void closeSheet(FrameLayout overlay, LinearLayout panel) {
        host.haptic(6);
        host.closeSheet(overlay, panel);
    }

    /* ── Toast ──────────────────────────────────────────────── */
    public static void toast(String msg, boolean error) {
        Context c = host;
        LinearLayout bar = h(c);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(D.dp(14), D.dp(10), D.dp(14), D.dp(11));
        bar.setBackground(D.ripple(D.round(error ? D.inverseSurface : D.success, 14), D.rippleColorLight));
        bar.setElevation(D.dp(8));
        bar.addView(icon(c, error ? R.drawable.ic_warn : R.drawable.ic_check, 18, error ? D.inverseOnSurface : 0xFF072712));
        TextView t = txt(c, msg, 12.5f, error ? D.inverseOnSurface : 0xFF072712, 600);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tp.leftMargin = D.dp(9);
        t.setLayoutParams(tp);
        bar.addView(t);
        host.showToast(bar);
    }

    /* ── Confirm dialog ─────────────────────────────────────── */
    public static void confirm(Context c, String title, String msg, String okLabel, boolean danger, final Runnable onOk) {
        AlertDialog.Builder b = new AlertDialog.Builder(c, android.R.style.Theme_Material_Light_Dialog_Alert);
        b.setTitle(title);
        b.setMessage(msg);
        b.setPositiveButton(okLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int w) { if (onOk != null) onOk.run(); }
        });
        b.setNegativeButton("বাতিল", null);
        AlertDialog d = b.show();
        d.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(D.tfSemi);
        if (danger) d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(D.error);
    }
}
