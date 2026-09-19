package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** ডেটা লেয়ার: মডেল, স্টোর (SharedPreferences+JSON), অথ, বিজনেস লজিক, সিঙ্ক। */
public final class Data {

    public static final String APP_NAME = "হালাল রক্ত দান";
    public static final String APP_NAME_EN = "Halal Rokto Dan";
    public static final String VERSION = "3.1.0";
    public static final String ORG = "হিজলি দিঘাপাড়া যুব সংঘ";
    public static final String ORG_EN = "Hizly Dighapara JUBO Sangho";
    public static final String SINCE = "Since ২০২৬";
    public static final String ADDRESS = "বাগাতিপাড়া, নাটোর";
    public static final String DISTRICT = "নাটোর জেলা, রাজশাহী বিভাগ";
    public static final int COOLDOWN_DAYS = 90;
    public static final int TARGET_STOCK = 8;

    public static final String[] BLOOD_GROUPS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    public static final String[][] COMPAT = {
            {"A+", "A+ A- O+ O-"}, {"A-", "A- O-"}, {"B+", "B+ B- O+ O-"}, {"B-", "B- O-"},
            {"AB+", "A+ A- B+ B- AB+ AB- O+ O-"}, {"AB-", "AB- A- B- O-"}, {"O+", "O+ O-"}, {"O-", "O-"}};
    public static String compatOf(String g) {
        for (String[] c : COMPAT) if (c[0].equals(g)) return c[1];
        return g;
    }
    public static String rarityOf(String g) {
        switch (g) {
            case "AB-": return "বিরল";
            case "B-": return "বিরল";
            case "A-": return "কম";
            case "AB+": return "অস্বাভাবিক";
            case "O-": return "সর্বজনীন দাতা";
            case "O+": return "সর্বাধিক চাহিদা";
            default: return "সাধারণ গ্রুপ";
        }
    }

    /* ══ মডেল ═════════════════════════════════════════════════ */
    public static class User {
        public String id, name, email, phone, bloodType, address, nid;
        public String password; // hash or null
        public int donationCount, trustScore, referralCount;
        public String referralCode, referredBy;
        public boolean verified;
        public long lastDonationDate; // 0 = never
        public long createdAt;
        public boolean isDemo;
        public String addedBy = "";

        JSONObject toJson() throws Exception {
            JSONObject o = new JSONObject();
            o.put("id", id).put("name", name).put("email", email).put("phone", phone)
                    .put("bloodType", bloodType).put("address", address).put("nid", nid)
                    .put("password", password == null ? "" : password)
                    .put("donationCount", donationCount).put("trustScore", trustScore)
                    .put("referralCount", referralCount).put("referralCode", referralCode)
                    .put("referredBy", referredBy).put("verified", verified)
                    .put("lastDonationDate", lastDonationDate).put("createdAt", createdAt)
                    .put("isDemo", isDemo).put("addedBy", addedBy);
            return o;
        }
        static User fromJson(JSONObject o) throws Exception {
            User u = new User();
            u.id = o.getString("id"); u.name = o.getString("name");
            u.email = o.optString("email", ""); u.phone = o.optString("phone", "");
            u.bloodType = o.optString("bloodType", "O+"); u.address = o.optString("address", "");
            u.nid = o.optString("nid", "");
            String pw = o.optString("password", "");
            u.password = pw.isEmpty() ? null : pw;
            u.donationCount = o.optInt("donationCount", 0);
            u.trustScore = o.optInt("trustScore", 40);
            u.referralCount = o.optInt("referralCount", 0);
            u.referralCode = o.optString("referralCode", "");
            u.referredBy = o.optString("referredBy", "");
            u.verified = o.optBoolean("verified", false);
            u.lastDonationDate = o.optLong("lastDonationDate", 0);
            u.createdAt = o.optLong("createdAt", System.currentTimeMillis());
            u.isDemo = o.optBoolean("isDemo", false);
            u.addedBy = o.optString("addedBy", "");
            return u;
        }
    }

    public static class Emergency {
        public String id, patientName, bloodGroup, hospital, location, contact, notes;
        public int units;
        public long createdAt;
        public String createdBy;
        public boolean fulfilled;

