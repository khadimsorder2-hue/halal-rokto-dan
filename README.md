# 🩸 হালাল রক্ত দান (Halal Rokto Dan) v2.0

<div align="center">

**রক্ত দিন, জীবন বাঁচান — হালাল পথে**

📱 Android App (API 26+, Android 8.0+) • 🇧🇩 বাংলা-প্রথম ডিজাইন • 📴 সম্পূর্ণ অফলাইন

**হিজলি দিঘাপাড়া যুব সংঘ** (Hizly Dighapara JUBO Sangho)
Since 2026 • বাগাতিপাড়া, নাটোর, বাংলাদেশ

</div>

---

## 📥 ডাউনলোড (Release APK)

👉 **[Releases](https://github.com/khadimsorder2-hue/halal-rokto-dan/releases)** পেজ থেকে সরাসরি APK ডাউনলোড করুন।

---

## ✨ ফিচারসমূহ

| ফিচার | বিবরণ |
|---|---|
| 🏠 **ড্যাশবোর্ড** | লাইভ স্ট্যাটস, ৯০ দিনের কুলডাউন ট্র্যাকার (প্রোগ্রেস রিং), জরুরি আবেদন |
| 🩸 **রক্তের স্টক** | ৮টি গ্রুপের রিয়েল-টাইম স্টক, স্ট্যাটাস ইন্ডিকেটর, সামঞ্জস্য (compatibility) তালিকা |
| 🚨 **জরুরি আবেদন** | রোগীর তথ্যসহ আবেদন তৈরি, সরাসরি কল, শেয়ার, পূরণ হিসেবে চিহ্নিতকরণ |
| 👥 **ডোনার ডিরেক্টরি** | গ্রুপ/নাম/এলাকা অনুযায়ী সার্চ, যোগ্যতা স্ট্যাটাস, নতুন ডোনার যোগ |
| 🏆 **র‌্যাংকিং** | পডিয়াম + ব্যাজ সিস্টেম (প্লাটিনাম/গোল্ড/সিলভার/ব্রোঞ্জ/রক্তবন্ধু) |
| 📅 **দানের ইতিহাস** | টাইমলাইন ভিউ, রেকর্ড যোগ করা, স্টকে অটো-আপডেট |
| 🪸 **ডোনার কার্ড** | সংগঠনের ব্র্যান্ডেড ডিজিটাল পরিচয়পত্র + যাচাই কোড |
| 🔔 **নোটিফিকেশন** | জরুরি, দান কনফার্মেশন, র‍্যাংক আপডেট — আনরিড ব্যাজ |
| 🎁 **রেফারেল** | ইউনিক কোড, রেফারেকৃত সদস্য তালিকা, পয়েন্ট |
| 🌙 **ডার্ক মোড** | সম্পূর্ণ Material 3 ডার্ক থিম |
| ☁️ **সার্ভার সিঙ্ক** | REST ব্যাকএন্ড URL দিলে ক্লাউড সিঙ্ক (ঐচ্ছিক) |

## 🏗️ প্রযুক্তি

- **Web Layer**: HTML/CSS/JS — Material 3 ডিজাইন সিস্টেম, Noto Sans Bengali (variable font)
- **Native Shell**: Java WebView + JS Bridge (toast, share, prefs, HTTP, vibrate, back-handling)
- **ডেটা**: অফলাইন-প্রথম (SharedPreferences + localStorage ডুয়াল পারসিস্টেন্স)
- **বিল্ড**: ম্যানুয়াল টুলচেইন — aapt2 → javac → d8 → zipalign → apksigner (Gradle ছাড়া!)
- **সাইজ**: মাত্র ~২০০KB

## 📁 স্ট্রাকচার

```
app/
├── AndroidManifest.xml          # minSdk 26, targetSdk 34
├── java/.../MainActivity.java   # WebView shell + JS bridge
├── assets/www/                  # সম্পূর্ণ ওয়েব অ্যাপ
│   ├── css/app.css              # M3 ডিজাইন সিস্টেম
│   ├── js/core.js               # state, auth, sync, utils
│   ├── js/ui.js                 # router, sheet, dialog, toast
│   ├── js/screens*.js           # ১৩টি স্ক্রিন
│   └── fonts/                   # Noto Sans Bengali woff2
└── res/                         # adaptive icon, splash, theme
scripts/build_apk.sh             # রিলিজ বিল্ড স্ক্রিপ্ট
```

## 🔨 বিল্ড

```bash
# প্রয়োজন: JDK 21, Android SDK (build-tools 35.0.0, platform android-34)
bash scripts/build_apk.sh
# আউটপুট: halal-rokto-dan-v2.0.0-release.apk
```

## 🔑 সাইনিং নোট

> ⚠️ রিলিজ APK টি সংগঠনের নিজস্ব কী-স্টোর দিয়ে সাইন করা। **ভবিষ্যৎ আপডেটের জন্য কী-স্টোর ও পাসওয়ার্ড সংরক্ষণ করুন** — হারিয়ে গেলে পুরনো ইনস্টলেশনের উপর আপডেট দেওয়া যাবে না।

## 🏢 সংগঠন

**হিজলি দিঘাপাড়া যুব সংঘ** — বাগাতিপাড়া, নাটোরের তরুণ সমাজের স্বেচ্ছাসেবী সংগঠন। এলাকার মানুষের সেবায় নিয়োজিত।

---

<div align="center">Made with ❤️ for the community</div>
