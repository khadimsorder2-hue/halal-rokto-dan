package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** চ্যাট লেয়ার — গ্রুপ চ্যানেল + ব্যক্তিগত বার্তা, লোকাল স্টোর, ডেমো অটো-রিপ্লাই, সার্ভার সিঙ্ক হুক। */
public final class Chat {

    private Chat() {}

    public static final class Msg {
        public String id, ch, senderId, senderName, text;
        public long ts;

        JSONObject toJson() throws Exception {
            return new JSONObject().put("id", id).put("ch", ch).put("senderId", senderId)
                    .put("senderName", senderName).put("text", text).put("ts", ts);
        }

        static Msg fromJson(JSONObject o) {
            Msg m = new Msg();
            m.id = o.optString("id", ""); m.ch = o.optString("ch", "general");
            m.senderId = o.optString("senderId", ""); m.senderName = o.optString("senderName", "");
            m.text = o.optString("text", ""); m.ts = o.optLong("ts", 0);
            return m;
        }
    }

    public static final class Channel {
        public final String id, name, desc;
        public final int icon;

        public Channel(String id, String name, String desc, int icon) {
            this.id = id; this.name = name; this.desc = desc; this.icon = icon;
        }
    }

    public static final Channel[] GROUPS = {
            new Channel("general", "সাধারণ আলোচনা", "সব সদস্যের গ্রুপ চ্যাট", R.drawable.ic_chat),
            new Channel("emergency", "জরুরি রক্ত সহায়তা", "জরুরি আবেদনে দ্রুত সাড়া দিন", R.drawable.ic_sos),
            new Channel("committee", "কমিটি আলোচনা", "সংগঠনের সিদ্ধান্ত ও পরিকল্পনা", R.drawable.ic_shield),
    };

    /** বর্তমানে খোলা চ্যাট রুম (নোটিফিকেশন দমনের জন্য), না খোলা থাকলে null। */
    public static String activeRoom = null;

    public static final List<Msg> msgs = new ArrayList<>();
    static final Map<String, Integer> unread = new HashMap<>();

    static SharedPreferences prefs;
    static final Random rnd = new Random();
    static final Handler main = new Handler(Looper.getMainLooper());
    static final List<Runnable> uiRefreshers = new ArrayList<>();

    /* ── Init & persistence ─────────────────────────────────── */
    public static void init(Context c) {
        if (prefs != null) return;
        prefs = c.getApplicationContext().getSharedPreferences("halal_rokto_chat_v1", Context.MODE_PRIVATE);
        load();
    }

    static void load() {
        msgs.clear();
        unread.clear();
        try {
            JSONObject o = new JSONObject(prefs.getString("chat", ""));
            JSONArray a = o.optJSONArray("msgs");
            if (a != null) for (int i = 0; i < a.length(); i++) msgs.add(Msg.fromJson(a.getJSONObject(i)));
            JSONObject u = o.optJSONObject("unread");
            if (u != null) {
                Iterator<String> it = u.keys();
                while (it.hasNext()) { String k = it.next(); unread.put(k, u.optInt(k)); }
            }
        } catch (Exception ignored) { }
        if (msgs.isEmpty()) seed();
    }

    static void save() {
        try {
            JSONObject o = new JSONObject();
            JSONArray a = new JSONArray();
            int from = Math.max(0, msgs.size() - 500);
            for (int i = from; i < msgs.size(); i++) a.put(msgs.get(i).toJson());
            o.put("msgs", a);
            JSONObject u = new JSONObject();
            for (Map.Entry<String, Integer> e : unread.entrySet()) u.put(e.getKey(), e.getValue());
            o.put("unread", u);
            prefs.edit().putString("chat", o.toString()).apply();
        } catch (Exception ignored) { }
    }

    /* ── Queries ────────────────────────────────────────────── */
    public static List<Msg> msgsOf(String ch) {
        List<Msg> out = new ArrayList<>();
        for (Msg m : msgs) if (m.ch.equals(ch)) out.add(m);
        Collections.sort(out, new Comparator<Msg>() {
            public int compare(Msg a, Msg b) { return Long.compare(a.ts, b.ts); }
        });
        return out;
    }

