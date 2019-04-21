# CMAutoTrack

> Android 自动化埋点方案

## 使用方法
```java

    implementation 'me.cangming.autotrack:autotrack:0.0.2'

```

## 已完成
1. 浏览页面显示监听
(1) 上报 title + 本机信息
(2) 适配权限，重复 onResume 事件 ，详情请看demo 中的写法
    ```java

    // 在申请权限的时候移除统计
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
             super.onRequestPermissionsResult(requestCode, permissions, grantResults);
             CmDataApi.getInstance().ignoreAutoTrackActivity(MainActivity.class);
        }
        // 在 onStop() 恢复申请权限的统计
        @Override
        protected void onStop() {
             super.onStop();
             CmDataApi.getInstance().removeAutoTrackActivity(MainActivity.class);
        }
    ```
2. Button类 View 点击可以监听

## todo
1. ASM 监听事件替代反射
