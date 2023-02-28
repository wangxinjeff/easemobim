package com.hyphenate.easemob.easeui.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.easemob.easeui.widget.listview.check.CheckListEntity;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MPGroupMemberEntity extends CheckListEntity implements Parcelable {

    private int id;
    private long createTime;
    private long lastUpdateTime;
    private int tenantId;
    private int userId;
    private int chatGroupId;
    private String type;
    private boolean mute;
    private long muteExpire;
    private boolean block;
    private int createUserId;
    private int lastUpdateUserId;
    private boolean disturb;
    private boolean contract;
    private boolean cluster;
    private int approve;
    private String nick;
    private String avatar;
    private String imUsername;
    private MPUserEntity userEntity;

    public MPGroupMemberEntity(){

    }

    protected MPGroupMemberEntity(Parcel in) {
        id = in.readInt();
        createTime = in.readLong();
        lastUpdateTime = in.readLong();
        tenantId = in.readInt();
        userId = in.readInt();
        chatGroupId = in.readInt();
        type = in.readString();
        mute = in.readByte() != 0;
        muteExpire = in.readLong();
        block = in.readByte() != 0;
        createUserId = in.readInt();
        lastUpdateUserId = in.readInt();
        disturb = in.readByte() != 0;
        contract = in.readByte() != 0;
        cluster = in.readByte() != 0;
        approve = in.readInt();
        nick = in.readString();
        avatar = in.readString();
        imUsername = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(createTime);
        dest.writeLong(lastUpdateTime);
        dest.writeInt(tenantId);
        dest.writeInt(userId);
        dest.writeInt(chatGroupId);
        dest.writeString(type);
        dest.writeByte((byte) (mute ? 1 : 0));
        dest.writeLong(muteExpire);
        dest.writeByte((byte) (block ? 1 : 0));
        dest.writeInt(createUserId);
        dest.writeInt(lastUpdateUserId);
        dest.writeByte((byte) (disturb ? 1 : 0));
        dest.writeByte((byte) (contract ? 1 : 0));
        dest.writeByte((byte) (cluster ? 1 : 0));
        dest.writeInt(approve);
        dest.writeString(nick);
        dest.writeString(avatar);
        dest.writeString(imUsername);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MPGroupMemberEntity> CREATOR = new Creator<MPGroupMemberEntity>() {
        @Override
        public MPGroupMemberEntity createFromParcel(Parcel in) {
            return new MPGroupMemberEntity(in);
        }

        @Override
        public MPGroupMemberEntity[] newArray(int size) {
            return new MPGroupMemberEntity[size];
        }
    };

    public static MPGroupMemberEntity create(JSONObject jsonEntity, boolean isRegion) {
        if (jsonEntity == null) {
            return null;
        }
        MPGroupMemberEntity entity = new MPGroupMemberEntity();
        entity.id = jsonEntity.optInt("id");
        entity.nick = jsonEntity.optString("realName");
        entity.createTime = jsonEntity.optLong("createTime");
        entity.lastUpdateTime = jsonEntity.optLong("lastUpdateTime");
        entity.tenantId = jsonEntity.optInt("tenantId");
        entity.userId = jsonEntity.optInt("userId");
        entity.chatGroupId = jsonEntity.optInt("chatGroupId");
        entity.type = jsonEntity.optString("type");
        entity.mute = jsonEntity.optBoolean("mute");
        entity.block = jsonEntity.optBoolean("block");
        entity.createUserId = jsonEntity.optInt("createUserId");
        entity.lastUpdateUserId = jsonEntity.optInt("lastUpdateUserId");
        entity.disturb = jsonEntity.optBoolean("disturb");
        entity.contract = jsonEntity.optBoolean("contract");
        entity.approve = jsonEntity.optInt("approve");
        entity.muteExpire = jsonEntity.optLong("muteExpire");
        entity.imUsername = jsonEntity.optString("imUsername");
        entity.cluster = jsonEntity.optBoolean("isRegion");
        if (!entity.cluster) {
            entity.cluster = isRegion;
        }
        JSONObject jsonUser = jsonEntity.optJSONObject("user");
        entity.userEntity = MPUserEntity.create(jsonUser);
        return entity;
    }

    public static List<MPGroupMemberEntity> create(JSONArray jsonArr, boolean isRegion) {
        List<MPGroupMemberEntity> memList = new ArrayList<>();
        if (jsonArr == null || jsonArr.length() == 0) {
            return memList;
        }
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonMem = jsonArr.optJSONObject(i);
            MPGroupMemberEntity entty = create(jsonMem, isRegion);
            entty.setCluster(isRegion);
            if (entty != null) {
                memList.add(entty);
            }
        }
        return memList;
    }

    public static List<MPGroupMemberEntity> create(JSONArray jsonArr) {
         return create(jsonArr, false);
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

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(int chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public int getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(int lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    public boolean isDisturb() {
        return disturb;
    }

    public void setDisturb(boolean disturb) {
        this.disturb = disturb;
    }

    public boolean isContract() {
        return contract;
    }

    public void setContract(boolean contract) {
        this.contract = contract;
    }

    public int getApprove() {
        return approve;
    }

    public void setApprove(int approve) {
        this.approve = approve;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getMuteExpire() {
        return muteExpire;
    }

    public void setMuteExpire(long muteExpire) {
        this.muteExpire = muteExpire;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public MPUserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(MPUserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getImUsername() {
        return imUsername;
    }

    public void setImUsername(String imUsername) {
        this.imUsername = imUsername;
    }
}