    public static Msg lastOf(String ch) {
        Msg last = null;
        for (Msg m : msgs) if (m.ch.equals(ch) && (last == null || m.ts > last.ts)) last = m;
        return last;
    }

    public static int unreadOf(String ch) {
        Integer n = unread.get(ch);
        return n == null ? 0 : n;
    }

    public static int unreadTotal() {
        int t = 0;
        for (int v : unread.values()) t += v;
        return t;
    }

    public static void markRead(String ch) {
        if (unread.remove(ch) != null) save();
    }

    public static String dmId(Data.User u) { return "dm_" + u.id; }

    public static Data.User dmPartner(String ch) {
        if (!ch.startsWith("dm_")) return null;
        return Data.userById(ch.substring(3));
    }

    /** যেসব ব্যক্তিগত চ্যাটে এরই মধ্যে কথোপকথন আছে। */
    public static List<Data.User> dmPartners() {
        List<Data.User> out = new ArrayList<>();
        for (Msg m : msgs) {
            if (!m.ch.startsWith("dm_")) continue;
            Data.User u = dmPartner(m.ch);
            if (u != null && !out.contains(u)) out.add(u);
        }
        return out;
    }

    public static String titleOf(String ch) {
        for (Channel g : GROUPS) if (g.id.equals(ch)) return g.name;
        Data.User u = dmPartner(ch);
        return u != null ? u.name : "চ্যাট";
    }

    /* ── Send & receive ─────────────────────────────────────── */
    public static void send(String ch, String text) {
        Data.User me = Data.me();
        if (me == null || text == null || text.trim().isEmpty()) return;
        add(ch, me.id, me.name, text.trim(), System.currentTimeMillis());
        Data.Sync.pushChat(ch, me.id, me.name, text.trim());
        maybeAutoReply(ch, text.trim());
    }

    /** অন্য কেউ বার্তা পাঠালে (ডেমো অটো-রিপ্লাই / সার্ভার পোল) — সবসময় মেইন থ্রেডে। */
    public static void receive(final String ch, final Data.User from, final String text) {
        if (from == null || text == null || text.trim().isEmpty()) return;
        main.post(new Runnable() {
            public void run() { add(ch, from.id, from.name, text.trim(), System.currentTimeMillis()); }
        });
    }

    static void add(String ch, String senderId, String senderName, String text, long ts) {
        Msg m = new Msg();
        m.id = Bn.uid("m"); m.ch = ch; m.senderId = senderId;
        m.senderName = senderName; m.text = text; m.ts = ts;
        msgs.add(m);

        boolean inRoom = ch.equals(activeRoom);
        if (!inRoom && !senderId.equals(Data.s.session)) {
            unread.put(ch, unreadOf(ch) + 1);
            notifySystem(ch, m);
        }
        save();

        for (Runnable r : new ArrayList<>(uiRefreshers)) {
            try { r.run(); } catch (Exception ignored) { }
        }
        try { if (Ui.host != null) Ui.host.updateNav(); } catch (Exception ignored) { }
    }

    static void notifySystem(String ch, Msg m) {
        try {
            Context app = Ui.host.getApplicationContext();
            NotifUtil.notify(app, NotifUtil.CH_CHAT, ("chatnotif_" + ch).hashCode(),
                    m.senderName, m.text, "chatroom", ch);
        } catch (Exception ignored) { }
    }

    /* ── UI refresher registry ──────────────────────────────── */
    public static void register(Runnable r) { uiRefreshers.add(r); }

    public static void unregister(Runnable r) { uiRefreshers.remove(r); }

    /* ── Demo auto-reply ────────────────────────────────────── */
    static void maybeAutoReply(final String ch, final String myText) {
        if (!Data.s.demoData || Data.Sync.enabled()) return;

        final Data.User partner = dmPartner(ch);
        final boolean isDm = ch.startsWith("dm_");
        if (isDm && (partner == null || !partner.isDemo)) return;

        final List<Data.User> candidates = new ArrayList<>();
        for (Data.User u : Data.visibleUsers())
            if (u.isDemo && !u.id.equals(Data.s.session)) {
                if (isDm) { if (u.id.equals(partner.id)) candidates.add(u); }
                else candidates.add(u);
            }
        if (candidates.isEmpty()) return;

        final Data.User donor = candidates.get(rnd.nextInt(candidates.size()));
        final int delay = 1400 + rnd.nextInt(2200);
        new Thread(new Runnable() {
            public void run() {
                try { Thread.sleep(delay); } catch (InterruptedException e) { return; }
                receive(ch, donor, cannedReply(myText, donor));
            }
        }, "chat-demo-reply").start();
    }

