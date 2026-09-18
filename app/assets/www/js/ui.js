/* ═══════════════════════════════════════════════════════════
   হালাল রক্ত দান v2.0 — UI kernel: router, toast, sheet, dialog
   ═══════════════════════════════════════════════════════════ */
(function () {
  'use strict';
  const { esc, haptic, nativeToast } = window.Core;

  /* ── Router ──────────────────────────────────────────────── */
  const routes = {};
  let current = null;
  let navStack = [];

  const Router = {
    screen(name) { return document.querySelector(`.screen[data-screen="${name}"]`); },
    add(name, fn) { routes[name] = fn; },
    go(name, opts) {
      opts = opts || {};
      if (!routes[name]) return;
      const prev = current;
      if (prev && !opts.replace && prev !== name) navStack.push(prev);
      current = name;
      // deactivate previous
      document.querySelectorAll('.screen.active').forEach(el => {
        el.classList.remove('active');
        if (opts.back) el.classList.add('back');
        setTimeout(() => el.classList.remove('back'), 300);
      });
      let el = this.screen(name);
      if (!el) {
        el = document.createElement('section');
        el.className = 'screen';
        el.dataset.screen = name;
        document.getElementById('screens').appendChild(el);
      }
      el.classList.remove('back');
      routes[name](el);
      el.scrollTop = 0;
      requestAnimationFrame(() => requestAnimationFrame(() => el.classList.add('active')));
      UI.syncNav(name);
      haptic(8);
    },
    back() {
      if (UI.closeTopmost()) return;
      const prev = navStack.pop();
      if (prev) this.go(prev, { back: true });
      else if (current !== 'home') this.go('home', { back: true });
      else NativeBridge.requestExit();
    },
    isTop(name) { return current === name; },
    currentName() { return current; },
    resetTo(name) { navStack = []; this.go(name, { replace: true }); }
  };

  /* ── Native back button hook ─────────────────────────────── */
  const NativeBridge = {
    requestExit() {
      const now = Date.now();
      if (this._lastBack && now - this._lastBack < 2200) {
        this._lastBack = 0;
        try { if (window.Android && window.Android.exitApp) { window.Android.exitApp(); return; } } catch (e) {}
        return;
      }
      this._lastBack = now;
      UI.toast('বন্ধ করতে ব্যাক বাটন আবার চাপুন', 'info');
    }
  };
  window.addEventListener('androidback', () => Router.back());

  /* ── Toast ───────────────────────────────────────────────── */
  const toastRoot = () => document.getElementById('toast-root');
  const UI = {
    toast(msg, type) {
      const root = toastRoot();
      if (!root) return;
      const t = document.createElement('div');
      t.className = 'toast' + (type === 'ok' ? ' ok' : '');
      t.innerHTML = (type === 'ok' ? window.icon('check') : type === 'err' ? window.icon('warn') : window.icon('info')) + '<span>' + esc(msg) + '</span>';
      root.appendChild(t);
      haptic(10);
      setTimeout(() => { t.classList.add('out'); setTimeout(() => t.remove(), 260); }, 2600);
    },

    /* ── Bottom sheet ─────────────────────────────────────── */
    sheet(o) {
      const root = document.getElementById('overlay-root');
      const scrim = document.createElement('div');
      scrim.className = 'scrim';
      const sheet = document.createElement('div');
      sheet.className = 'sheet';
      sheet.innerHTML = `
        <div class="sheet-grab"><i></i></div>
        ${o.title != null ? `<div class="sheet-head"><h3>${esc(o.title)}</h3>${o.icon ? `<span style="color:var(--primary);display:flex">${window.icon(o.icon)}</span>` : ''}</div>` : ''}
        <div class="sheet-body">${o.body || ''}</div>`;
      scrim.appendChild(sheet);
      root.appendChild(scrim);
      const close = () => {
        scrim.classList.add('closing');
        setTimeout(() => scrim.remove(), 240);
        document.querySelectorAll('.screen.active').forEach(s => s.style.overflow = '');
        if (o.onClose) o.onClose();
      };
      scrim.addEventListener('click', e => { if (e.target === scrim) close(); });
      document.querySelectorAll('.screen.active').forEach(s => s.style.overflow = 'hidden');
      this._topmost = close;
      sheet.querySelectorAll('[data-close]').forEach(b => b.addEventListener('click', close));
      if (o.onMount) o.onMount(sheet, close);
      return { el: sheet, close };
    },

    /* ── Dialog ───────────────────────────────────────────── */
    dialog(o) {
      const root = document.getElementById('overlay-root');
      const scrim = document.createElement('div');
      scrim.className = 'scrim center';
      const dlg = document.createElement('div');
      dlg.className = 'dialog';
      dlg.innerHTML = `
        ${o.icon ? `<div style="width:56px;height:56px;border-radius:18px;background:var(--primary-container);color:var(--on-primary-container);display:flex;align-items:center;justify-content:center;margin-bottom:14px">${window.icon(o.icon)}</div>` : ''}
        <h3>${esc(o.title)}</h3>
        <p>${o.body || ''}</p>
        <div class="dlg-actions">
          ${o.cancel ? `<button class="btn btn-text" data-x>${esc(o.cancel)}</button>` : ''}
          <button class="btn btn-text" data-ok style="color:${o.danger ? 'var(--error)' : 'var(--primary)'}">${esc(o.ok || 'ঠিক আছে')}</button>
        </div>`;
      scrim.appendChild(dlg);
      root.appendChild(scrim);
      const close = () => { scrim.classList.add('closing'); setTimeout(() => scrim.remove(), 200); };
      scrim.addEventListener('click', e => { if (e.target === scrim) { close(); if (o.onCancel) o.onCancel(); } });
      dlg.querySelector('[data-ok]').addEventListener('click', () => { close(); if (o.onOk) o.onOk(); });
      const cx = dlg.querySelector('[data-x]');
      if (cx) cx.addEventListener('click', () => { close(); if (o.onCancel) o.onCancel(); });
      this._topmost = close;
      haptic(12);
      return { close };
    },

    closeTopmost() {
      if (this._topmost) { this._topmost(); this._topmost = null; return true; }
      return false;
    },

    /* ── Confirm helper ───────────────────────────────────── */
    confirm(title, body, ok, danger) {
      return new Promise(res => {
        this.dialog({ title, body, ok: ok || 'হ্যাঁ', cancel: 'না', danger,
          onOk: () => res(true), onCancel: () => res(false) });
      });
    },

    /* ── Nav highlight ────────────────────────────────────── */
    syncNav(name) {
      const map = { home: 'home', stock: 'stock', emergency: 'emergency', donors: 'donors', more: 'more' };
      const nav = document.getElementById('bottomnav');
      if (!nav) return;
      nav.querySelectorAll('.bn-item').forEach(b => b.classList.toggle('on', b.dataset.nav === (map[name] || (['profile', 'ranking', 'history', 'notifications', 'referral', 'card', 'settings', 'about', 'donorcard'].includes(name) ? name === 'donorcard' ? '' : 'more' : ''))));
      if (['donorcard'].includes(name)) nav.querySelectorAll('.bn-item').forEach(b => b.classList.remove('on'));
    },

    showNav(show) {
      const nav = document.getElementById('bottomnav');
      if (nav) nav.hidden = !show;
    },

    /* ── appbar builder ───────────────────────────────────── */
    appbar(o) {
      return `
      <header class="appbar">
        ${o.back ? `<button class="icon-btn" data-back aria-label="ফিরে যান">${window.icon('back')}</button>` : ''}
        <div class="ab-title">${o.title || ''}${o.subtitle ? `<small>${o.subtitle}</small>` : ''}</div>
        ${o.actions || ''}
      </header>`;
    },
    bindAppbar(el) {
      el.querySelectorAll('[data-back]').forEach(b => b.addEventListener('click', () => Router.back()));
      el.querySelectorAll('.appbar').forEach(ab => {
        const scr = el;
        scr.addEventListener('scroll', () => ab.classList.toggle('scrolled', scr.scrollTop > 8), { passive: true });
      });
    },

    /* ── form field builder ───────────────────────────────── */
    field(o) {
      const id = o.id || Core.uid('f');
      let input;
      if (o.type === 'select') {
        input = `<select id="${id}" ${o.required ? 'data-req' : ''}>${(o.options || []).map(x => `<option value="${esc(x[0])}" ${x[0] === o.value ? 'selected' : ''}>${esc(x[1])}</option>`).join('')}</select>`;
      } else if (o.type === 'textarea') {
        input = `<textarea id="${id}" placeholder="${esc(o.placeholder || '')}" ${o.required ? 'data-req' : ''} ${o.maxlength ? `maxlength="${o.maxlength}"` : ''}>${esc(o.value || '')}</textarea>`;
      } else {
        input = `<input id="${id}" type="${o.type || 'text'}" placeholder="${esc(o.placeholder || '')}" value="${esc(o.value || '')}" ${o.required ? 'data-req' : ''} ${o.inputmode ? `inputmode="${o.inputmode}"` : ''} ${o.pattern ? `pattern="${esc(o.pattern)}"` : ''} ${o.maxlength ? `maxlength="${o.maxlength}"` : ''} ${o.readonly ? 'readonly' : ''}>`;
      }
      return `
      <div class="field" id="${id}_wrap">
        <label for="${id}">${o.label}${o.required ? ' <span class="req">*</span>' : ''}</label>
        ${input}
        ${o.err ? `<div class="err">${o.err}</div>` : ''}
        ${o.hint ? `<div class="hint">${o.hint}</div>` : ''}
      </div>`;
    },
    validate(el) {
      let ok = true;
      el.querySelectorAll('.field').forEach(w => {
        const inp = w.querySelector('input,select,textarea');
        if (!inp) return;
        w.classList.remove('invalid');
        inp.classList.remove('ng');
        const v = String(inp.value || '').trim();
        if (inp.hasAttribute('data-req') && !v) { w.classList.add('invalid'); ok = false; return; }
        if (!v) return;
        const t = inp.getAttribute('type');
        if (t === 'email' && !/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(v)) { w.classList.add('invalid'); ok = false; }
        if (inp.inputmode === 'tel' && !/^01[3-9]\d{8}$/.test(v.replace(/[\s-]/g, ''))) { w.classList.add('invalid'); ok = false; }
        if (t === 'password' && v.length < 6) { w.classList.add('invalid'); ok = false; }
      });
      return ok;
    },

    empty(icon, title, sub, action) {
      return `<div class="empty"><div class="e-ico">${window.icon(icon)}</div><b>${title}</b><p>${sub || ''}</p>${action ? `<div style="margin-top:18px">${action}</div>` : ''}</div>`;
    }
  };

  window.UI = UI;
  window.Router = Router;
})();
