package com.hpu.gasdatas.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.database.DatabaseHelper;
import com.hpu.gasdatas.activity.util.place;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hpu.gasdatas.activity.contants.Contants.DATABASE_NAME;
import static com.hpu.gasdatas.activity.contants.Contants.DATABASE_VERSION;
import static com.hpu.gasdatas.activity.contants.Contants.place_table;

public class AddTunnelActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    @BindView(R.id.btn_sure)
    Button mSureBtn;
    @BindView(R.id.btn_cancle)
    Button mCancleBtn;
    @BindView(R.id.ed_place_tunnel)
    EditText mTunnelEd;
    @BindView(R.id.ed_place_groups)
    EditText mGroupsEd;
    @BindView(R.id.ed_place_holes)
    EditText mHolesEd;
    @BindView(R.id.ed_detail)
    EditText mEdDetail;
    @BindView(R.id.tv_groups)
    TextView mGroupsTv;
    @BindView(R.id.tv_holes)
    TextView mHolesTv;
    @BindView(R.id.tv_tunnel)
    TextView mTunnelTv;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tunnel);
        ButterKnife.bind(this);
        dbHelper = new DatabaseHelper(this, DATABASE_NAME, null, DATABASE_VERSION);
        //调用SQLiteHelper.OnCreate()
        db = dbHelper.getWritableDatabase();

        //巷道号输入监听
        mTunnelEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                    search();


            }
        });
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
        mTunnelEd.setOnFocusChangeListener(this::onFocusChange);
        mGroupsEd.setOnFocusChangeListener(this::onFocusChange);
        mHolesEd.setOnFocusChangeListener(this::onFocusChange);
        mEdDetail.setOnFocusChangeListener(this::onFocusChange);
        mEdDetail.requestFocus();
        //禁止回车
        mEdDetail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
    }

    @OnClick({R.id.btn_sure, R.id.btn_cancle})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                insert();
                break;
            case R.id.btn_cancle:
                intent = new Intent(AddTunnelActivity.this, MeasurePlaceActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }

    //查询数据库中是否存在
    public void search() {
        Cursor cursor = db.query(place_table, new String[]{"tunnel"}, "tunnel=?", new String[]{mTunnelEd.getText().toString()}, null, null, null);

        if (cursor.getCount() != 0) {
            mTunnelTv.setText("该巷道已经存在!");

        } else {
            mTunnelTv.setText("");
        }
        cursor.close();
    }

    //往数据库中插入数据
    public void insert() {
        Cursor cursor = db.query(place_table, new String[]{"tunnel"}, "tunnel=?", new String[]{mTunnelEd.getText().toString()}, null, null, null);
        if (cursor.getCount() != 0) {
            mTunnelTv.setText("该巷道已经存在!");
            Toast.makeText(AddTunnelActivity.this, "该巷道已经存在", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mTunnelEd.getText().toString().equals("")) {
            mTunnelTv.setText("巷道号不能为空!");
            return;
        }
        if (mGroupsEd.getText().toString().equals("")) {
            mGroupsTv.setText("组数不能为空!");
            return;
        }
        if (mHolesEd.getText().toString().equals("")) {
            mHolesTv.setText("钻孔数不能为空!");
            return;
        }
        if (mEdDetail.getText().toString().equals("")){
            Toast.makeText(AddTunnelActivity.this,"巷道描述不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("tunnel", Integer.parseInt(mTunnelEd.getText().toString()));
        values.put("groups", Integer.parseInt(mGroupsEd.getText().toString()));
        values.put("holes", Integer.parseInt(mHolesEd.getText().toString()));
        values.put("detail",mEdDetail.getText().toString());
        db.insert(place_table, null, values);
        Toast.makeText(AddTunnelActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
        db.close();
        intent = new Intent(AddTunnelActivity.this, MeasurePlaceActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();

        SharedPreferences sp = getSharedPreferences("ch4data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

//            editor.putInt("tunnelItem",0);
            editor.putInt("tunnel", 0);
            editor.putInt("groups", 0);
            editor.putInt("holes",0);
//            editor.putInt("tunnelItem",0);
            editor.commit();


    }

    /**
     * 统一设置下划线样式
     *
     * @param med
     */
    public void EdStyle(EditText med) {
        mTunnelEd.setBackgroundResource(R.drawable.bar_edittext_);
        mGroupsEd.setBackgroundResource(R.drawable.bar_edittext_);
        mHolesEd.setBackgroundResource(R.drawable.bar_edittext_);
        mEdDetail.setBackgroundResource(R.drawable.bar_edittext_);
        med.setBackgroundResource(R.drawable.bar_edittext_special);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(AddTunnelActivity.this, MeasurePlaceActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    /**
     * 输入框获得焦点监听
     *
     * @param view
     * @param b 是否获得焦点
     */
    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.ed_place_tunnel:
                EdStyle(mTunnelEd);
                break;
            case R.id.ed_place_groups:
                EdStyle(mGroupsEd);
                break;
            case R.id.ed_place_holes:
                EdStyle(mHolesEd);
                break;
            case R.id.ed_detail:
                EdStyle(mEdDetail);
                break;
        }
    }
}
