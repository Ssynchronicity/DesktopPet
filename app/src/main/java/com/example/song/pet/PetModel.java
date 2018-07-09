package com.example.song.pet;

import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PetModel extends LitePalSupport {
    private String originalName;  // 宠物皮肤名称
    private String name;  // 宠物名称
    private String appellation;  // 对主人的称呼
    private String birthday;  // 生日
    private long id;

    public PetModel(String originalName, String name, String appellation) {
        this.originalName = originalName;
        this.name = name;
        this.appellation = appellation;
        this.birthday = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public PetModel() {
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public long getId() {
        return id;
    }
}
