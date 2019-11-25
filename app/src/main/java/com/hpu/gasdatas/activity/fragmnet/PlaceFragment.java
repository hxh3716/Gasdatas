package com.hpu.gasdatas.activity.fragmnet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hpu.gasdatas.R;
import com.hpu.gasdatas.activity.activity.AddTunnelActivity;
import com.hpu.gasdatas.activity.activity.ChangeTunnelActivity;
import com.hpu.gasdatas.activity.contants.Contants;
import com.hpu.gasdatas.activity.database.DatabaseHelper;
import com.hpu.gasdatas.activity.util.place;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hpu.gasdatas.activity.contants.Contants.DATABASE_NAME;
import static com.hpu.gasdatas.activity.contants.Contants.DATABASE_VERSION;
import static com.hpu.gasdatas.activity.contants.Contants.place_table;

/**
 * Created by：何学慧
 * Detail:测量地点
 * on 2019/10/27
 */

public class PlaceFragment extends Fragment {
    private static final String TAG = "PlaceFragment";
    private View rootview;
    @BindView(R.id.list_place)
    ListView mPlacelist;
    @BindView(R.id.btn_addtunnel)
    Button mAddtunnelBtn;
    @BindView(R.id.btn_alert_cancle)
    Button mAlertCancleBtn;
    @BindView(R.id.btn_alert_sure)
    Button mAlertSureBtn;
    @BindView(R.id.layout_delect)
    LinearLayout mDelectLayout;

    TunnelAdapter adapter;
    List<place> list = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String tunnel = "";
    private int id = 0;
    private int groups = 0;
    private int holes = 0;
    private Intent intent;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragmnet_measureplace, container, false);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, rootview);
       TextView mTvTitle= getActivity().findViewById(R.id.tv_title);
        mTvTitle.setText("测量地点设置");
//        //初始化并创建数据库
        try {
            dbHelper = new DatabaseHelper(getActivity(), DATABASE_NAME, null, DATABASE_VERSION);
            //调用SQLiteHelper.OnCreate()
            db = dbHelper.getWritableDatabase();
            query();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        adapter = new TunnelAdapter(list);
        mPlacelist.setAdapter(adapter);

    }

    //按钮点击事件
    @OnClick({R.id.btn_addtunnel, R.id.btn_alert_sure, R.id.btn_alert_cancle})
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.btn_addtunnel:
                intent = new Intent(getActivity(), AddTunnelActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
                getActivity().finish();
                break;


        }
    }

    /**
     * listview的适配器
     */
    class TunnelAdapter extends BaseAdapter {
        private List<place> data;

        public TunnelAdapter(List<place> data) {
            this.data = data;
        }
        @Override
        public int getCount() {
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.mylistview, viewGroup, false);
                holder.mTvId = view.findViewById(R.id.tv_id);
                holder.mTvTunnel = view.findViewById(R.id.tv_tunnel);
                holder.mTvGroups = view.findViewById(R.id.tv_groups);
                holder.mTvHoles = view.findViewById(R.id.tv_holes);
                holder.mTvDelect = view.findViewById(R.id.tv_delect);
                holder.mTvChange = view.findViewById(R.id.tv_sure);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.mTvId.setText(String.valueOf(data.get(i).getId()));
            holder.mTvTunnel.setText("H" + data.get(i).getTunnel());
            holder.mTvGroups.setText(String.valueOf(data.get(i).getGroups()));
            holder.mTvHoles.setText(String.valueOf(data.get(i).getHoles()));
            holder.mTvDelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //取消toolar
                    getActivity().findViewById(R.id.place_toolbar).setVisibility(View.GONE);
                    mDelectLayout.setVisibility(View.VISIBLE);
                    //删除巷道对话框“确定”
                    mAlertSureBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //删除本行和数据库中的记录
                            delect(data.get(i).getTunnel());
                            Log.d(TAG, "onClick: " + i);
                            list.remove(i);
                            notifyDataSetInvalidated();
                            mDelectLayout.setVisibility(View.INVISIBLE);
                            getActivity().findViewById(R.id.place_toolbar).setVisibility(View.VISIBLE);
                        }
                    });
                    //删除巷道对话框“取消”
                    mAlertCancleBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDelectLayout.setVisibility(View.INVISIBLE);
                            getActivity().findViewById(R.id.place_toolbar).setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
            holder.mTvChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //更改数据库中的数据
                    intent = new Intent(getActivity(), ChangeTunnelActivity.class);
                    intent.putExtra("tunnel", String.valueOf(data.get(i).getTunnel()));
                    intent.putExtra("groups", String.valueOf(data.get(i).getGroups()));
                    intent.putExtra("holes", String.valueOf(data.get(i).getHoles()));
                    intent.putExtra("detail",data.get(i).getDetail());
                    startActivity(intent);
                  getActivity().finish();
                }
            });
            return view;
        }
    }
    static class ViewHolder {
        TextView mTvId, mTvChange, mTvTunnel, mTvGroups, mTvHoles, mTvDelect;
    }

    //查询
    public void query() {
        list=new ArrayList<>();
        Cursor cursor = db.query(place_table, null, null, null, null, null, "tunnel");
        cursor.moveToFirst();
        int num = 1;
        while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
            place tunnelplace = new place();
            tunnelplace.setId(num);
            tunnelplace.setTunnel(cursor.getString(0));
            tunnelplace.setGroups(cursor.getInt(1));
            tunnelplace.setHoles(cursor.getInt(2));
            tunnelplace.setDetail(cursor.getString(3));
            list.add(tunnelplace);
            num = num + 1;
            cursor.moveToNext();
        }
        cursor.close();
    }


    //删除,只要删除巷道信息，下次数据录入时的位置就从第一个位置开始
    public void delect(String tunnel) {
        db.delete(place_table, "tunnel" + "=" + tunnel, null);
       SharedPreferences sp = getActivity().getSharedPreferences("ch4data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
           editor.putInt("tunnel", 0);
           editor.putInt("groups", 0);
           editor.putInt("holes",0);
           editor.commit();

        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();


    }
    //增加


    //页面销毁监听
    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭数据库
        db.close();
    }


}