        JSONObject toJson() throws Exception {
            return new JSONObject()
                    .put("id", id).put("patientName", patientName).put("bloodGroup", bloodGroup)
                    .put("units", units).put("hospital", hospital).put("location", location)
                    .put("contact", contact).put("notes", notes).put("createdAt", createdAt)
                    .put("createdBy", createdBy).put("fulfilled", fulfilled);
        }
        static Emergency fromJson(JSONObject o) throws Exception {
            Emergency e = new Emergency();
            e.id = o.getString("id"); e.patientName = o.getString("patientName");
            e.bloodGroup = o.optString("bloodGroup", "O+"); e.units = o.optInt("units", 1);
            e.hospital = o.optString("hospital", ""); e.location = o.optString("location", "");
            e.contact = o.optString("contact", ""); e.notes = o.optString("notes", "");
            e.createdAt = o.optLong("createdAt", System.currentTimeMillis());
            e.createdBy = o.optString("createdBy", ""); e.fulfilled = o.optBoolean("fulfilled", false);
            return e;
        }
    }

    public static class Donation {
        public String id, userId, recipientName, phone, hospital, location, notes;
        public long date, createdAt;
        public int amountMl;

        JSONObject toJson() throws Exception {
            return new JSONObject()
                    .put("id", id).put("userId", userId).put("date", date)
                    .put("recipientName", recipientName).put("phone", phone)
                    .put("hospital", hospital).put("location", location)
                    .put("amountMl", amountMl).put("notes", notes).put("createdAt", createdAt);
        }
        static Donation fromJson(JSONObject o) throws Exception {
            Donation d = new Donation();
            d.id = o.getString("id"); d.userId = o.optString("userId", "");
            d.date = o.optLong("date", System.currentTimeMillis());
            d.recipientName = o.optString("recipientName", ""); d.phone = o.optString("phone", "");
            d.hospital = o.optString("hospital", ""); d.location = o.optString("location", "");
            d.amountMl = o.optInt("amountMl", 450); d.notes = o.optString("notes", "");
            d.createdAt = o.optLong("createdAt", System.currentTimeMillis());
            return d;
        }
    }

    public static class Notif {
        public String id, userId, type, title, body;
        public long date;
        public boolean read;

        JSONObject toJson() throws Exception {
            return new JSONObject()
                    .put("id", id).put("userId", userId).put("type", type)
                    .put("title", title).put("body", body).put("date", date).put("read", read);
        }
        static Notif fromJson(JSONObject o) throws Exception {
            Notif n = new Notif();
            n.id = o.getString("id"); n.userId = o.optString("userId", "all");
            n.type = o.optString("type", "sys"); n.title = o.optString("title", "");
            n.body = o.optString("body", ""); n.date = o.optLong("date", System.currentTimeMillis());
            n.read = o.optBoolean("read", false);
            return n;
        }
    }

    /* ══ State ════════════════════════════════════════════════ */
    public static final class S {
        public String session = null; // user id
        public boolean onboardingDone = false;
        public List<User> users = new ArrayList<>();
        public List<Donation> donations = new ArrayList<>();
        public List<Emergency> emergencies = new ArrayList<>();
        public List<Notif> notifications = new ArrayList<>();
        public int[] stock = new int[8]; // index of BLOOD_GROUPS
        public boolean themeDark = false, demoData = true;
        public boolean notifOn = true;
        public String serverUrl = "";
        public long syncLast = 0;
        public String syncStatus = "off";
    }

    public static S s = new S();
    private static SharedPreferences prefs;

    public static void init(Context c) {
        prefs = c.getSharedPreferences("halal_rokto_dan_v3", Context.MODE_PRIVATE);
        load();
    }

    public static int stockIdx(String g) {
        for (int i = 0; i < BLOOD_GROUPS.length; i++) if (BLOOD_GROUPS[i].equals(g)) return i;
        return 6;
    }

