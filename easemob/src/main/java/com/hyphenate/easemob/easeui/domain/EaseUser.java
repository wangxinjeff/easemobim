/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easemob.easeui.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.chat.EMContact;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;

public class EaseUser extends EMContact implements Parcelable {

    protected int id;
    protected String username;
    protected String nick;
    /**
     * initial letter for nickname
     */
    protected String initialLetter;
    protected String avatar;
    protected String email;
    protected String mobilePhone;
    protected String telephone;
    protected int organizationId;
    protected int tenantId;
    protected String pinyin;
    protected String userType;

    protected String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public EaseUser() {
    }


    public static final Creator<EaseUser> CREATOR = new Creator<EaseUser>() {
        @Override
        public EaseUser createFromParcel(Parcel in) {
            return new EaseUser(in);
        }

        @Override
        public EaseUser[] newArray(int size) {
            return new EaseUser[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return getId();
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInitialLetter() {
        if (initialLetter == null) {
            EaseCommonUtils.setUserInitialLetter(this);
        }
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getPhone() {
        return getMobilePhone();
    }

    public void setPhone(String phone) {
        this.setMobilePhone(phone);
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEasemobName(String username) {
        this.setUsername(username);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getNickname() {
        return this.nick == null ? this.getUsername() : this.nick;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Deprecated
    public String getNick() {
        if (nick == null) {
            nick = getUsername();
        }
        return nick;
    }

    @Override
    public void setNickname(String s) {
        this.nick = s;
    }

    @Deprecated
    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(nick);
        dest.writeInt(id);
        dest.writeString(avatar);
        dest.writeString(email);
        dest.writeString(mobilePhone);
        dest.writeString(telephone);
        dest.writeInt(organizationId);
        dest.writeInt(tenantId);
        dest.writeString(pinyin);
        dest.writeString(userType);
    }

    protected EaseUser(Parcel in) {
        username = in.readString();
        nick = in.readString();
        id = in.readInt();
        avatar = in.readString();
        email = in.readString();
        mobilePhone = in.readString();
        telephone = in.readString();
        organizationId = in.readInt();
        tenantId = in.readInt();
        pinyin = in.readString();
        userType = in.readString();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof EaseUser)) {
            return false;
        }
        return getId() == ((EaseUser) other).getId();
    }

    @Override
    public String toString() {
        return "EaseUser{" +
                "id=" + id +
                ", initialLetter='" + initialLetter + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", organizationId=" + organizationId +
                ", tenantId=" + tenantId +
                '}';
    }
}
