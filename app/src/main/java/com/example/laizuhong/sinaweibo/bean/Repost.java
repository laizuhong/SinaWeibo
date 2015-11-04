package com.example.laizuhong.sinaweibo.bean;

import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

import java.io.Serializable;

/**
 * Created by laizuhong on 2015/11/4.
 */
public class Repost implements Serializable {
    String created_at;
    long id;
    String text;
    String source;
    boolean favorited;
    boolean truncated;
    String in_reply_to_status_id;
    String in_reply_to_user_id;
    String in_reply_to_screen_name;
    String geo;
    String mid;
    int reposts_count;
    int comments_count;
    String annotations;
    User user;
    Status retweeted_status;

    public Repost(String created_at, long id, String text, String source, boolean favorited, boolean truncated, String in_reply_to_status_id, String in_reply_to_user_id, String in_reply_to_screen_name, String geo, String mid, int reposts_count, int comments_count, String annotations, User user, Status retweeted_status) {
        this.created_at = created_at;
        this.id = id;
        this.text = text;
        this.source = source;
        this.favorited = favorited;
        this.truncated = truncated;
        this.in_reply_to_status_id = in_reply_to_status_id;
        this.in_reply_to_user_id = in_reply_to_user_id;
        this.in_reply_to_screen_name = in_reply_to_screen_name;
        this.geo = geo;
        this.mid = mid;
        this.reposts_count = reposts_count;
        this.comments_count = comments_count;
        this.annotations = annotations;
        this.user = user;
        this.retweeted_status = retweeted_status;
    }


    public Repost() {
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public String getIn_reply_to_status_id() {
        return in_reply_to_status_id;
    }

    public void setIn_reply_to_status_id(String in_reply_to_status_id) {
        this.in_reply_to_status_id = in_reply_to_status_id;
    }

    public String getIn_reply_to_user_id() {
        return in_reply_to_user_id;
    }

    public void setIn_reply_to_user_id(String in_reply_to_user_id) {
        this.in_reply_to_user_id = in_reply_to_user_id;
    }

    public String getIn_reply_to_screen_name() {
        return in_reply_to_screen_name;
    }

    public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
        this.in_reply_to_screen_name = in_reply_to_screen_name;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public int getReposts_count() {
        return reposts_count;
    }

    public void setReposts_count(int reposts_count) {
        this.reposts_count = reposts_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public String getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getRetweeted_status() {
        return retweeted_status;
    }

    public void setRetweeted_status(Status retweeted_status) {
        this.retweeted_status = retweeted_status;
    }
}