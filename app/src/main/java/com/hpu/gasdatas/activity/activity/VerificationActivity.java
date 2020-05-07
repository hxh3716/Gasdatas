package com.hpu.gasdatas.activity.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.util.MD5Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 授权验证后才能进入登录页面使用
 */
public class VerificationActivity extends AppCompatActivity {
    @BindView(R.id.btn_commit)
    Button mbtnCommit;
    @BindView(R.id.ed_code)
    EditText edCode;
    @BindView(R.id.tv_imei)
    TextView tvImei;


    private static final String TAG = "VerificationActivity";
    private static final int REQUEST_CODE = 01;
    public static final String SALT_STRING = "a7bd5e7fc8da6fdc764a56e9b0d5f4edac4673bd278dbfa7e563bd9a63b9sdf26se6sa6e6a7f54eabcd6643c67cd82ef8837";
    private SharedPreferences sp ;
    public String imei="";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        ButterKnife.bind(this);
        getIMEICode();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getIMEICode() {

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
            return;
        }
        getIMEI();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == 0) {
            getIMEI();
        } else {
            Toast.makeText(this, "为了不影响使用，请前往设置开启访问设备权限", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getIMEI() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            imei = telephonyManager.getDeviceId(0);

            if (!TextUtils.isEmpty(imei)) {
                tvImei.setText(imei);
                VertifyHash(imei);
            } else {
                Toast.makeText(this, "改设备暂不支持此应用", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "onRequestPermissionsResult: " + e);
        }
    }

    /**
     * 字节数组转化为16进制字符串
     */
    public static String byteToHexStr(byte[] bytes) {
        String r = "";

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;


    }

    @OnClick(R.id.btn_commit)
    public void Onclick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                if (!TextUtils.isEmpty(edCode.getText())){
                    if (!TextUtils.isEmpty(imei)){
                        MessageDigest md = null;
                        try {
                            md = MessageDigest.getInstance("SHA");
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        String hashResult = byteToHexStr(md.digest((SALT_STRING + imei).getBytes()));
                        if (hashResult.equals(edCode.getText().toString())){
                            sp=getSharedPreferences("Authorize", this.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("hash", hashResult);
                            editor.commit();
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(this,"授权码错误请重新输入",Toast.LENGTH_SHORT).show();
                            edCode.setText("");
                        }

                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 验证hash判断是否进入登录页面
     */
    private void VertifyHash(String imei) {
        getSharedPreferences("Authorize", this.MODE_PRIVATE);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String hashResult = byteToHexStr(md.digest((SALT_STRING + imei).getBytes()));
        Log.e(TAG, "VertifyHash: "+hashResult );
        String origiHash = sp.getString("hash", null);
        if (TextUtils.isEmpty(origiHash)) {
            //配置文件无哈希值，第一次进入
            Toast.makeText(this, "请联系管理员获取授权码！", Toast.LENGTH_SHORT).show();
        } else {
            //非首次进入，判断哈希值是否正确
            if (hashResult.equals(origiHash)) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                // 还未授权，提醒授权
                Toast.makeText(this,"请联系管理员获取授权码",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