    static String cannedReply(String myText, Data.User donor) {
        String t = myText == null ? "" : myText;
        if (t.contains("দরকার") || t.contains("প্রয়োজন") || t.contains("লাগবে") || t.contains("লাগবে"))
            return rnd.nextBoolean()
                    ? "আমার গ্রুপ " + donor.bloodType + " — দরকার হলে ইনশাআল্লাহ দিতে পারব।"
                    : "কাল সকালেই হাসপাতালে চলে যাব ইনশাআল্লাহ, ওয়ার্ড কোনটায় বলেন।";
        if (t.contains("রক্ত") && t.contains("ক্যাম্প"))
            return "ক্যাম্পের স্বেচ্ছাসেবক হিসেবে আমি থাকব ইনশাআল্লাহ।";
        if (t.contains("ক্যাম্প") || t.contains("শুক্র") || t.contains("পরিষদ"))
            return rnd.nextBoolean()
                    ? "মাশাআল্লাহ, এবারের ক্যাম্প আগের চেয়ে বড় হবে ইনশাআল্লাহ।"
                    : "টেন্ট আর চেয়ারের দায়িত্ব আমি নিচ্ছি, স্টকে আছে।";
        if (t.contains("ধন্যবাদ") || t.contains("শুকরিয়া") || t.contains("জাযাক") || t.contains("জাজাক"))
            return rnd.nextBoolean()
                    ? "আলহামদুলিল্লাহ, এটা তো আমাদের দায়িত্ব।"
                    : "এসব কাজে ধন্যবাদের কিছু নেই ভাই, আল্লাহর জন্য।";
        if (t.contains("কুলডাউন") || t.contains("৯০"))
            return "আমার ৯০ দিন পূর্ণ হয়ে গেছে — এখন যেকোনো সময় দিতে পারব।";
        if (t.contains("ডোনার") || t.contains("খুঁজ"))
            return "স্টক পেজে " + donor.bloodType + " গ্রুপে উপলব্ধ ডোনারদের তালিকা দেখুন, সরাসরি ফোনও করা যায়।";
        String[] pool = {
                "মাশাআল্লাহ, ভালো উদ্যোগ।",
                "আমি রাজি আছি।",
                "ইনশাআল্লাহ সম্ভব।",
                "আলহামদুলিল্লাহ, সব ঠিক আছে।",
                "কাল সকালে কথা হলে দ্রুত সিদ্ধান্ত হবে।",
                "ফোনে কথা বললে আরও পরিষ্কার হবে।",
                "আপনার সাথে একমত।",
                "কমিটিতে আলোচনা করে জানাচ্ছি।",
        };
        return pool[rnd.nextInt(pool.length)];
    }

