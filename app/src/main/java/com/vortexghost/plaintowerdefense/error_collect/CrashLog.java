package com.vortexghost.plaintowerdefense.error_collect;


/*
예상치 못한 종료 상황을 기록한 로그
 */
public class CrashLog {
    // db의 row 한줄 번호
    long id;
    // email, 발생 시간, exception 설명,stack trace
    private String email;
    private String time;
    private String description;
    private String stackTrace;

    public CrashLog(String email, String time, String description, String stackTrace) {
        this.email = email;
        this.time = time;
        this.description = description;
        this.stackTrace = stackTrace;
    }

    public CrashLog(long id, String email, String time, String description, String stackTrace) {
        this.id = id;
        this.email = email;
        this.time = time;
        this.description = description;
        this.stackTrace = stackTrace;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getStackTrace() {
        return stackTrace;
    }
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
