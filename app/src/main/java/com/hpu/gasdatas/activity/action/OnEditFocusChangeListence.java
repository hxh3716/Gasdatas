package com.hpu.gasdatas.activity.action;

import android.view.View;

/**
 * 说明：由于使用了OnFocusChangeListener接口，如果项目中需要使用到可以使用这个接口
 */
public interface OnEditFocusChangeListence {
    void OnFocusChangeListener(View v, boolean hasFocus);
}
