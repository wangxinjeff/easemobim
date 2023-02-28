package com.hyphenate.easemob.im.officeautomation.db;

import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织机构
 */
public class OrgDao {
    public static final String TABLE_NAME_ORG = "table_org_info";
    public static final String COLUMN_NAME_ORG_ID = "org_id";
    public static final String COLUMN_NAME_ORG_NAME = "org_name";
    public static final String COLUMN_NAME_ORG_PARENT_ID = "org_parent_id";
    public static final String COLUMN_NAME_ORG_COMPANY_ID = "org_company_id";
    public static final String COLUMN_NAME_ORG_TENANT_ID = "org_tenant_id";
    public static final String COLUMN_NAME_ORG_RANK = "org_rank";
    public static final String COLUMN_NAME_ORG_DEPTH = "org_depth";
    public static final String COLUMN_NAME_ORG_MEMBER_COUNT = "org_member_count";

    public static final String COLUMN_NAME_ORG_CODE = "org_code";
    public static final String COLUMN_NAME_ORG_REMARK = "org_remark";
    public static final String COLUMN_NAME_ORG_CREATE_USER_ID = "org_createUserId";
    public static final String COLUMN_NAME_ORG_LAST_UPDATE_USER_ID = "org_lastUpdateUserId";


    public void saveAllOrgsList(List<MPOrgEntity> entities) {
        if (AppDBManager.getInstance() == null){
            return;
        }
        AppDBManager.getInstance().saveAllOrgsList(entities);
    }
    public void saveOrgInfo(MPOrgEntity org) {
        if (AppDBManager.getInstance() == null){
            return;
        }
        AppDBManager.getInstance().saveOrgInfo(org);
    }

    public MPOrgEntity getOrgInfo(int orgId) {
        if (AppDBManager.getInstance() == null){
            return null;
        }
        return AppDBManager.getInstance().getOrgInfo(orgId);
    }

    public List<MPOrgEntity> getOrgsListByParent(int parentId) {
        if (AppDBManager.getInstance() == null){
            return new ArrayList<>();
        }
        return AppDBManager.getInstance().getOrgsListByParent(parentId);
    }

    public void delOrgInfo(int orgId) {
        if (AppDBManager.getInstance() == null){
            return;
        }
        AppDBManager.getInstance().delOrgInfo(orgId);
    }

    public boolean saveOrgsListByParentOrgId(List<MPOrgEntity> orgEntities) {
        return AppDBManager.getInstance() != null && AppDBManager.getInstance().saveOrgsListByParentOrgId(orgEntities);
    }

}
