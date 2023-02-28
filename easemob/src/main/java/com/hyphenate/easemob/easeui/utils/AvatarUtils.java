package com.hyphenate.easemob.easeui.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.widget.AvatarImageView;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 16/08/2018
 */

public class AvatarUtils {

    private static int[] colorArr = {Color.parseColor("#f07267"), Color.parseColor("#28c297"),
            Color.parseColor("#f5b367"), Color.parseColor("#b28a7b"), Color.parseColor("#5b8cad")};


    public static void setAvatarContent(Context context, AvatarImageView avatarImageView) {
        String nick = context.getResources().getString(R.string.file_transfer);
        nick = nick.substring(nick.length() - 2);
        avatarImageView.setTextAndColor(nick, Color.parseColor("#14C972"));
        avatarImageView.setTextColor(Color.WHITE);
    }


    public static void setGroupAvatarContent(String groupName, AvatarImageView avatarImageView) {
        String name = groupName;
        if (groupName != null && groupName.length() > 1) {
            name = groupName.substring(0,2);
        } else {
            name = "群";
        }
        avatarImageView.setTextAndColor(name, Color.parseColor("#E7F3FF"));
        avatarImageView.setTextColor(R.color.theme_color);
    }

    public static void setAvatarContent(Context context, String username, AvatarImageView avatarImageView) {
        EaseUser user = EaseUserUtils.getUserInfo(username);
        int desAvatar = R.drawable.ease_default_avatar;
        if (user != null) {
            if (!TextUtils.isEmpty(user.getAvatar())) {
                GlideUtils.load(context, user.getAvatar(), desAvatar, avatarImageView);
            } else if (!TextUtils.isEmpty(user.getNick())) {
//                String nick = user.getNick().trim();
//                if (nick.length() >= 2) {
//                    nick = nick.substring(nick.length() - 2);
//                }
//                avatarImageView.setTextAndColor(nick, colorArr[nick.charAt(0) % 5]);
                GlideUtils.load(context, desAvatar, avatarImageView);
            } else {
                GlideUtils.load(context, desAvatar, avatarImageView);
            }
        } else {
            GlideUtils.load(context, desAvatar, avatarImageView);
        }
    }

    public static void setAvatarContent(Context context, EaseUser user, AvatarImageView avatarImageView) {
        int desAvatar = R.drawable.ease_default_avatar;
        if (user != null) {
            if (!TextUtils.isEmpty(user.getAvatar())) {
                GlideUtils.load(context, user.getAvatar(), desAvatar, avatarImageView);
            } else if (!TextUtils.isEmpty(user.getNick())) {
//                String nick = user.getNick().trim();
//                if (nick.length() >= 2) {
//                    nick = nick.substring(nick.length() - 2, nick.length());
//                }
//                if (avatarImageView != null) {
//                    avatarImageView.setTextAndColor(nick, colorArr[nick.charAt(0) % 5]);
//                }
                GlideUtils.load(context, desAvatar, avatarImageView);
            } else {
                GlideUtils.load(context, desAvatar, avatarImageView);
            }
        } else {
            GlideUtils.load(context, desAvatar, avatarImageView);
        }
    }


    public static void setAvatarContent(Context context, String userNick, String avatar, AvatarImageView avatarImageView) {
        if (avatarImageView == null) {
            return;
        }
        int desAvatar = R.drawable.ease_default_avatar;
        if (!TextUtils.isEmpty(avatar)) {
            GlideUtils.load(context, avatar, desAvatar, avatarImageView);
        } else if (!TextUtils.isEmpty(userNick)) {
            String nick = userNick.trim();
            if (nick.length() >= 2) {
                nick = nick.substring(nick.length() - 2, nick.length());
            }
            avatarImageView.setTextAndColor(nick, colorArr[nick.charAt(0) % 5]);
        } else {
            avatarImageView.setImageResource(desAvatar);
        }
    }

}
