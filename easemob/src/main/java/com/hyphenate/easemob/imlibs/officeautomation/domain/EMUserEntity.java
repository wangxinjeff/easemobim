package com.hyphenate.easemob.imlibs.officeautomation.domain;

//public class EMUserEntity implements Parcelable {
//
//
//    /**
//     * status : OK
//     * errorCode : 0
//     * responseDate : 1564556890910
//     * entity : {"user":{"id":3,"realName":"崔永强","phone":"11907300003","imUser":{"userId":3,"appkey":"1151190730157140#mpapp1","imUsername":"oa_user_0f1dcbd956b345ecaf070cb8565b63ec","id":2,"lastUpdateTime":1564494422000,"tenantId":1,"imPassword":"IouPBJam","createTime":1564494422000},"tenantId":1,"avatar":"/user-avatar/cuiyongqiang","createTime":1564417320000,"pinyin":"cuiyongqiang","createUserId":1,"username":"cuiyongqiang","lastUpdateUserId":1,"lastUpdateTime":1564417320000,"email":"cuiyongqiang@easemob.com","gender":"1"},"companyList":[{"avatar":"/company0002-avatar","remark":"备注","createUserId":1,"id":2,"code":"07290002","lastUpdateTime":1564388596000,"tenantId":1,"lastUpdateUserId":1,"createTime":1564388596000,"name":"07290002"}],"organizationList":[{"id":2,"name":"部门2","tenantId":1,"code":"org2","createTime":1564389907000,"rank":"2","remark":"备注","createUserId":1,"lastUpdateUserId":1,"lastUpdateTime":1564389907000,"fullName":"部门2","fullId":"2","companyId":2,"parentId":-1}],"token":{"name":"SESSION","value":"edc55094-1230-4ca7-aec8-b096e74113a0","expires":"2592000"},"userCompanyRelationshipList":[{"userId":3,"id":3,"lastUpdateTime":1564417320000,"tenantId":1,"organizationId":2,"title":"安卓工程师","type":"normal","createTime":1564417320000,"companyId":2}]}
//     */
//
//    private String status;
//    private int errorCode;
//    private long responseDate;
//    private EntityBean entity;
//
//    public EMUserEntity() {
//    }
//
//
//    protected EMUserEntity(Parcel in) {
//        status = in.readString();
//        errorCode = in.readInt();
//        responseDate = in.readLong();
//        entity = in.readParcelable(EntityBean.class.getClassLoader());
//    }
//
//    public static final Creator<EMUserEntity> CREATOR = new Creator<EMUserEntity>() {
//        @Override
//        public EMUserEntity createFromParcel(Parcel in) {
//            return new EMUserEntity(in);
//        }
//
//        @Override
//        public EMUserEntity[] newArray(int size) {
//            return new EMUserEntity[size];
//        }
//    };
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public int getErrorCode() {
//        return errorCode;
//    }
//
//    public void setErrorCode(int errorCode) {
//        this.errorCode = errorCode;
//    }
//
//    public long getResponseDate() {
//        return responseDate;
//    }
//
//    public void setResponseDate(long responseDate) {
//        this.responseDate = responseDate;
//    }
//
//    public EntityBean getEntity() {
//        return entity;
//    }
//
//    public void setEntity(EntityBean entity) {
//        this.entity = entity;
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(status);
//        dest.writeInt(errorCode);
//        dest.writeLong(responseDate);
//        dest.writeParcelable(entity, flags);
//    }
//
//    public static class EntityBean implements Parcelable {
//        /**
//         * user : {"id":3,"realName":"崔永强","phone":"11907300003","imUser":{"userId":3,"appkey":"1151190730157140#mpapp1","imUsername":"oa_user_0f1dcbd956b345ecaf070cb8565b63ec","id":2,"lastUpdateTime":1564494422000,"tenantId":1,"imPassword":"IouPBJam","createTime":1564494422000},"tenantId":1,"avatar":"/user-avatar/cuiyongqiang","createTime":1564417320000,"pinyin":"cuiyongqiang","createUserId":1,"username":"cuiyongqiang","lastUpdateUserId":1,"lastUpdateTime":1564417320000,"email":"cuiyongqiang@easemob.com","gender":"1"}
//         * companyList : [{"avatar":"/company0002-avatar","remark":"备注","createUserId":1,"id":2,"code":"07290002","lastUpdateTime":1564388596000,"tenantId":1,"lastUpdateUserId":1,"createTime":1564388596000,"name":"07290002"}]
//         * organizationList : [{"id":2,"name":"部门2","tenantId":1,"code":"org2","createTime":1564389907000,"rank":"2","remark":"备注","createUserId":1,"lastUpdateUserId":1,"lastUpdateTime":1564389907000,"fullName":"部门2","fullId":"2","companyId":2,"parentId":-1}]
//         * token : {"name":"SESSION","value":"edc55094-1230-4ca7-aec8-b096e74113a0","expires":"2592000"}
//         * userCompanyRelationshipList : [{"userId":3,"id":3,"lastUpdateTime":1564417320000,"tenantId":1,"organizationId":2,"title":"安卓工程师","type":"normal","createTime":1564417320000,"companyId":2}]
//         */
//
//        private UserBean user;
//        private TokenBean token;
//        private List<CompanyListBean> companyList;
//        private List<OrganizationListBean> organizationList;
//        private List<UserCompanyRelationshipListBean> userCompanyRelationshipList;
//
//        protected EntityBean(Parcel in) {
//            user = in.readParcelable(UserBean.class.getClassLoader());
//            token = in.readParcelable(TokenBean.class.getClassLoader());
//            companyList = in.createTypedArrayList(CompanyListBean.CREATOR);
//            organizationList = in.createTypedArrayList(OrganizationListBean.CREATOR);
//            userCompanyRelationshipList = in.createTypedArrayList(UserCompanyRelationshipListBean.CREATOR);
//        }
//
//        public static final Creator<EntityBean> CREATOR = new Creator<EntityBean>() {
//            @Override
//            public EntityBean createFromParcel(Parcel in) {
//                return new EntityBean(in);
//            }
//
//            @Override
//            public EntityBean[] newArray(int size) {
//                return new EntityBean[size];
//            }
//        };
//
//        public UserBean getUser() {
//            return user;
//        }
//
//        public void setUser(UserBean user) {
//            this.user = user;
//        }
//
//        public TokenBean getToken() {
//            return token;
//        }
//
//        public void setToken(TokenBean token) {
//            this.token = token;
//        }
//
//        public List<CompanyListBean> getCompanyList() {
//            return companyList;
//        }
//
//        public void setCompanyList(List<CompanyListBean> companyList) {
//            this.companyList = companyList;
//        }
//
//        public List<OrganizationListBean> getOrganizationList() {
//            return organizationList;
//        }
//
//        public void setOrganizationList(List<OrganizationListBean> organizationList) {
//            this.organizationList = organizationList;
//        }
//
//        public List<UserCompanyRelationshipListBean> getUserCompanyRelationshipList() {
//            return userCompanyRelationshipList;
//        }
//
//        public void setUserCompanyRelationshipList(List<UserCompanyRelationshipListBean> userCompanyRelationshipList) {
//            this.userCompanyRelationshipList = userCompanyRelationshipList;
//        }
//
//        @Override
//        public int describeContents() {
//            return 0;
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            dest.writeParcelable(user, flags);
//            dest.writeParcelable(token, flags);
//            dest.writeTypedList(companyList);
//            dest.writeTypedList(organizationList);
//            dest.writeTypedList(userCompanyRelationshipList);
//        }
//
//        public static class UserBean implements Parcelable {
//            /**
//             * id : 3
//             * realName : 崔永强
//             * phone : 11907300003
//             * imUser : {"userId":3,"appkey":"1151190730157140#mpapp1","imUsername":"oa_user_0f1dcbd956b345ecaf070cb8565b63ec","id":2,"lastUpdateTime":1564494422000,"tenantId":1,"imPassword":"IouPBJam","createTime":1564494422000}
//             * tenantId : 1
//             * avatar : /user-avatar/cuiyongqiang
//             * createTime : 1564417320000
//             * pinyin : cuiyongqiang
//             * createUserId : 1
//             * username : cuiyongqiang
//             * lastUpdateUserId : 1
//             * lastUpdateTime : 1564417320000
//             * email : cuiyongqiang@easemob.com
//             * gender : 1
//             */
//
//            private int id;
//            private String realName;
//            private String phone;
//            private ImUserBean imUser;
//            private int tenantId;
//            private String avatar;
//            private long createTime;
//            private String pinyin;
//            private int createUserId;
//            private String username;
//            private int lastUpdateUserId;
//            private long lastUpdateTime;
//            private String email;
//            private String gender;
//            private String alias;
//
//            UserBean(Parcel in) {
//                id = in.readInt();
//                realName = in.readString();
//                phone = in.readString();
//                imUser = in.readParcelable(ImUserBean.class.getClassLoader());
//                tenantId = in.readInt();
//                avatar = in.readString();
//                createTime = in.readLong();
//                pinyin = in.readString();
//                createUserId = in.readInt();
//                username = in.readString();
//                lastUpdateUserId = in.readInt();
//                lastUpdateTime = in.readLong();
//                email = in.readString();
//                gender = in.readString();
//                alias = in.readString();
//            }
//
//            public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
//                @Override
//                public UserBean createFromParcel(Parcel in) {
//                    return new UserBean(in);
//                }
//
//                @Override
//                public UserBean[] newArray(int size) {
//                    return new UserBean[size];
//                }
//            };
//
//            public int getId() {
//                return id;
//            }
//
//            public void setId(int id) {
//                this.id = id;
//            }
//
//            public String getRealName() {
//                return realName;
//            }
//
//            public void setRealName(String realName) {
//                this.realName = realName;
//            }
//
//            public String getPhone() {
//                return phone;
//            }
//
//            public void setPhone(String phone) {
//                this.phone = phone;
//            }
//
//            public ImUserBean getImUser() {
//                return imUser;
//            }
//
//            public void setImUser(ImUserBean imUser) {
//                this.imUser = imUser;
//            }
//
//            public int getTenantId() {
//                return tenantId;
//            }
//
//            public void setTenantId(int tenantId) {
//                this.tenantId = tenantId;
//            }
//
//            public String getAvatar() {
//                return avatar;
//            }
//
//            public void setAvatar(String avatar) {
//                this.avatar = avatar;
//            }
//
//            public long getCreateTime() {
//                return createTime;
//            }
//
//            public void setCreateTime(long createTime) {
//                this.createTime = createTime;
//            }
//
//            public String getPinyin() {
//                return pinyin;
//            }
//
//            public void setPinyin(String pinyin) {
//                this.pinyin = pinyin;
//            }
//
//            public int getCreateUserId() {
//                return createUserId;
//            }
//
//            public void setCreateUserId(int createUserId) {
//                this.createUserId = createUserId;
//            }
//
//            public String getUsername() {
//                return username;
//            }
//
//            public void setUsername(String username) {
//                this.username = username;
//            }
//
//            public int getLastUpdateUserId() {
//                return lastUpdateUserId;
//            }
//
//            public void setLastUpdateUserId(int lastUpdateUserId) {
//                this.lastUpdateUserId = lastUpdateUserId;
//            }
//
//            public long getLastUpdateTime() {
//                return lastUpdateTime;
//            }
//
//            public void setLastUpdateTime(long lastUpdateTime) {
//                this.lastUpdateTime = lastUpdateTime;
//            }
//
//            public String getEmail() {
//                return email;
//            }
//
//            public void setEmail(String email) {
//                this.email = email;
//            }
//
//            public String getGender() {
//                return gender;
//            }
//
//            public void setGender(String gender) {
//                this.gender = gender;
//            }
//
//            public String getAlias() {
//                return alias;
//            }
//
//            public void setAlias(String alias) {
//                this.alias = alias;
//            }
//
//            @Override
//            public int describeContents() {
//                return 0;
//            }
//
//            @Override
//            public void writeToParcel(Parcel dest, int flags) {
//                dest.writeInt(id);
//                dest.writeString(realName);
//                dest.writeString(phone);
//                dest.writeParcelable(imUser, flags);
//                dest.writeInt(tenantId);
//                dest.writeString(avatar);
//                dest.writeLong(createTime);
//                dest.writeString(pinyin);
//                dest.writeInt(createUserId);
//                dest.writeString(username);
//                dest.writeInt(lastUpdateUserId);
//                dest.writeLong(lastUpdateTime);
//                dest.writeString(email);
//                dest.writeString(gender);
//                dest.writeString(alias);
//            }
//
//            public static class ImUserBean implements Parcelable {
//                /**
//                 * userId : 3
//                 * appkey : 1151190730157140#mpapp1
//                 * imUsername : oa_user_0f1dcbd956b345ecaf070cb8565b63ec
//                 * id : 2
//                 * lastUpdateTime : 1564494422000
//                 * tenantId : 1
//                 * imPassword : IouPBJam
//                 * createTime : 1564494422000
//                 */
//
//                private int userId;
//                private String appkey;
//                private String imUsername;
//                private int id;
//                private long lastUpdateTime;
//                private int tenantId;
//                private String imPassword;
//                private long createTime;
//
//                public ImUserBean() {
//                }
//
//                public ImUserBean(Parcel in) {
//                    userId = in.readInt();
//                    appkey = in.readString();
//                    imUsername = in.readString();
//                    id = in.readInt();
//                    lastUpdateTime = in.readLong();
//                    tenantId = in.readInt();
//                    imPassword = in.readString();
//                    createTime = in.readLong();
//                }
//
//                public static final Creator<ImUserBean> CREATOR = new Creator<ImUserBean>() {
//                    @Override
//                    public ImUserBean createFromParcel(Parcel in) {
//                        return new ImUserBean(in);
//                    }
//
//                    @Override
//                    public ImUserBean[] newArray(int size) {
//                        return new ImUserBean[size];
//                    }
//                };
//
//                public int getUserId() {
//                    return userId;
//                }
//
//                public void setUserId(int userId) {
//                    this.userId = userId;
//                }
//
//                public String getAppkey() {
//                    return appkey;
//                }
//
//                public void setAppkey(String appkey) {
//                    this.appkey = appkey;
//                }
//
//                public String getImUsername() {
//                    return imUsername;
//                }
//
//                public void setImUsername(String imUsername) {
//                    this.imUsername = imUsername;
//                }
//
//                public int getId() {
//                    return id;
//                }
//
//                public void setId(int id) {
//                    this.id = id;
//                }
//
//                public long getLastUpdateTime() {
//                    return lastUpdateTime;
//                }
//
//                public void setLastUpdateTime(long lastUpdateTime) {
//                    this.lastUpdateTime = lastUpdateTime;
//                }
//
//                public int getTenantId() {
//                    return tenantId;
//                }
//
//                public void setTenantId(int tenantId) {
//                    this.tenantId = tenantId;
//                }
//
//                public String getImPassword() {
//                    return imPassword;
//                }
//
//                public void setImPassword(String imPassword) {
//                    this.imPassword = imPassword;
//                }
//
//                public long getCreateTime() {
//                    return createTime;
//                }
//
//                public void setCreateTime(long createTime) {
//                    this.createTime = createTime;
//                }
//
//                @Override
//                public int describeContents() {
//                    return 0;
//                }
//
//                @Override
//                public void writeToParcel(Parcel dest, int flags) {
//                    dest.writeInt(userId);
//                    dest.writeString(appkey);
//                    dest.writeString(imUsername);
//                    dest.writeInt(id);
//                    dest.writeLong(lastUpdateTime);
//                    dest.writeInt(tenantId);
//                    dest.writeString(imPassword);
//                    dest.writeLong(createTime);
//                }
//            }
//        }
//
//        public static class TokenBean implements Parcelable {
//            /**
//             * name : SESSION
//             * value : edc55094-1230-4ca7-aec8-b096e74113a0
//             * expires : 2592000
//             */
//
//            private String name;
//            private String value;
//            private String expires;
//
//            TokenBean(Parcel in) {
//                name = in.readString();
//                value = in.readString();
//                expires = in.readString();
//            }
//
//            public static final Creator<TokenBean> CREATOR = new Creator<TokenBean>() {
//                @Override
//                public TokenBean createFromParcel(Parcel in) {
//                    return new TokenBean(in);
//                }
//
//                @Override
//                public TokenBean[] newArray(int size) {
//                    return new TokenBean[size];
//                }
//            };
//
//            public String getName() {
//                return name;
//            }
//
//            public void setName(String name) {
//                this.name = name;
//            }
//
//            public String getValue() {
//                return value;
//            }
//
//            public void setValue(String value) {
//                this.value = value;
//            }
//
//            public String getExpires() {
//                return expires;
//            }
//
//            public void setExpires(String expires) {
//                this.expires = expires;
//            }
//
//            @Override
//            public int describeContents() {
//                return 0;
//            }
//
//            @Override
//            public void writeToParcel(Parcel dest, int flags) {
//                dest.writeString(name);
//                dest.writeString(value);
//                dest.writeString(expires);
//            }
//        }
//
//        public static class CompanyListBean implements Parcelable {
//            /**
//             * avatar : /company0002-avatar
//             * remark : 备注
//             * createUserId : 1
//             * id : 2
//             * code : 07290002
//             * lastUpdateTime : 1564388596000
//             * tenantId : 1
//             * lastUpdateUserId : 1
//             * createTime : 1564388596000
//             * name : 07290002
//             */
//
//            private String avatar;
//            private String remark;
//            private int createUserId;
//            private int id;
//            private String code;
//            private long lastUpdateTime;
//            private int tenantId;
//            private int lastUpdateUserId;
//            private long createTime;
//            private String name;
//
//            CompanyListBean(Parcel in) {
//                avatar = in.readString();
//                remark = in.readString();
//                createUserId = in.readInt();
//                id = in.readInt();
//                code = in.readString();
//                lastUpdateTime = in.readLong();
//                tenantId = in.readInt();
//                lastUpdateUserId = in.readInt();
//                createTime = in.readLong();
//                name = in.readString();
//            }
//
//            public static final Creator<CompanyListBean> CREATOR = new Creator<CompanyListBean>() {
//                @Override
//                public CompanyListBean createFromParcel(Parcel in) {
//                    return new CompanyListBean(in);
//                }
//
//                @Override
//                public CompanyListBean[] newArray(int size) {
//                    return new CompanyListBean[size];
//                }
//            };
//
//            public String getAvatar() {
//                return avatar;
//            }
//
//            public void setAvatar(String avatar) {
//                this.avatar = avatar;
//            }
//
//            public String getRemark() {
//                return remark;
//            }
//
//            public void setRemark(String remark) {
//                this.remark = remark;
//            }
//
//            public int getCreateUserId() {
//                return createUserId;
//            }
//
//            public void setCreateUserId(int createUserId) {
//                this.createUserId = createUserId;
//            }
//
//            public int getId() {
//                return id;
//            }
//
//            public void setId(int id) {
//                this.id = id;
//            }
//
//            public String getCode() {
//                return code;
//            }
//
//            public void setCode(String code) {
//                this.code = code;
//            }
//
//            public long getLastUpdateTime() {
//                return lastUpdateTime;
//            }
//
//            public void setLastUpdateTime(long lastUpdateTime) {
//                this.lastUpdateTime = lastUpdateTime;
//            }
//
//            public int getTenantId() {
//                return tenantId;
//            }
//
//            public void setTenantId(int tenantId) {
//                this.tenantId = tenantId;
//            }
//
//            public int getLastUpdateUserId() {
//                return lastUpdateUserId;
//            }
//
//            public void setLastUpdateUserId(int lastUpdateUserId) {
//                this.lastUpdateUserId = lastUpdateUserId;
//            }
//
//            public long getCreateTime() {
//                return createTime;
//            }
//
//            public void setCreateTime(long createTime) {
//                this.createTime = createTime;
//            }
//
//            public String getName() {
//                return name;
//            }
//
//            public void setName(String name) {
//                this.name = name;
//            }
//
//            @Override
//            public int describeContents() {
//                return 0;
//            }
//
//            @Override
//            public void writeToParcel(Parcel dest, int flags) {
//                dest.writeString(avatar);
//                dest.writeString(remark);
//                dest.writeInt(createUserId);
//                dest.writeInt(id);
//                dest.writeString(code);
//                dest.writeLong(lastUpdateTime);
//                dest.writeInt(tenantId);
//                dest.writeInt(lastUpdateUserId);
//                dest.writeLong(createTime);
//                dest.writeString(name);
//            }
//        }
//
//        public static class OrganizationListBean implements Parcelable {
//            /**
//             * id : 2
//             * name : 部门2
//             * tenantId : 1
//             * code : org2
//             * createTime : 1564389907000
//             * rank : 2
//             * remark : 备注
//             * createUserId : 1
//             * lastUpdateUserId : 1
//             * lastUpdateTime : 1564389907000
//             * fullName : 部门2
//             * fullId : 2
//             * companyId : 2
//             * parentId : -1
//             */
//
//            private int id;
//            private String name;
//            private int tenantId;
//            private String code;
//            private long createTime;
//            private String rank;
//            private String remark;
//            private int createUserId;
//            private int lastUpdateUserId;
//            private long lastUpdateTime;
//            private String fullName;
//            private String fullId;
//            private int companyId;
//            private int parentId;
//
//            public OrganizationListBean() {
//            }
//
//            public OrganizationListBean(Parcel in) {
//                id = in.readInt();
//                name = in.readString();
//                tenantId = in.readInt();
//                code = in.readString();
//                createTime = in.readLong();
//                rank = in.readString();
//                remark = in.readString();
//                createUserId = in.readInt();
//                lastUpdateUserId = in.readInt();
//                lastUpdateTime = in.readLong();
//                fullName = in.readString();
//                fullId = in.readString();
//                companyId = in.readInt();
//                parentId = in.readInt();
//            }
//
//            public static final Creator<OrganizationListBean> CREATOR = new Creator<OrganizationListBean>() {
//                @Override
//                public OrganizationListBean createFromParcel(Parcel in) {
//                    return new OrganizationListBean(in);
//                }
//
//                @Override
//                public OrganizationListBean[] newArray(int size) {
//                    return new OrganizationListBean[size];
//                }
//            };
//
//            public int getId() {
//                return id;
//            }
//
//            public void setId(int id) {
//                this.id = id;
//            }
//
//            public String getName() {
//                return name;
//            }
//
//            public void setName(String name) {
//                this.name = name;
//            }
//
//            public int getTenantId() {
//                return tenantId;
//            }
//
//            public void setTenantId(int tenantId) {
//                this.tenantId = tenantId;
//            }
//
//            public String getCode() {
//                return code;
//            }
//
//            public void setCode(String code) {
//                this.code = code;
//            }
//
//            public long getCreateTime() {
//                return createTime;
//            }
//
//            public void setCreateTime(long createTime) {
//                this.createTime = createTime;
//            }
//
//            public String getRank() {
//                return rank;
//            }
//
//            public void setRank(String rank) {
//                this.rank = rank;
//            }
//
//            public String getRemark() {
//                return remark;
//            }
//
//            public void setRemark(String remark) {
//                this.remark = remark;
//            }
//
//            public int getCreateUserId() {
//                return createUserId;
//            }
//
//            public void setCreateUserId(int createUserId) {
//                this.createUserId = createUserId;
//            }
//
//            public int getLastUpdateUserId() {
//                return lastUpdateUserId;
//            }
//
//            public void setLastUpdateUserId(int lastUpdateUserId) {
//                this.lastUpdateUserId = lastUpdateUserId;
//            }
//
//            public long getLastUpdateTime() {
//                return lastUpdateTime;
//            }
//
//            public void setLastUpdateTime(long lastUpdateTime) {
//                this.lastUpdateTime = lastUpdateTime;
//            }
//
//            public String getFullName() {
//                return fullName;
//            }
//
//            public void setFullName(String fullName) {
//                this.fullName = fullName;
//            }
//
//            public String getFullId() {
//                return fullId;
//            }
//
//            public void setFullId(String fullId) {
//                this.fullId = fullId;
//            }
//
//            public int getCompanyId() {
//                return companyId;
//            }
//
//            public void setCompanyId(int companyId) {
//                this.companyId = companyId;
//            }
//
//            public int getParentId() {
//                return parentId;
//            }
//
//            public void setParentId(int parentId) {
//                this.parentId = parentId;
//            }
//
//            @Override
//            public int describeContents() {
//                return 0;
//            }
//
//            @Override
//            public void writeToParcel(Parcel dest, int flags) {
//                dest.writeInt(id);
//                dest.writeString(name);
//                dest.writeInt(tenantId);
//                dest.writeString(code);
//                dest.writeLong(createTime);
//                dest.writeString(rank);
//                dest.writeString(remark);
//                dest.writeInt(createUserId);
//                dest.writeInt(lastUpdateUserId);
//                dest.writeLong(lastUpdateTime);
//                dest.writeString(fullName);
//                dest.writeString(fullId);
//                dest.writeInt(companyId);
//                dest.writeInt(parentId);
//            }
//        }
//
//        public static class UserCompanyRelationshipListBean implements Parcelable {
//            /**
//             * userId : 3
//             * id : 3
//             * lastUpdateTime : 1564417320000
//             * tenantId : 1
//             * organizationId : 2
//             * title : 安卓工程师
//             * type : normal
//             * createTime : 1564417320000
//             * companyId : 2
//             */
//
//            private int userId;
//            private int id;
//            private long lastUpdateTime;
//            private int tenantId;
//            private int organizationId;
//            private String title;
//            private String type;
//            private long createTime;
//            private int companyId;
//
//            UserCompanyRelationshipListBean(Parcel in) {
//                userId = in.readInt();
//                id = in.readInt();
//                lastUpdateTime = in.readLong();
//                tenantId = in.readInt();
//                organizationId = in.readInt();
//                title = in.readString();
//                type = in.readString();
//                createTime = in.readLong();
//                companyId = in.readInt();
//            }
//
//            public static final Creator<UserCompanyRelationshipListBean> CREATOR = new Creator<UserCompanyRelationshipListBean>() {
//                @Override
//                public UserCompanyRelationshipListBean createFromParcel(Parcel in) {
//                    return new UserCompanyRelationshipListBean(in);
//                }
//
//                @Override
//                public UserCompanyRelationshipListBean[] newArray(int size) {
//                    return new UserCompanyRelationshipListBean[size];
//                }
//            };
//
//            public int getUserId() {
//                return userId;
//            }
//
//            public void setUserId(int userId) {
//                this.userId = userId;
//            }
//
//            public int getId() {
//                return id;
//            }
//
//            public void setId(int id) {
//                this.id = id;
//            }
//
//            public long getLastUpdateTime() {
//                return lastUpdateTime;
//            }
//
//            public void setLastUpdateTime(long lastUpdateTime) {
//                this.lastUpdateTime = lastUpdateTime;
//            }
//
//            public int getTenantId() {
//                return tenantId;
//            }
//
//            public void setTenantId(int tenantId) {
//                this.tenantId = tenantId;
//            }
//
//            public int getOrganizationId() {
//                return organizationId;
//            }
//
//            public void setOrganizationId(int organizationId) {
//                this.organizationId = organizationId;
//            }
//
//            public String getTitle() {
//                return title;
//            }
//
//            public void setTitle(String title) {
//                this.title = title;
//            }
//
//            public String getType() {
//                return type;
//            }
//
//            public void setType(String type) {
//                this.type = type;
//            }
//
//            public long getCreateTime() {
//                return createTime;
//            }
//
//            public void setCreateTime(long createTime) {
//                this.createTime = createTime;
//            }
//
//            public int getCompanyId() {
//                return companyId;
//            }
//
//            public void setCompanyId(int companyId) {
//                this.companyId = companyId;
//            }
//
//            @Override
//            public int describeContents() {
//                return 0;
//            }
//
//            @Override
//            public void writeToParcel(Parcel dest, int flags) {
//                dest.writeInt(userId);
//                dest.writeInt(id);
//                dest.writeLong(lastUpdateTime);
//                dest.writeInt(tenantId);
//                dest.writeInt(organizationId);
//                dest.writeString(title);
//                dest.writeString(type);
//                dest.writeLong(createTime);
//                dest.writeInt(companyId);
//            }
//        }
//    }
//}
