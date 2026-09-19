/* ═══════════════════════════════════════════════════════════
   হালাল রক্ত দান v2.0 — Main: boot, theme, nav bindings
   ═══════════════════════════════════════════════════════════ */
(function () {
  'use strict';
  const C = window.Core, UI = window.UI, Router = window.Router;
  const { State } = C;

  /* ── Boot ────────────────────────────────────────────────── */
  function boot() {
    State.load();

    // theme
    const theme = State.data.settings.theme || 'light';
    document.documentElement.classList.toggle('dark', theme === 'dark');

    // bottom nav bindings
    document.querySelectorAll('.bn-item').forEach(b => {
      b.addEventListener('click', () => {
        const t = b.dataset.nav;
        if (Router.currentName() === t) {
          // re-tap: scroll to top
          const scr = document.querySelector('.screen.active');
          if (scr) scr.scrollTo({ top: 0, behavior: 'smooth' });
          return;
        }
        Router.go(t);
      });
    });

    // route
    const me = State.me();
    if (!State.data.onboardingDone) Router.resetTo('onboarding');
    else if (!me) Router.resetTo('auth');
    else Router.resetTo('home');

    // reveal app
    const app = document.getElementById('app');
    app.hidden = false;
    const splash = document.getElementById('splash');
    setTimeout(() => splash.classList.add('hide'), 900);
    setTimeout(() => splash.remove(), 1400);

    // keep date/stat refresh when returning after hours
    document.addEventListener('visibilitychange', () => {
      if (!document.hidden && Router.currentName() === 'home') Router.go('home', { replace: true });
    });
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();
})();
