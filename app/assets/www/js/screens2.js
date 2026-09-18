/* ═══════════════════════════════════════════════════════════
   হালাল রক্ত দান v2.0 — Screens Part 2:
   emergency • donors • ranking • history
   ═══════════════════════════════════════════════════════════ */
(function () {
  'use strict';
  const C = window.Core, UI = window.UI, Router = window.Router;
  const { esc, toBn, bnNum, fmtDate, fmtDateShort, timeAgo } = C;
  const { BLOOD_GROUPS, State, APP, Sync, uid } = C;

  /* ═══ EMERGENCY ════════════════════════════════════════════ */
  Router.add('emergency', (el) => {
    const render = () => {
      const list = State.emergencies().sort((a, b) => (a.fulfilled - b.fulfilled) || b.createdAt - a.createdAt);

      el.innerHTML = `
      ${UI.appbar({ title: 'জরুরি রক্তের আবেদন', subtitle: 'মুহূর্তের প্রয়োজনে ডোনার খুঁজুন' })}
      <button class="fab" id="new-emg">${window.icon('sos')} নতুন আবেদন</button>
      ${list.length ? `<div style="padding:6px 0 10px">${list.map(e => emgCard(e)).join('')}</div>` :
        UI.empty('sos', 'এখন কোনো জরুরি আবেদন নেই', 'কারও জরুরি রক্তের প্রয়োজন হলে এখানে আবেদন করুন — সদস্যরা দ্রুত সাড়া দিতে পারবেন।')}
      <div class="gap24"></div>`;

      UI.bindAppbar(el);
      el.querySelector('#new-emg').onclick = () => newEmergency();
      el.querySelectorAll('[data-call]').forEach(a => a.addEventListener('click', e => { e.stopPropagation(); C.haptic(14); }));
      el.querySelectorAll('[data-share]').forEach(b => b.addEventListener('click', e => {
        e.stopPropagation();
        const e2 = State.emergencies().find(x => x.id === b.dataset.share);
        if (!e2) return;
        C.shareText(`🚨 জরুরি ${e2.bloodGroup} রক্তের প্রয়োজন!\nরোগী: ${e2.patientName}\nহাসপাতাল: ${e2.hospital}\nযোগাযোগ: ${e2.contact}\n— ${APP.org}`, 'জরুরি রক্ত শেয়ার');
      }));
      el.querySelectorAll('[data-fulfill]').forEach(b => b.addEventListener('click', async e => {
        e.stopPropagation();
        const em = State.emergencies().find(x => x.id === b.dataset.fulfill);
        if (!em) return;
        const ok = await UI.confirm('আবেদন পূরণ হয়েছে?', `<b>${esc(em.patientName)}</b>-এর ${esc(em.bloodGroup)} রক্তের আবেদনটি পূরণ হয়েছে হিসেবে চিহ্নিত করবেন? স্টক থেকে ${bnNum(em.units)} ব্যাগ বাদ যাবে।`, 'হ্যাঁ, পূরণ হয়েছে');
        if (!ok) return;
        em.fulfilled = true;
        State.data.stock[em.bloodGroup] = Math.max(0, (State.data.stock[em.bloodGroup] || 0) - em.units);
        State.save();
        UI.toast('আবেদনটি পূরণ হিসেবে চিহ্নিত হয়েছে', 'ok');
        render();
      }));
      el.querySelectorAll('.emg-card[data-open]').forEach(c => c.addEventListener('click', () => donorAlertSheet(c.dataset.open)));
    };

    const emgCard = (e) => `
      <div class="emg-card rise ${e.fulfilled ? 'done' : ''}" data-open="${e.id}">
        <div class="emg-head">
          ${e.fulfilled ? '<span class="pill ok">' + window.icon('check') + ' পূরণ হয়েছে</span>' : '<span class="em-pulse"></span>'}
          <b>${esc(e.patientName)}</b>
          <time>${timeAgo(e.createdAt)}</time>
        </div>
        <div class="emg-body">
          <div style="display:flex;align-items:center;gap:10px;margin-bottom:10px">
            <span class="bgroup solid">${esc(e.bloodGroup)}</span>
            <div class="grow" style="font-size:12.5px;font-weight:700;color:var(--on-surface-variant)">${bnNum(e.units)} ব্যাগ প্রয়োজন</div>
          </div>
          <div class="emg-meta">
            <span>${window.icon('hosp')} ${esc(e.hospital)}</span>
            <span>${window.icon('location')} ${esc(e.location)}</span>
            <span>${window.icon('phone')} ${esc(e.contact)}</span>
          </div>
          ${e.notes ? `<div class="emg-note">${esc(e.notes)}</div>` : ''}
          <div class="emg-actions">
            <a class="btn btn-success btn-sm grow" href="tel:${esc(e.contact)}" data-call>${window.icon('phone')} কল করুন</a>
            <button class="btn btn-tonal btn-sm" data-share="${e.id}">${window.icon('share')} শেয়ার</button>
            ${!e.fulfilled ? `<button class="btn btn-red-tonal btn-sm" data-fulfill="${e.id}">${window.icon('check')}</button>` : ''}
          </div>
        </div>
      </div>`;

    const donorAlertSheet = (id) => {
      const e = State.emergencies().find(x => x.id === id);
      if (!e) return;
      UI.sheet({
        title: 'ডোনারদের জানান',
        icon: 'sos',
        body: `
        <p style="font-size:13px;color:var(--on-surface-variant);line-height:1.75;margin-bottom:14px">${esc(e.patientName)}-এর জন্য ${esc(e.bloodGroup)} রক্ত দরকার। নিচের বাটনে চাপ দিলে <b>${esc(e.bloodGroup)}</b> গ্রুপের যোগ্য ডোনারদের তালিকা দেখতে পারবেন — সরাসরি কল করুন।</p>
        <button class="btn btn-grad btn-block" id="find-now">${window.icon('people')} ${esc(e.bloodGroup)} ডোনার দেখুন</button>`,
        onMount(sheet, close) {
          sheet.querySelector('#find-now').onclick = () => { close(); Router.go('donors', { filter: e.bloodGroup }); };
        }
      });
    };

    const newEmergency = () => {
      UI.sheet({
        title: 'নতুন জরুরি আবেদন',
        icon: 'sos',
        body: `
        ${UI.field({ id: 'e_patient', label: 'রোগীর নাম', required: true, placeholder: 'যেমন: জনাব আব্দুল জলিল (৬২)', err: 'রিসিপিয়েন্টের নাম দিন' })}
        <div class="field">
          <label>রক্তের গ্রুপ <span class="req">*</span></label>
          <div class="bg-grid" id="e_bg">${BLOOD_GROUPS.map(g => `<button class="bg-opt" data-g="${g}">${g}</button>`).join('')}</div>
        </div>
        ${UI.field({ id: 'e_units', label: 'কত ব্যাগ দরকার', type: 'number', required: true, value: '2', placeholder: '1-10', err: 'রক্তের পরিমাণ দিন' })}
        ${UI.field({ id: 'e_hosp', label: 'হাসপাতালের নাম', required: true, placeholder: 'যেমন: নাটোর সদর হাসপাতাল', err: 'হাসপাতালের নাম দিন' })}
        ${UI.field({ id: 'e_loc', label: 'হাসপাতালের লোকেশন', required: true, placeholder: 'যেমন: নাটোর সদর', err: 'লোকেশন দিন' })}
        ${UI.field({ id: 'e_contact', label: 'যোগাযোগ নম্বর', required: true, inputmode: 'tel', maxlength: 11, placeholder: '01XXXXXXXXX', err: 'সঠিক ফোন নম্বর দিন (01XXXXXXXXX)' })}
        ${UI.field({ id: 'e_notes', label: 'নোট (ঐচ্ছিক)', type: 'textarea', placeholder: 'রোগীর অবস্থা, অপারেশনের সময় ইত্যাদি...' })}
        <button class="btn btn-grad btn-block" id="e_save">${window.icon('sos')} জরুরি আবেদন পাঠান</button>
        <p style="font-size:11.5px;color:var(--outline);text-align:center;margin-top:10px">আবেদন করার আগে নিশ্চিত হয়ে নিন যে তথ্যগুলো সঠিক।</p>`,
        onMount(sheet, close) {
          let bg = '';
          sheet.querySelectorAll('#e_bg .bg-opt').forEach(b => b.onclick = () => {
            sheet.querySelectorAll('#e_bg .bg-opt').forEach(x => x.classList.remove('on'));
            b.classList.add('on'); bg = b.dataset.g; C.haptic(8);
          });
          sheet.querySelector('#e_save').onclick = () => {
            if (!UI.validate(sheet)) return;
            if (!bg) return UI.toast('রক্তের গ্রুপ নির্বাচন করুন', 'err');
            const units = Math.max(1, Math.min(10, parseInt(sheet.querySelector('#e_units').value) || 1));
            const em = {
              id: uid('em'), patientName: sheet.querySelector('#e_patient').value.trim(),
              bloodGroup: bg, units,
              hospital: sheet.querySelector('#e_hosp').value.trim(),
              location: sheet.querySelector('#e_loc').value.trim(),
              contact: sheet.querySelector('#e_contact').value.trim(),
              notes: sheet.querySelector('#e_notes').value.trim(),
              createdAt: Date.now(), createdBy: State.me().id, fulfilled: false
            };
            State.data.emergencies.unshift(em);
            State.push({ id: uid('n'), userId: 'all', type: 'emg', title: '🚨 জরুরি ' + bg + ' রক্তের প্রয়োজন!', body: `${em.patientName} — ${em.hospital}, ${em.location}. যোগাযোগ: ${em.contact}. ${bnNum(em.units)} ব্যাগ প্রয়োজন।`, date: Date.now(), read: false });
            State.save();
            Sync.pushEmergency(em);
            C.shareText(`🚨 জরুরি ${bg} রক্তের প্রয়োজন!\nরোগী: ${em.patientName}\nহাসপাতাল: ${em.hospital}, ${em.location}\nযোগাযোগ: ${em.contact}\n— ${APP.org}`, 'জরুরি রক্ত শেয়ার');
            close();
            UI.toast('জরুরি আবেদন তৈরি হয়েছে — শেয়ার করে সবাইকে জানান', 'ok');
            render();
          };
        }
      });
    };
    render();
  });

  /* ═══ DONORS ═══════════════════════════════════════════════ */
  let donorFilter = { group: '', q: '' };
  Router.add('donors', (el, opts) => {
    if (opts && opts.filter) donorFilter.group = opts.filter;
    donorFilter.q = '';

    const render = () => {
      let list = State.users();
      if (donorFilter.group) list = list.filter(u => u.bloodType === donorFilter.group);
      if (donorFilter.q) {
        const q = donorFilter.q.toLowerCase();
        list = list.filter(u => (u.name + ' ' + u.address + ' ' + u.phone).toLowerCase().includes(q));
      }
      list = list.sort((a, b) => {
        const ea = State.cooldown(a).eligible ? 0 : 1, eb = State.cooldown(b).eligible ? 0 : 1;
        return ea - eb || (b.donationCount || 0) - (a.donationCount || 0);
      });

      el.innerHTML = `
      ${UI.appbar({ title: 'ডোনার খুঁজুন', subtitle: 'রক্তের গ্রুপ অনুযায়ী যোগ্য ডোনার' })}
      <div class="pad" style="padding-bottom:8px">
        <div class="field" style="margin:0">
          <div style="position:relative">
            <input id="d_q" type="search" placeholder="নাম, এলাকা বা ফোন দিয়ে খুঁজুন..." value="${esc(donorFilter.q)}" style="padding-left:46px">
            <span style="position:absolute;left:14px;top:50%;transform:translateY(-50%);color:var(--outline);display:flex">${window.icon('search')}</span>
          </div>
        </div>
      </div>
      <div class="chip-row">
        <button class="chip chip-red ${!donorFilter.group ? 'on' : ''}" data-g="">সব গ্রুপ</button>
        ${BLOOD_GROUPS.map(g => `<button class="chip chip-red ${donorFilter.group === g ? 'on' : ''}" data-g="${g}">${g}</button>`).join('')}
      </div>
      <div style="padding:2px 0 8px">
        ${list.length ? `<div style="padding:0 16px 4px;font-size:12px;font-weight:700;color:var(--on-surface-variant)">${bnNum(list.length)} জন ডোনার পাওয়া গেছে</div>` : ''}
        ${list.length ? list.map(u => donorRow(u)).join('') :
          UI.empty('people', 'কোনো ডোনার পাওয়া যায়নি', 'অন্য গ্রুপ বা নতুন সার্চ চেষ্টা করুন।')}
      </div>
      <button class="fab" id="add-donor">${window.icon('add')} নতুন ডোনার</button>
      <div class="gap24"></div>`;

      UI.bindAppbar(el);
      const q = el.querySelector('#d_q');
      q.addEventListener('input', () => {
        donorFilter.q = q.value;
        clearTimeout(window._dt);
        window._dt = setTimeout(render, 220);
      });
      el.querySelectorAll('.chip[data-g]').forEach(ch => ch.onclick = () => {
        donorFilter.group = ch.dataset.g;
        C.haptic(8); render();
      });
      el.querySelectorAll('[data-open]').forEach(c => c.addEventListener('click', () => donorSheet(c.dataset.open)));
      el.querySelectorAll('[data-tel]').forEach(a => a.addEventListener('click', e => { e.stopPropagation(); C.haptic(14); }));
      el.querySelector('#add-donor').onclick = () => addDonorSheet(render);
    };

    const donorRow = (u) => {
      const cd = State.cooldown(u);
      return `
      <div class="donor-card rise" data-open="${u.id}">
        <div class="avatar" style="background:${C.avatarColor(u.id)};color:#fff">${esc(C.initials(u.name))}</div>
        <div class="dc-main">
          <div class="dc-name">${esc(u.name)} ${u.verified ? `<span class="verified-ico">${window.icon('verified')}</span>` : ''}</div>
          <div class="dc-sub">${esc(u.address)}</div>
          <div class="dc-stats">
            <span>${window.icon('drop')} ${bnNum(u.donationCount || 0)} বার</span>
            <span>${window.icon('medal')} ${State.badge(u)[1]}</span>
          </div>
        </div>
        <div style="display:flex;flex-direction:column;align-items:flex-end;gap:7px">
          <span class="bgroup sm ${cd.eligible ? '' : 'solid'}">${u.bloodType}</span>
          <span class="pill ${cd.eligible ? 'ok' : 'info'}" style="height:22px;font-size:10px">${cd.eligible ? 'দিতে পারেন' : bnNum(cd.days) + ' দিন'}</span>
        </div>
        <a class="call-fab" href="tel:${esc(u.phone)}" data-tel="${u.id}" aria-label="${esc(u.name)}-কে কল করুন">${window.icon('phone')}</a>
      </div>`;
    };

    const donorSheet = (id) => {
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
          <div class="dr"><span class="dr-ico">${window.icon('calendar')}</span><span class="dr-lbl">সদস্য হয়েছেন</span><span class="dr-val">${fmtDate(u.createdAt)}</span></div>
        </div>
        <div class="card card-pad" style="background:${cd.eligible ? 'var(--success-container);color:var(--on-success-container)' : 'var(--surface-container-high)'};border:0;margin-bottom:14px">
          ${cd.first ? '<b style="font-size:14px">এই ডোনার এখনো কোনো রক্ত দেননি</b>' :
            cd.eligible ? `<b style="font-size:14px">✅ এই ডোনার রক্ত দিতে পারেন</b><div style="font-size:12px;opacity:.8;margin-top:2px">শেষ দান ${bnNum(cd.lastDiff)} দিন আগে</div>` :
            `<b style="font-size:14px">⏳ ${bnNum(cd.days)} দিন পর রক্ত দিতে পারবেন</b><div style="font-size:12px;opacity:.8;margin-top:2px">শেষ দান: ${fmtDate(u.lastDonationDate)}</div>`}
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

    const addDonorSheet = (rerender) => {
      UI.sheet({
        title: 'নতুন ডোনার যোগ করুন',
        icon: 'add',
        body: `
        <p style="font-size:12.5px;color:var(--on-surface-variant);line-height:1.7;margin-bottom:14px">পরিচিত কাউকে ডোনার হিসেবে যোগ করুন — পরে তিনি নিজে রেজিস্ট্রেশন করে অ্যাকাউন্ট নিতে পারবেন।</p>
        ${UI.field({ id: 'ad_name', label: 'ডোনারের নাম', required: true, placeholder: 'পূর্ণ নাম লিখুন', err: 'নাম দিন' })}
        ${UI.field({ id: 'ad_phone', label: 'ফোন নম্বর', required: true, inputmode: 'tel', maxlength: 11, placeholder: '01XXXXXXXXX', err: 'সঠিক ফোন নম্বর দিন (01XXXXXXXXX)' })}
        <div class="field">
          <label>রক্তের গ্রুপ <span class="req">*</span></label>
          <div class="bg-grid" id="ad_bg">${BLOOD_GROUPS.map(g => `<button class="bg-opt" data-g="${g}">${g}</button>`).join('')}</div>
        </div>
        ${UI.field({ id: 'ad_addr', label: 'ঠিকানা / এলাকা', required: true, placeholder: 'যেমন: হিজলি, বাগাতিপাড়া', err: 'ঠিকানা দিন' })}
        <button class="btn btn-grad btn-block" id="ad_save">${window.icon('check')} ডোনার যোগ করুন</button>`,
        onMount(sheet, close) {
          let bg = '';
          sheet.querySelectorAll('#ad_bg .bg-opt').forEach(b => b.onclick = () => {
            sheet.querySelectorAll('#ad_bg .bg-opt').forEach(x => x.classList.remove('on'));
            b.classList.add('on'); bg = b.dataset.g; C.haptic(8);
          });
          sheet.querySelector('#ad_save').onclick = () => {
            if (!UI.validate(sheet)) return;
            if (!bg) return UI.toast('রক্তের গ্রুপ নির্বাচন করুন', 'err');
            if (State.data.users.find(x => x.phone === sheet.querySelector('#ad_phone').value.trim()))
              return UI.toast('এই ফোন নম্বর ইতিমধ্যে তালিকায় আছে', 'err');
            State.data.users.push({
              id: uid('u'), name: sheet.querySelector('#ad_name').value.trim(),
              phone: sheet.querySelector('#ad_phone').value.trim(), bloodType: bg,
              address: sheet.querySelector('#ad_addr').value.trim(),
              email: '', nid: '', password: null,
              donationCount: 0, trustScore: 40,
              referralCode: 'DJS-' + String(100000 + Math.abs(C.hashCode(bg + Date.now())) % 900000).slice(0, 5),
              referredBy: '', referralCount: 0, verified: false,
              lastDonationDate: null, createdAt: Date.now(),
              addedBy: State.me().name, isDemo: false
            });
            State.save();
            close(); UI.toast('নতুন ডোনার যোগ হয়েছে', 'ok');
            rerender();
          };
        }
      });
    };
    render();
  });

  /* ═══ RANKING ══════════════════════════════════════════════ */
  Router.add('ranking', (el) => {
    const list = State.ranking();
    const me = State.me();
    const myRank = list.findIndex(u => u.id === me.id) + 1;
    const podium = list.slice(0, 3);
    const medals = ['medal', 'medal', 'medal'];
    const mColors = ['#D4AF37', '#A8A9AD', '#CD7F32'];

    el.innerHTML = `
    ${UI.appbar({ title: 'ডোনার র‌্যাংকিং', subtitle: 'সম্মাননা ও পদক তালিকা', back: true })}
    ${podium.length >= 3 ? `
    <div class="podium rise">
      ${[1, 0, 2].map(i => {
        const u = podium[i];
        return `<div class="pod p${i + 1}">
          <span class="p-medal" style="color:${mColors[i]}">${window.icon(medals[i])}</span>
          <div class="avatar" style="background:${C.avatarColor(u.id)};color:#fff">${esc(C.initials(u.name))}</div>
          <div class="p-name">${esc(u.name)}</div>
          <div class="p-count">${bnNum(u.donationCount || 0)} বার দান</div>
        </div>`;
      }).join('')}
    </div>` : ''}
    <div class="gap8"></div>
    <div class="sec"><h2>${window.icon('medal')} সম্পূর্ণ তালিকা</h2></div>
    <div style="padding:0 0 8px">
      ${list.slice(3).map((u, i) => {
        const [bcls, blbl] = State.badge(u);
        return `<div class="rank-row ${u.id === me.id ? 'me' : ''}" data-open="${u.id}">
          <span class="rr-pos">${bnNum(i + 4)}</span>
          <div class="avatar sm" style="background:${C.avatarColor(u.id)};color:#fff">${esc(C.initials(u.name))}</div>
          <div class="rr-main">
            <div class="rr-name">${esc(u.name)} ${u.id === me.id ? '<span style="font-size:10px;color:var(--primary)">— আপনি</span>' : ''}</div>
            <div class="rr-sub">${esc(u.address)}</div>
          </div>
          <span class="rank-badge ${bcls}">${blbl}</span>
          <span class="rr-pos" style="color:var(--primary)">${bnNum(u.donationCount || 0)}</span>
        </div>`;
      }).join('')}
    </div>
    <div class="pad">
      <div class="card card-pad" style="background:var(--primary-container);color:var(--on-primary-container);border:0">
        <div class="row">
          <span class="qa-ico" style="width:46px;height:46px;border-radius:16px;background:rgba(255,255,255,.25);display:flex;align-items:center;justify-content:center">${window.icon('medal')}</span>
          <div class="grow"><b style="font-size:15px">🏆 আপনার র‌্যাংকিং: ${bnNum(myRank)} নং</b>
          <div style="font-size:12px;opacity:.8;margin-top:2px">মোট ${bnNum(me.donationCount || 0)} বার দান • ${bnNum(list.length)} জনের মধ্যে</div></div>
        </div>
      </div>
    </div>
    <div class="sec"><h2>${window.icon('info')} ব্যাজের নিয়মাবলি</h2></div>
    <div class="compat-list" style="margin-bottom:20px">
      ${[['platinum', 'প্লাটিনাম ডোনার', '৫০+ বার রক্তদান'], ['gold', 'গোল্ড ডোনার', '২০+ বার রক্তদান'], ['silver', 'সিলভার ডোনার', '১০+ বার রক্তদান'], ['bronze', 'ব্রোঞ্জ ডোনার', '৫+ বার রক্তদান'], ['friend', 'রক্তবন্ধু', 'অন্তত ১ বার রক্তদান'], ['new', 'নতুন ডোনার', 'এখনো রক্ত দেননি']].map(b => `
      <div class="compat-row"><span class="rank-badge ${b[0]}">${b[1]}</span><div class="cr-to grow" style="font-size:12.5px">${b[2]}</div></div>`).join('')}
    </div>
    <div class="gap24"></div>`;

    UI.bindAppbar(el);
    el.querySelectorAll('.rank-row[data-open]').forEach(r => r.addEventListener('click', () => window.openDonorSheet(r.dataset.open)));
  });

  /* ═══ HISTORY ══════════════════════════════════════════════ */
  Router.add('history', (el) => {
    const render = () => {
      const me = State.me();
      const myDons = State.donations().filter(d => d.userId === me.id);
      const cd = State.cooldown(me);
      const totalMl = myDons.reduce((s, d) => s + (d.amountMl || 450), 0);

      el.innerHTML = `
      ${UI.appbar({ title: 'রক্তদানের ইতিহাস', subtitle: 'আপনার সকল দানের রেকর্ড' })}
      ${myDons.length ? `
      <div class="pad" style="padding-bottom:6px">
        <div class="stock-total" style="background:var(--primary-container);color:var(--on-primary-container)">
          <b>${bnNum(myDons.length)}</b>
          <span>বার রক্তদান করেছেন<br><span style="font-weight:600;font-size:11.5px;opacity:.75">মোট প্রায় ${bnNum(Math.round(totalMl / 1000))} লিটার রক্ত — ${bnNum(myDons.length * 3)}+ প্রাণ স্পর্শ</span></span>
          ${window.icon('dropFill')}
        </div>
      </div>` : ''}
      ${myDons.length ? `<div class="tl">${myDons.map(d => `
        <div class="tl-item">
          <span class="tl-dot">${window.icon('dropFill')}</span>
          <div class="tl-main">
            <div class="row between" style="align-items:flex-start">
              <div class="grow"><div class="t-title">${esc(d.recipientName)}</div>
              <div class="t-meta">
                <span>${window.icon('calendar')} ${fmtDate(d.date)}</span>
                <span>${window.icon('hosp')} ${esc(d.hospital)}</span>
                <span class="tl-amount">${window.icon('drop')} ${bnNum(d.amountMl)} ml</span>
              </div></div>
            </div>
            ${d.location ? `<div class="t-meta" style="margin-top:5px"><span>${window.icon('location')} ${esc(d.location)}</span></div>` : ''}
            ${d.notes ? `<div class="t-note">${esc(d.notes)}</div>` : ''}
          </div>
        </div>`).join('')}
      </div>` : UI.empty('history', 'আপনি এখনো রক্ত দেননি', 'রক্ত দিতে পারেন ✅ — প্রথম দানের রেকর্ড যোগ করুন এবং রক্তবন্ধু ব্যাজ অর্জন করুন।')}
      <button class="fab" id="add-don">${window.icon('add')} রক্তদান যোগ করুন</button>
      <div class="gap24"></div>`;

      UI.bindAppbar(el);
      el.querySelector('#add-don').onclick = () => addDonation(render);
    };

    const addDonation = (rerender) => {
      const me = State.me();
      const cd = State.cooldown(me);
      UI.sheet({
        title: 'রক্তদানের রেকর্ড যোগ করুন',
        icon: 'dropFill',
        body: `
        ${!cd.eligible ? `<div class="card card-pad" style="background:var(--tertiary-container);color:var(--on-tertiary-container);border:0;margin-bottom:14px;font-size:12.5px;line-height:1.7"><b>⚠️ কুলডাউন চলছে:</b> শেষ দানের ${bnNum(cd.lastDiff)} দিন হয়েছে — স্বাস্থ্যগত কারণে ৯০ দিন অপেক্ষা করা উচিত। তবুও রেকর্ড করতে চাইলে সঠিক তারিখ দিন।</div>` : ''}
        ${UI.field({ id: 'dn_date', label: 'রক্তদানের তারিখ', type: 'date', required: true, value: new Date().toISOString().slice(0, 10), err: 'তারিখ দিন' })}
        ${UI.field({ id: 'dn_recipient', label: 'রিসিপিয়েন্টের নাম / রোগীর নাম', required: true, placeholder: 'যার জন্য রক্ত দিয়েছেন', err: 'রিসিপিয়েন্টের নাম দিন' })}
        ${UI.field({ id: 'dn_phone', label: 'রিসিপিয়েন্টের ফোন (ঐচ্ছিক)', inputmode: 'tel', maxlength: 11, placeholder: '01XXXXXXXXX' })}
        ${UI.field({ id: 'dn_hosp', label: 'হাসপাতালের নাম', required: true, placeholder: 'যেমন: নাটোর সদর হাসপাতাল', err: 'হাসপাতালের নাম দিন' })}
        ${UI.field({ id: 'dn_loc', label: 'লোকেশন (ঐচ্ছিক)', placeholder: 'যেমন: নাটোর সদর' })}
        ${UI.field({ id: 'dn_amount', label: 'রক্তের পরিমাণ (ml)', type: 'number', required: true, value: '450', placeholder: 'সাধারণত ৪৫০ ml', err: 'সঠিক পরিমাণ দিন' })}
        ${UI.field({ id: 'dn_notes', label: 'নোট (ঐচ্ছিক)', type: 'textarea', placeholder: 'কোনো বিশেষ তথ্য থাকলে লিখুন...' })}
        <button class="btn btn-grad btn-block" id="dn_save">${window.icon('check')} সংরক্ষণ করুন</button>
        <p style="font-size:11.5px;color:var(--outline);text-align:center;margin-top:10px">✅ সংরক্ষণের পর ৯০ দিনের কুলডাউন নতুন করে শুরু হবে</p>`,
        onMount(sheet, close) {
          sheet.querySelector('#dn_save').onclick = () => {
            if (!UI.validate(sheet)) return;
            const amount = Math.max(100, Math.min(1000, parseInt(sheet.querySelector('#dn_amount').value) || 450));
            State.addDonation({
              date: new Date(sheet.querySelector('#dn_date').value).getTime(),
              recipientName: sheet.querySelector('#dn_recipient').value.trim(),
              phone: sheet.querySelector('#dn_phone').value.trim(),
              hospital: sheet.querySelector('#dn_hosp').value.trim(),
              location: sheet.querySelector('#dn_loc').value.trim(),
              amountMl: amount,
              notes: sheet.querySelector('#dn_notes').value.trim()
            });
            const me = State.me();
            State.push({ id: uid('n'), userId: me.id, type: 'don', title: 'রক্তদান সফলভাবে সংরক্ষিত!', body: `মোট দান: ${bnNum(me.donationCount)} বার • পরবর্তী দানের সময়: ৯০ দিন পর। আপনার ব্যাজ: ${State.badge(me)[1]}।`, date: Date.now(), read: false });
            State.save();
            close();
            UI.toast('রক্তদান সফলভাবে সংরক্ষিত হয়েছে! 🎉', 'ok');
            rerender();
          };
        }
      });
    };
    render();
  });

})();
