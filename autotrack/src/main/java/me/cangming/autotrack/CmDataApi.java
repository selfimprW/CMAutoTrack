package me.cangming.autotrack;

import android.app.Application;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.util.Map;

@Keep
public class CmDataApi {
    private final String TAG = this.getClass().getSimpleName();
    public static final String SDK_VERSION = "0.0.1";
    private static CmDataApi INSTANCE;
    private static final Object mLock = new Object();
    private static Map<String, Object> mDeviceInfo;
    private String mDeviceId;

    @Keep
    @SuppressWarnings("UnusedReturnValue")
    public static CmDataApi init(Application application) {
        synchronized (mLock) {
            if (null == INSTANCE) {
                INSTANCE = new CmDataApi(application);
            }
            return INSTANCE;
        }
    }

    @Keep
    public static CmDataApi getInstance() {
        return INSTANCE;
    }

    /**
     * 私有化成员变量
     *
     * @param application
     */
    private CmDataApi(Application application) {
        mDeviceInfo = CmDataPrivate.getDeviceInfo(application);
        mDeviceId = CmDataPrivate.getAndroidId(application);
        CmDataPrivate.registerActivityLifecycleCallbacks(application);
    }

    /**
     * track 事件
     *
     * @param eventName  事件名称
     * @param properties json 事件属性
     */
    public void track(@NonNull String eventName, @Nullable JSONObject properties) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", eventName);
            jsonObject.put("device_id", mDeviceId);

            JSONObject sendProperties = new JSONObject(mDeviceInfo);

            if (properties != null) {
                CmDataPrivate.mergeJsonObjectByFormatDate(properties, sendProperties);
            }

            jsonObject.put("properties", sendProperties);
            jsonObject.put("time", System.currentTimeMillis());

            Log.i(TAG, CmDataPrivate.formatJson(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
