package com.hizlydighapara.halalrokto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** ফোন রিস্টার্ট বা অ্যাপ আপডেটের পর ব্যাকগ্রাউন্ড সার্ভিস আবার চালু করে। */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent == null ? "" : intent.getAction() == null ? "" : intent.getAction();
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !"android.intent.action.MY_PACKAGE_REPLACED".equals(action)) return;
        try {
            D.init(context);
            Data.init(context);
            Chat.init(context);
            NotifUtil.ensureService(context);
        } catch (Exception ignored) { }
    }
}
