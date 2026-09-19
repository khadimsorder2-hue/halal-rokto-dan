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
}
