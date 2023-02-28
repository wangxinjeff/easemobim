package com.hyphenate.easemob.imlibs.officeautomation.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MPUserEntity implements Parcelable {
    private int id;
    private long createTime;
    private long lastUpdateTime;
    private String username;
    private String phone;
    private String email;
    private String realName;
    private String avatar;
    private String gender;
    private String pinyin;
    private int createUserId;
    private int lastUpdateUserId;
    private String imUserId;
    private boolean disturb;
    private boolean star;
    private boolean superLevel;
    private int orgId;
    private String alias;
    private boolean active = true;
    private List<MPOrgEntity> orgEntities;
    private String telephone;


    //for friend notify
    //1待接受，2已同意
    private int status;
    private int userId;
    private int friendId;

    private int pickStatus;//0未选中，1暂时选中的，2已存在
    private boolean pickAll;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPickStatus() {
        return pickStatus;
    }

    public void setPickStatus(int pickStatus) {
        this.pickStatus = pickStatus;
    }

    public boolean isPickAll() {
        return pickAll;
    }

    public void setPickAll(boolean pickAll) {
        this.pickAll = pickAll;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public MPUserEntity() {
    }

    protected MPUserEntity(Parcel in) {
        id = in.readInt();
        createTime = in.readLong();
        lastUpdateTime = in.readLong();
        username = in.readString();
        phone = in.readString();
        telephone = in.readString();
        email = in.readString();
        realName = in.readString();
        avatar = in.readString();
        gender = in.readString();
        pinyin = in.readString();
        createUserId = in.readInt();
        lastUpdateUserId = in.readInt();
        imUserId = in.readString();
        disturb = in.readByte() != 0;
        star = in.readByte() != 0;
        superLevel = in.readByte() != 0;
        orgId = in.readInt();
        alias = in.readString();
        active = in.readByte() != 0;
        orgEntities = in.createTypedArrayList(MPOrgEntity.CREATOR);
    }

    public static final Creator<MPUserEntity> CREATOR = new Creator<MPUserEntity>() {
        @Override
        public MPUserEntity createFromParcel(Parcel in) {
            return new MPUserEntity(in);
        }

        @Override
        public MPUserEntity[] newArray(int size) {
            return new MPUserEntity[size];
        }
    };

    public static MPUserEntity createWithFriend(JSONObject jsonEntity) {
        JSONObject jsonUser = jsonEntity.optJSONObject("user");
        MPUserEntity userEntity = create(jsonUser);
        if (userEntity == null) {
            return null;
        }
        userEntity.setFriendId(jsonEntity.optInt("friendId"));
        userEntity.setStatus(jsonEntity.optInt("status"));
        userEntity.setUserId(jsonEntity.optInt("userId"));
        return userEntity;
    }

    public static MPUserEntity create(JSONObject jsonEntity) {
        if (jsonEntity == null) {
            return null;
        }

        MPUserEntity entity = new MPUserEntity();

        entity.id = jsonEntity.optInt("id");
        entity.createTime = jsonEntity.optLong("createTime");
        entity.lastUpdateTime = jsonEntity.optLong("lastUpdateTime");
        entity.username = jsonEntity.optString("username");
        entity.phone = jsonEntity.optString("phone");
        entity.telephone = jsonEntity.optString("telephone");
        entity.email = jsonEntity.optString("email");
        entity.realName = jsonEntity.optString("realName");
        entity.avatar = jsonEntity.optString("avatar");
        entity.gender = jsonEntity.optString("gender");
        entity.pinyin = jsonEntity.optString("pinyin");
        entity.createUserId = jsonEntity.optInt("createUserId");
        entity.lastUpdateUserId = jsonEntity.optInt("lastUpdateUserId");
        JSONObject jsonImUser = jsonEntity.optJSONObject("imUser");
        if (jsonImUser != null) {
            entity.imUserId = jsonImUser.optString("imUsername");
        } else {
            entity.imUserId = jsonEntity.optString("imUsername");
        }
        entity.disturb = jsonEntity.optBoolean("disturb");
        entity.star = jsonEntity.optBoolean("star");
        entity.superLevel = jsonEntity.optBoolean("superLevel");
        entity.orgId = jsonEntity.optInt("orgId");
        if (!jsonEntity.isNull("alias")) {
            entity.alias = jsonEntity.optString("alias");
        }
        entity.active = jsonEntity.optBoolean("active");
        return entity;
    }


    public static List<MPUserEntity> create(JSONArray jsonArr) {
        List<MPUserEntity> realList = new ArrayList<>();
        if (jsonArr == null) {
            return realList;
        }
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonEntity = jsonArr.optJSONObject(i);
            MPUserEntity entity = MPUserEntity.create(jsonEntity);
            if (entity != null) {
                realList.add(entity);
            }
        }
        return realList;
    }

    public static List<MPUserEntity> createWithFriend(JSONArray jsonArr) {
        List<MPUserEntity> realList = new ArrayList<>();
        if (jsonArr == null) {
            return realList;
        }
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonEntity = jsonArr.optJSONObject(i);
            MPUserEntity entity = MPUserEntity.createWithFriend(jsonEntity);
            if (entity != null) {
                realList.add(entity);
            }
        }
        return realList;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public String getImUserId() {
        return imUserId;
    }

    public void setImUserId(String imUserId) {
        this.imUserId = imUserId;
    }

    public boolean isDisturb() {
        return disturb;
    }

    public void setDisturb(boolean disturb) {
        this.disturb = disturb;
    }

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSuperLevel() {
        return superLevel;
    }

    public void setSuperLevel(boolean superLevel) {
        this.superLevel = superLevel;
    }

    public int getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(int lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    public List<MPOrgEntity> getOrgEntities() {
        return orgEntities;
    }

    public void setOrgEntities(List<MPOrgEntity> orgEntities) {
        this.orgEntities = orgEntities;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(createTime);
        dest.writeLong(lastUpdateTime);
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(telephone);
        dest.writeString(email);
        dest.writeString(realName);
        dest.writeString(avatar);
        dest.writeString(gender);
        dest.writeString(pinyin);
        dest.writeInt(createUserId);
        dest.writeInt(lastUpdateUserId);
        dest.writeString(imUserId);
        dest.writeByte((byte) (disturb ? 1 : 0));
        dest.writeByte((byte) (star ? 1 : 0));
        dest.writeByte((byte) (superLevel ? 1 : 0));
        dest.writeInt(orgId);
        dest.writeString(alias);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeTypedList(orgEntities);
    }
}
