package com.hyphenate.easemob.easeui.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MPGroupEntity implements Parcelable {
    private int id;
    private long createTime;
    private long lastUpdateTime;
    private String name;
    private String description;
    private String avatar;
    private int maxusers;
    private boolean newMemberCanreadHistory;
    private boolean allowInvites;
    private boolean membersOnly;
    private boolean isPublic;
    private String type;
    private int memberCount;
    private String groupNotice;
    private int createUserId;
    private int lastUpdateUserId;
    private String imChatGroupId;
    private boolean disturb;
    private boolean contract;
    private boolean cluster;
    private int ownerId;
    private boolean isNoticeChanged;


    private List<MPGroupMemberEntity> memberEntities;
    private Map<Integer, MPUserEntity> userMaps;

    public MPGroupEntity(){}

    protected MPGroupEntity(Parcel in) {
        id = in.readInt();
        createTime = in.readLong();
        lastUpdateTime = in.readLong();
        name = in.readString();
        description = in.readString();
        avatar = in.readString();
        maxusers = in.readInt();
        newMemberCanreadHistory = in.readByte() != 0;
        allowInvites = in.readByte() != 0;
        membersOnly = in.readByte() != 0;
        isPublic = in.readByte() != 0;
        type = in.readString();
        ownerId = in.readInt();
        memberCount = in.readInt();
        groupNotice = in.readString();
        createUserId = in.readInt();
        lastUpdateUserId = in.readInt();
        imChatGroupId = in.readString();
        disturb = in.readByte() != 0;
        contract = in.readByte() != 0;
        cluster = in.readByte() != 0;
        memberEntities = in.createTypedArrayList(MPGroupMemberEntity.CREATOR);
    }

    public static final Creator<MPGroupEntity> CREATOR = new Creator<MPGroupEntity>() {
        @Override
        public MPGroupEntity createFromParcel(Parcel in) {
            return new MPGroupEntity(in);
        }

        @Override
        public MPGroupEntity[] newArray(int size) {
            return new MPGroupEntity[size];
        }
    };

    public static List<MPGroupEntity> create(JSONArray jsonArr) {
        List<MPGroupEntity> groupEntities = new ArrayList<>();
        if (jsonArr == null || jsonArr.length() == 0) {
            return groupEntities;
        }
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.optJSONObject(i);
            MPGroupEntity groupEntity = create(jsonObj);
            if (groupEntity != null) {
                groupEntities.add(groupEntity);
            }
        }
        return groupEntities;
    }

    public static MPGroupEntity create(JSONObject jsonEntity) {
        if (jsonEntity == null) {
            return null;
        }
        MPGroupEntity entty = new MPGroupEntity();
        entty.id = jsonEntity.optInt("id");
        entty.createTime = jsonEntity.optLong("createTime");
        entty.lastUpdateTime = jsonEntity.optLong("lastUpdateTime");
        entty.name = jsonEntity.optString("name");
        entty.description = jsonEntity.optString("description");
        entty.avatar = jsonEntity.optString("avatar");
        entty.maxusers = jsonEntity.optInt("maxusers");
        entty.newMemberCanreadHistory = jsonEntity.optBoolean("newMemberCanreadHistory");
        entty.allowInvites = jsonEntity.optBoolean("allowInvites");
        entty.membersOnly = jsonEntity.optBoolean("membersOnly");
        entty.isPublic = jsonEntity.optBoolean("isPublic");
        entty.type = jsonEntity.optString("type");
        entty.memberCount = jsonEntity.optInt("memberCount");
        entty.groupNotice = jsonEntity.optString("groupNotice");
        entty.createUserId = jsonEntity.optInt("createUserId");
        entty.ownerId = jsonEntity.optInt("ownerId");
        entty.lastUpdateUserId = jsonEntity.optInt("lastUpdateUserId");
        entty.imChatGroupId = jsonEntity.optString("imChatGroupId");
        JSONObject jsonImGroup = jsonEntity.optJSONObject("imChatGroup");
        if (jsonImGroup != null) {
            entty.imChatGroupId = jsonImGroup.optString("imChatGroupId");
        }
        entty.disturb = jsonEntity.optBoolean("disturb");
        entty.contract = jsonEntity.optBoolean("contract");
        entty.cluster = jsonEntity.optBoolean("isRegionChatGroup");
        if (!entty.cluster) {
            entty.cluster = jsonEntity.optBoolean("isRegion", false);
        }
        entty.isNoticeChanged = jsonEntity.optBoolean("isNoticeChanged");
        return entty;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getMaxusers() {
        return maxusers;
    }

    public void setMaxusers(int maxusers) {
        this.maxusers = maxusers;
    }

    public boolean isNewMemberCanreadHistory() {
        return newMemberCanreadHistory;
    }

    public void setNewMemberCanreadHistory(boolean newMemberCanreadHistory) {
        this.newMemberCanreadHistory = newMemberCanreadHistory;
    }

    public boolean isAllowInvites() {
        return allowInvites;
    }

    public void setAllowInvites(boolean allowInvites) {
        this.allowInvites = allowInvites;
    }

    public boolean isMembersOnly() {
        return membersOnly;
    }

    public void setMembersOnly(boolean membersOnly) {
        this.membersOnly = membersOnly;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupNotice() {
        return groupNotice;
    }

    public void setGroupNotice(String groupNotice) {
        this.groupNotice = groupNotice;
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

    public String getImChatGroupId() {
        return imChatGroupId;
    }

    public void setImChatGroupId(String imChatGroupId) {
        this.imChatGroupId = imChatGroupId;
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

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isNoticeChanged() {
        return isNoticeChanged;
    }

    public void setNoticeChanged(boolean noticeChanged) {
        isNoticeChanged = noticeChanged;
    }

    public List<MPGroupMemberEntity> getMemberEntities() {
        return memberEntities;
    }

    public void setMemberEntities(List<MPGroupMemberEntity> memberEntities) {
        this.memberEntities = memberEntities;
        sortMemberList(memberEntities);
    }

    private void sortMemberList(List<MPGroupMemberEntity> memberEntities){
        if (memberEntities == null) {
            return;
        }
        Collections.sort(memberEntities, new Comparator<MPGroupMemberEntity>() {
            @Override
            public int compare(MPGroupMemberEntity l, MPGroupMemberEntity r) {
                if (l.getType() != null && l.getType().equals("owner")) {
                    return -1;
                }
                if (r.getType() != null && r.getType().equals("owner")) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public Map<Integer, MPUserEntity> getUserMaps() {
        return userMaps;
    }

    public void setUserMaps(Map<Integer, MPUserEntity> userMaps) {
        this.userMaps = userMaps;
    }

    public MPUserEntity getUserEntity(int memberId) {
        if (userMaps != null && userMaps.size() > 0) {
            return userMaps.get(memberId);
        }
        return null;
    }

    public void setUserMaps(List<MPUserEntity> userMaps) {
        Map<Integer, MPUserEntity> tempUserMaps = new HashMap<>();
        for (MPUserEntity item : userMaps) {
            tempUserMaps.put(item.getId(), item);
        }
        setUserMaps(tempUserMaps);
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
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(avatar);
        dest.writeInt(maxusers);
        dest.writeByte((byte) (newMemberCanreadHistory ? 1 : 0));
        dest.writeByte((byte) (allowInvites ? 1 : 0));
        dest.writeByte((byte) (membersOnly ? 1 : 0));
        dest.writeByte((byte) (isPublic ? 1 : 0));
        dest.writeString(type);
        dest.writeInt(ownerId);
        dest.writeInt(memberCount);
        dest.writeString(groupNotice);
        dest.writeInt(createUserId);
        dest.writeInt(lastUpdateUserId);
        dest.writeString(imChatGroupId);
        dest.writeByte((byte) (disturb ? 1 : 0));
        dest.writeByte((byte) (contract ? 1 : 0));
        dest.writeByte((byte) (cluster ? 1 : 0));
        dest.writeTypedList(memberEntities);
    }
}
