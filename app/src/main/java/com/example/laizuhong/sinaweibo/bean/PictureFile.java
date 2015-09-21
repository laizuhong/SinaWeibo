package com.example.laizuhong.sinaweibo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laizuhong on 2015/9/18.
 */
public class PictureFile {

    public String folderName;

    public int count;

    public List<Picture> sets = new ArrayList<Picture>();

    public String thumbnail;


    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Picture> getSets() {
        return sets;
    }

    public void setSets(List<Picture> sets) {
        this.sets = sets;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public PictureFile(String folderName, int count, List<Picture> sets, String thumbnail) {
        this.folderName = folderName;
        this.count = count;
        this.sets = sets;
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "PictureFile{" +
                "folderName='" + folderName + '\'' +
                ", count=" + count +
                ", sets=" + sets +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
