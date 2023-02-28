package com.hyphenate.easemob.im.officeautomation.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.hyphenate.easemob.R;

/**
 * @author qby
 * @date 2018/7/5 20:29
 * 错误码
 */
public class ErrorCode {

    //    用户相关(16)
    private static final String USER_HAS_COLLECT = "user_has_collect";
    private static final String USERREQ_IS_REQUIRED = "userreq_is_required";
    private static final String USERNAME_IS_REQUIRED = "username_is_required";
    private static final String PASSWORD_IS_REQUIRED = "password_is_required";
    private static final String USER_NOT_EXISTS = "user_not_exists";
    private static final String USER_ALREADY_EXISTS = "user_already_exists";
    private static final String PASSWORD_WRONG = "password_wrong";
    private static final String USER_INVALID = "user_invalid";
    private static final String USER_CREATE_ERROR = "user_create_error";
    private static final String USER_UPDATE_ERROR = "user_update_error";
    private static final String USER_DELETE_ERROR = "user_delete_error";
    private static final String USERID_IS_REQUIRED = "userid_is_required";
    private static final String OLDPASSWORD_IS_REQUIRED = "oldpassword_is_required";
    private static final String NEWPASSWORD_IS_REQUIRED = "newpassword_is_required";
    private static final String CONFIRMPASSWORD_IS_REQUIRED = "confirmpassword_is_required";
    private static final String CONFIRMPASSWORD_NOT_EQUALS_NEWPASSWORD = "confirmpassword_not_equals_newpassword";
    private static final String OLDPASSWORD_IS_WRONG = "oldpassword_is_wrong";

    //    群聊相关(15)
    private static final String QUERY_IMGROUP_FAILED = "query_imgroup_failed";
    private static final String QUERY_GROUP_FAILED = "query_group_failed";
    private static final String GROUPREQ_IS_REQUIRED = "groupreq_is_required";
    private static final String CHATNAME_IS_REQUIRED = "chatname_is_required";
    private static final String MEMBERCANREADHISTORY_IS_REQUIRED = "membercanreadhistory_is_required";
    private static final String ALLOWINVITES_IS_REQUIRED = "allowinvites_is_required";
    private static final String MEMBERONLY_IS_REQUIRED = "memberonly_is_required";
    private static final String MAXUSERS_IS_REQUIRED = "maxusers_is_required";
    private static final String ISPUBLIC_IS_REQUIRED = "ispublic_is_required";
    private static final String OWNER_IS_REQUIRED = "owner_is_required";
    private static final String CREATE_IMGROUP_FAILED = "create_imgroup_failed";
    private static final String CREATE_GROUP_FAILED = "create_group_failed";
    private static final String GROUPID_IS_REQUIRED = "groupid_is_required";
    private static final String GROUP_ISNOT_EXISTS = "group_isnot_exists";
    private static final String DELETE_GROUP_FAILED = "delete_group_failed";

    //    部门相关(9)
    private static final String ORGID_IS_REQUIRED = "orgid_is_required";
    private static final String REQUEST_IS_REQUIRED = "request_is_required";
    private static final String ORG_IS_NOT_EXIST = "org_is_not_exist";
    private static final String UPDATE_FILED = "update_filed";
    private static final String ORGNAME_IS_REQUIRED = "orgname_is_required";
    private static final String ORGRANK_IS_REQUIRED = "orgrank_is_required";
    private static final String PARENTID_IS_REQUIRED = "parentid_is_required";
    private static final String ORG_IS_ALREADY_EXIST = "org_is_already_exist";
    private static final String CREATE_ORG_FAILED = "create_org_failed";

    //    日程相关(10)
    private static final String SCHEDULEREQ_IS_REQUIRED = "schedulereq_is_required";
    private static final String CONTENT_IS_REQUIRED = "content_is_required";
    private static final String STARTTIME_IS_REQUIRED = "starttime_is_required";
    private static final String STARTTIME_CANNOT_BEFORE_CURRENTTIME = "starttime_cannot_before_currenttime";
    private static final String ENDTIME_IS_REQUIRED = "endtime_is_required";
    private static final String ISREPEAT_IS_REQUIRED = "isrepeat_is_required";
    private static final String ISALLDAY_IS_REQUIRED = "isallday_is_required";
    private static final String REMINDTIME_IS_REQUIRED = "remindtime_is_required";
    private static final String ID_IS_REQUIRED = "id_is_required";
    private static final String SCHEDULE_ISNOT_EXISTS = "schedule_isnot_exists";

    // 修改密码
    private static final String  OLD_PWD_IS_WRONG= "oldpassword_is_wrong";

