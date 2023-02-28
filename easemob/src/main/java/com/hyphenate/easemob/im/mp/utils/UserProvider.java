package com.hyphenate.easemob.im.mp.utils;

import android.text.TextUtils;

import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.im.mp.entity.LoginUser;
import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;
import com.hyphenate.easemob.im.officeautomation.db.AppDBManager;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 24/10/2018
 * 缓存常用用户数据，避免频繁的查询数据库
 */
public final class UserProvider {

    private static final String TAG = "UserProvider";

    // 乐加载
    private static UserProvider instance = new UserProvider();

    private int cacheSize = 20; // 20 存储常用的200个用户信息

    /**
     * 隐藏实例化对象
     */
    private UserProvider() {
    }

    /**
     * 获取用户提供者实例
     *
     * @return
     */
    public static UserProvider getInstance() {
        return instance;
    }

    /**
     * 当前登录的用户信息
     */
    private LoginUser mLoginUser = null;

    private BasicLRUCache<String, EaseUser> cacheEaseUsers = new BasicLRUCache<>(cacheSize);
    private BasicLRUCache<Integer, String> cacheUserIds = new BasicLRUCache<>(cacheSize * 2);

    public EaseUser getEaseUser(String username) {
        if (cacheEaseUsers.containsKey(username)) {
            return cacheEaseUsers.get(username);
        }
        EaseUser mpUser = AppDBManager.getInstance().getUserByEasemobName(username);
        if (mpUser != null) {
            cacheEaseUsers.put(mpUser.getUsername(), mpUser);
        } else {
            EMAPIManager.getInstance().getUserByImUser(username, new EMDataCallBack<String>() {
                @Override
                public void onSuccess(String value) {
                    try {
                        JSONObject jsonObj = new JSONObject(value);
                        JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                        MPUserEntity userEntity = MPUserEntity.create(jsonEntity);
                        if (userEntity != null) {
                            AppHelper.getInstance().getModel().saveUserInfo(userEntity);
                        }
                        cacheEaseUsers.removeKey(username);
                    } catch (Exception e) {
                        MPLog.e(TAG, "getUserByImUser "  + "username：" + username + "，error:"+ MPLog.getStackTraceString(e));
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int error, String errorMsg) {
                    MPLog.e(TAG, "getUserByImUser-onError:" + errorMsg);
                }
            });
        }
        return mpUser;
    }

    public void updateEaseUser(EaseUser easeUser) {
        cacheEaseUsers.put(easeUser.getUsername(), easeUser);
    }

    public void removeEaseUser(String imUsername) {
        if (imUsername == null) {
            return;
        }
        if (cacheEaseUsers.containsKey(imUsername)) {
            cacheEaseUsers.removeKey(imUsername);
        }
    }

    public EaseUser getEaseUserById(int userId) {
        if (cacheUserIds.containsKey(userId)) {
            return getEaseUser(cacheUserIds.get(userId));
        }
        EaseUser mpUser = AppDBManager.getInstance().getUserExtInfo(userId);
        if (mpUser != null) {
            cacheEaseUsers.put(mpUser.getUsername(), mpUser);
            cacheUserIds.put(mpUser.getId(), mpUser.getUsername());
        } else {
            EMAPIManager.getInstance().getUserInfo(userId, new EMDataCallBack<String>() {
                @Override
                public void onSuccess(String value) {
                    try {
                        JSONObject jsonObj = new JSONObject(value);
                        JSONObject jsonEntity = jsonObj.optJSONObject("entity");
                        MPUserEntity userEntity = MPUserEntity.create(jsonEntity);
                        if (userEntity != null) {
                            AppHelper.getInstance().getModel().saveUserInfo(userEntity);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MPLog.e(TAG, "getUserInfo error:" + MPLog.getStackTraceString(e));
                    }
                }

                @Override
                public void onError(int error, String errorMsg) {

                }
            });
        }
        return mpUser;
    }

    public LoginUser getLoginUser() {
        if (mLoginUser != null) {
            return mLoginUser;
        }
        String strUser = PrefsUtil.getInstance().getLoginUser();
        if (strUser != null) {
            return getLoginUserFromJson(strUser);
        }
        return null;
    }

    public void updateLoginUser(MPUserEntity userEntity) {
        if (userEntity == null) {
            return;
        }
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return;
        }

        MPUserEntity mpUserEntity = loginUser.getEntityBean();
        if (mpUserEntity == null) {
            return;
        }
        mpUserEntity.setAvatar(userEntity.getAvatar());
        mpUserEntity.setTelephone(userEntity.getTelephone());
        mpUserEntity.setPhone(userEntity.getPhone());
        mpUserEntity.setEmail(userEntity.getEmail());

        loginUser.setAvatar(userEntity.getAvatar());
        loginUser.setTelephone(userEntity.getTelephone());
        loginUser.setMobilePhone(userEntity.getPhone());
        loginUser.setEmail(userEntity.getEmail());

        String jsonEnttyStr = PrefsUtil.getInstance().getLoginUser();
        if (TextUtils.isEmpty(jsonEnttyStr)) {
            return;
        }
        try {
            JSONObject jsonEntity = new JSONObject(jsonEnttyStr);
            JSONObject jsonUserObj = jsonEntity.optJSONObject("user");
            if (jsonUserObj != null) {
                jsonUserObj.put("avatar", userEntity.getAvatar());
                jsonUserObj.put("phone", userEntity.getPhone());
                jsonUserObj.put("telephone", userEntity.getTelephone());
                jsonUserObj.put("email", userEntity.getEmail());
            }
            PrefsUtil.getInstance().setLoginUser(jsonEntity.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LoginUser getLoginUserFromJson(String jsonAll) {
        if (TextUtils.isEmpty(jsonAll)) {
            return null;
        }
        try {
            JSONObject jsonEntity = new JSONObject(jsonAll);
            JSONObject jsonUserObj = jsonEntity.optJSONObject("user");
            JSONArray jsonArrCompany = jsonEntity.optJSONArray("companyList");
            JSONArray jsonArrOrg = jsonEntity.optJSONArray("organizationList");
            JSONArray jsonArrUserCompany = jsonEntity.optJSONArray("userCompanyRelationshipList");
            List<MPOrgEntity> orgEntities = MPOrgEntity.create(jsonArrOrg, jsonArrCompany, jsonArrUserCompany);
            MPUserEntity userEntity = MPUserEntity.create(jsonUserObj);
            userEntity.setOrgEntities(orgEntities);
            if (mLoginUser == null)
                mLoginUser = new LoginUser();

            mLoginUser.setEntityBean(userEntity);
            mLoginUser.setAvatar(userEntity.getAvatar());
            mLoginUser.setNickname(userEntity.getRealName());
            mLoginUser.setEmail(userEntity.getEmail());
            mLoginUser.setMobilePhone(userEntity.getPhone());
            mLoginUser.setTelephone(userEntity.getTelephone());
            mLoginUser.setAlias(!TextUtils.isEmpty(userEntity.getAlias()) ? userEntity.getAlias() : userEntity.getRealName());
            int orgId = -1;
            if (userEntity.getOrgEntities() != null && !userEntity.getOrgEntities().isEmpty()) {
                orgId = userEntity.getOrgEntities().get(0).getId();
            }
            mLoginUser.setOrganizationId(orgId);
            mLoginUser.setUsername(userEntity.getImUserId());
            mLoginUser.setId(userEntity.getId());
            MPUserEntity entity = MPUserEntity.create(jsonUserObj);
            AppHelper.getInstance().getModel().saveUserInfo(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清理缓存数据
     */
    public void clear() {
        mLoginUser = null;
        cacheEaseUsers.clear();
    }


    static class BasicLRUCache<K, V> {
        private LinkedHashMap<K, V> map;
        private int cacheSize;

        static final int DEFAULT_CACHE_SIZE = 2;

        public BasicLRUCache() {
            this(DEFAULT_CACHE_SIZE);
        }

        public BasicLRUCache(int cacheSize) {
            this.cacheSize = cacheSize;
            int hashTableSize = (int) Math.ceil(cacheSize / 0.75f) + 1;
            map = new LinkedHashMap<K, V>(hashTableSize, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Entry<K, V> eldest) {
                    System.out.println("size=" + size() + " cacheSize=" + BasicLRUCache.this.cacheSize);
                    return size() > BasicLRUCache.this.cacheSize;
                }
            };
        }

        public V put(K key, V value) {
            return map.put(key, value);
        }

        public V get(Object key) {
            return map.get(key);
        }

        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        public V removeKey(Object key) {
            return map.remove(key);
        }

        public void clear() {
            if (map != null && !map.isEmpty()) {
                map.clear();
            }
        }
    }


}
