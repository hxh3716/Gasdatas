package com.hpu.gasdatas.activity.fragmnet;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.bg.ICellBackgroundFormat;
import com.bin.david.form.data.format.grid.BaseAbstractGridFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.style.LineStyle;
import com.bin.david.form.data.style.PointStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.utils.DensityUtils;
import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.activity.PceditActivity;
import com.hpu.gasdatas.activity.bean.historytable;
import com.hpu.gasdatas.activity.database.DatabaseHelper;
import com.hpu.gasdatas.activity.util.ContentGridStyle;
import com.hpu.gasdatas.activity.util.ExcelUtil;
import com.hpu.gasdatas.activity.util.NoStyle;
import com.hpu.gasdatas.activity.util.Order;
import com.hpu.gasdatas.activity.util.place;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hpu.gasdatas.activity.contants.Contants.DATABASE_NAME;
import static com.hpu.gasdatas.activity.contants.Contants.DATABASE_VERSION;
import static com.hpu.gasdatas.activity.contants.Contants.Title;
import static com.hpu.gasdatas.activity.contants.Contants.data_table;
import static com.hpu.gasdatas.activity.contants.Contants.place_table;

/**
 * Created by：何学慧
 * Detail:查看历史记录
 * on 2019/10/27
 */

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    @BindView(R.id.table)
    SmartTable mSmartTable;
    @BindView(R.id.spinner_time)
    TextView mTimeSp;
    @BindView(R.id.spinner_tunnel)
    Spinner mTunnelSp;
    @BindView(R.id.imgbtn_share)
    ImageButton mShareImgBtn;
    private List<String> timelist;
    private List<String> tunnellist;
    private List<String> tunnelIdlist;

    private ArrayAdapter<String> adptertunnel;

    private DatePickerDialog datePickerDialog;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private SQLiteDatabase Placedb;
    private List<historytable> datalist;
    private Cursor cursor;
    private Cursor cursor2;
    //excle处理
    private List<Order> orders;
    private File file;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int REQUEST_PERMISSION_CODE = 1000;
    private AlertDialog alertDialog;

    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private View rootview;

    private void requestPermission() {
        if (Build.VERSION.SDK_INT > 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    permissions[0])
                    == PackageManager.PERMISSION_GRANTED) {
                //授予权限
                Log.i("requestPermission:", "用户之前已经授予了权限！");
            } else {
                //未获得权限
                Log.i("requestPermission:", "未获得权限，现在申请！");
                requestPermissions(permissions
                        , REQUEST_PERMISSION_CODE);
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_history, container, false);
        return rootview;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, rootview);
        //申请权限
        requestPermission();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


        TextView mTvTitle = getActivity().findViewById(R.id.tv_title);
        mTvTitle.setText("查看记录");
        mShareImgBtn.setEnabled(false);
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sd = new SimpleDateFormat("MM月dd日");
        Date date = new Date();

        mTimeSp.setText(mYear + "年" + sd.format(date));
        dbHelper = new DatabaseHelper(getActivity(), DATABASE_NAME, null, DATABASE_VERSION);
        //调用SQLiteHelper.OnCreate()
        db = dbHelper.getWritableDatabase();
        Placedb = dbHelper.getWritableDatabase();
        //日历dialog
        stablestyle();
        searchTunnel(mYear + "年" + sd.format(date), "0");
        //时间选择监听，时间该变，相应的巷道号改变
        datePickerDialog = new DatePickerDialog(getContext(), R.style.MyDatePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                        //和数据库中数据保持一致，都变成两位数
                        String mothnum;
                        String daynum;
                        if (month < 9) {
                            mothnum = "0" + (month + 1);
                        } else {
                            mothnum = String.valueOf(month + 1);
                        }
                        if (dayOfMonth < 10) {
                            daynum = "0" + dayOfMonth;
                        } else {
                            daynum = String.valueOf(dayOfMonth);
                        }
                        final String data = year + "年" + mothnum + "月" + daynum + "日";
                        searchTunnel(data, "1");
                    }

                },
                mYear, mMonth, mDay);
        //巷道号改变时，网格显示数据改变
        mTunnelSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //查询相应数据
                searchDate(mTimeSp.getText().toString(), String.valueOf(tunnelIdlist.get(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @OnClick({R.id.spinner_time, R.id.imgbtn_share})
    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.spinner_time:
                datePickerDialog.show();
                break;
            case R.id.imgbtn_share:
                Exportexcel();
                break;
        }
    }


    /**
     * 设置表格样式
     */
    public void stablestyle() {
        //表格字体颜色
        mSmartTable.getConfig().setContentStyle(new FontStyle(50, Color.WHITE));
        mSmartTable.getConfig().setShowTableTitle(false);
        mSmartTable.getConfig().setShowXSequence(false);
        mSmartTable.getConfig().setShowYSequence(false);
        FontStyle.setDefaultTextSize(DensityUtils.sp2px(getContext(), 15)); //设置全局字体大小
        mSmartTable.getConfig().setColumnTitleStyle(new FontStyle(getContext(), 18, getResources().getColor(R.color.white)));
        mSmartTable.getConfig().setColumnTitleBackground(new BaseBackgroundFormat(getResources().getColor(R.color.bar)));
        mSmartTable.getConfig().setColumnTitleVerticalPadding(35);
        mSmartTable.getConfig().setColumnTitleGridStyle(new NoStyle());//标题栏网格颜色
        mSmartTable.getConfig().setVerticalPadding(25);
        mSmartTable.getConfig().setContentGridStyle(new ContentGridStyle());
        //去掉网格
        mSmartTable.getConfig().setTableGridFormat(new BaseAbstractGridFormat() {
            @Override
            protected boolean isShowVerticalLine(int col, int row, CellInfo cellInfo) {
                return false;
            }

            @Override
            protected boolean isShowHorizontalLine(int col, int row, CellInfo cellInfo) {
                return false;
            }
        });
        //设置单复数行不同颜色
//        ICellBackgroundFormat<CellInfo> backgroundFormat = new BaseCellBackgroundFormat<CellInfo>() {
//            @Override
//            public int getBackGroundColor(CellInfo cellInfo) {
//                if (cellInfo.row % 2 == 0) {
//                    return ContextCompat.getColor(getContext(), R.color.stab_2);
//                } else {
//                    return ContextCompat.getColor(getContext(), R.color.stab_1);
//                }
//            }
//        };
//        mSmartTable.getConfig().setContentCellBackgroundFormat(backgroundFormat);
    }

    public void tunnelclear(List<String> list) {
        //获取巷道号，去掉重复的·
        List listTemp = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (!listTemp.contains(list.get(i))) {
                listTemp.add(list.get(i));
            }
        }
        list.clear();
        list.addAll(listTemp);

    }

    /**
     * 判断数据库中是否有这个日期
     * 通过时间找到相应的航道号
     * flag用来判断是否是第一次进入
     */
    public void searchTunnel(String date, String flag) {
        cursor = db.query(data_table, new String[]{"id,tunnel,location,potency,pressure,flow,tem,person,date,time"}, "date=?", new String[]{date}, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            tunnelIdlist = new ArrayList<>();
            tunnellist = new ArrayList<>();
            while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                tunnellist.add("H" + cursor.getInt(cursor.getColumnIndex("tunnel")));
                tunnelIdlist.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("tunnel"))));
                cursor.moveToNext();
            }
            //去掉重复的
            tunnelclear(tunnellist);
            tunnelclear(tunnelIdlist);
            //搜素对应数据
            searchDate(date, String.valueOf(tunnelIdlist.get(0)));

        } else {
            if (flag.equals("1")) {
                tunnelIdlist = new ArrayList<>();
                tunnellist = new ArrayList<>();
                searchDate(date, "");
                //当数据为空时，设置一个空行,进行一个数据模拟，因为无法在数据为0时更新
                datalist = new ArrayList<>();
                historytable hi = new historytable();
                hi.setTime("");
                hi.setPeople("");
                datalist.add(hi);
                mSmartTable.setData(datalist);
                Toast.makeText(getContext(), "该日期没有历史数据", Toast.LENGTH_SHORT).show();
            } else if (flag.equals("0")) {
                //先测试有没有巷道信息
                int number = 0;
                Cursor c = Placedb.rawQuery("select * from " + place_table, null);
                number = c.getCount();
                if (number != 0) {
                    Toast.makeText(getContext(), "该日期没有历史数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                tunnelIdlist = new ArrayList<>();
                tunnellist = new ArrayList<>();
                searchDate(date, "");
                //当数据为空时，设置一个空行,进行一个数据模拟，因为无法再数据为0时更新
                datalist = new ArrayList<>();
                historytable hi = new historytable();
                hi.setTime("");
                hi.setPeople("");
                datalist.add(hi);
                mSmartTable.setData(datalist);
                Toast.makeText(getContext(), "请联系管理员添加巷道信息", Toast.LENGTH_SHORT).show();
            }
        }
        adptertunnel = new ArrayAdapter<>(getActivity(), R.layout.myspinner, tunnellist);
        mTunnelSp.setAdapter(adptertunnel);
        mTimeSp.setText(date);

    }

    /**
     * 通过巷道号查询巷道的描述
     * @param tunnelID 巷道号
     */
    public String getDeatil(int tunnelID) {
    Cursor csplace=Placedb.query(place_table,new String[]{"tunnel","detail"},"tunnel=?", new String[]{String.valueOf(tunnelID)},null,null,"tunnel");
   if (csplace.getCount()!=0){
       csplace.moveToFirst();
       return csplace.getString(csplace.getColumnIndex("detail"));
   }
   return "";
    }

    /**
     * 通过时间和航道号查询数据
     */
    public void searchDate(String date, String tunnel) {
        orders = new ArrayList<>();
        datalist = new ArrayList<>();
        cursor2 = db.query(data_table, new String[]{"id,tunnel,location,potency,pressure,flow,tem,person,date,time"}, "date=? and tunnel=?", new String[]{date, tunnel}, null, null, "id");
        if (cursor2.getCount() != 0) {
            cursor2.moveToFirst();
            int num = 1;
            while (!cursor2.isAfterLast() && (cursor2.getString(1) != null)) {
                //stable表
                historytable htable = new historytable();
                htable.setId(num);
                htable.setLocation("H" + cursor2.getString(cursor2.getColumnIndex("location")));
                htable.setPotency(cursor2.getString(cursor2.getColumnIndex("potency")));
                htable.setPressure(cursor2.getString(cursor2.getColumnIndex("pressure")));
                htable.setFlow(cursor2.getString(cursor2.getColumnIndex("flow")));
                htable.setTem(cursor2.getString(cursor2.getColumnIndex("tem")));
                htable.setPeople(cursor2.getString(cursor2.getColumnIndex("person")));
                htable.setTime(cursor2.getString(cursor2.getColumnIndex("date")).replace("年", ".").replace("月", ".").replace("日", "") + "  " + cursor2.getString(cursor2.getColumnIndex("time")));


                //excel表
                Order order = new Order();
                order.setId(String.valueOf(num));
                order.setLocation("H" + cursor2.getString(cursor2.getColumnIndex("location")));
                order.setPotency(cursor2.getString(cursor2.getColumnIndex("potency")));
                order.setPressure(cursor2.getString(cursor2.getColumnIndex("pressure")));
                order.setFlow(cursor2.getString(cursor2.getColumnIndex("flow")));
                order.setTem(cursor2.getString(cursor2.getColumnIndex("tem")));
                order.setPerson(cursor2.getString(cursor2.getColumnIndex("person")));
                order.setDate(cursor2.getString(cursor2.getColumnIndex("date")) + "  " + cursor2.getString(cursor2.getColumnIndex("time")).trim());
                num++;
                orders.add(order);
                datalist.add(htable);
                cursor2.moveToNext();
            }
            mSmartTable.setData(datalist);
            mShareImgBtn.setEnabled(true);

        } else {
            Toast.makeText(getContext(), "暂无数据！", Toast.LENGTH_SHORT).show();
        }
    }

    public void Exportexcel() {
        String filePath= getActivity().getFilesDir().getAbsolutePath()  +"/CH4Monitor";
        //判断是否有数据
        if (orders.size() == 0) {
            Toast.makeText(getContext(), "没有数据", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //文件夹下如果存在文件就先删除
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (file.list().length != 0) {
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    f.delete();
                }
            }
        }
        //文件名：日期_巷道号
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String filedata = sd.format(new Date());
        Log.e(TAG, "Exportexcel: "+System.currentTimeMillis() );
        String mesc=(String.valueOf(System.currentTimeMillis())).substring(9,13);
        String excelFileName = "/" + filedata+mesc + "_" + mTunnelSp.getSelectedItem() + ".xls";
        String[] title = {"序号", "钻孔位置", "瓦斯浓度", "孔口负压", "混合流量", "温度", "记录人", "记录时间"};
        //巷道描述文字
        String sheetName = getDeatil(Integer.parseInt(mTunnelSp.getSelectedItem().toString().replace("H","").trim()));
        filePath = filePath + excelFileName;
        ExcelUtil.initExcel(filePath, sheetName, title);
        ExcelUtil.writeObjListToExcel(orders, filePath, getActivity());
      //  Toast.makeText(getContext(), "已导出" + filePath, Toast.LENGTH_SHORT).show();
        //调取分享界面进行分享
        File excelfile=new File(filePath);
        if (excelfile==null || !excelfile.exists()){
            Toast.makeText(getContext(),"文件不存在！",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(),"com.hpu.gasdatas" + ".fileprovider", excelfile));
            shareIntent.setType("*/*");
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "分享到"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //申请权限回调@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("onPermissionsResult:", "权限" + permissions[0] + "申请成功");
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                Log.i("onPermissionsResult:", "用户拒绝了权限申请");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("permission")
                        .setMessage("点击允许才可以使用我们的app哦")
                        .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (alertDialog != null && alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                }
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        });
                alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }
    }

    @Override
    public void onDestroy() {
        cursor.close();
        db.close();
        super.onDestroy();
    }
}

