package com.jmx.fkinwebview;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.Spannable;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LinkService extends AccessibilityService {

    ArrayList<Integer> eventTypeList = new ArrayList<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();
//        XUtil.debug("getEventType: " + event.getEventType());
        eventTypeList.add(event.getEventType());
        if (event.getEventType() == 8192 || event.getEventType() == 2048 || event.getEventType() == 1 || event.getEventType() == 32) {
            if (XUtil.listNotNull(eventTypeList)) {
                if (eventTypeList.get(eventTypeList.size() - 1) == 16 || eventTypeList.get(eventTypeList.size() - 1) == 2048) {
                    eventTypeList = new ArrayList<>();
                    return;
                }
            }
            if (!getBrowserApp().packageName.equals(event.getPackageName()) && !event.getClassName().equals("android.widget.EditText")) {
                eventTypeList = new ArrayList<>();
                traverseNode(nodeInfo);
            }
        }
    }

    private void traverseNode(AccessibilityNodeInfo node) {
        try {
            if (null == node) return;
            CharSequence text = node.getText();
            if (null != text && text.length() > 0) {
                String url = text.toString();
                if (linkClick(url)) return;
                node.performAction(GLOBAL_ACTION_BACK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean linkClick(String url) {
        if (!XUtil.notEmptyOrNull(url)) {
            return true;
        }
        url = decorateTrendInSpannableString(url, MyPatterns.WEB_URL);
        if (!XUtil.notEmptyOrNull(url)) {
            return true;
        }
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getInstance().ctx.startActivity(it);
        return false;
    }


    // 把需要着色的下标找色
    public static String decorateTrendInSpannableString(String string,
                                                        Pattern pattern) {
//        XUtil.debug("==? string" + string);
        String ret = null;
        List<Map<String, Object>> list = getStartAndEndIndex(string, pattern);
        int size = list.size();
        if (list != null && size > 0) {
            for (int i = 0; i < size; i++) {
                Map<String, Object> map = list.get(i);
                int start = (Integer) map.get("startIndex");
                int end = (Integer) map.get("endIndex");
//                String spanStr = spannableString.subSequence(start, end).toString();
//                spannableString.setSpan(new URLSpanUtils(spanStr), start, end,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ret = string.substring(start, end);
//                XUtil.debug("==?" + start + " " + end + " " + ret);
            }
        }
        return ret;
    }

    // 找需要着色的下标
    public static List<Map<String, Object>> getStartAndEndIndex(String sourceStr, Pattern pattern) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Matcher matcher = pattern.matcher(sourceStr);
        boolean isFind = matcher.find();
        while (isFind) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("startIndex", matcher.start());
            map.put("endIndex", matcher.end());
            list.add(map);
            isFind = matcher.find((Integer) map.get("endIndex") - 1);
        }
        return list;
    }


    public static String getUrl(String str) {
        String REGEX_URL = "(http:\\/\\/|https:\\/\\/)((\\w|=|\\?|\\:|\\;|\\#|\\.|\\/|&|-)+)";
        String url = null;
        Pattern pattern = Pattern.compile(REGEX_URL, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        boolean isFind = matcher.find();
        while (isFind) {
            url = matcher.group();
            break;
        }
        return url;
    }

    public static ActivityInfo getBrowserApp() {
        String default_browser = "android.intent.category.DEFAULT";
        String browsable = "android.intent.category.BROWSABLE";
        String view = "android.intent.action.VIEW";
        Intent intent = new Intent(view);
        intent.addCategory(default_browser);
        intent.addCategory(browsable);
        Uri uri = Uri.parse("http://");
        intent.setDataAndType(uri, null);
        List<ResolveInfo> resolveInfoList = MyApplication.getInstance().ctx.getPackageManager().queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        if (resolveInfoList.size() > 0) {
            ActivityInfo activityInfo = resolveInfoList.get(0).activityInfo;
            return activityInfo;
        } else {
            return null;
        }
    }

    @Override
    public void onInterrupt() {

    }
}
