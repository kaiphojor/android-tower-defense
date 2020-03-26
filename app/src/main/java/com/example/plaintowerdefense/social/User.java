package com.example.plaintowerdefense.social;

public class User {
    /*
    TODO: 이메일, 이미지(더미)
     */
    // 별명
    String nickname;
    String memo;
    //TODO : 고민해서 뺄지 말지 결정해야
    // drawable에서 일단 선택 - 더미
    int imageSource;
    boolean isFavorite;

    public User(String nickname, String memo, int imageSource) {
        this.nickname = nickname;
        this.memo = memo;
        this.imageSource = imageSource;
        this.isFavorite = false;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getImageSource() {
        return imageSource;
    }

    public void setImageSource(int imageSource) {
        this.imageSource = imageSource;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