    private static volatile ErrorCode instance = null;

    private ErrorCode() {
    }

    public synchronized static ErrorCode getInstance() {
        if (instance == null) {
            synchronized (ErrorCode.class) {
                if (instance == null) {
                    instance = new ErrorCode();
                }
            }
        }
        return instance;
    }

    /*private final String[] errorCodes = new String[]{
            "user_has_collect",
            "userreq_is_required",
            "username_is_required",
            "password_is_required",
            "user_not_exists",
            "user_already_exists",
            "password_wrong",
            "user_invalid",
            "user_create_error",
            "user_update_error",
            "user_delete_error",
            "userid_is_required",
            "oldpassword_is_required",
            "newpassword_is_required",
            "confirmpassword_is_required",
            "confirmpassword_not_equals_newpassword",

            "query_imgroup_failed",
            "query_group_failed",
            "groupreq_is_required",
            "chatname_is_required",
            "membercanreadhistory_is_required",
            "allowinvites_is_required",
            "memberonly_is_required",
            "maxusers_is_required",
            "ispublic_is_required",
            "owner_is_required",
            "create_imgroup_failed",
            "create_group_failed",
            "groupid_is_required",
            "group_isnot_exists",
            "delete_group_failed",

            "orgid_is_required",
            "request_is_required",
            "org_is_not_exist",
            "update_filed",
            "orgname_is_required",
            "orgrank_is_required",
            "parentid_is_required",
            "org_is_already_exist",
            "create_org_failed",

            "schedulereq_is_required",
            "content_is_required",
            "starttime_is_required",
            "starttime_cannot_before_currenttime",
            "endtime_is_required",
            "isrepeat_is_required",
            "isallday_is_required",
            "remindtime_is_required",
            "id_is_required",
            "schedule_isnot_exists",

    };*/

    public Pair<Boolean, String> getErrorDesc(Context context, String errorCode) {
        int res = 0;
        switch (errorCode) {
            case USER_HAS_COLLECT:
                res = R.string.user_has_starred;
                break;
            case USERREQ_IS_REQUIRED:
                res = R.string.request_empty;
                break;
            case USERNAME_IS_REQUIRED:
                res = R.string.username_empty;
                break;
            case PASSWORD_IS_REQUIRED:
                res = R.string.password_empty;
                break;
            case USER_NOT_EXISTS:
                res = R.string.user_not_existed;
                break;
            case USER_ALREADY_EXISTS:
                res = R.string.user_already_existed;
                break;
            case PASSWORD_WRONG:
                res = R.string.password_wrong;
                break;
            case USER_INVALID:
                res = R.string.user_invalid;
                break;
            case USER_CREATE_ERROR:
                res = R.string.user_create_failed;
                break;
            case USER_UPDATE_ERROR:
                res = R.string.user_update_failed;
                break;
            case USER_DELETE_ERROR:
                res = R.string.user_delete_failed;
                break;
            case USERID_IS_REQUIRED:
                res = R.string.userid_empty;
                break;
            case OLDPASSWORD_IS_REQUIRED:
                res = R.string.original_password_empty;
                break;
            case NEWPASSWORD_IS_REQUIRED:
                res = R.string.new_password_empty;
                break;
            case CONFIRMPASSWORD_IS_REQUIRED:
                res = R.string.confirm_password_empty;
                break;
            case CONFIRMPASSWORD_NOT_EQUALS_NEWPASSWORD:
                res = R.string.confirm_new_password_not_equals;
                break;

            case QUERY_IMGROUP_FAILED:
                res = R.string.group_query_failed;
                break;
            case QUERY_GROUP_FAILED:
                res = R.string.group_query_failed;
                break;
            case GROUPREQ_IS_REQUIRED:
                res = R.string.request_empty;
                break;
            case CHATNAME_IS_REQUIRED:
                res = R.string.group_name_empty;
                break;
            case MEMBERCANREADHISTORY_IS_REQUIRED:
                res = R.string.group_if_show_record_empty;
                break;
            case ALLOWINVITES_IS_REQUIRED:
                res = R.string.group_if_allow_invite_empty;
                break;
            case MEMBERONLY_IS_REQUIRED:
                res = R.string.group_if_need_admin_allow_empty;
                break;
            case MAXUSERS_IS_REQUIRED:
                res = R.string.group_max_number_need_larger;
                break;
            case ISPUBLIC_IS_REQUIRED:
                res = R.string.group_if_group_public_empty;
                break;
            case OWNER_IS_REQUIRED:
                res = R.string.group_owner_empty;
                break;
            case CREATE_IMGROUP_FAILED:
                res = R.string.group_create_failed;
                break;
            case CREATE_GROUP_FAILED:
                res = R.string.group_create_failed;
                break;
            case GROUPID_IS_REQUIRED:
                res = R.string.group_id_empty;
                break;
            case GROUP_ISNOT_EXISTS:
                res = R.string.group_not_existed;
                break;
            case DELETE_GROUP_FAILED:
                res = R.string.group_delete_failed;
                break;

            case ORGID_IS_REQUIRED:
                res = R.string.depart_ID_empty;
                break;
            case REQUEST_IS_REQUIRED:
                res = R.string.request_empty;
                break;
            case ORG_IS_NOT_EXIST:
                res = R.string.depart_not_existed;
                break;
            case UPDATE_FILED:
                res = R.string.depart_update_failed;
                break;
            case ORGNAME_IS_REQUIRED:
                res = R.string.depart_name_empty;
                break;
            case ORGRANK_IS_REQUIRED:
                res = R.string.depart_level_empty;
                break;
            case PARENTID_IS_REQUIRED:
                res = R.string.depart_parent_id_empty;
                break;
            case ORG_IS_ALREADY_EXIST:
                res = R.string.depart_already_existed;
                break;
            case CREATE_ORG_FAILED:
                res = R.string.depart_create_failed;
                break;

            case SCHEDULEREQ_IS_REQUIRED:
                res = R.string.request_empty;
                break;
            case CONTENT_IS_REQUIRED:
                res = R.string.work_content_empty;
                break;
            case STARTTIME_IS_REQUIRED:
                res = R.string.work_start_time_empty;
                break;
            case STARTTIME_CANNOT_BEFORE_CURRENTTIME:
                res = R.string.work_start_time_smaller;
                break;
            case ENDTIME_IS_REQUIRED:
                res = R.string.work_end_time_empty;
                break;
            case ISREPEAT_IS_REQUIRED:
                res = R.string.work_repeat_empty;
                break;
            case ISALLDAY_IS_REQUIRED:
                res = R.string.work_all_day_empty;
                break;
            case REMINDTIME_IS_REQUIRED:
                res = R.string.work_remain_empty;
                break;
            case ID_IS_REQUIRED:
                res = R.string.work_id_empty;
                break;
            case SCHEDULE_ISNOT_EXISTS:
                res = R.string.work_not_existed;
                break;
            case OLD_PWD_IS_WRONG:
                res = R.string.old_pwd_wrong;
                break;
            default:
                break;
        }
        if (res > 0) {
            return new Pair<>(true, context.getString(res));
        } else {
            return new Pair<>(false, null);
        }
    }

