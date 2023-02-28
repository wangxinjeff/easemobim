package com.hyphenate.easemob.im.mp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPUserEntity;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 24/10/2018
 */
public class LoginUser extends EaseUser implements Parcelable {

    private MPUserEntity entityBean;

    public LoginUser() {
    }

    protected LoginUser(Parcel in) {
        super(in);
        entityBean = in.readParcelable(MPUserEntity.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(entityBean, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LoginUser> CREATOR = new Creator<LoginUser>() {
        @Override
        public LoginUser createFromParcel(Parcel in) {
            return new LoginUser(in);
        }

        @Override
        public LoginUser[] newArray(int size) {
            return new LoginUser[size];
        }
    };

    public MPUserEntity getEntityBean() {
        return entityBean;
    }

    public void setEntityBean(MPUserEntity entityBean) {
        this.entityBean = entityBean;
    }
}
