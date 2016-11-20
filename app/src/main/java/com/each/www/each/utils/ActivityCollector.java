package com.each.www.each.utils;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by veyron 2016/10/18/14:33.
 * 全体 Activity管理类
 */
public class ActivityCollector {
    public static List<Activity> activties;

    static {
        activties = new ArrayList();
    }

    public static void addActivity(Activity activity) {
        activties.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activties.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activties) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
