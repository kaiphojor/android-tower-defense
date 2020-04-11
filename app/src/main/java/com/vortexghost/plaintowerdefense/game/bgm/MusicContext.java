package com.vortexghost.plaintowerdefense.game.bgm;

import android.content.Context;
/*
실행할 음악의 context와 resource id를 담는 class,반복 여부
async task 에서 doinbackground 에 전달용
 */
public class MusicContext {
    Context context;
    int resourceId;
    boolean isLooping;

    public MusicContext(Context context, int resourceId,boolean isLooping) {
        this.context = context;
        this.resourceId = resourceId;
        this.isLooping = isLooping;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }
}