    public static void load() {
        String raw = prefs.getString("state", null);
        if (raw != null) {
            try {
                JSONObject o = new JSONObject(raw);
                s.session = o.optString("session", null);
                if ("".equals(s.session)) s.session = null;
                s.onboardingDone = o.optBoolean("onboardingDone", false);
                s.users.clear();
                JSONArray a = o.optJSONArray("users");
                if (a != null) for (int i = 0; i < a.length(); i++) s.users.add(User.fromJson(a.getJSONObject(i)));
                s.donations.clear();
                a = o.optJSONArray("donations");
                if (a != null) for (int i = 0; i < a.length(); i++) s.donations.add(Donation.fromJson(a.getJSONObject(i)));
                s.emergencies.clear();
                a = o.optJSONArray("emergencies");
                if (a != null) for (int i = 0; i < a.length(); i++) s.emergencies.add(Emergency.fromJson(a.getJSONObject(i)));
                s.notifications.clear();
                a = o.optJSONArray("notifications");
                if (a != null) for (int i = 0; i < a.length(); i++) s.notifications.add(Notif.fromJson(a.getJSONObject(i)));
                a = o.optJSONArray("stock");
                if (a != null) for (int i = 0; i < 8 && i < a.length(); i++) s.stock[i] = a.optInt(i, 0);
                JSONObject st = o.optJSONObject("settings");
                if (st != null) {
                    s.themeDark = st.optBoolean("themeDark", false);
                    s.demoData = st.optBoolean("demoData", true);
                    s.notifOn = st.optBoolean("notifOn", true);
                    s.serverUrl = st.optString("serverUrl", "");
                }
                JSONObject sy = o.optJSONObject("sync");
                if (sy != null) { s.syncLast = sy.optLong("last", 0); s.syncStatus = sy.optString("status", "off"); }
                return;
            } catch (Exception ignored) { }
        }
        seed();
        save();
    }

    public static void save() {
        try {
            JSONObject o = new JSONObject();
            o.put("session", s.session == null ? "" : s.session);
            o.put("onboardingDone", s.onboardingDone);
            JSONArray a = new JSONArray(); for (User u : s.users) a.put(u.toJson()); o.put("users", a);
            a = new JSONArray(); for (Donation d : s.donations) a.put(d.toJson()); o.put("donations", a);
            a = new JSONArray(); for (Emergency e : s.emergencies) a.put(e.toJson()); o.put("emergencies", a);
            a = new JSONArray(); for (Notif n : s.notifications) a.put(n.toJson()); o.put("notifications", a);
            a = new JSONArray(); for (int i : s.stock) a.put(i); o.put("stock", a);
            o.put("settings", new JSONObject()
                    .put("themeDark", s.themeDark).put("demoData", s.demoData)
                    .put("notifOn", s.notifOn).put("serverUrl", s.serverUrl));
            o.put("sync", new JSONObject().put("last", s.syncLast).put("status", s.syncStatus));
            prefs.edit().putString("state", o.toString()).apply();
        } catch (Exception ignored) { }
    }

    public static void wipe() {
        prefs.edit().remove("state").apply();
        s = new S();
        seed();
        save();
    }

