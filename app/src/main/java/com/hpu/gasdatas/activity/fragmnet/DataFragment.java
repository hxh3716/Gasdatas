package com.hpu.gasdatas.activity.fragmnet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.SystemKeyboard;
import com.hpu.gasdatas.activity.action.IKeyBoardUI;
import com.hpu.gasdatas.activity.action.KeyBoardActionListence;
import com.hpu.gasdatas.activity.activity.PceditActivity;
import com.hpu.gasdatas.activity.database.DatabaseHelper;
import com.hpu.gasdatas.activity.util.Detityutil;
import com.hpu.gasdatas.activity.util.place;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hpu.gasdatas.activity.contants.Contants.DATABASE_NAME;
import static com.hpu.gasdatas.activity.contants.Contants.DATABASE_VERSION;
import static com.hpu.gasdatas.activity.contants.Contants.data_table;
import static com.hpu.gasdatas.activity.contants.Contants.place_table;

/**
 * Created by：何学慧
 * Detail:数据录入
 * on 2019/10/27
 */

public class DataFragment extends Fragment implements View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "DataFragment";
    private View rootview;
    @BindView(R.id.tv_detail)
    TextView mTvDetail;
    @BindView(R.id.systemkeyboard)
    SystemKeyboard mKeyboard;
    @BindView(R.id.ed_potency)
    EditText mEdPotency;
    @BindView(R.id.ed_pressure)
    EditText mEdPressure;
    @BindView(R.id.ed_flow)
    EditText mEdFlow;
    @BindView(R.id.ed_tem)
    EditText mEdTem;
    @BindView(R.id.spinner_tunnel)
    Spinner mSpTunnel;
    @BindView(R.id.spinner_groups)
    Spinner mSpGroups;
    @BindView(R.id.spinner_hole)
    Spinner mSpHole;
    private Intent intent;
    //位置表
    private DatabaseHelper dbHelperPlace;
    private SQLiteDatabase dbPlace;
    //数据表
    private DatabaseHelper dbHelperData;
    private SQLiteDatabase dbData;
    private ArrayAdapter<String> adapterTunnel;
    private ArrayAdapter<Integer> adapterGroups;
    private ArrayAdapter<Integer> adapterHoles;

    private List<Integer> groupsTotallist = new ArrayList<>();
    private List<Integer> holesTotallist = new ArrayList<>();
    private List<String> tunnelList = new ArrayList<>();
    private List<String> detailList = new ArrayList<>();
    private List<Integer> groupslist = new ArrayList<>();
    private List<Integer> holeslist = new ArrayList<>();
    private SharedPreferences sp;
    private int Startflag = 0;
    private SimpleDateFormat sdf1;
    private String time;
    //记住下拉框位置
    private String mSpTunenlPosition = "";
    private String mSpGroupsPosition = "";
    private String mSpHolesPosition = "";
    private TextToSpeech textToSpeech = null;  //语音对象
    private TextView mTxVoiceFlag;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragmnet_data, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mTxVoiceFlag=getActivity().findViewById(R.id.voiceflag);
           initTTs();


        intent = getActivity().getIntent();
        //显示保存的数据
        sp = getActivity().getSharedPreferences("ch4data", Context.MODE_PRIVATE);
        mEdPressure.setText(sp.getString("pressure", null));
        mEdFlow.setText(sp.getString("flow", null));
        mEdTem.setText(sp.getString("tem", null));
        //获取activity的数据
        TextView mTvTitle = getActivity().findViewById(R.id.tv_title);
        mTvTitle.setText("数据录入");

        rootview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    save();
                    return true;
                } else {
                    return false;
                }
            }
        });

        mEdPotency.setOnFocusChangeListener(this::onFocusChange);
        mEdFlow.setOnFocusChangeListener(this::onFocusChange);
        mEdPressure.setOnFocusChangeListener(this::onFocusChange);
        mEdTem.setOnFocusChangeListener(this::onFocusChange);
        //设置键盘样式
        mKeyboard.setKeyboardUI(new IKeyBoardUI() {
            @Override
            public Paint setPaint(Paint paint) {
                paint.setColor(Color.WHITE);
                paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
                paint.setTextSize(Detityutil.dpToPx(getContext(), 38));
                return paint;
            }
        });
        mKeyboard.setEditText(mEdPressure);
        mKeyboard.setEditText(mEdTem);
        mKeyboard.setEditText(mEdFlow);
        mKeyboard.setEditText(mEdPotency);
        //用来获取最新的输入
        final String[] ss = {""};
        //自定义键盘按钮监听
        mKeyboard.setOnKeyboardActionListener(new KeyBoardActionListence() {
            @Override
            public void onComplete() {
                if (mTxVoiceFlag.getText().toString().equals("true")){
               startAuto("下一个");
                }
                btnSure();
            }
            @Override
            public void onTextChange(Editable editable) {

            }
            @Override
            public void onClear() {
                if (mTxVoiceFlag.getText().toString().equals("true")){
                   startAuto("删除");
                }
            }
            @Override
            public void onClearAll() {

            }
            @Override
            public void onText(int primaryCode) {
                if (mTxVoiceFlag.getText().toString().equals("true")){
                    if (primaryCode==57){
                        startAuto("9");
                    }else if (primaryCode==56){
                        startAuto("8");
                    }else if (primaryCode==55){
                        startAuto("7");
                    }else if (primaryCode==54){
                        startAuto("6");
                    }else if (primaryCode==53){
                        startAuto("5");
                    }else if (primaryCode==52){
                        startAuto("4");
                    }else if (primaryCode==51){
                        startAuto("3");
                    }else if (primaryCode==50){
                        startAuto("2");
                    }else if (primaryCode==49){
                        startAuto("1");
                    }else if (primaryCode==48){
                        startAuto("0");
                    }else if (primaryCode==46){
                        startAuto("点");
                    }
                }
            }
        });
        //初始化并创建数据库
        try {
            //位置表初始化
            dbHelperPlace = new DatabaseHelper(getActivity(), DATABASE_NAME, null, DATABASE_VERSION);
            //调用SQLiteHelper.OnCreate()
            dbPlace = dbHelperPlace.getWritableDatabase();
            //数据表初始化
            dbHelperData = new DatabaseHelper(getActivity(), DATABASE_NAME, null, DATABASE_VERSION);
            dbData = dbHelperData.getWritableDatabase();
            query();

            //巷道list
            adapterTunnel = new ArrayAdapter<>(getActivity(), R.layout.myspinner, tunnelList);
            mSpTunnel.setAdapter(adapterTunnel);
            //设置初始的巷道号
            getInfobytime();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        //设置监听是否是第一次设置位置信息
        mSpTunnel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //下拉框改变时保存先保存数据
                if (mEdPotency.getText().toString().equals("")) {

                } else if (checkDigit(mEdPotency) && Float.parseFloat(mEdPotency.getText().toString()) <= 100.0f) {
                    if (Float.parseFloat(mEdPotency.getText().toString()) * 1.0 == 0) {
                        mEdPotency.setText("0");
                    }
                    insert(mSpTunenlPosition, mSpGroupsPosition, mSpHolesPosition);
                } else {
                    showShortToast("浓度数值不合理");
                    mEdPotency.setText("");
                }
                //初始值设置和spinnerlist中调用了setOnItemSelectedListener，所以一共两次
                if (Startflag <= 1) {
                    spinnerlist(sp.getInt("tunnel", 0), sp.getInt("groups", 0), sp.getInt("holes", 0), "start");
                } else {
                    spinnerlist(i, 0, 0, "");
                }

                String groups;
                String holes;
                if (mSpGroups.getSelectedItem().toString().length() == 1) {
                    groups = "0" + mSpGroups.getSelectedItem().toString().trim();
                } else {
                    groups = mSpGroups.getSelectedItem().toString().trim();
                }
                if (mSpHole.getSelectedItem().toString().length() == 1) {
                    holes = "0" + mSpHole.getSelectedItem().toString().trim();
                } else {
                    holes = mSpHole.getSelectedItem().toString().trim();
                }
                queryData(tunnelList.get(i).replace("H", "") + groups + holes);
                mEdPotency.setFocusable(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //spinnerlist(sp.getInt("tunnel",0),sp.getInt("groups",0),sp.getInt("holes",0),"start");

        //设置光标
        mEdPotency.setFocusable(true);
        mEdPotency.requestFocus();
        //设置spinner的监听，保证切换时浓度输入框刷新
        mSpHole.setOnItemSelectedListener(this);
        mSpGroups.setOnItemSelectedListener(this);
//        //浓度值得输入监听
        mEdPotency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //有输入就记住当前位置
                if (tunnelList.size() == 0 || groupslist.size() == 0 || holeslist.size() == 0) {
                    return;
                }
                mSpTunenlPosition = mSpTunnel.getSelectedItem().toString();
                mSpGroupsPosition = mSpGroups.getSelectedItem().toString();
                mSpHolesPosition = mSpHole.getSelectedItem().toString();
            }
        });
    }

    //自定义键盘监听，不调用系统键盘
    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            switch (view.getId()) {
                case R.id.ed_potency:
                    mKeyboard.setEditText((EditText) view);
                    EdStyle(mEdPotency);
                    break;
                case R.id.ed_pressure:
                    mKeyboard.setEditText((EditText) view);
                    EdStyle(mEdPressure);
                    break;
                case R.id.ed_tem:
                    mKeyboard.setEditText((EditText) view);
                    EdStyle(mEdTem);
                    break;
                case R.id.ed_flow:
                    mKeyboard.setEditText((EditText) view);
                    EdStyle(mEdFlow);
                    break;
                default:
                    break;
            }
        } else {
            if (tunnelList.size() == 0 || groupslist.size() == 0 || holeslist.size() == 0) {
                return;
            }
            switch (view.getId()) {
                case R.id.ed_potency:
                    if (mEdPotency.getText().toString().equals("")) {

                    } else if (checkDigit(mEdPotency) && Float.parseFloat(mEdPotency.getText().toString()) <= 100.0f) {
                        if (Float.parseFloat(mEdPotency.getText().toString()) * 1.0 == 0) {
                            mEdPotency.setText("0");
                        }
                        insert(mSpTunnel.getSelectedItem().toString(), mSpGroups.getSelectedItem().toString(), mSpHole.getSelectedItem().toString());
                    } else {
                        showShortToast("浓度数值不合理");
                        mEdPotency.setText("");
                    }
                    break;
                case R.id.ed_pressure:
                    if (mEdPressure.getText().toString().equals("")) {

                    } else if (checkDigit(mEdPressure) && Float.parseFloat(mEdPressure.getText().toString()) <= 100.0f) {
                        insert(mSpTunnel.getSelectedItem().toString(), mSpGroups.getSelectedItem().toString(), mSpHole.getSelectedItem().toString());

                    } else {
                        showShortToast("负压数值不合理");
                        mEdPressure.setText("");
                    }
                    break;
                case R.id.ed_tem:
                    if (mEdTem.getText().toString().equals("")) {

                    } else if (checkDigit(mEdTem) && Float.parseFloat(mEdTem.getText().toString()) <= 100.0f) {
                        insert(mSpTunnel.getSelectedItem().toString(), mSpGroups.getSelectedItem().toString(), mSpHole.getSelectedItem().toString());

                    } else {
                        showShortToast("温度数值不合理");
                        mEdTem.setText("");
                    }
                    break;
                case R.id.ed_flow:
                    if (mEdFlow.getText().toString().equals("")) {

                    } else if (checkDigit(mEdFlow)) {
                        if (Float.parseFloat(mEdFlow.getText().toString()) * 1.0 == 0) {
                            mEdFlow.setText("0");
                        }
                        insert(mSpTunnel.getSelectedItem().toString(), mSpGroups.getSelectedItem().toString(), mSpHole.getSelectedItem().toString());

                    } else {
                        showShortToast("流量数值不合理");
                        mEdFlow.setText("");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //插入记录数据
    public void insert(String tunnelItem, String groupsItem, String holesItem) {
        sdf1 = new SimpleDateFormat("HH:mm:ss");
        time = sdf1.format(new Date());
        if (tunnelList.size() == 0 || groupslist.size() == 0 || holeslist.size() == 0) {
            return;
        }
        //如果巷道浓度为空则不插入
        if (mEdPotency.getText().toString().equals("")) {
            return;
        }
        String tunnel = tunnelItem.replace("H", "").trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String date = sdf.format(new Date());

        SimpleDateFormat sdfdate = new SimpleDateFormat("yyyyMMdd");
        String IDdata = sdfdate.format(new Date());

        String groups;
        String holes;

        String location = tunnel + "-" + groupsItem + "-" + holesItem.trim();
        //判断巷道组数是否是一位数，设置相同位数的id，方便比较大小
        if (groupsItem.length() == 1) {
            groups = "0" + groupsItem.trim();
        } else {
            groups = groupsItem.trim();
        }
        if (holesItem.length() == 1) {
            holes = "0" + holesItem.trim();
        } else {
            holes = holesItem.trim();
        }
        Integer id = Integer.parseInt(tunnel + groups + holes);
        String timeid = String.valueOf(id) + IDdata;

        //查询数据库中是否已经存在这个数据，如果存在则更改，如果不存在就插入
        //判断日期是否为当天，当天就修改，否则就插入
        //设置了一个主键id 由巷道位置加日期实现
        Cursor cursor = dbData.query(data_table, new String[]{"id,tunnelname,idtime,tunnel,location,potency,pressure,flow,tem,person,date,time"}, "idtime=?", new String[]{timeid}, null, null, "id");
        if (cursor.getCount() != 0) {
            //更改数据库数据
            ContentValues values = new ContentValues();
            values.put("potency", mEdPotency.getText().toString());
            values.put("pressure", mEdPressure.getText().toString());
            values.put("flow", mEdFlow.getText().toString());
            values.put("tem", mEdTem.getText().toString());
            values.put("person", intent.getStringExtra("name"));
            values.put("date", date);
            values.put("time", time);
            dbData.update(data_table, values, "idtime" + "=" + timeid, null);
        } else {
            //插入数据
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("idtime", String.valueOf(id) + IDdata);
            values.put("tunnel", Integer.parseInt(tunnel));
            values.put("tunnelname", mTvDetail.getText().toString());
            values.put("location", location);
            values.put("potency", mEdPotency.getText().toString());
            values.put("pressure", mEdPressure.getText().toString());
            values.put("flow", mEdFlow.getText().toString());
            values.put("tem", mEdTem.getText().toString());
            values.put("person", intent.getStringExtra("name"));
            values.put("date", date);
            values.put("time", time);
            dbData.insert(data_table, null, values);
        }


    }

    //查询所有的位置信息
    public void query() {
        Cursor cursor = dbData.query(place_table, null, null, null, null, null, "tunnel");
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(0) != null)) {
                tunnelList.add("H" + cursor.getString(0));
                groupsTotallist.add(cursor.getInt(1));
                holesTotallist.add(cursor.getInt(2));
                detailList.add(cursor.getString(3));
                cursor.moveToNext();
            }
        }
        cursor.close();
    }


    //通过巷道spinner的位置选择确定组数和钻孔数
    public void spinnerlist(int position, int groupsPosition, int holesPosition, String flag) {
        groupslist = new ArrayList<>();
        holeslist = new ArrayList<>();
        //组数
        int gNum = 0;
        //钻孔数
        int HNum = 0;
        if (groupsTotallist.size() != 0) {
            for (int i = 0; i < groupsTotallist.get(position); i++) {
                gNum++;
                groupslist.add(gNum);
            }
        }
        if (holesTotallist.size() != 0) {
            for (int i = 0; i < holesTotallist.get(position); i++) {
                HNum++;
                holeslist.add(HNum);
            }
        }
        //组数list
        adapterGroups = new ArrayAdapter<>(getActivity(), R.layout.myspinner, groupslist);
        mSpGroups.setAdapter(adapterGroups);
        //钻孔list
        adapterHoles = new ArrayAdapter<>(getActivity(), R.layout.myspinner, holeslist);
        mSpHole.setAdapter(adapterHoles);
        mTvDetail.setText(detailList.get(position));
        if (flag.equals("start")) {
            if (position == 0) {
                mSpTunnel.setSelection(position);
                mSpGroups.setSelection(groupsPosition);
                mSpHole.setSelection(holesPosition);
                Startflag = 2;
            } else {
                mSpTunnel.setSelection(position);
                mSpGroups.setSelection(groupsPosition);
                mSpHole.setSelection(holesPosition);
                Startflag++;
            }

        }

    }

    /**
     * 设置组数和钻孔数监听事件
     *
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //下拉框改变时保存先保存数据
        if (mEdPotency.getText().toString().equals("")) {

        } else if (checkDigit(mEdPotency) && Float.parseFloat(mEdPotency.getText().toString()) <= 100.0f) {
            if (Float.parseFloat(mEdPotency.getText().toString()) * 1.0 == 0) {
                mEdPotency.setText("0");
            }
            insert(mSpTunenlPosition, mSpGroupsPosition, mSpHolesPosition);
        } else {
            showShortToast("浓度数值不合理");
            mEdPotency.setText("");
        }
        String groups;
        String holes;
        if (mSpGroups.getSelectedItem().toString().length() == 1) {
            groups = "0" + mSpGroups.getSelectedItem().toString().trim();
        } else {
            groups = mSpGroups.getSelectedItem().toString().trim();
        }
        if (mSpHole.getSelectedItem().toString().length() == 1) {
            holes = "0" + mSpHole.getSelectedItem().toString().trim();
        } else {
            holes = mSpHole.getSelectedItem().toString().trim();
        }
        queryData(mSpTunnel.getSelectedItem().toString().replace("H", "") + groups + holes);
        mEdPotency.setFocusable(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    /**
     * @param mEd
     * @return
     */
    public boolean checkDigit(EditText mEd) {
        //先判断"."是不是在第一位
        if (mEd.getText().toString().indexOf(".") == 0) {
            return false;
        } else {
            //小数点后保留5位
            String regex = "^(([^0][0-9]+|0)\\.([0-9]{1,3})$)|^(([^0][0-9]+|0)$)|^(([1-9]+)\\.([0-9]{1,3})$)|^(([1-9]+|0)$)";
            if (Pattern.matches(regex, mEd.getText().toString()) || mEd.getText().toString().equals("")) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 键盘完成事件
     * 点击确定后钻孔数位置会加一，如果钻孔是最后一个，则组数加一，巷道编号同理
     */
    public void btnSure() {
        if (tunnelList.size() == 0 || groupslist.size() == 0 || holeslist.size() == 0) {
            showShortToast("请联系管理员添加巷道信息");
            return;
        }
        //如果Editviw有焦点，首先移向下一个
        if (mEdPotency.isFocused()) {
            mEdPotency.clearFocus();
            mEdPressure.requestFocus();
            CursorSet(mEdPressure);
        } else if (mEdPressure.isFocused()) {
            mEdPressure.clearFocus();
            mEdFlow.requestFocus();
            CursorSet(mEdFlow);
        } else if (mEdFlow.isFocused()) {
            mEdFlow.clearFocus();
            mEdTem.requestFocus();
            CursorSet(mEdTem);
        } else {
//            mEdPotency.setText("");
            mEdPotency.setFocusable(true);
            mEdPotency.requestFocus();

            if (mSpHole.getSelectedItemId() < holeslist.size() - 1) {
                mSpHole.setSelection((int) (mSpHole.getSelectedItemId() + 1));
            } else if (mSpHole.getSelectedItemId() == holeslist.size() - 1) {
                mSpHole.setSelection(0);
                //组数spinner显示的不是最后一个则+1
                if (mSpGroups.getSelectedItemId() < groupslist.size() - 1) {
                    mSpGroups.setSelection((int) mSpGroups.getSelectedItemId() + 1);
                } else {
                    mSpGroups.setSelection(0);
                    //巷道数不是最后一个则+1
                    if (mSpTunnel.getSelectedItemId() < tunnelList.size() - 1) {
                        mSpTunnel.setSelection((int) mSpTunnel.getSelectedItemId() + 1);
                    } else {
                        showShortToast("已经是最后一个巷道了");
                        mSpTunnel.setSelection(tunnelList.size() - 1);
                        mSpGroups.setSelection(groupslist.size() - 1);
                        mSpHole.setSelection(holeslist.size() - 1);
                    }
                }


            }
        }

    }

    /**
     * 通过时间查询当前是否有数据
     */
    public void getInfobytime() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
        String date = sf.format(new Date());
        Cursor s = dbPlace.query(data_table, new String[]{"date"}, "date=?", new String[]{date}, null, null, null);
        if (s.getCount() == 0) {
            mSpTunnel.setSelection(0);
            mSpGroups.setSelection(0);
            mSpHole.setSelection(0);
            mEdPotency.setText("");
            mEdPressure.setText("");
            mEdFlow.setText("");
            mEdTem.setText("");
        }
    }

    /**
     * 保存巷道位置
     */
    public void save() {
        SimpleDateFormat sdfdate = new SimpleDateFormat("yyyyMMdd");
        String IDdata = sdfdate.format(new Date());
        //保存数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("voiceFlag",mTxVoiceFlag.getText().toString());
        if (tunnelList.size() != 0 || groupslist.size() != 0 || holeslist.size() != 0) {
            editor.putInt("tunnel", (int) mSpTunnel.getSelectedItemId());
            editor.putInt("groups", (int) mSpGroups.getSelectedItemId());
            editor.putInt("holes", (int) mSpHole.getSelectedItemId());
            //  editor.putInt("tunnelItem", Integer.parseInt(mSpTunnel.getSelectedItem().toString().replace("H", "")));
            editor.putString("pressure", mEdPressure.getText().toString());
            editor.putString("flow", mEdFlow.getText().toString());
            editor.putString("tem", mEdTem.getText().toString());
            editor.putString("date", IDdata);
            editor.commit();
        }
    }


    /**
     * @param id 是数据表中唯一id
     */
    public void queryData(String id) {
        SimpleDateFormat sdfdate = new SimpleDateFormat("yyyyMMdd");
        String IDdata = sdfdate.format(new Date());
        Cursor cursor = dbData.query(data_table, new String[]{"id,idtime,tunnel,location,potency,pressure,flow,tem,person,date,time"}, "idtime=?", new String[]{String.valueOf(id) + IDdata}, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            //在editview中填入数据
            mEdPotency.setText(cursor.getString(cursor.getColumnIndex("potency")));
            mEdPressure.setText(cursor.getString(cursor.getColumnIndex("pressure")));
            mEdFlow.setText(cursor.getString(cursor.getColumnIndex("flow")));
            mEdTem.setText(cursor.getString(cursor.getColumnIndex("tem")));
            CursorSet(mEdPotency);
        } else {
            mEdPotency.setText("");
        }
    }

    /**
     * 统一设置下划线样式
     *
     * @param med
     */
    public void EdStyle(EditText med) {
        mEdPotency.setBackgroundResource(R.drawable.edittext_);
        mEdPressure.setBackgroundResource(R.drawable.edittext_);
        mEdFlow.setBackgroundResource(R.drawable.edittext_);
        mEdTem.setBackgroundResource(R.drawable.edittext_);
        med.setBackgroundResource(R.drawable.edittext_special);
    }

    //定义提示框
    private void showShortToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置光标在文字后面，默认在前面
     *
     * @param mEd
     */
    public void CursorSet(EditText mEd) {
        if (mEd.getText() != null && !mEd.getText().equals("")) {
            mEd.setSelection(mEd.getText().length());
        }
    }

    /**
     * 初始化语音对象
     */
    private void initTTs() {
        textToSpeech=new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==textToSpeech.SUCCESS){
                    //判断支持的语言
                    int result1=textToSpeech.setLanguage(Locale.US);
                    int result2=textToSpeech.setLanguage(Locale.SIMPLIFIED_CHINESE);
                    boolean a = (result1 == TextToSpeech.LANG_MISSING_DATA || result1 == TextToSpeech.LANG_NOT_SUPPORTED);
                    boolean b = (result2 == TextToSpeech.LANG_MISSING_DATA || result2 == TextToSpeech.LANG_NOT_SUPPORTED);
                   // Log.e(TAG, "onInit: "+"us: "+a+"  ch"+b );
                }
            }
        });
    }

    /**
     * 设置声音
     * @param data
     */
    public void startAuto(String data) {
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        textToSpeech.setPitch(1.0f);
        // 设置语速
        textToSpeech.setSpeechRate(3f);
        textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁前保存数据
        save();
        //页面销毁时关闭表
        dbData.close();
        dbPlace.close();
        //关闭语音，防止内存泄露
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }

    /**
     * 返回时保存巷道位置
     *
     * @return
     */
    public boolean onBackPressed() {
        save();
        return true;
    }


}

