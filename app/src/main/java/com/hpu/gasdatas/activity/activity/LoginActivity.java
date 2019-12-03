package com.hpu.gasdatas.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.util.SoftKeyboardUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by：何学慧
 * Detail:登录页面
 * on 2019/10/27
 */
public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.tx_login_setting)
    TextView mTxSeeting;
    @BindView(R.id.btn_login_sub)
    Button mBtnSure;
    @BindView(R.id.tv_show)
    TextView mTvShow;
    @BindView(R.id.ed_login_name)
    EditText mEdName;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.layout_title)
    LinearLayout mLayouttitle;


    private static final String TAG = "LoginActivity";
    private Intent intent;
    private SharedPreferences sp;
    private SharedPreferences datasp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        SimpleDateFormat sdfdate= new SimpleDateFormat("yyyyMMdd");
        String IDdata = sdfdate.format(new Date());
        datasp= getSharedPreferences("ch4data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=datasp.edit();
        if (datasp.getString("date",null)!=null){
            if (!datasp.getString("date",null).equals(IDdata)){
                editor.putInt("tunnel", 0);
                editor.putInt("groups", 0);
                editor.putInt("holes",0);
//                editor.putInt("tunnelItem",0);
                editor.putString("pressure",null);
                editor.putString("flow",null);
                editor.putString("tem", null);
                editor.commit();
            }
        }

        sp=getSharedPreferences("login",MODE_PRIVATE);
        mEdName.setText(sp.getString("name",null));
        mEdName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
       scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
           @Override
           public void onGlobalLayout() {
               scrollView.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       //判断现在软键盘的开关状态
                       if(SoftKeyboardUtils.isSoftShowing(LoginActivity.this)){
                           mLayouttitle.setVisibility(View.GONE);
                           mTvShow.setVisibility(View.GONE);
                           //StatusBarCompat.setLightStatusBarColor(LoginActivity.this); //关闭沉浸式
                       }else{
                           mLayouttitle.setVisibility(View.VISIBLE);
                           mTvShow.setVisibility(View.VISIBLE);
                          // StatusBarCompat.translucentStatusBar(LoginActivity.this, true); //开启沉浸式
                       }
                   }
               },10L);
           }
       });
    }



    @OnClick({R.id.tx_login_setting, R.id.btn_login_sub})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tx_login_setting:
                intent = new Intent(LoginActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login_sub:
                if (mEdName.getText().toString().equals("")) {
                    showtoast("请输入姓名");
                    return;
                }
                if (Regex(mEdName.getText().toString())){
                    intent = new Intent(LoginActivity.this, PceditActivity.class);
                    intent.putExtra("name", mEdName.getText().toString());
                    startActivity(intent);
                    //保存姓名和密码
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("name",mEdName.getText().toString());
                    editor.commit();
                }else {
                    showtoast("姓名格式有误");
                }

                break;
            default:
                break;
        }
    }

    public boolean Regex(String str) {
        String regex="^[\\u4e00-\\u9fa5]{0,}$";
        return Pattern.matches(regex,str);
    }

    public void showtoast(String mes) {
        Toast.makeText(LoginActivity.this, mes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