    /* ══ Seed (ডেমো ডেটা) ═════════════════════════════════════ */
    private static void seed() {
        long now = System.currentTimeMillis();
        final long D = 86400000L;
        Object[][] demo = {
                {"মোঃ রহিম মিয়া", "rahim@example.com", "01712345678", "O+", "হিজলি, বাগাতিপাড়া", 12, now - 32L * D, true},
                {"আব্দুল করিম", "karim@example.com", "01812345679", "B+", "দিঘাপাড়া, বাগাতিপাড়া", 9, now - 105L * D, true},
                {"মোসাঃ সুমাইয়া আক্তার", "sumaiya@example.com", "01912345680", "A+", "বাগাতিপাড়া বাজার", 7, now - 95L * D, true},
                {"হাফেজ মাহমুদুল হাসান", "mahmud@example.com", "01612345681", "AB+", "উল্লাপাড়া রোড, বাগাতিপাড়া", 15, now - 160L * D, true},
                {"মোঃ সোহাগ মিয়া", "sohag@example.com", "01512345682", "O-", "দিঘাপাড়া, বাগাতিপাড়া", 5, now - 100L * D, true},
                {"নুসরাত জাহান", "nusrat@example.com", "01712345683", "B-", "বাগাতিপাড়া, নাটোর", 3, now - 88L * D, false},
                {"মোঃ জসিম উদ্দিন", "jasim@example.com", "01812345684", "A-", "হিজলি, বাগাতিপাড়া", 6, now - 120L * D, true},
                {"ইমরান হোসেন", "imran@example.com", "01912345685", "O+", "পাকশি রোড, বাগাতিপাড়া", 2, now - 170L * D, false},
                {"মোসাঃ রিনা বেগম", "rina@example.com", "01612345686", "AB-", "দিঘাপাড়া, বাগাতিপাড়া", 4, now - 51L * D, true},
                {"আল-আমিন শেখ", "alamin@example.com", "01512345687", "B+", "হিজলি বাজার", 8, now - 26L * D, true},
        };
        for (int i = 0; i < demo.length; i++) {
            Object[] r = demo[i];
            User u = new User();
            u.id = "demo_" + (i + 1);
            u.name = (String) r[0]; u.email = (String) r[1]; u.phone = (String) r[2];
            u.bloodType = (String) r[3]; u.address = (String) r[4];
            u.donationCount = (Integer) r[5];
            u.trustScore = Math.min(100, 40 + u.donationCount * 4);
            u.referralCode = "DJS-" + (1000 + i * 137);
            u.referredBy = ""; u.referralCount = Math.max(0, (i - 1) / 3);
            u.verified = (Boolean) r[7];
            u.lastDonationDate = (Long) r[6];
            u.createdAt = now - (300L - i * 12L) * D;
            u.isDemo = true;
            u.password = null;
            s.users.add(u);
        }

        Notif n1 = new Notif();
        n1.id = Bn.uid("n"); n1.userId = "all"; n1.type = "sys"; n1.read = false;
        n1.title = "স্বাগতম!";
        n1.body = ORG + "-এর হালাল রক্ত দান অ্যাপে আপনাকে স্বাগতম। রেজিস্ট্রেশন করে ডোনার হিসেবে যোগ দিন।";
        n1.date = now - 2L * D;
        s.notifications.add(n1);
        Notif n2 = new Notif();
        n2.id = Bn.uid("n"); n2.userId = "all"; n2.type = "don"; n2.read = false;
        n2.title = "রক্তদান ক্যাম্পের ঘোষণা";
        n2.body = "আগামী শুক্রবার বাগাতিপাড়া ইউনিয়ন পরিষদ মাঠে স্বেচ্ছায় রক্তদান ক্যাম্প অনুষ্ঠিত হবে, সকাল ৯টা থেকে দুপুর ২টা পর্যন্ত।";
        n2.date = now - 1L * D;
        s.notifications.add(n2);

        Emergency e1 = new Emergency();
        e1.id = Bn.uid("em"); e1.patientName = "জনাব আব্দুল জলিল (৬২)"; e1.bloodGroup = "B+";
        e1.units = 2; e1.hospital = "নাটোর সদর হাসপাতাল"; e1.location = "নাটোর সদর";
        e1.contact = "01711111111";
        e1.notes = "অপারেশনের জন্য জরুরি B+ রক্ত প্রয়োজন। আগের দাতা সুস্থ হয়ে গেছেন, নতুন দাতা প্রয়োজন।";
        e1.createdAt = now - 5L * 3600000L; e1.createdBy = "demo_2"; e1.fulfilled = false;
        s.emergencies.add(e1);
        Emergency e2 = new Emergency();
        e2.id = Bn.uid("em"); e2.patientName = "শিশু তানজিলা (৮)"; e2.bloodGroup = "O-";
        e2.units = 1; e2.hospital = "রাজশাহী মেডিকেল কলেজ হাসপাতাল"; e2.location = "রাজশাহী";
        e2.contact = "01822222222";
        e2.notes = "থ্যালাসেমিয়া রোগী, মাসিক ট্রান্সফিউশনের জন্য O- রক্ত দরকার।";
        e2.createdAt = now - 26L * 3600000L; e2.createdBy = "demo_1"; e2.fulfilled = false;
        s.emergencies.add(e2);

        s.stock = new int[]{6, 3, 5, 2, 4, 1, 7, 2};
    }

    /* ══ Queries ══════════════════════════════════════════════ */
    public static User me() {
        if (s.session == null) return null;
        for (User u : s.users) if (u.id.equals(s.session)) return u;
        return null;
    }

    public static User userById(String id) {
        for (User u : s.users) if (u.id.equals(id)) return u;
        return null;
    }

