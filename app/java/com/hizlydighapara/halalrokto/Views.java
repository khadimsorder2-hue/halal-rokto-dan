package com.hizlydighapara.halalrokto;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Random;

/** কাস্টম ক্যানভাস ভিউ — রিং, প্যাটার্ন, পালস। */
public final class Views {

    /** ৯০ দিনের কুলডাউন প্রগ্রেস রিং (sweep gradient + entry animation)। */
    public static class RingView extends View {
        private float progress = 0f; // 0..1
        private float animProgress = 0f;
        private int trackColor, ringBg;
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint gp = new Paint(Paint.ANTI_ALIAS_FLAG);
        private RectF rect = new RectF();
        private ValueAnimator anim;

        public RingView(Context c) {
            super(c);
            trackColor = 0xFFFFFFFF;
            ringBg = 0x33FFFFFF;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            gp.setStyle(Paint.Style.STROKE);
            gp.setStrokeCap(Paint.Cap.ROUND);
        }

        public void setProgress(float p) {
            this.progress = Math.max(0f, Math.min(1f, p));
            if (anim != null) anim.cancel();
            anim = ValueAnimator.ofFloat(0f, this.progress);
            anim.setDuration(900L);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator a) {
                    animProgress = (Float) a.getAnimatedValue();
                    invalidate();
                }
            });
            anim.start();
        }

        @Override
        protected void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh);
            int stroke = Math.round(w * 0.085f);
            paint.setStrokeWidth(stroke);
            gp.setStrokeWidth(stroke);
            rect.set(stroke / 2f + D.dp(2), stroke / 2f + D.dp(2), w - stroke / 2f - D.dp(2), h - stroke / 2f - D.dp(2));
            SweepGradient sg = new SweepGradient(w / 2f, h / 2f,
                    new int[]{0xFFFFCDD2, 0xFFFFFFFF, 0xFFFFCDD2}, new float[]{0f, 0.75f, 1f});
            gp.setShader(sg);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setColor(ringBg);
            canvas.drawArc(rect, 0, 360, false, paint);
            if (animProgress > 0.01f) {
                canvas.save();
                canvas.rotate(-90f, getWidth() / 2f, getHeight() / 2f);
                canvas.drawArc(rect, 0, 360f * animProgress, false, gp);
                canvas.restore();
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (anim != null) anim.cancel();
        }
    }

    /** ডোনার কার্ডের যাচাই প্যাটার্ন (ডিটারমিনিস্টিক 7×7 গ্রিড)। */
    public static class PatternView extends View {
        private boolean[] cells = new boolean[49];
        private Paint pFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint pBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int color = 0xFFFFFFFF;

        public PatternView(Context c) {
            super(c);
            pFill.setColor(color);
            pBg.setColor(0x22FFFFFF);
        }

        public void seed(String source, int color) {
            this.color = color;
            pFill.setColor(color);
            Random r = new Random(Bn.hash(source));
            for (int i = 0; i < 49; i++) cells[i] = r.nextBoolean();
            // fixed corners like a QR
            cells[0] = cells[6] = cells[42] = cells[48] = true;
            cells[8] = cells[14] = cells[36] = cells[20] = false;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            float cell = w / 7f;
            float gap = cell * 0.16f;
            float r = cell * 0.24f;
            for (int y = 0; y < 7; y++) {
                for (int x = 0; x < 7; x++) {
                    float l = x * cell + gap, t = y * cell + gap;
                    float rgt = (x + 1) * cell - gap, b = (y + 1) * cell - gap;
                    canvas.drawRoundRect(l, t, rgt, b, r, r, cells[y * 7 + x] ? pFill : pBg);
                }
            }
        }
    }

    /** জরুরি কার্ডের পালসিং লাল ডট। */
    public static class PulseView extends View {
        private Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private ValueAnimator anim;
        private int baseColor = 0xFFD32F2F;

        public PulseView(Context c) {
            super(c);
            p.setColor(baseColor);
            anim = ValueAnimator.ofFloat(0.35f, 1f);
            anim.setDuration(900L);
            anim.setRepeatCount(ValueAnimator.INFINITE);
            anim.setRepeatMode(ValueAnimator.REVERSE);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator a) {
                    p.setAlpha((int) (255 * (Float) a.getAnimatedValue()));
                    invalidate();
                }
            });
            anim.start();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f, cy = getHeight() / 2f, r = Math.min(cx, cy) - D.dp(1);
            canvas.drawCircle(cx, cy, r, p);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (anim != null) anim.cancel();
        }
    }

    /** বার-চার্ট — শেষ ৬ মাসের রক্তদান (এন্ট্রি অ্যানিমেশনসহ)। */
    public static class BarChartView extends View {
        private int[] values = new int[6];
        private String[] labels = new String[6];
        private float animT = 0f;
        private Paint bar = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint grid = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint txt = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint val = new Paint(Paint.ANTI_ALIAS_FLAG);
        private ValueAnimator anim;
        private final RectF rr = new RectF();

        public BarChartView(Context c) {
            super(c);
            track.setColor(D.surfaceCH);
            grid.setColor(D.outlineVar);
            grid.setStrokeWidth(Math.max(1, D.dp(0.6f)));
            txt.setColor(D.onSurfaceVar);
            txt.setTextSize(D.dp(9));
            txt.setTypeface(D.tfMedium);
            txt.setTextAlign(Paint.Align.CENTER);
            val.setColor(D.onPrimaryC);
            val.setTextSize(D.dp(9.5f));
            val.setTypeface(D.tfBold);
            val.setTextAlign(Paint.Align.CENTER);
        }

        public void setData(int[] v, String[] labels) {
            this.values = v; this.labels = labels;
            if (anim != null) anim.cancel();
            anim = ValueAnimator.ofFloat(0f, 1f);
            anim.setDuration(850L);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator a) {
                    animT = (Float) a.getAnimatedValue();
                    invalidate();
                }
            });
            anim.start();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth(), h = getHeight();
            float bot = h - D.dp(18);
            float top = D.dp(8);
            int max = 1;
            for (int v : values) if (v > max) max = v;
            int slots = values.length;
            float slot = w / (float) slots;
            float barW = Math.min(slot * 0.46f, D.dp(26));

            /* গ্রিড লাইন (২টি) */
            canvas.drawLine(D.dp(2), top + (bot - top) * 0.33f, w - D.dp(2), top + (bot - top) * 0.33f, grid);
            canvas.drawLine(D.dp(2), top + (bot - top) * 0.66f, w - D.dp(2), top + (bot - top) * 0.66f, grid);

            for (int i = 0; i < slots; i++) {
                float cx = slot * i + slot / 2f;
                float frac = values[i] / (float) max;
                float fullH = (bot - top) * frac;
                float bh = fullH * animT;
                if (values[i] > 0) {
                    bar.setShader(null);
                    int a = D.dark ? 0xFFE8B7B1 : 0xFFFFCDD2;
                    int b = D.dark ? 0xFF93000A : 0xFFB3261E;
                    bar.setShader(new android.graphics.LinearGradient(0, bot - fullH, 0, bot,
                            new int[]{a, b}, null, android.graphics.Shader.TileMode.CLAMP));
                    if (bh > 0) {
                        rr.set(cx - barW / 2f, bot - bh, cx + barW / 2f, bot);
                        canvas.drawRoundRect(rr, D.dp(4), D.dp(4), bar);
                    }
                } else {
                    rr.set(cx - barW / 2f, bot - D.dp(3), cx + barW / 2f, bot);
                    canvas.drawRoundRect(rr, D.dp(2), D.dp(2), track);
                }
                if (labels != null && i < labels.length)
                    canvas.drawText(labels[i], cx, h - D.dp(4), txt);
                if (values[i] > 0 && animT > 0.96f)
                    canvas.drawText(Bn.bn(values[i]), cx, bot - fullH - D.dp(5), val);
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (anim != null) anim.cancel();
        }
    }

    /** ডোনাট-চার্ট — রক্তের গ্রুপ অনুযায়ী ডোনার বিভাজন। */
    public static class DonutView extends View {
        private int[] values = new int[8];
        private float animT = 0f;
        private Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint txt = new Paint(Paint.ANTI_ALIAS_FLAG);
        private ValueAnimator anim;
        private final RectF rr = new RectF();
        static final int[] COLORS = {
                0xFFB3261E, 0xFFE8544B, 0xFF8E1710, 0xFFFF7A6E, 0xFFC1443B, 0xFF6B0F09, 0xFF9E2A22, 0xFFD0453C};

        public DonutView(Context c) {
            super(c);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeCap(Paint.Cap.BUTT);
            track.setStyle(Paint.Style.STROKE);
            track.setColor(D.surfaceCH);
            txt.setColor(D.onSurface);
            txt.setTextSize(D.dp(22));
            txt.setTypeface(D.tfBold);
            txt.setTextAlign(Paint.Align.CENTER);
        }

        public void setData(int[] v) {
            this.values = v;
            if (anim != null) anim.cancel();
            anim = ValueAnimator.ofFloat(0f, 1f);
            anim.setDuration(900L);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator a) {
                    animT = (Float) a.getAnimatedValue();
                    invalidate();
                }
            });
            anim.start();
        }

        @Override
        protected void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh);
            float stroke = Math.min(w, h) * 0.16f;
            p.setStrokeWidth(stroke);
            track.setStrokeWidth(stroke);
            float pad = stroke / 2f + D.dp(2);
            rr.set(pad, pad, w - pad, h - pad);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawArc(rr, 0, 360, false, track);
            int total = 0;
            for (int v : values) total += v;
            if (total == 0) {
                txt.setTextSize(D.dp(13));
                canvas.drawText("কোনো ডোনার নেই", getWidth() / 2f, getHeight() / 2f + D.dp(4), txt);
                return;
            }
            float start = -90f;
            for (int i = 0; i < values.length; i++) {
                if (values[i] == 0) continue;
                float sweep = 360f * values[i] / (float) total;
                p.setColor(COLORS[i]);
                canvas.drawArc(rr, start, sweep * animT, false, p);
                start += sweep;
            }
            txt.setTextSize(D.dp(22));
            canvas.drawText(Bn.bn(total), getWidth() / 2f, getHeight() / 2f - D.dp(2), txt);
            txt.setTextSize(D.dp(8.5f));
            txt.setColor(D.onSurfaceVar);
            canvas.drawText("মোট ডোনার", getWidth() / 2f, getHeight() / 2f + D.dp(12), txt);
            txt.setColor(D.onSurface);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (anim != null) anim.cancel();
        }
    }
}
