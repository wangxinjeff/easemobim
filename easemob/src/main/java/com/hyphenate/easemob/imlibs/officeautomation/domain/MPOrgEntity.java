package com.hyphenate.easemob.imlibs.officeautomation.domain;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MPOrgEntity implements Parcelable {

    private int id;
    private long createTime;
    private long lastUpdateTime;
    private int tenantId;
    private int companyId;
    private int parentId;
    private String name;
    private String code;
    private String rank;
    private String remark;
    private int createUserId;
    private int lastUpdateUserId;
    private String fullName;
    private String fullId;
    private String companyName;
    private String position;
    private String userType;
    private int depth;
    private int memberCount;

    private int pickStatus;

    public int getPickStatus() {
        return pickStatus;
    }

    public void setPickStatus(int pickStatus) {
        this.pickStatus = pickStatus;
    }

    public MPOrgEntity() {

    }

    protected MPOrgEntity(Parcel in) {
        id = in.readInt();
        createTime = in.readLong();
        lastUpdateTime = in.readLong();
        tenantId = in.readInt();
        companyId = in.readInt();
        parentId = in.readInt();
        name = in.readString();
        code = in.readString();
        rank = in.readString();
        remark = in.readString();
        createUserId = in.readInt();
        lastUpdateUserId = in.readInt();
        fullName = in.readString();
        fullId = in.readString();
        companyName = in.readString();
        position = in.readString();
        userType = in.readString();
        depth = in.readInt();
        memberCount = in.readInt();
    }

    public static final Creator<MPOrgEntity> CREATOR = new Creator<MPOrgEntity>() {
        @Override
        public MPOrgEntity createFromParcel(Parcel in) {
            return new MPOrgEntity(in);
        }

        @Override
        public MPOrgEntity[] newArray(int size) {
            return new MPOrgEntity[size];
        }
    };

    public static List<MPOrgEntity> create(JSONArray jsonArrOrg, JSONArray jsonArrCompany, JSONArray jsonArrRelationship) {
        if (jsonArrOrg == null || jsonArrCompany == null || jsonArrRelationship == null) {
            return null;
        }
        List<MPCompanyEntity> companyEntities = createCompany(jsonArrCompany);
        List<MPUserCompanyRS> userCompanyRSList = createUserCompanyRS(jsonArrRelationship);
        List<MPOrgEntity> orgEntities = new ArrayList<>();

        if (jsonArrOrg.length() > 0) {
            for (int i = 0; i < jsonArrOrg.length(); i++) {
                JSONObject jsonObj = jsonArrOrg.optJSONObject(i);
                if (jsonObj != null) {
                    MPOrgEntity orgEntity = MPOrgEntity.create(jsonObj);
                    if (orgEntity != null) {
                        orgEntity.companyName = getCompanyName(orgEntity.companyId, companyEntities);
                        MPUserCompanyRS userCompanyRS = getUserCompanyRS(orgEntity.id, userCompanyRSList);
                        if (userCompanyRS != null) {
                            orgEntity.position = userCompanyRS.title;
                            orgEntity.userType = userCompanyRS.type;
                        }
                    }
                    orgEntities.add(orgEntity);
                }
            }
        } else {
            for (int i = 0; i < jsonArrCompany.length(); i++) {
                JSONObject jsonObj = jsonArrCompany.optJSONObject(i);
                if (jsonObj != null) {
                    MPCompanyEntity companyEntity = createCompany(jsonObj);
                    MPOrgEntity orgEntity = new MPOrgEntity();
                    orgEntity.id = -1;
                    orgEntity.companyId = companyEntity.companyId;
                    orgEntity.companyName = companyEntity.companyName;
                    orgEntities.add(orgEntity);
                }
            }
        }
        return orgEntities;
    }

    private static String getCompanyName(int companyId, List<MPCompanyEntity> companyEntities) {
        if (companyEntities == null) {
            return null;
        }
        for (MPCompanyEntity entty : companyEntities) {
            if (entty.companyId == companyId) {
                return entty.companyName;
            }
        }
        return null;
    }


    private static MPCompanyEntity createCompany(JSONObject jsonCompany) {
        if (jsonCompany == null) {
            return null;
        }
        MPCompanyEntity companyEntity = new MPCompanyEntity();
        companyEntity.companyId = jsonCompany.optInt("id");
        companyEntity.companyName = jsonCompany.optString("name");
        return companyEntity;
    }

    private static List<MPCompanyEntity> createCompany(JSONArray jsonArrCompany) {
        List<MPCompanyEntity> companyEntities = new ArrayList<>();
        if (jsonArrCompany == null) {
            return companyEntities;
        }
        for (int i = 0; i < jsonArrCompany.length(); i++) {
            JSONObject jsonObj = jsonArrCompany.optJSONObject(i);
            MPCompanyEntity entty = createCompany(jsonObj);
            if (entty != null) {
                companyEntities.add(entty);
            }
        }
        return companyEntities;
    }

    private static MPUserCompanyRS getUserCompanyRS(int orgId, List<MPUserCompanyRS> userCompanyRSList) {
        if (userCompanyRSList == null || userCompanyRSList.isEmpty()) {
            return null;
        }
        for (MPUserCompanyRS item : userCompanyRSList) {
            if (item.organizationId == orgId) {
                return item;
            }
        }
        return null;
    }

    private static List<MPUserCompanyRS> createUserCompanyRS(JSONArray jsonArr) {
        List<MPUserCompanyRS> mpUserCompanyRSList = new ArrayList<>();
        if (jsonArr == null) {
            return mpUserCompanyRSList;
        }
        for (int i = 0; i < jsonArr.length(); i++) {
            MPUserCompanyRS mpUserCompanyRS = createUserCompanyRS(jsonArr.optJSONObject(i));
            if (mpUserCompanyRS != null) {
                mpUserCompanyRSList.add(mpUserCompanyRS);
            }
        }
        return mpUserCompanyRSList;

    }

    private static MPUserCompanyRS createUserCompanyRS(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        MPUserCompanyRS userCompanyRS = new MPUserCompanyRS();
        userCompanyRS.id = jsonObj.optInt("id");
        userCompanyRS.createTime = jsonObj.optLong("createTime");
        userCompanyRS.lastUpdateTime = jsonObj.optLong("lastUpdateTime");
        userCompanyRS.userId = jsonObj.optInt("userId");
        userCompanyRS.companyId = jsonObj.optInt("companyId");
        userCompanyRS.organizationId = jsonObj.optInt("organizationId");
        userCompanyRS.type = jsonObj.optString("type");
        userCompanyRS.title = jsonObj.optString("title");
        return userCompanyRS;
    }


    public static MPOrgEntity create(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }

        MPOrgEntity entty = new MPOrgEntity();
        entty.id = jsonObj.optInt("id");
        entty.createTime = jsonObj.optLong("createTime");
        entty.lastUpdateTime = jsonObj.optLong("lastUpdateTime");
        entty.tenantId = jsonObj.optInt("tenantId");
        entty.companyId = jsonObj.optInt("companyId");
        entty.parentId = jsonObj.optInt("parentId");
        entty.name = jsonObj.optString("name");
        entty.code = jsonObj.optString("code");
        entty.rank = jsonObj.optString("rank");
        entty.remark = jsonObj.optString("remark");
        entty.createUserId = jsonObj.optInt("createUserId");
        entty.lastUpdateUserId = jsonObj.optInt("lastUpdateUserId");
        entty.fullName = jsonObj.optString("fullName");
        entty.fullId = jsonObj.optString("fullId");
        entty.depth = jsonObj.optInt("depth");
        entty.memberCount = jsonObj.optInt("memberCount");
        return entty;
    }

    public static List<MPOrgEntity> create(JSONArray jsonArr) {
        List<MPOrgEntity> orgEntities = new ArrayList<>();
        if (jsonArr == null) {
            return orgEntities;
        }
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.optJSONObject(i);
            MPOrgEntity entty = MPOrgEntity.create(jsonObj);
            if (entty != null) {
                orgEntities.add(entty);
            }
        }
        return orgEntities;
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

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullId() {
        return fullId;
    }

    public void setFullId(String fullId) {
        this.fullId = fullId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
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
        dest.writeInt(tenantId);
        dest.writeInt(companyId);
        dest.writeInt(parentId);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(rank);
        dest.writeString(remark);
        dest.writeInt(createUserId);
        dest.writeInt(lastUpdateUserId);
        dest.writeString(fullName);
        dest.writeString(fullId);
        dest.writeString(companyName);
        dest.writeString(position);
        dest.writeString(userType);
        dest.writeInt(depth);
        dest.writeInt(memberCount);
    }

    private static class MPCompanyEntity {
        private int companyId;
        private String companyName;

        public int getCompanyId() {
            return companyId;
        }

        public void setCompanyId(int companyId) {
            this.companyId = companyId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }
    }

    private static class MPUserCompanyRS {
        private int id;
        private long createTime;
        private long lastUpdateTime;
        private int userId;
        private int companyId;
        private int organizationId;
        private String type;
        private String title;
    }
}
