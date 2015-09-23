package com.example.laizuhong.sinaweibo.bean;

import java.io.Serializable;

/**
 * Created by laizuhong on 2015/9/18.
 */
public class Picture implements Serializable {

    public String parentName;
    public long size;
    public String displayName;
    public String path;
    public boolean isChecked;
    public int id;
    public int child;
    public int group;

    public Picture() {
    }

    public Picture(String parentName, long size, String displayName, String path, boolean isChecked, int id, int child, int group) {
        this.parentName = parentName;
        this.size = size;
        this.displayName = displayName;
        this.path = path;
        this.isChecked = isChecked;
        this.id = id;
        this.child = child;
        this.group = group;
    }

    public Picture(String path) {
        this.path = path;
    }

    public Picture(String parentName, long size, String displayName, String path, boolean isChecked, int id) {
        this.parentName = parentName;
        this.size = size;
        this.displayName = displayName;
        this.path = path;
        this.isChecked = isChecked;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "parentName='" + parentName + '\'' +
                ", size=" + size +
                ", displayName='" + displayName + '\'' +
                ", path='" + path + '\'' +
                ", isChecked=" + isChecked +
                ", id=" + id +
                ", child=" + child +
                ", group=" + group +
                '}';
    }

    public int getChild() {

        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
