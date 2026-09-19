package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

/** ডিজাইন সিস্টেম — Material 3 (red seed), light + dark, programmatic. */
public final class D {

    private D() {}

    /* ── Theme tokens ───────────────────────────────────────── */
    public static boolean dark = false;

    public static int primary, onPrimary, primaryC, onPrimaryC, primaryHover;
    public static int secondary, secondaryC, onSecondaryC;
    public static int tertiary, tertiaryC, onTertiaryC;
    public static int success, successC, onSuccessC;
    public static int error, errorC, onErrorC;
    public static int bg, onBg, surface, onSurface;
    public static int surfaceCL, surfaceC, surfaceCH, surfaceCHH, surfaceCLo;
    public static int onSurfaceVar, outline, outlineVar;
    public static int inverseSurface, inverseOnSurface;
    public static int[] heroGrad, redGrad, heroGradDeep;
    public static int rippleColor, rippleColorLight;

    public static void setDark(boolean d) {
        dark = d;
        if (!d) {
            primary = 0xFFB3261E; onPrimary = 0xFFFFFFFF;
            primaryC = 0xFFFFDAD6; onPrimaryC = 0xFF410E0B;
            secondary = 0xFF775652; secondaryC = 0xFFFFDAD6; onSecondaryC = 0xFF2C1512;
            tertiary = 0xFF705C2E; tertiaryC = 0xFFFBDFAB; onTertiaryC = 0xFF251A00;
            success = 0xFF1E6B33; successC = 0xFFC4EFCE; onSuccessC = 0xFF072712;
            error = 0xFF93000A; errorC = 0xFFFFDAD6; onErrorC = 0xFF410002;
            bg = 0xFFFCF8F8; onBg = 0xFF201A19; surface = 0xFFFCF8F8; onSurface = 0xFF201A19;
            surfaceCLo = 0xFFFFFFFF; surfaceCL = 0xFFF6EDED; surfaceC = 0xFFF1EAEA;
            surfaceCH = 0xFFEBE4E4; surfaceCHH = 0xFFE5DEDE;
            onSurfaceVar = 0xFF5D4F4D; outline = 0xFF857371; outlineVar = 0xFFD8C7C5;
            inverseSurface = 0xFF362F2E; inverseOnSurface = 0xFFFBEEEC;
            heroGrad = new int[]{0xFFC13328, 0xFFB3261E, 0xFF7E130D};
            redGrad = new int[]{0xFFD0453C, 0xFFB3261E, 0xFF8E1710};
            rippleColor = 0x33B3261E; rippleColorLight = 0x33FFFFFF;
        } else {
            primary = 0xFFFFB4AB; onPrimary = 0xFF690002;
            primaryC = 0xFF93000A; onPrimaryC = 0xFFFFDAD6;
            secondary = 0xFFE7BDB7; secondaryC = 0xFF5D3F3B; onSecondaryC = 0xFFFFDAD6;
            tertiary = 0xFFE0C26C; tertiaryC = 0xFF544319; onTertiaryC = 0xFFFBDFAB;
            success = 0xFF8BD89E; successC = 0xFF1E4E2C; onSuccessC = 0xFFC4EFCE;
            error = 0xFFFFB4AB; errorC = 0xFF690002; onErrorC = 0xFFFFDAD6;
            bg = 0xFF191111; onBg = 0xFFF1DEDB; surface = 0xFF191111; onSurface = 0xFFF1DEDB;
            surfaceCLo = 0xFF140908; surfaceCL = 0xFF221918; surfaceC = 0xFF261D1C;
            surfaceCH = 0xFF312827; surfaceCHH = 0xFF3C3231;
            onSurfaceVar = 0xFFE0D2D0; outline = 0xFFAB9A98; outlineVar = 0xFF5D4F4D;
            inverseSurface = 0xFFF1DEDB; inverseOnSurface = 0xFF362F2E;
            heroGrad = new int[]{0xFF8E1710, 0xFF6E0F09, 0xFF4C0906};
            redGrad = new int[]{0xFF9E1710, 0xFF7E130D, 0xFF5C0E09};
            rippleColor = 0x33FFB4AB; rippleColorLight = 0x33FFFFFF;
        }
    }

