package me.cangming.autotrack;

import android.support.annotation.Keep;
import android.util.Log;

/**
 * 日志工具类
 *
 * @author cangming
 */
@Keep
public class CmLog {

    private static final String TAG = "autotrack";

    @Keep
    public static void i(String message) {
        Log.i(TAG, message);
    }

    @Keep
    public static void d(String message) {
        Log.d(TAG, message);
    }

    @Keep
    public static void w(String message) {
        Log.w(TAG, message);
    }

    @Keep
    public static void v(String message) {
        Log.v(TAG, message);
    }

    @Keep
    public static void e(String message) {
        Log.e(TAG, message);
    }
}
