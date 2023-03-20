package com.hyphenate.easemob.im.officeautomation.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TodoListEntity {

    private List<Entity> unDealList;
    private List<Entity> dealList;

    public List<Entity> getUnDealList() {
        return unDealList;
    }

    public List<Entity> getDealList() {
        return dealList;
    }

    public static TodoListEntity create(JSONObject jsonObject){
        if(jsonObject == null){
            return null;
        }
        TodoListEntity todoListEntity = new TodoListEntity();
        JSONObject entity = jsonObject.optJSONObject("entity");
        JSONArray unDealArray = entity.optJSONArray("0");
        List<Entity> unDealList = new ArrayList<>();
        if(unDealArray != null){
            for(int i = 0; i < unDealArray.length(); i ++){
                Entity entityBean = new Entity();
                JSONObject itemJson = unDealArray.optJSONObject(i);
                entityBean.setId(itemJson.optInt("id"));
                entityBean.setUserId(itemJson.optInt("userId"));
                entityBean.setMsgId(itemJson.optString("msgId"));
                entityBean.setCreateTime(itemJson.optLong("createTime"));
                entityBean.setUpdateTime(itemJson.optLong("updateTime"));
                entityBean.setStatus(itemJson.optInt("status"));
                entityBean.setMsgExt(itemJson.optString("msgExt"));

                JSONObject msgJson = itemJson.optJSONObject("msg");
                MsgEntity msgEntity = new MsgEntity();
                msgEntity.setId(msgJson.optString("id"));
                msgEntity.setTimestamp(msgJson.optLong("timestamp"));
                msgEntity.setMsg(msgJson.optString("msg"));
                msgEntity.setType(msgJson.optString("type"));
                msgEntity.setChatType(msgJson.optString("chat_type"));
                msgEntity.setGroupId(msgJson.optString("group_id"));
                msgEntity.setToId(msgJson.optString("to_id"));
                msgEntity.setFromId(msgJson.optString("from_id"));
                msgEntity.setMsgId(msgJson.optString("msg_id"));
                msgEntity.setFileLength(msgJson.optLong("file_length"));
                msgEntity.setExt(msgJson.optString("ext"));
                msgEntity.setLat(msgJson.optDouble("lat"));
                msgEntity.setLng(msgJson.optDouble("lng"));
                msgEntity.setAddr(msgJson.optString("addr"));
                entityBean.setMsgEntity(msgEntity);
                unDealList.add(entityBean);
            }
        }

        JSONArray dealArray = entity.optJSONArray("1");
        List<Entity> dealList = new ArrayList<>();
        if(dealArray != null){
            for(int i = 0; i < dealArray.length(); i ++){
                Entity entityBean = new Entity();
                JSONObject itemJson = dealArray.optJSONObject(i);
                entityBean.setId(itemJson.optInt("id"));
                entityBean.setUserId(itemJson.optInt("userId"));
                entityBean.setMsgId(itemJson.optString("msgId"));
                entityBean.setCreateTime(itemJson.optLong("createTime"));
                entityBean.setUpdateTime(itemJson.optLong("updateTime"));
                entityBean.setStatus(itemJson.optInt("status"));
                entityBean.setMsgExt(itemJson.optString("msgExt"));

                JSONObject msgJson = itemJson.optJSONObject("msg");
                MsgEntity msgEntity = new MsgEntity();
                msgEntity.setId(msgJson.optString("id"));
                msgEntity.setTimestamp(msgJson.optLong("timestamp"));
                msgEntity.setMsg(msgJson.optString("msg"));
                msgEntity.setType(msgJson.optString("type"));
                msgEntity.setChatType(msgJson.optString("chat_type"));
                msgEntity.setGroupId(msgJson.optString("group_id"));
                msgEntity.setToId(msgJson.optString("to_id"));
                msgEntity.setFromId(msgJson.optString("from_id"));
                msgEntity.setMsgId(msgJson.optString("msg_id"));
                msgEntity.setFileLength(msgJson.optLong("file_length"));
                msgEntity.setExt(msgJson.optString("ext"));
                msgEntity.setLat(msgJson.optDouble("lat"));
                msgEntity.setLng(msgJson.optDouble("lng"));
                msgEntity.setAddr(msgJson.optString("addr"));
                entityBean.setMsgEntity(msgEntity);
                dealList.add(entityBean);
            }
        }

        todoListEntity.unDealList = unDealList;
        todoListEntity.dealList = dealList;

        return todoListEntity;
    }

    public static class Entity{
        private int id;
        private int userId;
        private String msgId;
        private long createTime;
        private long updateTime;
        private int status;
        private MsgEntity msgEntity;
        private String msgExt;

        public int getId() {
            return id;
        }

        public int getUserId() {
            return userId;
        }

        public String getMsgId() {
            return msgId;
        }

        public long getCreateTime() {
            return createTime;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public int getStatus() {
            return status;
        }

        public String getMsgExt() {
            return msgExt;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setMsgExt(String msgExt) {
            this.msgExt = msgExt;
        }

        public MsgEntity getMsgEntity() {
            return msgEntity;
        }

        public void setMsgEntity(MsgEntity msgEntity) {
            this.msgEntity = msgEntity;
        }
    }

    public static class MsgEntity{
        private String id;
        private long timestamp;
        private String msg;
        private String type;
        private double lng;
        private double lat;
        private String addr;
        private String chatType;
        private String groupId;
        private String toId;
        private String fromId;
        private long fileLength;
        private String msgId;
        private String ext;

        public String getId() {
            return id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getMsg() {
            return msg;
        }

        public String getType() {
            return type;
        }

        public double getLng() {
            return lng;
        }

        public double getLat() {
            return lat;
        }

        public String getAddr() {
            return addr;
        }

        public String getChatType() {
            return chatType;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getToId() {
            return toId;
        }

        public String getFromId() {
            return fromId;
        }

        public long getFileLength() {
            return fileLength;
        }

        public String getMsgId() {
            return msgId;
        }

        public String getExt() {
            return ext;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public void setChatType(String chatType) {
            this.chatType = chatType;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public void setToId(String toId) {
            this.toId = toId;
        }

        public void setFromId(String fromId) {
            this.fromId = fromId;
        }

        public void setFileLength(long fileLength) {
            this.fileLength = fileLength;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }
    }
}