    /* ── Seed conversation ──────────────────────────────────── */
    static void seed() {
        long now = System.currentTimeMillis();
        long H = 3600000L, D = 86400000L;
        Data.User rahim = Data.userById("demo_1");   // রহিম O+
        Data.User karim = Data.userById("demo_2");   // করিম B+
        Data.User sumaiya = Data.userById("demo_3"); // সুমাইয়া A+
        Data.User sohag = Data.userById("demo_5");   // সোহাগ O-
        String rahimN = rahim != null ? rahim.name : "মোঃ রহিম মিয়া";
        String karimN = karim != null ? karim.name : "আব্দুল করিম";
        String sumaiyaN = sumaiya != null ? sumaiya.name : "মোসাঃ সুমাইয়া আক্তার";
        String sohagN = sohag != null ? sohag.name : "মোঃ সোহাগ মিয়া";
        String rahimId = rahim != null ? rahim.id : "demo_1";
        String karimId = karim != null ? karim.id : "demo_2";
        String sumaiyaId = sumaiya != null ? sumaiya.id : "demo_3";
        String sohagId = sohag != null ? sohag.id : "demo_5";

        addSeed("general", rahimId, rahimN, "আসসালামু আলাইকুম সবাইকে। শুক্রবারের রক্তদান ক্যাম্পের প্রস্তুতি কেমন চলছে?", now - 26 * H);
        addSeed("general", sumaiyaId, sumaiyaN, "ওয়ালাইকুম আসসালাম। আলহামদুলিল্লাহ ভালো চলছে, ইউনিয়ন পরিষদের অনুমতিও পাওয়া গেছে।", now - 25 * H);
        addSeed("general", karimId, karimN, "চেয়ার আর টেন্ট স্টকে আছে — আগের ক্যাম্পের জিনিস এখনো ভালো অবস্থায়।", now - 24 * H);
        addSeed("general", rahimId, rahimN, "মাশাআল্লাহ। আমি স্বেচ্ছাসেবক হিসেবে সারাদিন থাকব, রেজিস্ট্রেশন টেবিলের দায়িত্ব দিন আমাকে।", now - 23 * H);
        addSeed("general", sumaiyaId, sumaiyaN, "স্বাগতম ব্যানারটা ছাপাতে হবে, খরচ কমিটি বহন করবে ইনশাআল্লাহ।", now - 20 * H);
        addSeed("general", karimId, karimN, "ঠিক আছে, কাল নাটোর গিয়ে ব্যানারের অর্ডার দিয়ে আসব।", now - 6 * H);

        addSeed("emergency", sumaiyaId, sumaiyaN, "জনাব আব্দুল জলিল সাহেবের B+ রক্ত দরকার, নাটোর সদর হাসপাতালে ২ ব্যাগ।", now - 5 * H);
        addSeed("emergency", karimId, karimN, "আমার গ্রুপ B+ — আমি দিতে পারব, এক ঘণ্টার মধ্যে হাসপাতালে পৌঁছে যাব ইনশাআল্লাহ।", now - 4 * H);
        addSeed("emergency", sohagId, sohagN, "জরুরি হলে আমিও রেডি আছি, O- সর্বজনীন দাতা — যেকোনো গ্রুপে লাগবে।", now - 3 * H);
        addSeed("emergency", sumaiyaId, sumaiyaN, "জাযাকাল্লাহ খাইরুন ভাইয়েরা। আবেদনটা অ্যাপের জরুরি পেজে দেওয়া আছে, সেখানে ফোন নম্বরও আছে।", now - 2 * H);

        addSeed("committee", rahimId, rahimN, "পরবর্তী কমিটি বৈঠক কবে হবে? এই মাসের এজেন্ডা: সদস্য ফি ও নতুন ডোনার তালিকা।", now - 2 * D);
        addSeed("committee", sumaiyaId, sumaiyaN, "শুক্রবার বাদ মাগরিব সংগঠনের অফিসে হোক ইনশাআল্লাহ।", now - 2 * D + 2 * H);

        addSeed("dm_demo_1", rahimId, rahimN, "ভাই, আপনার কুলডাউন কবে শেষ হবে? এক রোগীর জন্য দরকার হতে পারে।", now - 28 * H);
        addSeed("dm_demo_1", Data.s.session == null ? "me" : Data.s.session, "আমি",
                "আমার ৯০ দিন পূর্ণ হতে চলেছে, কয়েকদিন পরই দিতে পারব ইনশাআল্লাহ।", now - 27 * H);
        addSeed("dm_demo_1", rahimId, rahimN, "আলহামদুলিল্লাহ, জাযাকাল্লাহ ভাই।", now - 26 * H);

        unread.put("general", 2);
        unread.put("emergency", 1);
        save();
    }

    static void addSeed(String ch, String senderId, String senderName, String text, long ts) {
        Msg m = new Msg();
        m.id = Bn.uid("m"); m.ch = ch; m.senderId = senderId; m.senderName = senderName;
        m.text = text; m.ts = ts;
        msgs.add(m);
    }
}