    public static List<User> visibleUsers() {
        List<User> out = new ArrayList<>();
        for (User u : s.users) if (s.demoData || !u.isDemo) out.add(u);
        return out;
    }

    public static List<Donation> visibleDonations() {
        List<Donation> out = new ArrayList<>();
        for (Donation d : s.donations) {
            if (s.demoData) { out.add(d); continue; }
            User u = userById(d.userId);
            if (u != null && !u.isDemo) out.add(d);
        }
        return out;
    }

    public static List<Notif> myNotifs() {
        List<Notif> out = new ArrayList<>();
        for (Notif n : s.notifications)
            if ("all".equals(n.userId) || (s.session != null && n.userId.equals(s.session))) out.add(n);
        return out;
    }

    public static int unreadCount() {
        int c = 0;
        for (Notif n : myNotifs()) if (!n.read) c++;
        return c;
    }

    public static void pushNotif(String userId, String type, String title, String body) {
        Notif n = new Notif();
        n.id = Bn.uid("n"); n.userId = userId; n.type = type; n.title = title; n.body = body;
        n.date = System.currentTimeMillis(); n.read = false;
        s.notifications.add(0, n);
        save();
    }

    public static int stockFor(String g) { return s.stock[stockIdx(g)]; }
    public static int stockTotal() { int t = 0; for (int i : s.stock) t += i; return t; }

    /* ══ ডোনার উপলব্ধতা (স্টক বিস্তারিত) ═══════════════ */

    static void sortByStrength(List<User> list) {
        Collections.sort(list, new Comparator<User>() {
            public int compare(User a, User b) {
                int d = b.donationCount - a.donationCount;
                return d != 0 ? d : b.trustScore - a.trustScore;
            }
        });
    }

    /** এই গ্রুপের ডোনার যারা এখনই রক্ত দিতে পারবে (কুলডাউন শেষ)। */
    public static List<User> availableSame(String g) {
        List<User> out = new ArrayList<>();
        for (User u : visibleUsers())
            if (u.bloodType.equals(g) && cooldown(u).eligible) out.add(u);
        sortByStrength(out);
        return out;
    }

    /** সামঞ্জস্যপূর্ণ গ্রুপের ডোনার যারা এখনই দিতে পারবে (যেমন A+ দিতে পারে A-, O+, O-)। */
    public static List<User> availableCompat(String g) {
        String compat = " " + compatOf(g) + " ";
        List<User> out = new ArrayList<>();
        for (User u : visibleUsers())
            if (!u.bloodType.equals(g) && compat.contains(" " + u.bloodType + " ")
                    && cooldown(u).eligible) out.add(u);
        sortByStrength(out);
        return out;
    }

    /** এই গ্রুপের যারা এখনো কুলডাউনে — অবশিষ্ট দিন অনুযায়ী। */
    public static List<User> coolingSame(String g) {
        List<User> out = new ArrayList<>();
        for (User u : visibleUsers())
            if (u.bloodType.equals(g) && !cooldown(u).eligible && u.lastDonationDate > 0) out.add(u);
        Collections.sort(out, new Comparator<User>() {
            public int compare(User a, User b) {
                return Integer.compare(cooldown(a).days, cooldown(b).days);
            }
        });
        return out;
    }

    /** unfulfilled first, then newest first */
    public static List<Emergency> visibleEmergencies() {
        List<Emergency> out = new ArrayList<>(s.emergencies);
        Collections.sort(out, new Comparator<Emergency>() {
            public int compare(Emergency a, Emergency b) {
                int d = (a.fulfilled ? 1 : 0) - (b.fulfilled ? 1 : 0);
                return d != 0 ? d : Long.compare(b.createdAt, a.createdAt);
            }
        });
        return out;
    }

    public static final class Cooldown {
        public int days, elapsed, lastDiff;
        public boolean eligible, first;
    }

    public static Cooldown cooldown(User u) {
        Cooldown c = new Cooldown();
        if (u == null || u.lastDonationDate == 0) { c.days = 0; c.eligible = true; c.first = true; return c; }
        long diff = (System.currentTimeMillis() - u.lastDonationDate) / 86400000L;
        c.lastDiff = (int) diff; c.elapsed = (int) diff; c.first = false;
        if (diff >= COOLDOWN_DAYS) { c.days = 0; c.eligible = true; }
        else c.days = COOLDOWN_DAYS - (int) diff;
        return c;
    }

