package com.example.song.pet;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class PetAlarmModel extends LitePalSupport {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int hour;

    @Column(nullable = false)
    private int minute;

    @Column(nullable = false)
    private long id;

    @Column(nullable = false)
    private int pid;

    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public long getId() {
        return id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