    public String getErrorInfo(Context context, String errorCode) {
        if (TextUtils.isEmpty(errorCode)) return null;
        int res = R.string.error_code_json_invalid;
        if (errorCode.equals(USER_HAS_COLLECT)) {
            res = R.string.user_has_starred;

        } else if (errorCode.equals(USERREQ_IS_REQUIRED)) {
            res = R.string.request_empty;

        } else if (errorCode.equals(USERNAME_IS_REQUIRED)) {
            res = R.string.username_empty;

        } else if (errorCode.equals(PASSWORD_IS_REQUIRED)) {
            res = R.string.password_empty;

        } else if (errorCode.equals(USER_NOT_EXISTS)) {
            res = R.string.user_not_existed;

        } else if (errorCode.equals(USER_ALREADY_EXISTS)) {
            res = R.string.user_already_existed;

        } else if (errorCode.equals(PASSWORD_WRONG)) {
            res = R.string.password_wrong;

        } else if (errorCode.equals(USER_INVALID)) {
            res = R.string.user_invalid;

        } else if (errorCode.equals(USER_CREATE_ERROR)) {
            res = R.string.user_create_failed;

        } else if (errorCode.equals(USER_UPDATE_ERROR)) {
            res = R.string.user_update_failed;

        } else if (errorCode.equals(USER_DELETE_ERROR)) {
            res = R.string.user_delete_failed;

        } else if (errorCode.equals(USERID_IS_REQUIRED)) {
            res = R.string.userid_empty;

        } else if (errorCode.equals(OLDPASSWORD_IS_REQUIRED)) {
            res = R.string.original_password_empty;

        } else if (errorCode.equals(NEWPASSWORD_IS_REQUIRED)) {
            res = R.string.new_password_empty;

        } else if (errorCode.equals(CONFIRMPASSWORD_IS_REQUIRED)) {
            res = R.string.confirm_password_empty;

        } else if (errorCode.equals(CONFIRMPASSWORD_NOT_EQUALS_NEWPASSWORD)) {
            res = R.string.confirm_new_password_not_equals;

        } else if (errorCode.equals(QUERY_IMGROUP_FAILED)) {
            res = R.string.group_query_failed;

        } else if (errorCode.equals(QUERY_GROUP_FAILED)) {
            res = R.string.group_query_failed;

        } else if (errorCode.equals(GROUPREQ_IS_REQUIRED)) {
            res = R.string.request_empty;

        } else if (errorCode.equals(CHATNAME_IS_REQUIRED)) {
            res = R.string.group_name_empty;

        } else if (errorCode.equals(MEMBERCANREADHISTORY_IS_REQUIRED)) {
            res = R.string.group_if_show_record_empty;

        } else if (errorCode.equals(ALLOWINVITES_IS_REQUIRED)) {
            res = R.string.group_if_allow_invite_empty;

        } else if (errorCode.equals(MEMBERONLY_IS_REQUIRED)) {
            res = R.string.group_if_need_admin_allow_empty;

        } else if (errorCode.equals(MAXUSERS_IS_REQUIRED)) {
            res = R.string.group_max_number_need_larger;

        } else if (errorCode.equals(ISPUBLIC_IS_REQUIRED)) {
            res = R.string.group_if_group_public_empty;

        } else if (errorCode.equals(OWNER_IS_REQUIRED)) {
            res = R.string.group_owner_empty;

        } else if (errorCode.equals(CREATE_IMGROUP_FAILED)) {
            res = R.string.group_create_failed;

        } else if (errorCode.equals(CREATE_GROUP_FAILED)) {
            res = R.string.group_create_failed;

        } else if (errorCode.equals(GROUPID_IS_REQUIRED)) {
            res = R.string.group_id_empty;

        } else if (errorCode.equals(GROUP_ISNOT_EXISTS)) {
            res = R.string.group_not_existed;

        } else if (errorCode.equals(DELETE_GROUP_FAILED)) {
            res = R.string.group_delete_failed;

        } else if (errorCode.equals(ORGID_IS_REQUIRED)) {
            res = R.string.depart_ID_empty;

        } else if (errorCode.equals(REQUEST_IS_REQUIRED)) {
            res = R.string.request_empty;

        } else if (errorCode.equals(ORG_IS_NOT_EXIST)) {
            res = R.string.depart_not_existed;

        } else if (errorCode.equals(UPDATE_FILED)) {
            res = R.string.depart_update_failed;

        } else if (errorCode.equals(ORGNAME_IS_REQUIRED)) {
            res = R.string.depart_name_empty;

        } else if (errorCode.equals(ORGRANK_IS_REQUIRED)) {
            res = R.string.depart_level_empty;

        } else if (errorCode.equals(PARENTID_IS_REQUIRED)) {
            res = R.string.depart_parent_id_empty;

        } else if (errorCode.equals(ORG_IS_ALREADY_EXIST)) {
            res = R.string.depart_already_existed;

        } else if (errorCode.equals(CREATE_ORG_FAILED)) {
            res = R.string.depart_create_failed;

        } else if (errorCode.equals(SCHEDULEREQ_IS_REQUIRED)) {
            res = R.string.request_empty;

        } else if (errorCode.equals(CONTENT_IS_REQUIRED)) {
            res = R.string.work_content_empty;

        } else if (errorCode.equals(STARTTIME_IS_REQUIRED)) {
            res = R.string.work_start_time_empty;

        } else if (errorCode.equals(STARTTIME_CANNOT_BEFORE_CURRENTTIME)) {
            res = R.string.work_start_time_smaller;

        } else if (errorCode.equals(ENDTIME_IS_REQUIRED)) {
            res = R.string.work_end_time_empty;

        } else if (errorCode.equals(ISREPEAT_IS_REQUIRED)) {
            res = R.string.work_repeat_empty;

        } else if (errorCode.equals(ISALLDAY_IS_REQUIRED)) {
            res = R.string.work_all_day_empty;

        } else if (errorCode.equals(REMINDTIME_IS_REQUIRED)) {
            res = R.string.work_remain_empty;

        } else if (errorCode.equals(ID_IS_REQUIRED)) {
            res = R.string.work_id_empty;
        } else if (errorCode.equals(SCHEDULE_ISNOT_EXISTS)) {
            res = R.string.work_not_existed;
        } else if (errorCode.equals(OLDPASSWORD_IS_WRONG)){
            res = R.string.tip_oldpwd_iswrong;
        }
        else {
            return null;
        }
        return context.getString(res);
    }


}
