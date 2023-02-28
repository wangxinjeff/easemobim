package com.hyphenate.easemob.easeui.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;

import org.json.JSONObject;

public class EaseUserUtils {

    static EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according userId
     *
     * @param userId
     * @return
     */
    public static EaseUser getUserInfo(int userId) {
        if (userProvider != null)
            return userProvider.getUser(userId);

        return null;
    }

    /**
     * get EaseUser according username
     *
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null)
            return userProvider.getUser(username);

        return null;
    }

//    public static List<EaseUser> getUserInfos(List<String> usernames){
//        if (userProvider != null){
//            return userProvider.getUsers(usernames);
//        }
//        return null;
//    }

    public static GroupBean getGroupInfo(String imGroupId) {
        if (userProvider != null)
            return userProvider.getGroupBean(imGroupId);
        return null;
    }

    public static GroupBean getGroupInfoById(int groupId) {
        if (userProvider != null)
            return userProvider.getGroupBeanById(groupId);
        return null;
    }

    /**
     * set user avatar
     *
     * @param userId
     */
//    public static void setUserAvatar(Context context, int userId, ImageView imageView) {
//        EaseUser user = getUserInfo(userId);
//        if (user != null && user.getAvatar() != null) {
//            String remoteUrl = user.getAvatar();
////            Glide.with(context).load(remoteUrl).apply(RequestOptions.placeholderOf(R.drawable.ease_default_avatar)).into(imageView);
//            GlideUtils.load(context, remoteUrl, R.drawable.ease_default_avatar, imageView);
//        } else {
////            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
//            GlideUtils.load(context, R.drawable.ease_default_image, imageView);
//        }
//    }

    /**
     * set user's nickname
     */
    public static void setUserNick(int userId, TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(userId);
            if (user != null) {
                if (user.getNick() != null) {
                    textView.setText(user.getNick());
                } else {
                    textView.setText(user.getUsername());
                }
            } else {
                textView.setText(String.valueOf(userId));
            }
        }
    }

    /**
     * set user avatar
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        setUserAvatar(context, username, imageView, R.drawable.boy1);
    }

    /**
     * set user avatar
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView, int res) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            String remoteUrl = user.getAvatar();
            GlideUtils.load(context, remoteUrl, res, imageView);
        } else {
            if (imageView != null) {
                GlideUtils.load(context, res, imageView);
            }
        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (!TextUtils.isEmpty(user.getAlias())) {
                textView.setText(user.getAlias());
            } else if (user.getNick() != null) {
                textView.setText(user.getNick());
            } else {
                textView.setText(username);
            }
        }
    }

    public static void setUserNick(String username, TextView textView, Context context) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (!TextUtils.isEmpty(user.getAlias())) {
                textView.setText(user.getAlias());
            } else if (user.getNick() != null && user.getNickname().length() != 40) {
                textView.setText(user.getNick());
            } else {
                getNickNamefromServer(username, textView, context);
            }
        }
    }

    private static void getNickNamefromServer(final String imUserName, final TextView textView, final Context context) {
        EMAPIManager.getInstance().getUserByImUser(imUserName, new EMDataCallBack<String>() {
            @Override
            public void onSuccess(final String value) {
                try {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                    final MPUserEntity userEntity = MPUserEntity.create(jsonEntity);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(userEntity != null) {
                                textView.setText(userEntity.getRealName());
                            } else {
                                MPLog.e("EaseUserUtils", "getNickNamefromServer-value:" + value);
                                try{
                                    throw new Exception();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                textView.setText(imUserName);
                            }

                        }
                    });
                } catch (Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(imUserName);
                        }
                    });
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(imUserName);
                    }
                });
            }
        });
    }

}
