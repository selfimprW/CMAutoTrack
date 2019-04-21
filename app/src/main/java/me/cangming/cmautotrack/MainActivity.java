package me.cangming.cmautotrack;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import me.cangming.autotrack.CmDataApi;

/**
 * @author cangming
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * data
     */
    public static final int PERMISSION_READ_CONTACTS = 100;
    /**
     * UI
     */
    private Button mBtnClickTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initPermission();
    }

    private void initView() {
        mBtnClickTest = findViewById(R.id.btn_click_test);
        mBtnClickTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_click_test:
                Toast.makeText(this, "点击测试", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            //具有权限
        } else {
            //没有权限，需要申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        CmDataApi.getInstance().ignoreAutoTrackActivity(MainActivity.class);

        switch (requestCode) {
            case PERMISSION_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户点击允许
                } else {
                    //用户点击机制
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        CmDataApi.getInstance().removeAutoTrackActivity(MainActivity.class);
    }


}
