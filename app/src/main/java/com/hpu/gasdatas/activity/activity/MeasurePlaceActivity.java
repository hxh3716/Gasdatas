package com.hpu.gasdatas.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.fragmnet.HistoryFragment;
import com.hpu.gasdatas.activity.fragmnet.PlaceFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeasurePlaceActivity extends AppCompatActivity {
    @BindView(R.id.draw)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNalayout;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.place_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_adname)
    TextView mTvName;




    private Intent intent;

    private PlaceFragment placeFragment;
    private HistoryFragment historyFragment;
    private FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_place);
        ButterKnife.bind(this);
        SharedPreferences sp=getSharedPreferences("admin",MODE_PRIVATE);
        mTvName.setText(sp.getString("adminname",null));
        mContainer=findViewById(R.id.frag_container);
        placeFragment=new PlaceFragment();
        historyFragment=new HistoryFragment();
        FragmentTransaction   fragmentTransaction=getSupportFragmentManager().beginTransaction();
       // fragmentTransaction.add(R.id.frag_container,historyFragment);
        fragmentTransaction.add(R.id.frag_container,placeFragment);
     //   fragmentTransaction.hide(historyFragment);
        fragmentTransaction.commit();
    }

    @OnClick({R.id.place_toolbar,R.id.btn_drawer_place,R.id.btn_drawer_exit,R.id.btn_drawer_history})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.place_toolbar:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.btn_drawer_place:
                FragmentTransaction   fragmentTransaction=getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, placeFragment).commit();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.btn_drawer_exit:
                intent = new Intent(MeasurePlaceActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_drawer_history:
                FragmentTransaction   frg=getSupportFragmentManager().beginTransaction();
                frg.replace(R.id.frag_container, historyFragment).commit();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
                break;

        }
    }
}