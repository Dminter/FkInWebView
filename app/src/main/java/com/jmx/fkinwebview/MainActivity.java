package com.jmx.fkinwebview;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import java.util.List;

public class MainActivity extends Activity {
    private final Intent mAccessibleIntent =
            new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    private Button switchPlugin, switchCopy;
    private SharedPreferences mySharedPreferences;
    private boolean isCopyListen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySharedPreferences = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        switchPlugin = (Button) findViewById(R.id.switchPlugin);
        switchCopy = (Button) findViewById(R.id.switchCopy);
        switchPlugin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mAccessibleIntent);
            }
        });
        startService(new Intent(this, LinkService.class));
        initCopyBtn();
        switchCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipCopy(!isCopyListen);
            }
        });
    }

    private void initCopyBtn() {
        isCopyListen = mySharedPreferences.getBoolean("copy", true);
        if (isCopyListen) {
            switchCopy.setText("复制链接即刻打开");
        } else {
            switchCopy.setText("复制链接无动作");
        }
    }

    private void swipCopy(boolean isCopyListen) {
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("copy", isCopyListen);
        editor.commit();
        initCopyBtn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
    }

    private void updateServiceStatus() {
        boolean serviceEnabled = false;
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.LinkService")) {
                serviceEnabled = true;
                break;
            }
        }
        if (serviceEnabled) {
            switchPlugin.setText("关闭插件");
        } else {
            switchPlugin.setText("开启插件");
        }
    }
}
