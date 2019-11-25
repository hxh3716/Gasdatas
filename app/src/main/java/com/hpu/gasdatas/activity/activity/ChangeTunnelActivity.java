package com.hpu.gasdatas.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.contants.Contants;
import com.hpu.gasdatas.activity.database.DatabaseHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 更改巷道
 */
public class ChangeTunnelActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    @BindView(R.id.btn_cancle)
    Button mCancleBtn;
    @BindView(R.id.btn_sure)
    Button mSureBtn;
    @BindView(R.id.tv_tunnel)
    TextView mTunnelTv ;
    @BindView(R.id.tv_groups)
    TextView mGroupsTv;
    @BindView(R.id.tv_holes)
    TextView mHolesTv;
    @BindView(R.id.tv_detail)
    TextView mTvDetail;
    @BindView(R.id.ed_groups)
    EditText mGroupsEd;
    @BindView(R.id.ed_holes)
    EditText mHolesEd;
   private DatabaseHelper dbhelper;
   private SQLiteDatabase db;
   private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_tunnel);
        ButterKnife.bind(this);
        intent=getIntent();
        mTunnelTv.setText("H"+intent.getStringExtra("tunnel"));
        mGroupsEd.setText(intent.getStringExtra("groups"));
        mHolesEd.setText(intent.getStringExtra("holes"));
        mTvDetail.setText(intent.getStringExtra("detail"));
        dbhelper=new DatabaseHelper(this, Contants.DATABASE_NAME,null,Contants.DATABASE_VERSION);
        db=dbhelper.getWritableDatabase();
        mGroupsEd.setOnFocusChangeListener(this::onFocusChange);
        mHolesEd.setOnFocusChangeListener(this::onFocusChange);
        //组数输入监听
        mGroupsEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mGroupsTv.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //钻孔数输入监听
        mHolesEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mHolesTv.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    @OnClick({R.id.btn_sure,R.id.btn_cancle})
    public void Click(View v ){
        switch (v.getId()){
            case R.id.btn_cancle:
                intent=new Intent(ChangeTunnelActivity.this,MeasurePlaceActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
                break;
            case R.id.btn_sure:
                Change();
                break;
        }
    }
    //更改数据库巷道信息
    public void Change(){

        if (mHolesEd.getText().toString().equals(intent.getStringExtra("holes"))&& mGroupsEd.getText().toString().equals(intent.getStringExtra("groups"))){
            Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
            intent=new Intent(ChangeTunnelActivity.this,MeasurePlaceActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
            return;
        }
        if (mGroupsEd.getText().toString().equals("")){
           mGroupsTv.setText("组数不能为空！");
            return;
        }
        if (mHolesEd.getText().toString().equals("")){
            mHolesTv.setText("钻孔数不能为空！");
            return;
        }
        ContentValues contentValues=new ContentValues();
        contentValues.put("groups",Integer.parseInt(mGroupsEd.getText().toString()));
        contentValues.put("holes",Integer.parseInt(mHolesEd.getText().toString()));
        db.update(Contants.place_table,contentValues,"tunnel"+"="+Integer.parseInt(intent.getStringExtra("tunnel")),null);
        db.close();
        Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
        intent=new Intent(ChangeTunnelActivity.this,MeasurePlaceActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()){
            case R.id.ed_groups:
                EdStyle(mGroupsEd);
                break;
            case R.id.ed_holes:
                EdStyle(mHolesEd);
                break;
        }
    }

    /**
     * 设置焦点
     * @param mEd
     */
    public void EdStyle(EditText mEd){
        mGroupsEd.setBackgroundResource(R.drawable.bar_edittext_);
        mHolesEd.setBackgroundResource(R.drawable.bar_edittext_);
        mEd.setBackgroundResource(R.drawable.bar_edittext_special);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent=new Intent(ChangeTunnelActivity.this,MeasurePlaceActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
}