    public static void addDonation(Donation rec) {
        User me = me();
        rec.id = Bn.uid("don"); rec.userId = me.id; rec.createdAt = System.currentTimeMillis();
        s.donations.add(0, rec);
        me.donationCount = me.donationCount + 1;
        me.lastDonationDate = rec.date;
        me.trustScore = Math.min(100, me.trustScore + 4);
        int idx = stockIdx(me.bloodType);
        s.stock[idx] = s.stock[idx] + Math.max(1, Math.round(rec.amountMl / 450f));
        save();
    }

    public static List<User> ranking() {
        List<User> list = new ArrayList<>(visibleUsers());
        Collections.sort(list, new Comparator<User>() {
            public int compare(User a, User b) {
                int d = (b.donationCount) - (a.donationCount);
                return d != 0 ? d : (b.trustScore) - (a.trustScore);
            }
        });
        return list;
    }

    public static int myRank() {
        List<User> r = ranking();
        for (int i = 0; i < r.size(); i++) if (s.session != null && r.get(i).id.equals(s.session)) return i + 1;
        return 0;
    }

    /** @return {style, label} — style: platinum/gold/silver/bronze/friend/new */
    public static String[] badge(User u) {
        int c = u.donationCount;
        if (c >= 50) return new String[]{"platinum", "প্লাটিনাম ডোনার"};
        if (c >= 20) return new String[]{"gold", "গোল্ড ডোনার"};
        if (c >= 10) return new String[]{"silver", "সিলভার ডোনার"};
        if (c >= 5) return new String[]{"bronze", "ব্রোঞ্জ ডোনার"};
        if (c >= 1) return new String[]{"friend", "রক্তবন্ধু"};
        return new String[]{"new", "নতুন ডোনার"};
    }

    /* ══ Auth ═════════════════════════════════════════════════ */
    public static final class AuthResult {
        public boolean ok; public String msg; public User user;
    }

    public static String pwHash(String email, String password) {
        return String.valueOf(Bn.hash(email.toLowerCase() + ":" + password));
    }

    public static AuthResult register(String name, String email, String phone, String bloodType,
                                      String address, String nid, String password, String referredBy) {
        AuthResult r = new AuthResult();
        String em = email.toLowerCase();
        for (User u : s.users)
            if (u.email.toLowerCase().equals(em) && !u.email.isEmpty()) {
                r.msg = "এই ইমেইল দিয়ে ইতিমধ্যে একটি অ্যাকাউন্ট আছে। লগইন করুন।"; return r;
            }
        for (User u : s.users)
            if (!phone.isEmpty() && u.phone.equals(phone)) {
                r.msg = "এই ফোন নম্বর ইতিমধ্যে নিবন্ধিত।"; return r;
            }
        User referrer = null;
        if (referredBy != null && !referredBy.trim().isEmpty()) {
            String code = referredBy.trim().toUpperCase();
            for (User u : s.users) if (u.referralCode.equals(code)) { referrer = u; break; }
            if (referrer == null) { r.msg = "রেফারেল কোডটি সঠিক নয়। খালি রাখুন বা সঠিক কোড দিন।"; return r; }
        }
        String code = "DJS-" + String.valueOf(100000 + Math.abs(Bn.hash(phone + name)) % 900000).substring(0, 5);
        User u = new User();
        u.id = Bn.uid("u"); u.name = name; u.email = email; u.phone = phone;
        u.bloodType = bloodType; u.address = address; u.nid = nid == null ? "" : nid;
        u.password = pwHash(email, password);
        u.donationCount = 0; u.trustScore = 40;
        u.referralCode = code; u.referredBy = referrer != null ? referrer.referralCode : "";
        u.referralCount = 0; u.verified = false; u.lastDonationDate = 0;
        u.createdAt = System.currentTimeMillis(); u.isDemo = false;
        s.users.add(u);
        if (referrer != null) {
            referrer.referralCount = referrer.referralCount + 1;
            pushNotif(referrer.id, "rank", "নতুন রেফারেল!",
                    name + " আপনার রেফারেল কোড ব্যবহার করে রেজিস্টার করেছেন। আপনার মোট রেফারেল: " + Bn.bn(referrer.referralCount) + " জন।");
        }
        s.session = u.id;
        pushNotif(u.id, "sys", "স্বাগতম, " + name + "!",
                ORG + "-এ আপনার রেজিস্ট্রেশন সফল হয়েছে। আপনার রেফারেল কোড: " + code + " — বন্ধুদের আমন্ত্রণ জানান।");
        save();
        Sync.register(u, password);
        r.ok = true; r.user = u;
        return r;
    }

