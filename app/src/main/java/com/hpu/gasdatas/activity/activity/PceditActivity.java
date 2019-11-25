package com.hpu.gasdatas.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.fragmnet.DataFragment;
import com.hpu.gasdatas.activity.fragmnet.HistoryFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    }

    @OnClick({R.id.tv_name, R.id.btn_drawer_data, R.id.btn_drawer_change, R.id.btn_drawer_history})
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
//                hidefragment(fragmentTransaction);
//                if (dataFragment==null){
//                    fragmentTransaction.add(R.id.frag_container,dataFragment);
//                }else {
//                    fragmentTransaction.show(dataFragment);
//                }
//                fragmentTransaction.commit();
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
            default:
                break;

        }
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
}
