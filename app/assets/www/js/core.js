/* ═══════════════════════════════════════════════════════════
   হালাল রক্ত দান v2.0 — Core: utils, state, data, sync
   ═══════════════════════════════════════════════════════════ */
(function () {
  'use strict';

  const APP = {
    name: 'হালাল রক্ত দান',
    nameEn: 'Halal Rokto Dan',
    version: '2.0.0',
    org: 'হিজলি দিঘাপাড়া যুব সংঘ',
    orgEn: 'Hizly Dighapara JUBO Sangho',
    since: 'Since ২০২৬',
    address: 'বাগাতিপাড়া, নাটোর',
    district: 'নাটোর জেলা, রাজশাহী বিভাগ',
    cooldownDays: 90,
    targetStock: 8
  };
  window.APP = APP;

  /* ── Bengali helpers ─────────────────────────────────────── */
  const BN_DIGITS = ['০','১','২','৩','৪','৫','৬','৭','৮','৯'];
  const BN_MONTHS = ['জানুয়ারি','ফেব্রুয়ারি','মার্চ','এপ্রিল','মে','জুন','জুলাই','আগস্ট','সেপ্টেম্বর','অক্টোবর','নভেম্বর','ডিসেম্বর'];
  const BN_DAYS = ['রবিবার','সোমবার','মঙ্গলবার','বুধবার','বৃহস্পতিবার','শুক্রবার','শনিবার'];

  function toBn(n) {
    return String(n).replace(/[0-9]/g, d => BN_DIGITS[+d]);
  }
  function bnNum(n, pad) {
    let s = String(Math.abs(n));
    if (pad && s.length < pad) s = '0'.repeat(pad - s.length) + s;
    return (n < 0 ? '-' : '') + toBn(s);
  }
  function fmtDate(ts) {
    const d = new Date(ts);
    return `${bnNum(d.getDate())} ${BN_MONTHS[d.getMonth()]} ${bnNum(d.getFullYear())}`;
  }
  function fmtDateShort(ts) {
    const d = new Date(ts);
    return `${bnNum(d.getDate())}/${bnNum(d.getMonth() + 1)}/${bnNum(d.getFullYear())}`;
  }
  function dayName(ts) { return BN_DAYS[new Date(ts).getDay()]; }
  function timeAgo(ts) {
    const s = Math.floor((Date.now() - ts) / 1000);
    if (s < 60) return 'এখনই';
    if (s < 3600) return `${bnNum(Math.floor(s / 60))} মিনিট আগে`;
    if (s < 86400) return `${bnNum(Math.floor(s / 3600))} ঘণ্টা আগে`;
    const d = Math.floor(s / 86400);
    if (d < 7) return `${bnNum(d)} দিন আগে`;
    if (d < 30) return `${bnNum(Math.floor(d / 7))} সপ্তাহ আগে`;
    return fmtDateShort(ts);
  }

  /* ── Misc utils ──────────────────────────────────────────── */
  const uid = (p) => (p || 'id') + '_' + Date.now().toString(36) + Math.random().toString(36).slice(2, 8);
  const esc = (s) => String(s == null ? '' : s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  const initials = (name) => String(name || '?').trim().charAt(0).toUpperCase();
  const AVATAR_COLORS = ['#775652', '#5D4F4D', '#8E5A54', '#6B5E56', '#93625B', '#5E6B52', '#52666B', '#6B5266'];
  function avatarColor(id) {
    let h = 0;
    for (const c of String(id)) h = (h * 31 + c.charCodeAt(0)) % 997;
    return AVATAR_COLORS[h % AVATAR_COLORS.length];
  }
  function hashCode(s) {
    let h = 0;
    for (let i = 0; i < s.length; i++) { h = (h << 5) - h + s.charCodeAt(i); h |= 0; }
    return h;
  }
  function maskPhone(p) {
    const s = String(p || '');
    return s.length >= 4 ? '••••••' + s.slice(-4) : s;
  }

  const BLOOD_GROUPS = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];
  const COMPAT = {
    'A+': ['A+', 'A-', 'O+', 'O-'], 'A-': ['A-', 'O-'],
    'B+': ['B+', 'B-', 'O+', 'O-'], 'B-': ['B-', 'O-'],
    'AB+': BLOOD_GROUPS, 'AB-': ['AB-', 'A-', 'B-', 'O-'],
    'O+': ['O+', 'O-'], 'O-': ['O-']
  };
  const GROUP_RARITY = { 'AB-': 'বিরল', 'B-': 'বিরল', 'A-': 'কম', 'AB+': 'অস্বাভাবিক', 'O-': 'সর্বজনীন দাতা', 'O+': 'সর্বাধিক চাহিদা' };

  /* ── Storage (native bridge first, localStorage fallback) ── */
  const BRIDGE = window.Android || null;
  const STORE_KEY = 'halal_rokto_dan_v2';

  const Store = {
    read() {
      try {
        if (BRIDGE && typeof BRIDGE.getPref === 'function') {
          const raw = BRIDGE.getPref(STORE_KEY);
          if (raw) return JSON.parse(raw);
        }
      } catch (e) { /* fall through */ }
      try {
        const raw = localStorage.getItem(STORE_KEY);
        if (raw) return JSON.parse(raw);
      } catch (e) {}
      return null;
    },
    write(state) {
      const raw = JSON.stringify(state);
      try {
        if (BRIDGE && typeof BRIDGE.setPref === 'function') BRIDGE.setPref(STORE_KEY, raw);
      } catch (e) {}
      try { localStorage.setItem(STORE_KEY, raw); } catch (e) {}
    },
    wipe() {
      try {
        if (BRIDGE && typeof BRIDGE.setPref === 'function') BRIDGE.setPref(STORE_KEY, '');
      } catch (e) {}
      try { localStorage.removeItem(STORE_KEY); } catch (e) {}
    }
  };

  /* ── Demo seed data ──────────────────────────────────────── */
  function makeSeed() {
    const now = Date.now();
    const D = 86400000;
    const demoUsers = [
      ['মোঃ রহিম মিয়া', 'rahim@example.com', '01712345678', 'O+', 'হিজলি, বাগাতিপাড়া', 12, now - 32 * D, true],
      ['আব্দুল করিম', 'karim@example.com', '01812345679', 'B+', 'দিঘাপাড়া, বাগাতিপাড়া', 9, now - 105 * D, true],
      ['মোসাঃ সুমাইয়া আক্তার', 'sumaiya@example.com', '01912345680', 'A+', 'বাগাতিপাড়া বাজার', 7, now - 20 * D, true],
      ['হাফেজ মাহমুদুল হাসান', 'mahmud@example.com', '01612345681', 'AB+', 'উল্লাপাড়া রোড, বাগাতিপাড়া', 15, now - 60 * D, true],
      ['মোঃ সোহাগ মিয়া', 'sohag@example.com', '01512345682', 'O-', 'দিঘাপাড়া, বাগাতিপাড়া', 5, now - 15 * D, true],
      ['নুসরাত জাহান', 'nusrat@example.com', '01712345683', 'B-', 'বাগাতিপাড়া, নাটোর', 3, now - 88 * D, false],
      ['মোঃ জসিম উদ্দিন', 'jasim@example.com', '01812345684', 'A-', 'হিজলি, বাগাতিপাড়া', 6, now - 45 * D, true],
      ['ইমরান হোসেন', 'imran@example.com', '01912345685', 'O+', 'পাকশি রোড, বাগাতিপাড়া', 2, now - 70 * D, false],
      ['মোসাঃ রিনা বেগম', 'rina@example.com', '01612345686', 'AB-', 'দিঘাপাড়া, বাগাতিপাড়া', 4, now - 51 * D, true],
      ['আল-আমিন শেখ', 'alamin@example.com', '01512345687', 'B+', 'হিজলি বাজার', 8, now - 26 * D, true]
    ].map((u, i) => ({
      id: 'demo_' + (i + 1),
      name: u[0], email: u[1], phone: u[2], bloodType: u[3], address: u[4],
      nid: '', password: null,
      donationCount: u[5], trustScore: Math.min(100, 40 + u[5] * 4),
      referralCode: 'DJS-' + (1000 + i * 137),
      referredBy: '', referralCount: Math.max(0, Math.floor((i - 1) / 3)),
      verified: u[7], lastDonationDate: u[6], createdAt: now - (300 - i * 12) * D,
      isDemo: true
    }));

    const seedNotifs = [
      { id: uid('n'), userId: 'all', type: 'sys', title: 'স্বাগতম!', body: `${APP.org}-এর হালাল রক্ত দান অ্যাপে আপনাকে স্বাগতম। রেজিস্ট্রেশন করে ডোনার হিসেবে যোগ দিন।`, date: now - 2 * D, read: false },
      { id: uid('n'), userId: 'all', type: 'don', title: 'রক্তদান ক্যাম্পের ঘোষণা', body: 'আগামী শুক্রবার বাগাতিপাড়া ইউনিয়ন পরিষদ মাঠে স্বেচ্ছায় রক্তদান ক্যাম্প অনুষ্ঠিত হবে, সকাল ৯টা থেকে দুপুর ২টা পর্যন্ত।', date: now - 1 * D, read: false }
    ];

    const seedEmergencies = [
      { id: uid('em'), patientName: 'জনাব আব্দুল জলিল (৬২)', bloodGroup: 'B+', units: 2, hospital: 'নাটোর সদর হাসপাতাল', location: 'নাটোর সদর', contact: '01711111111', notes: 'অপারেশনের জন্য জরুরি B+ রক্ত প্রয়োজন। আগের দাতা সুস্থ হয়ে গেছেন, নতুন দাতা প্রয়োজন।', createdAt: now - 5 * 3600000, createdBy: 'demo_2', fulfilled: false },
      { id: uid('em'), patientName: 'শিশু তানজিলা (৮)', bloodGroup: 'O-', units: 1, hospital: 'রাজশাহী মেডিকেল কলেজ হাসপাতাল', location: 'রাজশাহী', contact: '01822222222', notes: 'থ্যালাসেমিয়া রোগী, মাসিক ট্রান্সফিউশনের জন্য O- রক্ত দরকার।', createdAt: now - 26 * 3600000, createdBy: 'demo_1', fulfilled: false }
    ];

    return {
      version: 2,
      session: null,
      onboardingDone: false,
      users: demoUsers,
      donations: [],
      emergencies: seedEmergencies,
      notifications: seedNotifs,
      stock: { 'A+': 6, 'A-': 3, 'B+': 5, 'B-': 2, 'AB+': 4, 'AB-': 1, 'O+': 7, 'O-': 2 },
      settings: { theme: 'light', serverUrl: '', demoData: true },
      sync: { last: null, status: 'off' }
    };
  }

  /* ── App State ───────────────────────────────────────────── */
  const State = {
    data: null,
    load() {
      this.data = Store.read();
      if (!this.data || this.data.version !== 2) {
        this.data = makeSeed();
        Store.write(this.data);
      }
      return this.data;
    },
    save() { Store.write(this.data); },
    me() { return this.data.session ? this.data.users.find(u => u.id === this.data.session) : null; },
    users() { return this.data.users.filter(u => this.data.settings.demoData || !u.isDemo); },
    donations() {
      const demo = this.data.settings.demoData;
      return this.data.donations.filter(d => {
        if (demo) return true;
        const u = this.data.users.find(x => x.id === d.userId);
        return u && !u.isDemo;
      });
    },
    emergencies() { return this.data.emergencies; },
    notifs() {
      return this.data.notifications.filter(n => n.userId === 'all' || n.userId === this.data.session);
    },
    unreadCount() { return this.notifs().filter(n => !n.read).length; },
    push(notif) { this.data.notifications.unshift(notif); this.save(); },
    stockFor(group) { return this.data.stock[group] || 0; },
    stockTotal() { return BLOOD_GROUPS.reduce((s, g) => s + (this.data.stock[g] || 0), 0); },

    /* cooldown: days remaining before user can donate again */
    cooldown(u) {
      if (!u) return { days: 0, eligible: true, first: true };
      if (!u.lastDonationDate) return { days: 0, eligible: true, first: true };
      const last = new Date(u.lastDonationDate);
      const diff = Math.floor((Date.now() - last.getTime()) / 86400000);
      if (diff >= APP.cooldownDays) return { days: 0, eligible: true, first: false, lastDiff: diff };
      return { days: APP.cooldownDays - diff, eligible: false, first: false, lastDiff: diff, elapsed: diff };
    },
    addDonation(rec) {
      const me = this.me();
      rec.id = uid('don'); rec.userId = me.id; rec.createdAt = Date.now();
      this.data.donations.unshift(rec);
      me.donationCount = (me.donationCount || 0) + 1;
      me.lastDonationDate = new Date(rec.date).getTime();
      me.trustScore = Math.min(100, (me.trustScore || 40) + 4);
      this.data.stock[me.bloodType] = (this.data.stock[me.bloodType] || 0) + Math.max(1, Math.round((rec.amountMl || 450) / 450));
      this.save();
      return rec;
    },
    ranking() {
      return [...this.users()].sort((a, b) => (b.donationCount || 0) - (a.donationCount || 0) || (b.trustScore || 0) - (a.trustScore || 0));
    },
    badge(u) {
      const c = u.donationCount || 0;
      if (c >= 50) return ['platinum', 'প্লাটিনাম ডোনার'];
      if (c >= 20) return ['gold', 'গোল্ড ডোনার'];
      if (c >= 10) return ['silver', 'সিলভার ডোনার'];
      if (c >= 5) return ['bronze', 'ব্রোঞ্জ ডোনার'];
      if (c >= 1) return ['friend', 'রক্তবন্ধু'];
      return ['new', 'নতুন ডোনার'];
    }
  };

  /* ── Auth ────────────────────────────────────────────────── */
  const Auth = {
    register(f) {
      const d = State.data;
      if (d.users.find(u => u.email.toLowerCase() === f.email.toLowerCase()))
        return { ok: false, msg: 'এই ইমেইল দিয়ে ইতিমধ্যে একটি অ্যাকাউন্ট আছে। লগইন করুন।' };
      if (d.users.find(u => u.phone === f.phone))
        return { ok: false, msg: 'এই ফোন নম্বর ইতিমধ্যে নিবন্ধিত।' };
      let referredByUser = null;
      if (f.referredBy) {
        referredByUser = d.users.find(u => u.referralCode === f.referredBy.trim().toUpperCase());
        if (!referredByUser) return { ok: false, msg: 'রেফারেল কোডটি সঠিক নয়। খালি রাখুন বা সঠিক কোড দিন।' };
      }
      const code = 'DJS-' + String(100000 + Math.abs(hashCode(f.phone + f.name)) % 900000).slice(0, 5);
      const user = {
        id: uid('u'), name: f.name, email: f.email, phone: f.phone,
        bloodType: f.bloodType, address: f.address, nid: f.nid || '',
        password: String(hashCode(f.email.toLowerCase() + ':' + f.password)),
        donationCount: 0, trustScore: 40,
        referralCode: code, referredBy: referredByUser ? referredByUser.referralCode : '',
        referralCount: 0, verified: false, lastDonationDate: null,
        createdAt: Date.now(), isDemo: false
      };
      d.users.push(user);
      if (referredByUser) {
        referredByUser.referralCount = (referredByUser.referralCount || 0) + 1;
        State.push({ id: uid('n'), userId: referredByUser.id, type: 'rank', title: 'নতুন রেফারেল!', body: `${f.name} আপনার রেফারেল কোড ব্যবহার করে রেজিস্টার করেছেন। আপনার মোট রেফারেল: ${bnNum(referredByUser.referralCount)} জন।`, date: Date.now(), read: false });
      }
      d.session = user.id;
      State.push({ id: uid('n'), userId: user.id, type: 'sys', title: 'স্বাগতম, ' + f.name + '!', body: `${APP.org}-এ আপনার রেজিস্ট্রেশন সফল হয়েছে। আপনার রেফারেল কোড: ${code} — বন্ধুদের আমন্ত্রণ জানান।`, date: Date.now(), read: false });
      State.save();
      Sync.register(user, f.password);
      return { ok: true, user };
    },
    login(email, password) {
      const u = State.data.users.find(x => x.email.toLowerCase() === email.toLowerCase() && x.password != null);
      if (!u) return { ok: false, msg: 'এই ইমেইল দিয়ে কোনো অ্যাকাউন্ট পাওয়া যায়নি।' };
      if (u.password !== String(hashCode(u.email.toLowerCase() + ':' + password)))
        return { ok: false, msg: 'পাসওয়ার্ড সঠিক নয়। আবার চেষ্টা করুন।' };
      State.data.session = u.id;
      State.save();
      Sync.login(email, password);
      return { ok: true, user: u };
    },
    logout() { State.data.session = null; State.save(); }
  };

  /* ── Sync service (optional REST backend) ────────────────── */
  const Sync = {
    _cbs: {}, _n: 0,
    enabled() { return !!(State.data.settings.serverUrl || '').trim(); },
    base() { return String(State.data.settings.serverUrl || '').trim().replace(/\/+$/, ''); },
    http(path, method, body) {
      const base = this.base();
      if (!base) return Promise.reject(new Error('sync-off'));
      return new Promise((resolve, reject) => {
        if (BRIDGE && typeof BRIDGE.http === 'function') {
          const id = 'cb' + (++this._n);
          this._cbs[id] = { resolve, reject };
          try { BRIDGE.http(method, base + path, JSON.stringify(body || {}), id); }
          catch (e) { delete this._cbs[id]; reject(e); }
          setTimeout(() => { if (this._cbs[id]) { delete this._cbs[id]; reject(new Error('timeout')); } }, 12000);
        } else {
          fetch(base + path, { method, headers: { 'Content-Type': 'application/json' }, body: method === 'GET' ? undefined : JSON.stringify(body || {}) })
            .then(r => r.json()).then(resolve).catch(reject);
        }
      });
    },
    _done(id, status, text) {  // called from native
      const cb = this._cbs[id];
      if (!cb) return;
      delete this._cbs[id];
      if (status >= 200 && status < 300) { try { cb.resolve(JSON.parse(text || '{}')); } catch (e) { cb.resolve({}); } }
      else cb.reject(new Error('http-' + status));
    },
    _register(user, password) {
      if (!this.enabled()) return;
      this.http('/api/auth/register', 'POST', { name: user.name, email: user.email, phone: user.phone, password, bloodType: user.bloodType, address: user.address, nid: user.nid, referralCode: user.referredBy || '' })
        .then(r => { State.data.sync = { last: Date.now(), status: 'ok' }; State.save(); })
        .catch(() => { State.data.sync = { last: Date.now(), status: 'err' }; State.save(); });
    },
    register: null,
    _login(email, password) {
      if (!this.enabled()) return;
      this.http('/api/auth/login', 'POST', { email, password })
        .then(r => { State.data.sync = { last: Date.now(), status: 'ok' }; State.save(); })
        .catch(() => {});
    },
    login: null,
    pushEmergency(em) {
      if (!this.enabled()) return;
      this.http('/api/emergency-requests', 'POST', { patientName: em.patientName, bloodGroup: em.bloodGroup, units: em.units, hospital: em.hospital, location: em.location, contact: em.contact, notes: em.notes })
        .then(r => { State.data.sync = { last: Date.now(), status: 'ok' }; State.save(); })
        .catch(() => {});
    }
  };
  Sync.register = Sync._register;
  Sync.login = Sync._login;
  window.__syncDone = (id, status, text) => Sync._done(id, status, text);

  /* ── Haptics & platform bridge helpers ───────────────────── */
  function haptic(ms) {
    try { if (BRIDGE && BRIDGE.vibrate) BRIDGE.vibrate(ms || 12); else if (navigator.vibrate) navigator.vibrate(ms || 12); } catch (e) {}
  }
  function nativeToast(msg) {
    try { if (BRIDGE && BRIDGE.toast) { BRIDGE.toast(msg); return true; } } catch (e) {}
    return false;
  }
  function shareText(text, title) {
    try {
      if (BRIDGE && BRIDGE.share) { BRIDGE.share(text, title || APP.name); return; }
      if (navigator.share) { navigator.share({ title: title || APP.name, text }).catch(() => {}); return; }
    } catch (e) {}
    try {
      navigator.clipboard.writeText(text);
      UI.toast('কপি হয়েছে — পেস্ট করে শেয়ার করুন', 'ok');
    } catch (e) {}
  }

  /* ── Export ──────────────────────────────────────────────── */
  window.Core = {
    APP, toBn, bnNum, fmtDate, fmtDateShort, dayName, timeAgo,
    uid, esc, initials, avatarColor, maskPhone, hashCode,
    BLOOD_GROUPS, COMPAT, GROUP_RARITY,
    State, Auth, Sync, Store, makeSeed,
    haptic, nativeToast, shareText, BRIDGE
  };
})();