    /* ── Metrics ────────────────────────────────────────────── */
    public static float density = 2.5f;
    public static int dp(float v) { return Math.round(v * density); }

    /* ── Typefaces ──────────────────────────────────────────── */
    public static Typeface tfRegular, tfMedium, tfSemi, tfBold;

    public static void init(Context c) {
        density = c.getResources().getDisplayMetrics().density;
        tfRegular = c.getResources().getFont(R.font.notosansbengali_regular);
        tfMedium = c.getResources().getFont(R.font.notosansbengali_medium);
        tfSemi = c.getResources().getFont(R.font.notosansbengali_semibold);
        tfBold = c.getResources().getFont(R.font.notosansbengali_bold);
        setDark(false);
    }

    /* ── Drawable factories ─────────────────────────────────── */
    public static GradientDrawable round(int color, float radiusDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radiusDp));
        return g;
    }

    public static GradientDrawable roundStroke(int color, float radiusDp, int strokeColor, float strokeDp) {
        GradientDrawable g = round(color, radiusDp);
        g.setStroke(Math.max(1, dp(strokeDp)), strokeColor);
        return g;
    }

    public static GradientDrawable grad(int[] colors, float radiusDp) {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        g.setCornerRadius(dp(radiusDp));
        return g;
    }

    public static GradientDrawable gradAngle(int[] colors, float radiusDp, int angle) {
        GradientDrawable.Orientation o = GradientDrawable.Orientation.TL_BR;
        switch (((angle % 360) + 360) % 360) {
            case 0: o = GradientDrawable.Orientation.LEFT_RIGHT; break;
            case 45: o = GradientDrawable.Orientation.TL_BR; break;
            case 90: o = GradientDrawable.Orientation.BOTTOM_TOP; break;
            case 135: o = GradientDrawable.Orientation.BL_TR; break;
            case 180: o = GradientDrawable.Orientation.RIGHT_LEFT; break;
            case 225: o = GradientDrawable.Orientation.TR_BL; break;
            case 270: o = GradientDrawable.Orientation.TOP_BOTTOM; break;
            case 315: o = GradientDrawable.Orientation.BR_TL; break;
        }
        GradientDrawable g = new GradientDrawable(o, colors);
        g.setCornerRadius(dp(radiusDp));
        return g;
    }

    public static Drawable ripple(Drawable bg, int color) {
        return new RippleDrawable(ColorStateList.valueOf(color), bg,
                new GradientDrawable()); // transparent content masks ripple to bg shape
    }

    public static Drawable rippleSolid(int bgColor, float radiusDp, int rippleCol) {
        return ripple(round(bgColor, radiusDp), rippleCol);
    }

    /* ── Quick view helpers ─────────────────────────────────── */
    public static TextView text(Context c, String s, float sizeSp, int color, Typeface tf) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        t.setTextColor(color);
        t.setTypeface(tf);
        t.setIncludeFontPadding(true);
        return t;
    }

    public static TextView text(Context c, String s, float sizeSp, int color, int weight) {
        return text(c, s, sizeSp, color, weight == 700 ? tfBold : weight == 600 ? tfSemi : weight == 500 ? tfMedium : tfRegular);
    }

    public static int withAlpha(int color, int alpha255) {
        return (color & 0x00FFFFFF) | ((alpha255 & 0xFF) << 24);
    }

    public static int argb(float alpha, int color) {
        return Color.argb((int) (alpha * 255), Color.red(color), Color.green(color), Color.blue(color));
    }

    /* avatar palette — muted warm tones */
    public static final int[] AVATAR_COLORS = {
            0xFF775652, 0xFF5D4F4D, 0xFF8E5A54, 0xFF6B5E56,
            0xFF93625B, 0xFF5E6B52, 0xFF52666B, 0xFF6B5266};

    public static int avatarColor(String id) {
        int h = 0;
        for (int i = 0; i < id.length(); i++) h = (h * 31 + id.charAt(i)) % 997;
        return AVATAR_COLORS[h % AVATAR_COLORS.length];
    }
}
