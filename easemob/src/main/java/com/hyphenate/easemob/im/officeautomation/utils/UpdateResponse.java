package com.hyphenate.easemob.im.officeautomation.utils;

import android.content.Context;
import android.text.TextUtils;

import com.hyphenate.easemob.R;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 *
 */
public class UpdateResponse implements Serializable {

    /**
     * status : OK
     * errorCode : 0
     * responseDate : 1565794419194
     * entity : {"id":1,"appUrl":"/appurl/windows1","updateLog":"啊啊啊啊啊啊啊啊啊","effectiveTime":1565784000000,"current":true,"published":true,"tenantId":1,"targetSize":1048576,"versionName":"1.1.1","lastUpdateUserId":1,"createTime":1565777583000,"platform":1,"versionCode":1,"force":false,"createUserId":1,"lastUpdateTime":1565777583000,"md5":"aaabbbccc"}
     */

    private String status;
    private int errorCode;
    private long responseDate;
    private UpdateEntity entity;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public long getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(long responseDate) {
        this.responseDate = responseDate;
    }

    public UpdateEntity getEntity() {
        return entity;
    }

    public void setEntity(UpdateEntity entity) {
        this.entity = entity;
    }


    public static class UpdateEntity {
        /**
         * id : 1
         * appUrl : /appurl/windows1
         * updateLog : 啊啊啊啊啊啊啊啊啊
         * effectiveTime : 1565784000000
         * current : true
         * published : true
         * tenantId : 1
         * targetSize : 1048576
         * versionName : 1.1.1
         * lastUpdateUserId : 1
         * createTime : 1565777583000
         * platform : 1
         * versionCode : 1
         * force : false
         * createUserId : 1
         * lastUpdateTime : 1565777583000
         * md5 : aaabbbccc
         */

        private int id;
        private String appUrl;
        private String updateLog;
        private long effectiveTime;
        private boolean current;
        private boolean published;
        private int tenantId;
        private long targetSize;
        private String versionName;
        private int lastUpdateUserId;
        private long createTime;
        private int platform;
        private int versionCode;
        private boolean force;
        private int createUserId;
        private long lastUpdateTime;
        private String md5;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAppUrl() {
            return appUrl;
        }

        public void setAppUrl(String appUrl) {
            this.appUrl = appUrl;
        }

        public String getUpdateLog() {
            return updateLog;
        }

        public void setUpdateLog(String updateLog) {
            this.updateLog = updateLog;
        }

        public long getEffectiveTime() {
            return effectiveTime;
        }

        public void setEffectiveTime(long effectiveTime) {
            this.effectiveTime = effectiveTime;
        }

        public boolean isCurrent() {
            return current;
        }

        public void setCurrent(boolean current) {
            this.current = current;
        }

        public boolean isPublished() {
            return published;
        }

        public void setPublished(boolean published) {
            this.published = published;
        }

        public int getTenantId() {
            return tenantId;
        }

        public void setTenantId(int tenantId) {
            this.tenantId = tenantId;
        }

        public long getTargetSize() {
            return targetSize;
        }

        public void setTargetSize(long targetSize) {
            this.targetSize = targetSize;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getLastUpdateUserId() {
            return lastUpdateUserId;
        }

        public void setLastUpdateUserId(int lastUpdateUserId) {
            this.lastUpdateUserId = lastUpdateUserId;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getPlatform() {
            return platform;
        }

        public void setPlatform(int platform) {
            this.platform = platform;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public boolean isForce() {
            return force;
        }

        public void setForce(boolean force) {
            this.force = force;
        }

        public int getCreateUserId() {
            return createUserId;
        }

        public void setCreateUserId(int createUserId) {
            this.createUserId = createUserId;
        }

        public long getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(long lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }

        public String getMd5() {
            if (TextUtils.isEmpty(md5)) {
                return "";
            }
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }


        public String getUpdateString(Context context, boolean fileIsDownloaded) {
            String newVersionStr = context.getString(R.string.UMNewVersion);
            String targetSizeStr = context.getString(R.string.UMTargetSize);
            String var5 = context.getString(R.string.UMUpdateSize);
            String updateContentStr = context.getString(R.string.UMUpdateContent);
            String dialogInstallAPKStr = context.getString(R.string.UMDialog_InstallAPK);
            if (fileIsDownloaded) {
                return String.format("%s %s\n%s\n\n%s\n%s\n", newVersionStr, this.versionName, dialogInstallAPKStr, updateContentStr, this.updateLog);
            }
//            else if (this.delta && this.deltaVersion == CheckVersion.getInstance().getVersionCode(context)) {
//                return String.format("%s %s\n%s %s\n\n%s\n%s\n", newVersionStr, this.versionName, var5, formatSize(this.deltaSize), updateContentStr, this.updateLog);
//            }
            else {
                return String.format("%s %s\n%s %s\n\n%s\n%s\n", newVersionStr, this.versionName, targetSizeStr, formatSize(this.targetSize), updateContentStr, this.updateLog);
            }
        }

        public static String formatSize(long targetSize) {
            String formatedString;
            long longSize;
            try {
                longSize = targetSize;
            } catch (NumberFormatException e) {
                return String.valueOf(targetSize);
            }

            if (longSize < 1024L) {
                formatedString = (int) longSize + "B";
            } else {
                DecimalFormat decimalFormat;
                if (longSize < 1048576L) {
                    decimalFormat = new DecimalFormat("#0.00");
                    formatedString = decimalFormat.format((double) ((float) longSize) / 1024.0D) + "K";
                } else if (longSize < 1073741824L) {
                    decimalFormat = new DecimalFormat("#0.00");
                    formatedString = decimalFormat.format((double) ((float) longSize) / 1048576.0D) + "M";
                } else {
                    decimalFormat = new DecimalFormat("#0.00");
                    formatedString = decimalFormat.format((double) ((float) longSize) / 1.073741824E9D) + "G";
                }
            }

            return formatedString;
        }

    }
}