    public static AuthResult login(String email, String password) {
        AuthResult r = new AuthResult();
        String em = email.toLowerCase();
        User found = null;
        for (User u : s.users) if (!u.email.isEmpty() && u.email.toLowerCase().equals(em) && u.password != null) { found = u; break; }
        if (found == null) { r.msg = "এই ইমেইল দিয়ে কোনো অ্যাকাউন্ট পাওয়া যায়নি।"; return r; }
        if (!found.password.equals(pwHash(found.email, password))) { r.msg = "পাসওয়ার্ড সঠিক নয়। আবার চেষ্টা করুন।"; return r; }
        s.session = found.id;
        save();
        Sync.login(email, password);
        r.ok = true; r.user = found;
        return r;
    }

    public static void logout() { s.session = null; save(); }

    /* ══ Sync (ঐচ্ছিক REST) ═══════════════════════════════════ */
    public static final class Sync {
        private Sync() {}

        static boolean enabled() { return s.serverUrl != null && !s.serverUrl.trim().isEmpty(); }

        static void post(final String path, final String json) {
            if (!enabled()) return;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        HttpURLConnection c = (HttpURLConnection) new URL(s.serverUrl.trim() + path).openConnection();
                        c.setRequestMethod("POST");
                        c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                        c.setDoOutput(true);
                        c.setConnectTimeout(10000);
                        c.setReadTimeout(12000);
                        OutputStream os = c.getOutputStream();
                        os.write(json.getBytes("UTF-8"));
                        os.close();
                        int code = c.getResponseCode();
                        s.syncLast = System.currentTimeMillis();
                        s.syncStatus = code >= 200 && code < 300 ? "ok" : "err";
                        save();
                    } catch (Exception e) {
                        s.syncLast = System.currentTimeMillis();
                        s.syncStatus = "err";
                        save();
                    }
                }
            }, "sync-http").start();
        }

        static String j(String k, String v) {
            JSONObject o = new JSONObject();
            try { o.put(k, v == null ? "" : v); } catch (Exception ignored) {}
            return o.toString();
        }


        public static void register(User u, String password) {
            if (!enabled()) return;
            JSONObject o = new JSONObject();
            try {
                o.put("name", u.name).put("email", u.email).put("phone", u.phone)
                        .put("password", password).put("bloodType", u.bloodType)
                        .put("address", u.address).put("nid", u.nid)
                        .put("referralCode", u.referredBy == null ? "" : u.referredBy);
            } catch (Exception ignored) {}
            post("/api/auth/register", o.toString());
        }

        public static void login(String email, String password) {
            if (!enabled()) return;
            JSONObject o = new JSONObject();
            try { o.put("email", email).put("password", password); } catch (Exception ignored) {}
            post("/api/auth/login", o.toString());
        }

        public static void pushEmergency(Emergency e) {
            if (!enabled()) return;
            JSONObject o = new JSONObject();
            try {
                o.put("patientName", e.patientName).put("bloodGroup", e.bloodGroup)
                        .put("units", e.units).put("hospital", e.hospital)
                        .put("location", e.location).put("contact", e.contact).put("notes", e.notes);
            } catch (Exception ignored) {}
            post("/api/emergency-requests", o.toString());
        }

        public static void pushChat(String channel, String senderId, String senderName, String text) {
            if (!enabled()) return;
            JSONObject o = new JSONObject();
            try {
                o.put("channel", channel).put("senderId", senderId)
                        .put("senderName", senderName).put("text", text)
                        .put("ts", System.currentTimeMillis());
            } catch (Exception ignored) {}
            post("/api/chat", o.toString());
        }
    }
}
