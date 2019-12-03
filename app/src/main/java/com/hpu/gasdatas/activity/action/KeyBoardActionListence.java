package com.hpu.gasdatas.activity.action;

import android.text.Editable;

/**
 * 说明：键盘输入监听
 */
public interface KeyBoardActionListence {
    void onComplete(); //完成点击

    void onTextChange(Editable editable); //文本改变

    void onClear(); //正在删除

    void onClearAll(); //全部清除
    void onText(int primaryCode ); //点击当前的按键
}
