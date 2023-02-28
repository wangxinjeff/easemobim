package com.hyphenate.easemob.im.officeautomation.domain;

/**
 * 合并转发userType
 */
public class ExtUserType {
    public int userid;
    public String nick;
    public String avatar;

    public ExtUserType(int userid, String nick, String avatar) {
        this.userid = userid;
        this.nick = nick;
        this.avatar = avatar;
    }
}
