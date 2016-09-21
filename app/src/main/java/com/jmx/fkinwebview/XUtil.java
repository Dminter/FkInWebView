package com.jmx.fkinwebview;


import android.util.Log;

import java.util.List;

public class XUtil {


    public static void debug(Object string) {
        try {
            Log.i(MyApplication.getInstance().ctx.getPackageName(), String.valueOf(string));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean notEmptyOrNull(String string) {
        if (string != null && !string.equalsIgnoreCase("null") && string.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public static <T> boolean listNotNull(List<T> t) {
        if (t != null && t.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
