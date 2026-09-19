package com.hizlydighapara.halalrokto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    FrameLayout root;
    LinearLayout contentColumn;   // appbar+screen stack slot
    FrameLayout screenHost;       // current screen container
    FrameLayout sheetHost;        // bottom sheet overlay
    LinearLayout toastHost;       // toast bar slot
    LinearLayout bottomNav;

    int statusBarH = 0, navBarH = 0;
    String currentScreen = "";
    final List<String> backStack = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        D.init(this);
        Data.init(this);
        Ui.host = this;
        D.setDark(Data.s.themeDark);

        buildRoot();

        if (!Data.s.onboardingDone) go("onboarding", true);
        else if (Data.s.session == null) go("auth", true);
        else go("home", true);
    }

    /* ── Root chrome ────────────────────────────────────────── */
    void buildRoot() {
        root = new FrameLayout(this);
        root.setBackgroundColor(D.bg);

        contentColumn = Ui.v(this);
        contentColumn.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        screenHost = new FrameLayout(this);
        screenHost.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        contentColumn.addView(screenHost);

        bottomNav = buildBottomNav();
        contentColumn.addView(bottomNav);

        root.addView(contentColumn);

        sheetHost = new FrameLayout(this);
        sheetHost.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        sheetHost.setClickable(true);
        sheetHost.setVisibility(View.GONE);
        root.addView(sheetHost);

        toastHost = Ui.v(this);
        toastHost.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        toastHost.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP));
        root.addView(toastHost);

        setContentView(root);
        applyInsets();
    }

    void applyInsets() {
        statusBarH = getResDim("status_bar_height");
        navBarH = hasNavigationBar() ? getResDim("navigation_bar_height") : 0;
        int navMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean sysDark = navMode == Configuration.UI_MODE_NIGHT_YES;
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            w.setDecorFitsSystemWindows(false);
            w.setStatusBarColor(Color.TRANSPARENT);
            w.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            w.setStatusBarColor(D.dark ? 0xFF191111 : 0xFF9E1710);
            w.setNavigationBarColor(D.dark ? 0xFF191111 : 0xFFFCF8F8);
        }
        setLightStatus(!D.dark);
    }

    boolean hasNavigationBar() {
        int id = getResources().getIdentifier("config_show_navigation_bar", "bool", "android");
        return id == 0 || getResources().getBoolean(id);
    }

    int getResDim(String name) {
        int id = getResources().getIdentifier(name, "dimen", "android");
        return id > 0 ? getResources().getDimensionPixelSize(id) : D.dp(28);
    }

    void setLightStatus(boolean light) {
        View d = getWindow().getDecorView();
        int flags = d.getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= 23) {
            if (light) flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            else flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (Build.VERSION.SDK_INT < 30) flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        d.setSystemUiVisibility(flags);
    }

    /* ── Bottom navigation ──────────────────────────────────── */
    LinearLayout buildBottomNav() {
        LinearLayout nav = Ui.h(this);
        nav.setGravity(Gravity.CENTER_VERTICAL);
        nav.setPadding(D.dp(6), D.dp(8), D.dp(6), D.dp(8));
        nav.setBackgroundColor(D.bg);
        nav.setVisibility(View.GONE);

        String[][] tabs = {
                {"home", "হোম"}, {"stock", "স্টক"}, {"emergency", "জরুরি"}, {"donors", "ডোনার"}, {"more", "আরও"}
        };
        for (final String[] tab : tabs) {
            LinearLayout item = Ui.v(this);
            item.setGravity(Gravity.CENTER_HORIZONTAL);
            item.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            FrameLayout iconSlot = new FrameLayout(this);
            iconSlot.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, D.dp(30)));
            int resId = tab[0].equals("home") ? R.drawable.ic_home
                    : tab[0].equals("stock") ? R.drawable.ic_bloodbank
                    : tab[0].equals("emergency") ? R.drawable.ic_sos
                    : tab[0].equals("donors") ? R.drawable.ic_people
                    : R.drawable.ic_grid;
            ImageView iv = Ui.icon(this, resId, 22, D.onSurfaceVar);
            iv.setLayoutParams(Ui.flp(D.dp(22), D.dp(22), Gravity.CENTER));
            iconSlot.addView(iv);
            item.addView(iconSlot);

            TextView label = Ui.txt(this, tab[1], 10f, D.onSurfaceVar, 600);
            label.setGravity(Gravity.CENTER);
            label.setPadding(0, D.dp(3), 0, 0);
            item.addView(label);

            item.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    haptic(8);
                    go(tab[0], true);
                }
            });
            item.setTag(tab[0]);
            nav.addView(item);
        }
        return nav;
    }

    void updateNav() {
        boolean showNav = currentScreen.equals("home") || currentScreen.equals("stock")
                || currentScreen.equals("emergency") || currentScreen.equals("donors")
                || currentScreen.equals("more");
        bottomNav.setVisibility(showNav ? View.VISIBLE : View.GONE);
        int navPad = showNav ? navBarH - D.dp(8) : 0;
        if (navPad < 0) navPad = 0;
        bottomNav.setPadding(D.dp(6), D.dp(8), D.dp(6), D.dp(8) + navPad);
        int count = bottomNav.getChildCount();
        for (int i = 0; i < count; i++) {
            LinearLayout item = (LinearLayout) bottomNav.getChildAt(i);
            String tag = (String) item.getTag();
            boolean active = tag.equals(currentScreen);
            FrameLayout slot = (FrameLayout) item.getChildAt(0);
            ImageView iv = (ImageView) slot.getChildAt(0);
            TextView label = (TextView) item.getChildAt(1);
            iv.setColorFilter(active ? D.onPrimary : D.onSurfaceVar);
            label.setTextColor(active ? D.primary : D.onSurfaceVar);
            label.setTypeface(active ? D.tfBold : D.tfMedium);
            if (active) {
                slot.setBackground(D.round(D.primaryC, 50));
                slot.getLayoutParams().height = D.dp(30);
            } else slot.setBackground(null);
        }
    }

    /* ── Navigation ─────────────────────────────────────────── */
    public void go(String screen, boolean reset) {
        if (screen.equals(currentScreen)) return;
        if (reset) backStack.clear();
        else if (currentScreen.length() > 0) backStack.add(currentScreen);

        currentScreen = screen;
        View v = buildScreen(screen);
        if (v == null) return;
        v.setPadding(0, statusBarH, 0, 0);

        final View old = screenHost.getChildAt(0);
        screenHost.addView(v);
        if (old != null) {
            v.setTranslationX(D.dp(40));
            v.setAlpha(0f);
            v.animate().translationX(0).alpha(1f).setDuration(230)
                    .setInterpolator(new DecelerateInterpolator()).start();
            old.animate().translationX(-D.dp(26)).alpha(0f).setDuration(230)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(new Runnable() {
                        public void run() { screenHost.removeView(old); }
                    }).start();
        }
        updateNav();
    }

    public void back() {
        if (backStack.isEmpty()) { moveTaskToBack(true); return; }
        String prev = backStack.remove(backStack.size() - 1);
        currentScreen = "";
        go(prev, false);
    }

    @Override
    public void onBackPressed() {
        if (sheetHost.getVisibility() == View.VISIBLE && sheetHost.getChildCount() > 0) {
            View overlay = sheetHost.getChildAt(0);
            if (overlay instanceof FrameLayout) {
                View panel = ((FrameLayout) overlay).findViewWithTag("panel");
                if (panel != null) { closeSheet((FrameLayout) overlay, (LinearLayout) panel); return; }
            }
        }
        if (backStack.isEmpty()) { moveTaskToBack(true); return; }
        String prev = backStack.remove(backStack.size() - 1);
        currentScreen = "";
        go(prev, false);
    }

    View buildScreen(String id) {
        Context c = this;
        if (id.equals("onboarding")) return ScrOnboard.build(c);
        if (id.equals("auth")) return ScrAuth.build(c);
        if (id.equals("home")) return ScrHome.build(c);
        if (id.equals("stock")) return ScrStock.build(c);
        if (id.equals("emergency")) return ScrEmergency.build(c);
        if (id.equals("donors")) return ScrDonors.build(c);
        if (id.equals("ranking")) return ScrRanking.build(c);
        if (id.equals("history")) return ScrHistory.build(c);
        if (id.equals("donorcard")) return ScrDonorCard.build(c);
        if (id.equals("notifs")) return ScrNotifs.build(c);
        if (id.equals("referral")) return ScrReferral.build(c);
        if (id.equals("more")) return ScrMore.build(c);
        if (id.equals("profile")) return ScrProfile.build(c);
        if (id.equals("settings")) return ScrSettings.build(c);
        if (id.equals("about")) return ScrAbout.build(c);
        return null;
    }

    /* ── Sheet & toast plumbing ─────────────────────────────── */
    public void openSheet(final FrameLayout overlay, final LinearLayout panel) {
        sheetHost.removeAllViews();
        sheetHost.addView(overlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        sheetHost.setVisibility(View.VISIBLE);
        overlay.setAlpha(0f);
        overlay.animate().alpha(1f).setDuration(180).start();
        panel.setTranslationY(panel.getHeight() == 0 ? D.dp(600) : panel.getHeight());
        panel.post(new Runnable() {
            public void run() {
                panel.setTranslationY(panel.getHeight() + D.dp(40));
                panel.animate().translationY(0).setDuration(300)
                        .setInterpolator(new DecelerateInterpolator(1.1f)).start();
            }
        });
    }

    public void closeSheet(final FrameLayout overlay, final LinearLayout panel) {
        panel.animate().translationY(panel.getHeight() + D.dp(60)).setDuration(240)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    public void run() {
                        sheetHost.removeAllViews();
                        sheetHost.setVisibility(View.GONE);
                    }
                }).start();
        overlay.animate().alpha(0f).setDuration(240).start();
    }

    public void showToast(final View bar) {
        toastHost.removeAllViews();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(D.dp(14), statusBarH + D.dp(10), D.dp(14), 0);
        bar.setLayoutParams(p);
        toastHost.addView(bar);
        bar.setTranslationY(-D.dp(80));
        bar.setAlpha(0f);
        bar.animate().translationY(0).alpha(1f).setDuration(260)
                .setInterpolator(new DecelerateInterpolator()).start();
        bar.postDelayed(new Runnable() {
            public void run() {
                bar.animate().alpha(0f).setDuration(300)
                        .withEndAction(new Runnable() {
                            public void run() { toastHost.removeView(bar); }
                        }).start();
            }
        }, 2600);
    }

    /* ── Platform helpers ───────────────────────────────────── */
    public void haptic(int ms) {
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v == null) return;
            if (Build.VERSION.SDK_INT >= 26)
                v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
            else v.vibrate(ms);
        } catch (Exception ignored) {}
    }

    public void dial(String phone) {
        try {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
        } catch (Exception e) {
            Ui.toast("ডায়াল করা যায়নি: " + phone, true);
        }
    }

    public void share(String text, String title) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, text);
            i.putExtra(Intent.EXTRA_TITLE, title);
            startActivity(Intent.createChooser(i, title));
        } catch (Exception e) {
            Ui.toast("শেয়ার করা যায়নি", true);
        }
    }

    /** Rebuild current screen after theme/data change. */
    public void refresh() {
        View old = screenHost.getChildAt(0);
        if (old != null) screenHost.removeView(old);
        String cur = currentScreen;
        currentScreen = "";
        go(cur, false);
    }

    /** Rebuild everything after dark-mode toggle. */
    public void applyTheme() {
        D.setDark(Data.s.themeDark);
        root.setBackgroundColor(D.bg);
        applyInsets();
        refresh();
    }
}
