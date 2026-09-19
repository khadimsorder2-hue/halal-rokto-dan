/* ═══════════════════════════════════════════════════════════
   হালাল রক্ত দান v2.0 — Screens Part 3:
   donorcard • notifications • referral • more/profile • settings • about
   ═══════════════════════════════════════════════════════════ */
(function () {
  'use strict';
  const C = window.Core, UI = window.UI, Router = window.Router;
  const { esc, toBn, bnNum, fmtDate, fmtDateShort, timeAgo } = C;
  const { BLOOD_GROUPS, State, APP, uid } = C;

  /* ── Global donor detail sheet (used by ranking etc.) ────── */
  window.openDonorSheet = function (id) {
    const u = State.users().find(x => x.id === id);
    if (!u) return;
    const cd = State.cooldown(u);
    const rank = State.ranking().findIndex(x => x.id === u.id) + 1;
    const [bcls, blbl] = State.badge(u);
    UI.sheet({
      title: '',
      body: `
      <div style="display:flex;align-items:center;gap:14px;margin-bottom:16px">
        <div class="avatar lg" style="background:${C.avatarColor(u.id)};color:#fff">${esc(C.initials(u.name))}</div>
        <div class="grow">
          <h3 style="font-size:20px;font-weight:800;display:flex;align-items:center;gap:7px">${esc(u.name)} ${u.verified ? `<span class="verified-ico">${window.icon('verified')}</span>` : ''}</h3>
          <div style="display:flex;gap:8px;margin-top:7px;align-items:center">
            <span class="bgroup sm solid">${u.bloodType}</span>
            <span class="rank-badge ${bcls}">${blbl}</span>
          </div>
        </div>
      </div>
      <div class="detail-rows">
        <div class="dr"><span class="dr-ico">${window.icon('phone')}</span><span class="dr-lbl">ফোন নম্বর</span><span class="dr-val">${esc(u.phone)}</span></div>
        <div class="dr"><span class="dr-ico">${window.icon('location')}</span><span class="dr-lbl">ঠিকানা</span><span class="dr-val">${esc(u.address)}</span></div>
        <div class="dr"><span class="dr-ico">${window.icon('drop')}</span><span class="dr-lbl">মোট রক্তদান</span><span class="dr-val">${bnNum(u.donationCount || 0)} বার</span></div>
        <div class="dr"><span class="dr-ico">${window.icon('clock')}</span><span class="dr-lbl">শেষ রক্তদান</span><span class="dr-val">${u.lastDonationDate ? fmtDate(u.lastDonationDate) : 'এখনো দেননি'}</span></div>
        <div class="dr"><span class="dr-ico">${window.icon('medal')}</span><span class="dr-lbl">র‌্যাংকিং</span><span class="dr-val">${bnNum(rank)} নং</span></div>
      </div>
      <div style="display:flex;gap:9px">
        <a class="btn btn-success grow" href="tel:${esc(u.phone)}">${window.icon('phone')} কল করুন</a>
        <button class="btn btn-tonal" id="shr" style="flex:0 0 56px">${window.icon('share')}</button>
      </div>`,
      onMount(sheet) {
        sheet.querySelector('#shr').onclick = () => {
          C.shareText(`🩸 রক্তদাতার তথ্য\nনাম: ${u.name}\nগ্রুপ: ${u.bloodType}\nফোন: ${u.phone}\nএলাকা: ${u.address}\n— ${APP.org}`, 'ডোনার তথ্য শেয়ার');
        };
      }
    });
  };

  /* ═══ DONOR CARD ═══════════════════════════════════════════ */
  Router.add('donorcard', (el) => {
    const me = State.me();
    const [bcls, blbl] = State.badge(me);
    const rank = State.ranking().findIndex(u => u.id === me.id) + 1;
    const memberId = 'DJS-' + String(10000 + Math.abs(C.hashCode(me.id)) % 89999);
    // deterministic QR-ish verification pattern from member id
    const bits = [];
    let seed = Math.abs(C.hashCode(me.id + me.phone));
    for (let i = 0; i < 49; i++) { seed = (seed * 1103515245 + 12345) & 0x7fffffff; bits.push(((seed >> 8) & 1) ? '' : 'o'); }
    const verCode = memberId.slice(4) + String(Math.abs(C.hashCode(me.phone)) % 900 + 100);

    el.innerHTML = `
    ${UI.appbar({ title: 'ডোনার কার্ড', subtitle: 'আপনার ডিজিটাল পরিচয়পত্র', back: true })}
    <div class="dcard-wrap">
      <div class="dcard rise">
        <div class="dc-org">
          <span class="dc-logo">${window.icon('dropFill')}</span>
          <div><b>${esc(APP.org)}</b><span>${esc(APP.since)} • ${esc(APP.address)}</span></div>
        </div>
        <div class="dc-blood"><b>${me.bloodType}</b><span>রক্তের গ্রুপ</span></div>
        <div class="dc-name">${esc(me.name)}</div>
        <div class="dc-meta">
          <span>সদস্য নং: ${memberId}</span>
          <span>${blbl}</span>
          ${me.verified ? '<span>ভেরিফায়েড ডোনার ✓</span>' : '<span>ভেরিফিকেশন অপেক্ষমান</span>'}
        </div>
        <div class="dc-bottom">
          <div class="dc-id">
            মোট দান: ${bnNum(me.donationCount || 0)} বার • র‌্যাংক: ${bnNum(rank)}<br>
            যোগদান: ${fmtDate(me.createdAt)}<br>
            যাচাই কোড: ${verCode}
          </div>
          <div class="dc-qrv">
            <div class="qrv">${bits.map(b => `<i class="${b}"></i>`).join('')}</div>
            <span>${esc(APP.nameEn.toUpperCase())}</span>
          </div>
        </div>
      </div>
      <div class="dc-actions">
        <button class="btn btn-grad grow" id="share-card">${window.icon('share')} কার্ড শেয়ার করুন</button>
        ${me.phone ? `<a class="btn btn-tonal" href="tel:${esc(me.phone)}" style="flex:0 0 56px">${window.icon('phone')}</a>` : ''}
      </div>
      <div class="gap12"></div>
      <div class="card card-pad rise d2">
        <div class="row" style="align-items:flex-start;gap:10px">
          <span style="color:var(--primary);display:flex;flex:0 0 auto">${window.icon('info')}</span>
          <p style="font-size:12.5px;color:var(--on-surface-variant);line-height:1.75">জরুরি মুহূর্তে এই কার্ড দেখিয়ে দ্রুত পরিচয় যাচাই করুন। কার্ডের যাচাই কোড (${verCode}) সংগঠনের রেকর্ডের সাথে মিলিয়ে ডোনারের সত্যতা নিশ্চিত হওয়া যায়। প্রতিটি রক্তদানের পর কার্ড স্বয়ংক্রিয়ভাবে আপডেট হয়।</p>
        </div>
      </div>
    </div>
    <div class="gap24"></div>`;

    UI.bindAppbar(el);
    el.querySelector('#share-card').onclick = () => {
      C.shareText(`🪸 ${APP.name} — ডোনার কার্ড\nসংগঠন: ${APP.org} (${APP.since})\nনাম: ${me.name}\nরক্তের গ্রুপ: ${me.bloodType}\nসদস্য নং: ${memberId}\nমোট দান: ${bnNum(me.donationCount || 0)} বার\nযোগদান: ${fmtDate(me.createdAt)}`, 'ডোনার কার্ড শেয়ার');
    };
  });

  /* ═══ NOTIFICATIONS ════════════════════════════════════════ */
  Router.add('notifications', (el) => {
    const render = () => {
      const list = State.notifs();
      const ICONMAP = { emg: ['emg', 'sos'], don: ['don', 'dropFill'], rank: ['rank', 'medal'], sys: ['sys', 'info'] };
      el.innerHTML = `
      ${UI.appbar({ title: 'নোটিফিকেশন', subtitle: 'রক্ত দান সম্পর্কিত সকল আপডেট', actions: list.some(n => !n.read) ? `<button class="btn btn-text" id="read-all" style="font-size:12.5px">সব পড়া হয়েছে</button>` : '' })}
      ${list.length ? `<div>${list.map(n => {
        const [cls, ic] = ICONMAP[n.type] || ICONMAP.sys;
        return `<div class="notif-row ${n.read ? '' : 'unread'}">
          <span class="n-ico ${cls}">${window.icon(ic)}</span>
          <div class="n-main">
            <div class="n-title"><span class="grow" style="min-width:0;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${esc(n.title)}</span><time>${timeAgo(n.date)}</time></div>
            <div class="n-body">${esc(n.body)}</div>
          </div>
        </div>`;
      }).join('')}</div>` : UI.empty('bell', 'কোনো নোটিফিকেশন নেই', 'নতুন জরুরি আবেদন বা রক্তদানের আপডেট এখানে দেখা যাবে।')}
      <div class="gap24"></div>`;
      UI.bindAppbar(el);
      const ra = el.querySelector('#read-all');
      if (ra) ra.onclick = () => {
        State.data.notifications.forEach(n => { if (n.userId === 'all' || n.userId === State.data.session) n.read = true; });
        State.save(); render();
      };
    };
    render();
  });

  /* ═══ REFERRAL ═════════════════════════════════════════════ */
  Router.add('referral', (el) => {
    const me = State.me();
    const referred = State.users().filter(u => u.referredBy === me.referralCode);

    el.innerHTML = `
    ${UI.appbar({ title: 'রেফারেল প্রোগ্রাম', subtitle: 'বন্ধুদের আমন্ত্রণ জানান', back: true })}
    <div class="ref-hero rise">
      <div class="rh-lbl">আপনার রেফারেল কোড</div>
      <div class="rh-code">${esc(me.referralCode)}</div>
      <div class="rh-acts">
        <button class="btn btn-grad btn-sm" id="cp">${window.icon('copy')} কপি</button>
        <button class="btn btn-sm" style="background:rgba(255,255,255,.55);color:var(--on-primary-container)" id="sh">${window.icon('whatsapp')} শেয়ার</button>
      </div>
    </div>
    <div class="quick-grid" style="padding:0 16px">
      <div class="qa" style="grid-column:span 2"><span class="qa-ico" style="background:var(--primary-container);color:var(--on-primary-container)">${window.icon('people')}</span><span>মোট রেফার<br><b style="font-size:17px">${bnNum(referred.length)}</b> জন</span></div>
      <div class="qa" style="grid-column:span 2"><span class="qa-ico" style="background:var(--tertiary-container);color:var(--on-tertiary-container)">${window.icon('medal')}</span><span>রেফারেল পয়েন্ট<br><b style="font-size:17px">${bnNum(referred.length * 10)}</b></span></div>
    </div>
    <div class="sec"><h2>${window.icon('people')} আপনার রেফারেকৃত সদস্য</h2></div>
    ${referred.length ? `<div style="padding:0 16px 10px">
      ${referred.map(u => `
      <div class="donor-card" style="margin-bottom:10px" data-open="${u.id}">
        <div class="avatar sm" style="background:${C.avatarColor(u.id)};color:#fff">${esc(C.initials(u.name))}</div>
        <div class="dc-main"><div class="dc-name" style="font-size:14px">${esc(u.name)}</div><div class="dc-sub">যোগদান: ${fmtDateShort(u.createdAt)} • ${bnNum(u.donationCount || 0)} বার দান</div></div>
        <span class="bgroup xs">${u.bloodType}</span>
      </div>`).join('')}
    </div>` : `<div class="empty" style="padding-top:26px"><div class="e-ico">${window.icon('people')}</div><b>এখনো কেউ যোগ দেননি</b><p>আপনার কোড শেয়ার করুন — যারা রেজিস্ট্রেশনের সময় কোডটি দেবেন, তারা এখানে তালিকাভুক্ত হবেন।</p></div>`}
    <div class="sec"><h2>${window.icon('info')} কীভাবে কাজ করে?</h2></div>
    <div class="pad" style="padding-top:0">
      <div class="card card-pad">
        ${[['১.', 'আপনার ইউনিক কোড বন্ধুদের শেয়ার করুন'], ['২.', 'তিনি রেজিস্ট্রেশনের সময় "রেফারেল কোড" ঘরে কোডটি দেবেন'], ['৩.', 'প্রতিটি সফল রেফারেলে আপনি ১০ পয়েন্ট পাবেন এবং তালিকায় যুক্ত হবেন']].map(s => `
        <div class="row" style="align-items:flex-start;margin-bottom:10px"><b style="color:var(--primary);font-size:15px">${s[0]}</b><span style="font-size:13px;color:var(--on-surface-variant);line-height:1.7">${s[1]}</span></div>`).join('')}
      </div>
    </div>
    <div class="gap24"></div>`;

    UI.bindAppbar(el);
    el.querySelector('#cp').onclick = () => {
      try { navigator.clipboard.writeText(me.referralCode); } catch (e) {}
      UI.toast('কোড কপি হয়েছে: ' + me.referralCode, 'ok');
    };
    el.querySelector('#sh').onclick = () => {
      C.shareText(`🩸 ${APP.name} অ্যাপে যোগ দিন!\nরক্ত দিন, জীবন বাঁচান — ${APP.org}\n\nরেজিস্ট্রেশনের সময় আমার রেফারেল কোড দিন: ${me.referralCode}`, 'রেফারেল শেয়ার');
    };
    el.querySelectorAll('[data-open]').forEach(c => c.addEventListener('click', () => window.openDonorSheet(c.dataset.open)));
  });

  /* ═══ MORE / PROFILE HUB ════════════════════════════════════ */
  Router.add('more', (el) => {
    const me = State.me();
    const [bcls, blbl] = State.badge(me);
    const rank = State.ranking().findIndex(u => u.id === me.id) + 1;

    el.innerHTML = `
    ${UI.appbar({ title: 'আরও সেবা', subtitle: esc(APP.org) })}
    <div class="pro-head rise">
      <div class="avatar lg" style="background:${C.avatarColor(me.id)};color:#fff">${esc(C.initials(me.name))}</div>
      <h2>${esc(me.name)} ${me.verified ? window.icon('verified') : ''}</h2>
      <div class="p-sub">${me.bloodType} • ${blbl} • র‌্যাংক ${bnNum(rank)}</div>
      <div class="pro-stats">
        <div class="ps"><b>${bnNum(me.donationCount || 0)}</b><span>মোট দান</span></div>
        <div class="ps"><b>${bnNum(me.referralCount || 0)}</b><span>রেফারেল</span></div>
        <div class="ps"><b>${bnNum(me.trustScore || 40)}%</b><span>ট্রাস্ট স্কোর</span></div>
      </div>
    </div>
    <div class="quick-grid" style="grid-template-columns:repeat(4,1fr)">
      <button class="qa purple" data-go="donorcard"><span class="qa-ico">${window.icon('card')}</span><span>ডোনার<br>কার্ড</span></button>
      <button class="qa amber" data-go="ranking"><span class="qa-ico">${window.icon('medal')}</span><span>র‌্যাংকিং</span></button>
      <button class="qa teal" data-go="referral"><span class="qa-ico">${window.icon('people')}</span><span>রেফারেল</span></button>
      <button class="qa red" data-go="profile"><span class="qa-ico">${window.icon('person')}</span><span>প্রোফাইল<br>সম্পাদনা</span></button>
    </div>
    <div class="gap12"></div>
    <div class="sec"><h2>${window.icon('grid')} সব সেবা</h2></div>
    <div class="menu-card">
      ${[
        ['history', 'history', 'var(--primary-container)', 'var(--on-primary-container)', 'রক্তদানের ইতিহাস', 'আপনার সকল দানের রেকর্ড ও টাইমলাইন'],
        ['notifications', 'bell', 'var(--tertiary-container)', 'var(--on-tertiary-container)', 'নোটিফিকেশন', `পড়া হয়নি ${bnNum(State.unreadCount())} টি`],
        ['settings', 'settings', 'var(--surface-container-high)', 'var(--on-surface-variant)', 'সেটিংস', 'থিম, ডেটা ও সার্ভার সিঙ্ক'],
        ['about', 'info', 'var(--surface-container-high)', 'var(--on-surface-variant)', 'অ্যাপ ও সংগঠন সম্পর্কে', `${APP.org}`]
      ].map(m => `
      <div class="menu-row" data-go="${m[0]}">
        <span class="mr-ico" style="background:${m[2]};color:${m[3]}">${window.icon(m[1])}</span>
        <div class="mr-main"><b>${m[4]}</b><span>${m[5]}</span></div>
        <span class="mr-end">${window.icon('chevron')}</span>
      </div>`).join('')}
    </div>
    <div class="gap12"></div>
    <div class="menu-card" style="margin-bottom:20px">
      <div class="menu-row" id="logout">
        <span class="mr-ico" style="background:var(--error-container);color:var(--on-error-container)">${window.icon('logout')}</span>
        <div class="mr-main"><b>লগআউট</b><span>অ্যাকাউন্ট থেকে বেরিয়ে যান</span></div>
      </div>
    </div>
    <div style="text-align:center;padding-bottom:20px">
      <span style="font-size:11px;color:var(--outline);font-weight:600">${esc(APP.name)} v${APP.version} • ${esc(APP.org)}</span>
    </div>`;

    UI.showNav(true);
    UI.bindAppbar(el);
    el.querySelectorAll('[data-go]').forEach(b => b.addEventListener('click', () => Router.go(b.dataset.go)));
    el.querySelector('#logout').onclick = async () => {
      const ok = await UI.confirm('লগআউট করবেন?', 'আপনার অ্যাকাউন্ট থেকে বেরিয়ে যাবেন। ডেটা সংরক্ষিত থাকবে, আবার লগইন করে ফিরে আসতে পারবেন।', 'লগআউট');
      if (!ok) return;
      C.Auth.logout();
      Router.resetTo('auth');
    };
  });

  /* ═══ PROFILE EDIT ═════════════════════════════════════════ */
  Router.add('profile', (el) => {
    const me = State.me();
    const render = () => {
      const m = State.me();
      el.innerHTML = `
      ${UI.appbar({ title: 'প্রোফাইল সম্পাদনা', subtitle: 'আপনার তথ্য হালনাগাদ করুন', back: true })}
      <div class="pad">
        <div class="card card-pad">
          <div style="text-align:center;margin-bottom:20px">
            <div class="avatar lg" style="background:${C.avatarColor(m.id)};color:#fff;margin:0 auto">${esc(C.initials(m.name))}</div>
            <div style="margin-top:10px"><span class="bgroup sm">${m.bloodType}</span> ${m.verified ? `<span class="pill ok">${window.icon('verified')} ভেরিফায়েড</span>` : `<span class="pill info">ভেরিফিকেশন অপেক্ষমান</span>`}</div>
          </div>
          ${UI.field({ id: 'p_name', label: 'পূর্ণ নাম', required: true, value: m.name, err: 'নাম দিন' })}
          ${UI.field({ id: 'p_phone', label: 'ফোন নম্বর', required: true, value: m.phone, inputmode: 'tel', maxlength: 11, err: 'সঠিক ফোন নম্বর দিন (01XXXXXXXXX)' })}
          ${UI.field({ id: 'p_email', label: 'ইমেইল', type: 'email', required: true, value: m.email, err: 'সঠিক ইমেইল দিন' })}
          ${UI.field({ id: 'p_addr', label: 'ঠিকানা / এলাকা', required: true, value: m.address, err: 'ঠিকানা দিন' })}
          ${UI.field({ id: 'p_nid', label: 'জাতীয় পরিচয়পত্র নম্বর (ঐচ্ছিক)', value: m.nid || '', inputmode: 'numeric', maxlength: 17, hint: 'ভেরিফিকেশনের জন্য পরে ব্যবহৃত হতে পারে' })}
          <button class="btn btn-grad btn-block" id="p_save">${window.icon('check')} সংরক্ষণ করুন</button>
        </div>
        <div class="gap16"></div>
        <div class="card card-pad" style="background:var(--surface-container)">
          <div class="row between">
            <div><b style="font-size:14px">রেফারেল কোড</b><div style="font-size:12px;color:var(--on-surface-variant)">${esc(m.referralCode)}</div></div>
            <button class="btn btn-sm btn-tonal" id="p_copy">${window.icon('copy')} কপি</button>
          </div>
        </div>
      </div>
      <div class="gap24"></div>`;
      UI.bindAppbar(el);
      el.querySelector('#p_copy').onclick = () => { try { navigator.clipboard.writeText(m.referralCode); } catch (e) {} UI.toast('কোড কপি হয়েছে', 'ok'); };
      el.querySelector('#p_save').onclick = () => {
        if (!UI.validate(el)) return;
        m.name = el.querySelector('#p_name').value.trim();
        m.phone = el.querySelector('#p_phone').value.trim();
        m.email = el.querySelector('#p_email').value.trim();
        m.address = el.querySelector('#p_addr').value.trim();
        m.nid = el.querySelector('#p_nid').value.trim();
        State.save();
        UI.toast('প্রোফাইল আপডেট হয়েছে', 'ok');
        Router.back();
      };
    };
    render();
  });

  /* ═══ SETTINGS ═════════════════════════════════════════════ */
  Router.add('settings', (el) => {
    const s = State.data.settings;
    const render = () => {
      el.innerHTML = `
      ${UI.appbar({ title: 'সেটিংস', back: true })}
      <div class="sec"><h2>${window.icon('dark')} উপস্থিতি</h2></div>
      <div class="menu-card">
        <div class="menu-row">
          <span class="mr-ico" style="background:var(--surface-container-high);color:var(--on-surface-variant)">${window.icon('dark')}</span>
          <div class="mr-main"><b>ডার্ক মোড</b><span>রাতে চোখের আরামের জন্য</span></div>
          <label class="switch"><input type="checkbox" id="st_dark" ${document.documentElement.classList.contains('dark') ? 'checked' : ''}><span class="tr"></span><span class="th"></span></label>
        </div>
      </div>
      <div class="sec"><h2>${window.icon('sync')} ডেটা ও সিঙ্ক</h2></div>
      <div class="menu-card">
        <div class="menu-row">
          <span class="mr-ico" style="background:var(--tertiary-container);color:var(--on-tertiary-container)">${window.icon('people')}</span>
          <div class="mr-main"><b>ডেমো ডেটা</b><span>নমুনা ডোনার ও আবেদন দেখান</span></div>
          <label class="switch"><input type="checkbox" id="st_demo" ${s.demoData ? 'checked' : ''}><span class="tr"></span><span class="th"></span></label>
        </div>
        <div class="menu-row" id="row-server">
          <span class="mr-ico" style="background:var(--primary-container);color:var(--on-primary-container)">${window.icon('sync')}</span>
          <div class="mr-main"><b>সার্ভার সিঙ্ক URL</b><span>${s.serverUrl ? esc(s.serverUrl) : 'ক্লাউড ব্যাকএন্ড নির্ধারণ করুন (ঐচ্ছিক)'}</span></div>
          <span class="mr-end">${window.icon('chevron')}</span>
        </div>
        <div class="menu-row" id="row-wipe">
          <span class="mr-ico" style="background:var(--error-container);color:var(--on-error-container)">${window.icon('trash')}</span>
          <div class="mr-main"><b>সব ডেটা মুছুন</b><span>অ্যাপটি নতুন অবস্থায় ফিরবে</span></div>
        </div>
      </div>
      <div class="sec"><h2>${window.icon('info')} অ্যাপ</h2></div>
      <div class="menu-card" style="margin-bottom:20px">
        <div class="menu-row" data-go="about">
          <span class="mr-ico" style="background:var(--primary-container);color:var(--on-primary-container)">${window.icon('dropFill')}</span>
          <div class="mr-main"><b>অ্যাপ ও সংগঠন সম্পর্কে</b><span>${esc(APP.name)} v${APP.version}</span></div>
          <span class="mr-end">${window.icon('chevron')}</span>
        </div>
      </div>`;
      UI.bindAppbar(el);

      el.querySelector('#st_dark').onchange = (e) => {
        s.theme = e.target.checked ? 'dark' : 'light';
        State.save();
        document.documentElement.classList.toggle('dark', e.target.checked);
        C.haptic(10);
      };
      el.querySelector('#st_demo').onchange = (e) => {
        s.demoData = e.target.checked;
        State.save();
        UI.toast(s.demoData ? 'ডেমো ডেটা চালু হয়েছে' : 'ডেমো ডেটা বন্ধ — এখন শুধু আসল সদস্যরা দেখা যাবে', 'ok');
      };
      el.querySelector('#row-server').onclick = () => serverSheet(render);
      el.querySelector('#row-wipe').onclick = async () => {
        const ok = await UI.confirm('সব ডেটা মুছে ফেলবেন?', 'আপনার অ্যাকাউন্ট, রক্তদানের রেকর্ড, জরুরি আবেদন — সবকিছু মুছে যাবে। এই কাজ ফেরানো যাবে না।', 'মুছে ফেলুন', true);
        if (!ok) return;
        C.Store.wipe();
        location.reload();
      };
      el.querySelectorAll('[data-go]').forEach(b => b.addEventListener('click', () => Router.go(b.dataset.go)));
    };

    const serverSheet = (rerender) => {
      UI.sheet({
        title: 'সার্ভার সিঙ্ক URL',
        icon: 'sync',
        body: `
        <p style="font-size:12.5px;color:var(--on-surface-variant);line-height:1.75;margin-bottom:14px">সংগঠনের REST ব্যাকএন্ড হোস্ট করা থাকলে এখানে URL দিন — রেজিস্ট্রেশন, লগইন ও জরুরি আবেদন সার্ভারেও সিঙ্ক হবে। না জানলে খালি রাখুন; অ্যাপ সম্পূর্ণ অফলাইনে কাজ করে।</p>
        ${UI.field({ id: 'sv_url', label: 'ব্যাকএন্ড URL', type: 'url', value: State.data.settings.serverUrl || '', placeholder: 'https://api.example.com', hint: 'যেমন: https://halal-rokto.vercel.app' })}
        <button class="btn btn-grad btn-block" id="sv_save">${window.icon('check')} সংরক্ষণ করুন</button>`,
        onMount(sheet, close) {
          sheet.querySelector('#sv_save').onclick = () => {
            const v = sheet.querySelector('#sv_url').value.trim();
            if (v && !/^https?:\/\/.+\..+/.test(v)) return UI.toast('সঠিক URL দিন (https:// দিয়ে শুরু)', 'err');
            State.data.settings.serverUrl = v;
            State.data.sync = { last: null, status: v ? 'on' : 'off' };
            State.save();
            close();
            UI.toast(v ? 'সার্ভার সিঙ্ক চালু: ' + v : 'সার্ভার সিঙ্ক বন্ধ', 'ok');
            rerender();
          };
        }
      });
    };
    render();
  });

  /* ═══ ABOUT ════════════════════════════════════════════════ */
  Router.add('about', (el) => {
    el.innerHTML = `
    ${UI.appbar({ title: 'সংগঠন সম্পর্কে', back: true })}
    <div class="about-hero rise">
      <div class="logo-badge">${window.icon('dropFill')}</div>
      <h1>${esc(APP.org)}</h1>
      <div class="a-tag">${esc(APP.nameEn)} — ${esc(APP.name)}</div>
      <div class="a-since">${window.icon('shield')} ${esc(APP.since)} • ${esc(APP.address)}</div>
    </div>
    <div class="about-sec rise d1">
      <h3>${window.icon('info')} আমাদের কথা</h3>
      <p>${esc(APP.org)} বাগাতিপাড়া, নাটোরের তরুণ সমাজের একটি স্বেচ্ছাসেবী সংগঠন। ২০২৬ সাল থেকে সংগঠনটি এলাকার মানুষের সেবায় নিয়োজিত। হালাল রক্ত দান অ্যাপের মাধ্যমে আমরা রক্তদাতাদের একটি সুসংগঠিত ডেটাবেস গড়ে তুলছি — যাতে জরুরি মুহূর্তে কেউ রক্তের জন্য দরদর না ঘুরে বেড়ায়।</p>
    </div>
    <div class="about-sec rise d2">
      <h3>${window.icon('dropFill')} আমাদের লক্ষ্য</h3>
      <p>এলাকার প্রতিটি যোগ্য ব্যক্তিকে নিয়মিত রক্তদাতা হিসেবে সংগঠিত করা, রোগী ও দাতার মধ্যে দ্রুত যোগাযোগ নিশ্চিত করা এবং হালাল ও নিরাপদ পথে রক্ত সংগ্রহ ও বিতরণ ব্যবস্থাপনা করা। প্রতিটি ব্যাগ রক্ত বাঁচায় তিনটি প্রাণ — আমাদের এই বিশ্বাস নিয়েই আমরা কাজ করি।</p>
    </div>
    <div class="about-sec rise d3">
      <h3>${window.icon('hosp')} রক্তদানের নিয়ম</h3>
      <p>১৮-৬০ বছর বয়সী সুস্থ প্রাপ্তবয়স্করা রক্ত দিতে পারেন। দুইবার রক্তদানের মধ্যে কমপক্ষে ৯০ দিন (৩ মাস) বিরতি রাখতে হয়। রক্ত দেওার আগে হালকা খাবার খাওয়া জরুরি; খালি পাকস্থলীতে রক্ত দেওয়া উচিত নয়। দানের পর প্রচুর পানি পান করুন এবং দিনটি বিশ্রামে কাটান।</p>
    </div>
    <div class="pad rise d4">
      <div class="card card-pad" style="text-align:center;background:var(--primary-container);color:var(--on-primary-container);border:0">
        <b style="font-size:15px">${esc(APP.name)} v${APP.version}</b>
        <div style="font-size:11.5px;opacity:.8;margin-top:3px">${esc(APP.org)} • ${esc(APP.since)} • ${esc(APP.address)}, ${esc(APP.district)}</div>
      </div>
    </div>
    <div class="gap24"></div>`;
    UI.bindAppbar(el);
  });

})();
