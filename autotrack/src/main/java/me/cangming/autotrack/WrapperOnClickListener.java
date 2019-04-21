package me.cangming.autotrack;

import android.view.View;

/**
 * 自定义 onClickListener
 *
 * @author cangming
 */
public class WrapperOnClickListener implements View.OnClickListener {

    private View.OnClickListener mSource;

    public WrapperOnClickListener(View.OnClickListener listener) {
        mSource = listener;
    }

    @Override
    public void onClick(View view) {
        try {
            if (mSource != null) {
                mSource.onClick(view);
            }
            // 插入埋点代码
            CmDataPrivate.trackAppViewOnClick(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
