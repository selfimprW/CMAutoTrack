package me.cangming.autotrack;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*public*/ class CmDataPrivate {
    private static List<Integer> mIgnoreActivities;

    static {
        mIgnoreActivities = new ArrayList<>();
    }

    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"
            + " .SSS", Locale.CHINA);

    /**
     * 添加除忽略统计Activity集合，为了权限判断的兼容处理
     *
     * @param activity 页面
     */
    public static void ignoreAutoTrackActivity(Class<?> activity) {
        if (activity == null) {
            return;
        }

        mIgnoreActivities.add(activity.hashCode());
    }

    /**
     * 移除忽略统计Activity集合,为了权限判断的兼容处理
     *
     * @param activity
     */
    public static void removeIgnoreActivity(Class<?> activity) {
        if (activity == null) {
            return;
        }
        if (mIgnoreActivities.contains(activity.hashCode())) {
            mIgnoreActivities.remove(activity.hashCode());
        }
    }

    /**
     * JsonObject格式化时间
     *
     * @param source 源处理数据（final形容，防止改变）
     * @param dest   处理后的数据
     */
    public static void mergeJsonObjectByFormatDate(final JSONObject source, JSONObject dest) throws JSONException {
        Iterator<String> superPropertiesIterator = source.keys();
        while (superPropertiesIterator.hasNext()) {
            String key = superPropertiesIterator.next();
            Object value = source.get(key);
            if (value instanceof Date) {
                dest.put(key, mDateFormat.format(value));
            } else {
                dest.put(key, value);
            }
        }
    }

    /**
     * 获取 ToolBar的标题
     *
     * @return 标题
     */
    @TargetApi(11)
    private static String getToolBarTitle(Activity activity) {

        if (activity == null) {
            return null;
        }

        try {
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                if (!TextUtils.isEmpty(actionBar.getTitle())) {
                    return actionBar.getTitle().toString();
                }
            } else {
                if (activity instanceof AppCompatActivity) {
                    AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
                    android.support.v7.app.ActionBar supportActionBar = appCompatActivity.getSupportActionBar();
                    if (supportActionBar != null) {
                        if (!TextUtils.isEmpty(supportActionBar.getTitle())) {
                            return supportActionBar.getTitle().toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取 Activity 的Title
     *
     * @param activity Activity
     * @return String 当前页面 title
     */
    private static String getActivityTitle(Activity activity) {
        String activityTitle = null;

        if (activity == null) {
            return null;
        }

        try {
            activityTitle = activity.getTitle().toString();

            if (Build.VERSION.SDK_INT >= 11) {
                String toolBarTitle = getToolBarTitle(activity);
                if (!TextUtils.isEmpty(toolBarTitle)) {
                    activityTitle = toolBarTitle;
                }
            } else {
                PackageManager packageManager = activity.getPackageManager();
                if (packageManager != null) {
                    ActivityInfo activityInfo = packageManager.getActivityInfo(activity.getComponentName(), 0);
                    if (activityInfo != null) {
                        activityTitle = activityInfo.loadLabel(packageManager).toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return activityTitle;
    }

    /**
     * 跟踪 页面显示的方法
     *
     * @param activity Activity
     */
    @Keep
    public static void trackAppViewScreen(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            //如果在忽略列表中，则返回
            if (mIgnoreActivities.contains(activity.hashCode())) {
                return;
            }
            JSONObject properties = new JSONObject();
            properties.put("$activity", activity.getClass().getCanonicalName());
            properties.put("title", getActivityTitle(activity));
            CmDataApi.getInstance().track("$AppViewScreen", properties);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册 Application.registerActivityLifecycleCallbacks 监听生命周期
     *
     * @param application
     */
    @TargetApi(14)
    public static void registerActivityLifecycleCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                // 显示的时候开始监听
                trackAppViewScreen(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * 获取设备信息
     *
     * @param context
     * @return
     */
    public static Map<String, Object> getDeviceInfo(Context context) {
        final Map<String, Object> deviceInfo = new HashMap<>();
        {
            deviceInfo.put("$lib", "Android");
            deviceInfo.put("$lib_version", CmDataApi.SDK_VERSION);
            deviceInfo.put("$os", "Android");
            deviceInfo.put("$os_version", Build.VERSION.RELEASE == null ? "UNKNOWN" : Build.VERSION.RELEASE);
            deviceInfo.put("$manufacturer", Build.MANUFACTURER == null ? "UNKNOWN" : Build.MANUFACTURER);

            if (TextUtils.isEmpty(Build.MODEL)) {
                deviceInfo.put("$model", "UNKNOWN");
            } else {
                deviceInfo.put("$model", Build.MODEL.trim());
            }

            try {
                final PackageManager manager = context.getPackageManager();
                PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
                deviceInfo.put("$app_version", packageInfo.versionName);

                int labelRes = packageInfo.applicationInfo.labelRes;
                deviceInfo.put("$app_name", context.getResources().getString(labelRes));
            } catch (Exception e) {
                e.printStackTrace();
            }

            final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            deviceInfo.put("$screen_height", displayMetrics.heightPixels);
            deviceInfo.put("$screen_width", displayMetrics.widthPixels);

            // 返回的Map 不可修改
            return Collections.unmodifiableMap(deviceInfo);
        }
    }

    /**
     * 获取 AndroidID
     *
     * @param context Contenxt
     * @return String AndroidID
     */
    public static String getAndroidId(Context context) {
        String androidID = "";
        try {
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return androidID;
    }

    /**
     * {字符添加间距\t}
     *
     * @param sb     字符
     * @param indent 多少次
     */
    private static void addIntentBlank(StringBuilder sb, int indent) {
        try {
            for (int i = 0; i < indent; i++) {
                sb.append("\t");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 格式化字符
     *
     * @param jsonStr String 输入
     * @return 格式化后的结果
     */
    public static String formatJson(String jsonStr) {
        try {
            if (TextUtils.isEmpty(jsonStr)) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            char last;
            char current = '\0';
            int indent = 0;
            boolean isInQuotationMarks = false;

            for (int i = 0; i < jsonStr.length(); i++) {
                last = current;
                current = jsonStr.charAt(i);
                switch (current) {
                    case '"':
                        if (last != '\\') {
                            isInQuotationMarks = !isInQuotationMarks;
                        }
                        sb.append(current);
                        break;
                    case '{':
                    case '[':
                        sb.append(current);
                        if (!isInQuotationMarks) {
                            sb.append('\n');
                            indent++;
                            addIntentBlank(sb, indent);
                        }
                        break;
                    case '}':
                    case ']':
                        if (!isInQuotationMarks) {
                            sb.append('\n');
                            indent--;
                            addIntentBlank(sb, indent);
                        }
                        sb.append(current);
                        break;
                    case ',':
                        sb.append(current);
                        if (last != '\\' && !isInQuotationMarks) {
                            sb.append('\n');
                            addIntentBlank(sb, indent);
                        }
                        break;
                    default:
                        sb.append(current);
                        break;
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


}
