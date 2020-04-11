package com.vortexghost.plaintowerdefense.gem_shop;

import java.util.Date;

public class Gem {
    String title;
    int price;
    int imageResource;
    int amount;
    // 행사할 때 필요한 변수

    // 홍보여부, 할인 비율, 할인 가격, 남은 할인 시간
    boolean isPromotion;
    int promotionRate;
    int promotedPrice;
    String promoteString;
    Date promoteDate;

    public Gem(String title, int price, int imageResource) {
        this.title = title;
        this.price = price;
        this.imageResource = imageResource;
    }

    public Gem(String title, int price, int imageResource, int amount) {
        this.title = title;
        this.price = price;
        this.imageResource = imageResource;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion) {
        isPromotion = promotion;
    }

    public int getPromotionRate() {
        return promotionRate;
    }

    public void setPromotionRate(int promotionRate) {
        this.promotionRate = promotionRate;
    }

    public int getPromotedPrice() {
        return promotedPrice;
    }

    public void setPromotedPrice(int promotedPrice) {
        this.promotedPrice = promotedPrice;
    }

    public Date getPromoteDate() {
        return promoteDate;
    }

    public void setPromoteDate(Date promoteDate) {
        this.promoteDate = promoteDate;
    }

    public String getPromoteString() {
        return promoteString;
    }

    public void setPromoteString(String promoteString) {
        this.promoteString = promoteString;
    }
}
