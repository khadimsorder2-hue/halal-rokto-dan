/* ═══════════════════════════════════════════════════════════
   হালাল রক্ত দান v2.0 — Screens Part 1:
   onboarding • auth • home • stock
   ═══════════════════════════════════════════════════════════ */
(function () {
  'use strict';
  const C = window.Core, UI = window.UI, Router = window.Router;
  const { esc, toBn, bnNum, fmtDate, dayName, timeAgo } = C;
  const { BLOOD_GROUPS, State, APP } = C;

  /* ═══ ONBOARDING ═══════════════════════════════════════════ */
  const ONB_SLIDES = [
    { icon: 'dropFill', title: 'রক্ত দিন, জীবন বাঁচান', sub: 'প্রতিটি ব্যাগ রক্ত একটি প্রাণ বাঁচায়। হালাল পথে সঠিক নিয়মে রক্তদান করুন এবং মানুষের জীবনে হাসি ফোটান।' },
    { icon: 'sos', title: 'জরুরি মুহূর্তে ডোনার খুঁজুন', sub: 'মুহূর্তের প্রয়োজনে রক্তের গ্রুপ অনুযায়ী যোগ্য ডোনার খুঁজুন, সরাসরি কল করুন — সময় নষ্ট না হয়।' },
    { icon: 'medal', title: 'ডোনার কার্ড ও র‌্যাংকিং', sub: 'নিজের ডিজিটাল ডোনার কার্ড পান, রক্তদানের ইতিহাস রাখুন এবং র‌্যাংকিংয়ে নিজের অবস্থান দেখুন।' }
  ];

  Router.add('onboarding', (el) => {
    let idx = 0;
    const render = () => {
      const s = ONB_SLIDES[idx];
      el.innerHTML = `
      <div class="onb">
        <div class="onb-media">
          <span class="ring r1"></span><span class="ring r2"></span><span class="ring r3"></span>
          <div class="onb-ico">${window.icon(s.icon)}</div>
        </div>
        <div class="onb-body">
          <h2>${s.title}</h2>
          <p>${s.sub}</p>
          <div class="onb-dots">${ONB_SLIDES.map((_, i) => `<i class="${i === idx ? 'on' : ''}"></i>`).join('')}</div>
        </div>
        <div class="onb-foot">
          ${idx > 0 ? `<button class="skip" data-prev>পেছনে</button>` : `<button class="skip" data-skip>স্কিপ</button>`}
          <button class="btn btn-grad grow" style="height:52px" data-next>${idx === ONB_SLIDES.length - 1 ? 'শুরু করুন' : 'পরবর্তী'}</button>
        </div>
        <div style="text-align:center;padding:0 20px calc(12px + var(--safe-b));">
          <span style="font-size:11px;font-weight:700;color:var(--on-surface-variant)">${esc(APP.org)} • ${esc(APP.since)} • ${esc(APP.address)}</span>
        </div>
      </div>`;
      el.querySelector('[data-next]').onclick = () => {
        C.haptic(10);
        if (idx < ONB_SLIDES.length - 1) { idx++; render(); }
        else finish();
      };
      const p = el.querySelector('[data-prev]');
      if (p) p.onclick = () => { idx--; render(); };
      const sk = el.querySelector('[data-skip]');
      if (sk) sk.onclick = finish;
    };
    const finish = () => {
      State.data.onboardingDone = true;
      State.save();
      Router.resetTo('auth');
    };
    render();
  });

  /* ═══ AUTH ═════════════════════════════════════════════════ */
  function authFooter() {
    return `
    <div class="auth-org-foot">
      <b>${esc(APP.org)}</b>
      <span>${esc(APP.since)} • ${esc(APP.address)}</span>
    </div>`;
  }

  Router.add('auth', (el) => {
    el.innerHTML = `
    <div class="auth-wrap">
      <div class="auth-hero rise">
        <div class="logo-badge">${window.icon('dropFill')}</div>
        <h1>${esc(APP.name)}</h1>
        <p>রক্ত দিন, জীবন বাঁচান — হালাল পথে</p>
      </div>
      <div class="card auth-card rise d1">
        <div style="display:flex;gap:6px;background:var(--surface-container);padding:5px;border-radius:999px;margin-bottom:20px">
          <button class="btn btn-sm grow" id="tab-login" style="border-radius:999px">লগইন</button>
          <button class="btn btn-sm grow" id="tab-reg" style="border-radius:999px">রেজিস্ট্রেশন</button>
        </div>
        <div id="auth-body"></div>
      </div>
      ${authFooter()}
    </div>`;
    const body = el.querySelector('#auth-body');
    const tabL = el.querySelector('#tab-login'), tabR = el.querySelector('#tab-reg');
    UI.showNav(false);

    const setTab = (login) => {
      tabL.className = 'btn btn-sm grow ' + (login ? 'btn-grad' : '');
      tabR.className = 'btn btn-sm grow ' + (!login ? 'btn-grad' : '');
      login ? renderLogin() : renderRegister();
    };
    tabL.onclick = () => setTab(true);
    tabR.onclick = () => setTab(false);

    function renderLogin() {
      body.innerHTML = `
        ${UI.field({ id: 'l_email', label: 'ইমেইল', type: 'email', required: true, placeholder: 'you@example.com', err: 'সঠিক ইমেইল দিন' })}
        ${UI.field({ id: 'l_pass', label: 'পাসওয়ার্ড', type: 'password', required: true, placeholder: '••••••', err: 'পাসওয়ার্ড কমপক্ষে ৬ অক্ষর হতে হবে' })}
        <button class="btn btn-grad btn-block" id="do-login">লগইন করুন</button>
        <div class="auth-switch">অ্যাকাউন্ট নেই? <a id="go-reg">রেজিস্ট্রেশন করুন</a></div>`;
      body.querySelector('#go-reg').onclick = () => setTab(false);
      body.querySelector('#do-login').onclick = () => {
        if (!UI.validate(body)) return;
        const r = C.Auth.login(body.querySelector('#l_email').value.trim(), body.querySelector('#l_pass').value);
        if (!r.ok) return UI.toast(r.msg, 'err');
        UI.toast('স্বাগতম, ' + r.user.name.split(' ')[0] + '!', 'ok');
        Router.resetTo('home');
      };
    }

    function renderRegister() {
      body.innerHTML = `
        ${UI.field({ id: 'r_name', label: 'পূর্ণ নাম', required: true, placeholder: 'যেমন: মোঃ রহিম মিয়া', err: 'নাম দিন' })}
        ${UI.field({ id: 'r_phone', label: 'ফোন নম্বর', required: true, placeholder: '01XXXXXXXXX', inputmode: 'tel', maxlength: 11, err: 'সঠিক ফোন নম্বর দিন (01XXXXXXXXX)' })}
        <div class="field">
          <label>আপনার রক্তের গ্রুপ <span class="req">*</span></label>
          <div class="bg-grid" id="r_bg">
            ${BLOOD_GROUPS.map(g => `<button class="bg-opt" data-g="${g}">${g}</button>`).join('')}
          </div>
          <div class="err" id="r_bg_err">রক্তের গ্রুপ নির্বাচন করুন</div>
        </div>
        ${UI.field({ id: 'r_addr', label: 'ঠিকানা / এলাকা', required: true, placeholder: 'যেমন: হিজলি, বাগাতিপাড়া', err: 'ঠিকানা দিন' })}
        ${UI.field({ id: 'r_email', label: 'ইমেইল', type: 'email', required: true, placeholder: 'you@example.com', err: 'সঠিক ইমেইল দিন' })}
        ${UI.field({ id: 'r_pass', label: 'পাসওয়ার্ড', type: 'password', required: true, placeholder: 'কমপক্ষে ৬ অক্ষর', err: 'পাসওয়ার্ড কমপক্ষে ৬ অক্ষর হতে হবে' })}
        ${UI.field({ id: 'r_ref', label: 'রেফারেল কোড (ঐচ্ছিক)', placeholder: 'DJS-XXXXX', hint: 'বন্ধুর কোড থাকলে দিন — না থাকলে খালি রাখুন' })}
        <button class="btn btn-grad btn-block" id="do-reg">রেজিস্ট্রেশন করুন</button>
        <div class="auth-switch">অ্যাকাউন্ট আছে? <a id="go-login">লগইন করুন</a></div>`;
      let bg = '';
      body.querySelectorAll('#r_bg .bg-opt').forEach(b => b.onclick = () => {
        body.querySelectorAll('#r_bg .bg-opt').forEach(x => x.classList.remove('on'));
        b.classList.add('on'); bg = b.dataset.g; C.haptic(8);
      });
      body.querySelector('#go-login').onclick = () => setTab(true);
      body.querySelector('#do-reg').onclick = () => {
        const errEl = body.querySelector('#r_bg_err').parentElement;
        errEl.classList.remove('invalid');
        if (!UI.validate(body)) return;
        if (!bg) { errEl.classList.add('invalid'); UI.toast('রক্তের গ্রুপ নির্বাচন করুন', 'err'); return; }
        const r = C.Auth.register({
          name: body.querySelector('#r_name').value.trim(),
          phone: body.querySelector('#r_phone').value.trim(),
          bloodType: bg,
          address: body.querySelector('#r_addr').value.trim(),
          email: body.querySelector('#r_email').value.trim(),
          password: body.querySelector('#r_pass').value,
          referredBy: body.querySelector('#r_ref').value.trim()
        });
        if (!r.ok) return UI.toast(r.msg, 'err');
        UI.toast('রেজিস্ট্রেশন সফল! স্বাগতম 🎉', 'ok');
        Router.resetTo('home');
      };
    }
    setTab(true);
  });

  /* ═══ HOME ═════════════════════════════════════════════════ */
  Router.add('home', (el) => {
    const me = State.me();
    const cd = State.cooldown(me);
    const users = State.users();
    const emgs = State.emergencies().filter(e => !e.fulfilled);
    const myRank = State.ranking().findIndex(u => u.id === me.id) + 1;
    const now = new Date();

    const cdCard = () => {
      if (cd.first) {
        return `<div class="cool-card first rise d2">
          <div class="cc-row">
            <div class="cool-ring"><svg viewBox="0 0 62 62"><circle class="cr-bg" cx="31" cy="31" r="26"/><circle class="cr-fg" cx="31" cy="31" r="26" stroke-dasharray="163.4" stroke-dashoffset="0"/></svg><div class="cr-num">${window.icon('heart')}</div></div>
            <div class="grow"><h3>আপনি এখনো রক্ত দেননি</h3><p>প্রথমবার রক্ত দিয়ে রক্তবন্ধু হয়ে যান — জীবন বাঁচানোর মহান পথ শুরু করুন।</p></div>
          </div><div class="cc-droplet">${window.icon('dropFill')}</div>
        </div>`;
      }
      if (cd.eligible) {
        return `<div class="cool-card ready rise d2">
          <div class="cc-row">
            <div class="cool-ring"><svg viewBox="0 0 62 62"><circle class="cr-bg" cx="31" cy="31" r="26"/><circle class="cr-fg" cx="31" cy="31" r="26" stroke-dasharray="163.4" stroke-dashoffset="0"/></svg><div class="cr-num">${window.icon('check')}</div></div>
            <div class="grow"><h3>আপনি রক্ত দিতে পারেন ✅</h3><p>শেষ দান ${bnNum(cd.lastDiff)} দিন আগে — ৯০ দিনের কুলডাউন পূর্ণ। এখনই দান করতে পারেন।</p></div>
          </div><div class="cc-droplet">${window.icon('dropFill')}</div>
        </div>`;
      }
      const pct = Math.round((cd.elapsed / APP.cooldownDays) * 100);
      const off = 163.4 * (1 - cd.elapsed / APP.cooldownDays);
      return `<div class="cool-card wait rise d2">
        <div class="cc-row">
          <div class="cool-ring"><svg viewBox="0 0 62 62"><circle class="cr-bg" cx="31" cy="31" r="26"/><circle class="cr-fg" cx="31" cy="31" r="26" stroke-dasharray="163.4" stroke-dashoffset="${off.toFixed(1)}"/></svg><div class="cr-num">${bnNum(cd.days)}</div></div>
          <div class="grow"><h3>${bnNum(cd.days)} দিন পর রক্ত দিতে পারবেন</h3><p>শেষ দান: ${fmtDate(me.lastDonationDate)} — ৯০ দিনের কুলডাউন চলছে (${pct}% পূর্ণ)।</p></div>
        </div><div class="cc-droplet">${window.icon('dropFill')}</div>
      </div>`;
    };

    el.innerHTML = `
    ${UI.appbar({ title: esc(APP.name), actions: `
      <button class="icon-btn" data-go="notifications" aria-label="নোটিফিকেশন">${window.icon('bell')}${State.unreadCount() ? `<span class="notif-dot">${bnNum(State.unreadCount())}</span>` : ''}</button>
      <button class="icon-btn" data-go="profile" aria-label="প্রোফাইল" style="margin-right:4px">${window.icon('person')}</button>` })}
    <div class="home-hero">
      <div class="hh-top">
        <div class="avatar" style="background:${C.avatarColor(me.id)};color:#fff">${esc(C.initials(me.name))}</div>
        <div class="grow">
          <div class="hh-hi">আসসালামু আলাইকুম,</div>
          <div class="hh-name">${esc(me.name)}</div>
        </div>
        <div class="hh-date">${dayName(Date.now())}<b>${bnNum(now.getDate())}</b>${['জানুয়ারি','ফেব্রুয়ারি','মার্চ','এপ্রিল','মে','জুন','জুলাই','আগস্ট','সেপ্টেম্বর','অক্টোবর','নভেম্বর','ডিসেম্বর'][now.getMonth()]}</div>
      </div>
    </div>

    <div class="stat-row rise d1">
      <div class="stat"><div class="st-num">${bnNum(users.length)}</div><div class="st-lbl">মোট ডোনার</div></div>
      <div class="stat"><div class="st-num hot">${bnNum(State.donations().length)}</div><div class="st-lbl">মোট রক্তদান</div></div>
      <div class="stat"><div class="st-num">${bnNum(emgs.length)}</div><div class="st-lbl">সক্রিয় জরুরি</div></div>
    </div>

    ${cdCard()}

    <div class="sec"><h2>${window.icon('grid')} দ্রুত সেবা</h2></div>
    <div class="quick-grid">
      <button class="qa red" data-go="stock"><span class="qa-ico">${window.icon('bloodbank')}</span><span>রক্তের<br>স্টক</span></button>
      <button class="qa teal" data-go="donors"><span class="qa-ico">${window.icon('people')}</span><span>ডোনার<br>খুঁজুন</span></button>
      <button class="qa amber" data-go="history"><span class="qa-ico">${window.icon('add')}</span><span>রক্তদান<br>যোগ করুন</span></button>
      <button class="qa purple" data-go="donorcard"><span class="qa-ico">${window.icon('card')}</span><span>ডোনার<br>কার্ড</span></button>
    </div>

    ${emgs.length ? `
    <div class="sec"><h2>${window.icon('sos')} জরুরি রক্তের প্রয়োজন</h2><button class="sec-link" data-go="emergency">সব দেখুন</button></div>
    <div style="display:flex;flex-direction:column;gap:10px;padding:0 16px">
      ${emgs.slice(0, 2).map(e => `
      <div class="emg-mini rise" data-go="emergency">
        <span class="em-pulse"></span>
        <span class="bgroup sm solid">${esc(e.bloodGroup)}</span>
        <div class="em-main">
          <div class="em-t">${esc(e.patientName)}</div>
          <div class="em-s">${esc(e.hospital)} • ${bnNum(e.units)} ব্যাগ • ${timeAgo(e.createdAt)}</div>
        </div>
        <a class="call-fab" href="tel:${esc(e.contact)}" aria-label="কল করুন">${window.icon('phone')}</a>
      </div>`).join('')}
    </div>` : ''}

    <div class="sec"><h2>${window.icon('chart')} আপনার অবস্থান</h2></div>
    <div style="padding:0 16px">
      <div class="card card-pad rise d3">
        <div class="row">
          <span class="qa-ico" style="width:46px;height:46px;border-radius:16px;background:var(--tertiary-container);color:var(--on-tertiary-container);display:flex;align-items:center;justify-content:center">${window.icon('medal')}</span>
          <div class="grow"><b style="font-size:15px">র‌্যাংকিং: ${bnNum(myRank)} নং</b><div class="muted" style="font-size:12.5px">মোট ${bnNum(me.donationCount || 0)} বার রক্তদান • ${State.badge(me)[1]}</div></div>
          <button class="btn btn-sm btn-tonal" data-go="ranking">র‌্যাংকিং</button>
        </div>
      </div>
    </div>

    <div class="org-banner rise d4">
      <span class="ob-ico">${window.icon('shield')}</span>
      <div class="grow"><b>${esc(APP.org)}</b><span>${esc(APP.since)} • ${esc(APP.address)}</span></div>
      <button class="btn btn-sm btn-text" data-go="about">বিস্তারিত</button>
    </div>
    <div class="gap24"></div>`;

    UI.showNav(true);
    UI.bindAppbar(el);
    el.querySelectorAll('[data-go]').forEach(b => b.addEventListener('click', () => Router.go(b.dataset.go)));
  });

  /* ═══ BLOOD STOCK ══════════════════════════════════════════ */
  Router.add('stock', (el) => {
    const render = () => {
      const stock = State.data.stock;
      const total = State.stockTotal();
      const status = (n) => n <= 1 ? ['err', 'মারাত্মক ঘাটতি'] : n <= 3 ? ['warn', 'কম আছে'] : n >= 6 ? ['ok', 'পর্যাপ্ত'] : ['info', 'সচল'];
      el.innerHTML = `
      ${UI.appbar({ title: 'রক্তের স্টক', subtitle: 'রিয়েল-টাইম ব্লাড ব্যাংক অবস্থা' })}
      <div class="stock-total rise">
        <b>${bnNum(total)}</b>
        <span>ব্যাগ রক্ত মোট স্টকে আছে<br><span style="font-weight:600;font-size:11.5px;opacity:.75">প্রতি গ্রুপে ${bnNum(APP.targetStock)} ব্যাগ লক্ষ্যমাত্রা</span></span>
        ${window.icon('bloodbank')}
      </div>
      <div class="gap12"></div>
      <div class="stock-grid">
        ${BLOOD_GROUPS.map(g => {
          const n = stock[g] || 0;
          const [cls, lbl] = status(n);
          const pct = Math.min(100, Math.round(n / APP.targetStock * 100));
          return `<div class="stock-card rise">
            <div class="sc-top">
              <span class="bgroup sm ${n <= 1 ? 'solid' : ''}">${g}</span>
              <span class="pill ${cls}">${lbl}</span>
            </div>
            <div class="sc-num">${bnNum(n)} <small>ব্যাগ</small></div>
            <div class="prog ${cls}" style="margin-top:9px"><i style="width:${pct}%"></i></div>
            <div class="sc-lbl">${C.GROUP_RARITY[g] || 'সাধারণ গ্রুপ'}${n <= 1 ? ' — জরুরি ডোনার প্রয়োজন!' : ''}</div>
          </div>`;
        }).join('')}
      </div>

      <div class="sec"><h2>${window.icon('info')} রক্তের সামঞ্জস্য তালিকা</h2></div>
      <div class="compat-list rise">
        ${BLOOD_GROUPS.map(g => `
        <div class="compat-row">
          <span class="bgroup xs">${g}</span>
          <span class="cr-arrow">${window.icon('chevron')}</span>
          <div class="cr-to grow">${C.COMPAT[g].join(', ')} <span style="color:var(--outline)">রক্ত নিতে পারে</span></div>
        </div>`).join('')}
      </div>
      <div class="pad" style="padding-top:14px">
        <div class="card card-pad" style="background:var(--tertiary-container);color:var(--on-tertiary-container);border:0">
          <div class="row" style="align-items:flex-start;gap:10px">
            ${window.icon('info')}
            <p style="font-size:12.5px;line-height:1.75"><b>টিপস:</b> রক্তদানের পর প্রতিটি ব্যাগ স্বয়ংক্রিয়ভাবে এই স্টকে যোগ হয়। জরুরি আবেদন পূরণ হলে সংশ্লিষ্ট গ্রুপের স্টক থেকে বাদ যায়। O− গ্রুপের ডোনার "সর্বজনীন দাতা" — সবাই তার রক্ত নিতে পারে।</p>
          </div>
        </div>
      </div>
      <div class="gap24"></div>`;

      UI.bindAppbar(el);
    };
    render();
  });

})();
