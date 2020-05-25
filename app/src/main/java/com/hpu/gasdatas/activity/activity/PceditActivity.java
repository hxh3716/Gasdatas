package com.hpu.gasdatas.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.fragmnet.DataFragment;
import com.hpu.gasdatas.activity.fragmnet.HistoryFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hpu.gasdatas.activity.contants.Contants.Title;

/**
 * Created by：何学慧
 * Detail:数据录入
 * on 2019/10/27
 */
public class PceditActivity extends AppCompatActivity {
    private static final String TAG = "PceditActivity";
    @BindView(R.id.voiceflag)
    TextView mTxVoiceFalg;  //设置当前的语音是否开启,方便进行传值
    @BindView(R.id.img_voice)
    ImageView mVoiceImg;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.date)
    TextView mTvDate;
    @BindView(R.id.btn_drawer_data)
    Button mData;
    @BindView(R.id.btn_drawer_history)
    Button mBtnHistory;
    @BindView(R.id.btn_drawer_change)
    Button mBtnChange;
    @BindView(R.id.draw_data)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNalayout;
    private Intent intent;
    private HistoryFragment historyFragment;
    private DataFragment dataFragment;
    private TextToSpeech textToSpeech = null;  //语音对象
    private boolean flag=false;
    private  SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcedit);
        ButterKnife.bind(this);
        intent = getIntent();
        mTvName.setText(intent.getStringExtra("name"));
        mTvDate.setText(sdfTime());
        historyFragment = new HistoryFragment();
        dataFragment = new DataFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.frag_container,historyFragment);
        fragmentTransaction.add(R.id.frag_container, dataFragment);
//        fragmentTransaction.hide(historyFragment);
        fragmentTransaction.commit();

        sp = getSharedPreferences("ch4data", Context.MODE_PRIVATE);
       //获得上次关闭页面时记住的`语音的选择
       if (sp!=null){
           mTxVoiceFalg.setText(sp.getString("voiceFlag","false"));
           if (sp.getString("voiceFlag","false").equals("true")){
               mVoiceImg.setImageResource(R.drawable.horn);
           }
       }
       //初始化语音
        initTTs();

    }

    @OnClick({R.id.tv_name, R.id.btn_drawer_data, R.id.btn_drawer_change, R.id.btn_drawer_history,R.id.img_voice})
    public void Onclick(View v) {
        switch (v.getId()) {
            case R.id.tv_name:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.btn_drawer_data:
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                dataFragment = new DataFragment();
                fragmentTransaction.replace(R.id.frag_container, dataFragment).commit();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.btn_drawer_change:
                intent = new Intent(PceditActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_drawer_history:
                FragmentTransaction frg = getSupportFragmentManager().beginTransaction();
                frg.replace(R.id.frag_container, historyFragment).commit();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.img_voice:
                //设置语音开关位开
                if (!flag){
                    return;
                }
                SharedPreferences.Editor edit=sp.edit();

                if (mTxVoiceFalg.getText().toString().equals("false")){
                    mVoiceImg.setImageResource(R.drawable.horn);
                    mTxVoiceFalg.setText("true");
                    edit.putString("voiceFlag","true");
                    Toast.makeText(PceditActivity.this,"语音输入已打开",Toast.LENGTH_SHORT).show();
                }else {
                    mVoiceImg.setImageResource(R.drawable.mute);
                    mTxVoiceFalg.setText("false");
                    edit.putString("voiceFlag","false");
                    Toast.makeText(PceditActivity.this,"语音输入已关闭",Toast.LENGTH_SHORT).show();
                }
                edit.commit();

                break;
            default:
                break;

        }
    }
    /**
     * 初始化语音对象
     */
    private void initTTs() {
        textToSpeech=new TextToSpeech(PceditActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==textToSpeech.SUCCESS){
                    flag=true;
                }else {
                    Toast.makeText(PceditActivity.this,"该设备暂不支持语音",Toast.LENGTH_SHORT).show();
                    //设置当前语音是否可用
                    flag=false;
                }
            }
        });
    }
    /**
     * 获取当前日期
     */
    public String sdfTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String date = sdf.format(new Date());
        return date;
    }

    @Override
    public void onBackPressed() {
        if (dataFragment != null && dataFragment instanceof DataFragment && !dataFragment.isHidden() && !((DataFragment) dataFragment).onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (textToSpeech!=null){
            textToSpeech.stop(); // 不管是否正在朗读TTS都被打断
            textToSpeech.shutdown(); // 关闭，释放资源
        }

    }
}
