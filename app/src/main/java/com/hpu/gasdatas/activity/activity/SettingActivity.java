package com.hpu.gasdatas.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bin.david.form.annotation.SmartTable;
import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.util.SoftKeyboardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    @BindView(R.id.ed_ad_name)
    EditText mEdName;
    @BindView(R.id.ed_ad_pwd)
    EditText mEdPwd;
    @BindView(R.id.btn_setting_sub)
    Button mBtnSure;
    @BindView(R.id.btn_setting_back)
    Button mBtnBack;

    @BindView(R.id.layout_title)
    LinearLayout mLayoutTitle;
    @BindView(R.id.tv_show)
    TextView mTvShow;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        //用户名输入不能回车(密码回车默认确定)
        mEdName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER);
            }
        });

        mEdName.setText("管理员");

        mEdName.setOnFocusChangeListener(this::onFocusChange);
        mEdPwd.setOnFocusChangeListener(this::onFocusChange);

    }

    @OnClick({R.id.btn_setting_sub, R.id.btn_setting_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setting_back:
                finish();
                break;
            case R.id.btn_setting_sub:
               if (mEdName.getText().toString().equals("")){
                   Toast.makeText(SettingActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                   return;
               }
                if (mEdPwd.getText().toString().equals("")){
                    Toast.makeText(SettingActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mEdName.getText().toString().equals("管理员")&&mEdPwd.getText().toString().equals("123")){
                    Intent intent = new Intent(SettingActivity.this, MeasurePlaceActivity.class);
                    startActivity(intent);
                    SharedPreferences sp=getSharedPreferences("admin",MODE_PRIVATE);
                   SharedPreferences.Editor editor=sp.edit();
                   editor.putString("adminname",mEdName.getText().toString());
                   editor.commit();
                }else {
                    Toast.makeText(SettingActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()){
            case R.id.ed_ad_name:
                EdStyle(mEdName);
                break;
            case R.id.ed_ad_pwd:
                EdStyle(mEdPwd);
                break;
        }
    }
    public void EdStyle(EditText mEd){
        mEdName.setBackgroundResource(R.drawable.edittext_);
        mEdPwd.setBackgroundResource(R.drawable.edittext_);
        mEd.setBackgroundResource(R.drawable.edittext_special);
    }
}
