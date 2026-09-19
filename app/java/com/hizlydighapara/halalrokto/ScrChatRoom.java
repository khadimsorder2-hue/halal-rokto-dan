package com.hizlydighapara.halalrokto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * চ্যাট রুম — রিসাইকেল ListView (ViewHolder), মেসেজ বাবল, ইনপুট বার।
 * লো-এন্ড ফোনে মসৃণ ৬০/৯০/১২০Hz স্ক্রলের জন্য ভিউ রিসাইক্লিং ব্যবহৃত।
 */
public final class ScrChatRoom {

    private ScrChatRoom() {}

    public static View build(final Context c) {
        String ch = Ui.host.pendingRoom;
        if (ch == null) ch = "general";
        Ui.host.pendingRoom = null;
        final String channelId = ch;

        final boolean isDm = channelId.startsWith("dm_");
        final Chat.Channel gc = groupOf(channelId);
        final Data.User partner = isDm ? Chat.dmPartner(channelId) : null;
        final String title = gc != null ? gc.name : (partner != null ? partner.name : "চ্যাট");

        Chat.markRead(channelId);
        Chat.activeRoom = channelId;

        /* root: outer view gets status-bar padding from MainActivity, inner keeps nav inset */
        FrameLayout outer = new FrameLayout(c);
        outer.setBackgroundColor(D.bg);
        final LinearLayout root = Ui.v(c);
        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.setPadding(0, 0, 0, Math.max(D.dp(6), Ui.host.navBarH));
        outer.addView(root);

        /* ── header ── */
        LinearLayout head = Ui.h(c);
        head.setGravity(Gravity.CENTER_VERTICAL);
        head.setPadding(D.dp(8), D.dp(10), D.dp(14), D.dp(10));
        head.setBackground(D.round(D.bg, 0));

        ImageView back = Ui.icon(c, R.drawable.ic_back, 20, D.onSurface);
        back.setBackground(D.ripple(D.round(D.surfaceCH, 50), D.rippleColor));
        LinearLayout.LayoutParams bp = Ui.lp(D.dp(38), D.dp(38));
        bp.rightMargin = D.dp(10);
        back.setLayoutParams(bp);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { Ui.host.back(); }
        });
        head.addView(back);

        View lead;
        if (gc != null) {
            lead = Ui.icon(c, gc.icon, 18, 0xFFFFFFFF);
            lead.setPadding(D.dp(9), D.dp(9), D.dp(9), D.dp(9));
            lead.setBackground(D.gradAngle(D.redGrad, 22, 315));
        } else {
            lead = Ui.avatar(c, Bn.initials(title),
                    partner != null ? D.avatarColor(partner.id) : D.primaryC, 40, 15);
        }
        head.addView(lead);
        ((LinearLayout.LayoutParams) lead.getLayoutParams()).rightMargin = D.dp(11);

        LinearLayout hw = Ui.v(c);
        hw.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        hw.addView(Ui.txt(c, title, 16.5f, D.onSurface, 700));
        String sub;
        if (gc != null) sub = gc.desc + " • " + Bn.bn(Data.visibleUsers().size()) + " জন সদস্য";
        else if (partner != null) sub = partner.bloodType + " • " + Data.badge(partner)[1]
                + (partner.verified ? " • ভেরিফায়েড" : "");
        else sub = "";
        TextView st = Ui.txt(c, sub, 11f, D.onSurfaceVar, 500);
        st.setPadding(0, D.dp(1), 0, 0);
        hw.addView(st);
        head.addView(hw);

        if (partner != null && partner.phone != null && partner.phone.length() >= 4) {
            ImageView call = Ui.icon(c, R.drawable.ic_phone, 18, 0xFFFFFFFF);
            call.setPadding(D.dp(10), D.dp(10), D.dp(10), D.dp(10));
            call.setBackground(D.ripple(D.gradAngle(new int[]{0xFF2E9E4F, 0xFF1E6B33}, 50, 315), D.rippleColorLight));
            call.setElevation(D.dp(2));
            call.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { Ui.host.haptic(10); Ui.host.dial(partner.phone); }
            });
            head.addView(call);
        }
        root.addView(head);

        View hr = new View(c);
        hr.setBackground(D.round(D.outlineVar, 1));
        hr.setLayoutParams(Ui.lp(ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, D.dp(0.8f))));
        root.addView(hr);

        /* ── messages list (recycled) ── */
        final MsgAdapter adapter = new MsgAdapter(c, channelId, isDm);
        final ListView lv = new ListView(c);
        lv.setDivider(null);
        lv.setStackFromBottom(true);
        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        lv.setPadding(D.dp(12), D.dp(10), D.dp(12), D.dp(8));
        lv.setClipToPadding(false);
        lv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        lv.setAdapter(adapter);
        lv.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        root.addView(lv);
        lv.post(new Runnable() {
            public void run() { lv.setSelection(adapter.getCount() - 1); }
        });

        /* ── input bar ── */
        LinearLayout bar = Ui.h(c);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(D.dp(10), D.dp(8), D.dp(10), D.dp(8));

        final EditText et = new EditText(c);
        et.setTypeface(D.tfRegular);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        et.setTextColor(D.onSurface);
        et.setHintTextColor(D.outline);
        et.setHint("মেসেজ লিখুন...");
        et.setBackground(D.roundStroke(D.surfaceC, 22, D.outlineVar, 1.2f));
        et.setPadding(D.dp(16), D.dp(11), D.dp(16), D.dp(11));
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        et.setMaxLines(4);
        et.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        bar.addView(et);

        FrameLayout sendBtn = new FrameLayout(c);
        sendBtn.setLayoutParams(new LinearLayout.LayoutParams(D.dp(46), D.dp(46)));
        ((LinearLayout.LayoutParams) sendBtn.getLayoutParams()).leftMargin = D.dp(8);
        sendBtn.setBackground(D.ripple(D.gradAngle(D.redGrad, 50, 315), D.rippleColorLight));
        sendBtn.setElevation(D.dp(3));
        ImageView si = Ui.icon(c, R.drawable.ic_send, 19, 0xFFFFFFFF);
        si.setLayoutParams(Ui.flp(D.dp(19), D.dp(19), Gravity.CENTER));
        sendBtn.addView(si);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String text = et.getText().toString().trim();
                if (text.isEmpty()) return;
                Ui.host.haptic(9);
                et.setText("");
                Chat.send(channelId, text);
                adapter.refresh();
                adapter.notifyDataSetChanged();
                lv.setSelection(adapter.getCount() - 1);
            }
        });
        bar.addView(sendBtn);
        root.addView(bar);

        /* ── live refresh (নতুন মেসেজ এলে) ── */
        final Runnable refresher = new Runnable() {
            public void run() {
                if (!lv.isAttachedToWindow()) {
                    Chat.unregister(this);
                    return;
                }
                Chat.markRead(channelId);
                adapter.refresh();
                adapter.notifyDataSetChanged();
                lv.setSelection(adapter.getCount() - 1);
            }
        };
        Chat.register(refresher);

        return outer;
    }

    static Chat.Channel groupOf(String ch) {
        for (Chat.Channel g : Chat.GROUPS) if (g.id.equals(ch)) return g;
        return null;
    }

    /* ── Adapter (ViewHolder recycling) ─────────────────────── */
    static final class MsgAdapter extends BaseAdapter {

        final Context c;
        final String ch;
        final boolean isDm;
        final int textMaxWidth;
        List<Chat.Msg> data;

        MsgAdapter(Context c, String ch, boolean isDm) {
            this.c = c;
            this.ch = ch;
            this.isDm = isDm;
            this.textMaxWidth = c.getResources().getDisplayMetrics().widthPixels - D.dp(96);
            refresh();
        }

        void refresh() { data = Chat.msgsOf(ch); }

        public int getCount() { return data.size(); }
        public Object getItem(int position) { return data.get(position); }
        public long getItemId(int position) { return position; }

        public int getViewTypeCount() { return 2; }

        public int getItemViewType(int position) {
            return isMine(data.get(position)) ? 1 : 0;
        }

        static boolean isMine(Chat.Msg m) {
            return m.senderId.equals(Data.s.session) || "me".equals(m.senderId);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final Chat.Msg m = data.get(position);
            boolean mine = isMine(m);
            Holder h;
            if (convertView == null) {
                h = new Holder();
                convertView = buildRow(mine, h);
                convertView.setTag(h);
            } else {
                h = (Holder) convertView.getTag();
            }

            if (h.avatar != null) {
                h.avatar.setText(Bn.initials(m.senderName));
                h.avatar.setBackground(D.round(D.avatarColor(m.senderId), 15));
            }
            if (!isDm && !mine) {
                h.name.setText(m.senderName);
                h.name.setTextColor(D.avatarColor(m.senderId));
                h.name.setVisibility(View.VISIBLE);
            } else {
                h.name.setVisibility(View.GONE);
            }
            h.text.setText(m.text);
            h.text.setMaxWidth(textMaxWidth);
            h.time.setText(Bn.timeAgo(m.ts));
            return convertView;
        }

        View buildRow(boolean mine, Holder h) {
            Context c = this.c;

            FrameLayout row = new FrameLayout(c);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rp.topMargin = D.dp(3);
            rp.bottomMargin = D.dp(3);
            row.setLayoutParams(rp);

            LinearLayout line = Ui.h(c);
            line.setGravity(Gravity.TOP);

            if (!mine) {
                TextView av = new TextView(c);
                av.setGravity(Gravity.CENTER);
                av.setTypeface(D.tfBold);
                av.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                av.setTextColor(0xFFFFFFFF);
                av.setBackground(D.round(D.primaryC, 15));
                av.setLayoutParams(Ui.lp(D.dp(30), D.dp(30)));
                av.setPadding(0, 0, 0, D.dp(1));
                h.avatar = av;
                line.addView(av);
                ((LinearLayout.LayoutParams) av.getLayoutParams()).rightMargin = D.dp(7);
            }

            LinearLayout bubble = Ui.v(c);
            bubble.setPadding(D.dp(13), D.dp(8), D.dp(13), D.dp(7));

            h.name = Ui.txt(c, "", 10.5f, D.onSurface, 700);
            h.name.setPadding(0, 0, 0, D.dp(2));
            h.name.setVisibility(View.GONE);
            bubble.addView(h.name);

            h.text = Ui.txt(c, "", 13.5f, mine ? 0xFFFFFFFF : D.onSurface, 400);
            h.text.setLineSpacing(0, 1.3f);
            bubble.addView(h.text);

            h.time = Ui.txt(c, "", 9f, mine ? D.withAlpha(0xFFFFFFFF, 185) : D.outline, 500);
            h.time.setGravity(Gravity.END);
            h.time.setPadding(0, D.dp(2), 0, 0);
            bubble.addView(h.time);

            if (mine) {
                bubble.setBackground(D.gradAngle(D.redGrad, 18, 315));
                bubble.setElevation(D.dp(2));
            } else {
                bubble.setBackground(D.roundStroke(D.surfaceCLo, 18, D.outlineVar, 1));
            }

            line.addView(bubble);
            bubble.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    mine ? Gravity.END : Gravity.START);
            if (mine) fp.rightMargin = 0; else fp.leftMargin = 0;
            line.setLayoutParams(fp);
            row.addView(line);
            return row;
        }

        static final class Holder {
            TextView name, text, time, avatar;
        }
    }
}
