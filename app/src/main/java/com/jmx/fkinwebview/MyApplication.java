package com.jmx.fkinwebview;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;


public class MyApplication extends Application {
    public Context ctx;
    public static MyApplication instance;
    private ClipboardManager cb;
    private SharedPreferences mySharedPreferences;
    private boolean isCopyListen;

    @Override
    public void onCreate() {
        super.onCreate();
        this.ctx = this.getApplicationContext();
        instance = this;
        mySharedPreferences = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        cb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cb.setPrimaryClip(ClipData.newPlainText("", ""));
        cb.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                // 具体实现
                try {
                    String content = cb.getText().toString();
                    isCopyListen = mySharedPreferences.getBoolean("copy", true);
                    if (isCopyListen && XUtil.notEmptyOrNull(content)) {
                        LinkService.linkClick(content);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static MyApplication getInstance() {
        return instance;
    }

}
