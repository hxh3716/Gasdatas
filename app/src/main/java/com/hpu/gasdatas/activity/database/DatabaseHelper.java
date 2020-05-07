package com.hpu.gasdatas.activity.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.hpu.gasdatas.activity.contants.Contants;

/**
 * Created by：何学慧
 * Detail:SQLiteOpenHelper继承类
 * on 2019/10/29
 * 数据库中保存的巷道数都不加H
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //创建时的回调，第一次创建调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        //巷道位置表
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                Contants.place_table + "(" +
                "tunnel" + " integer primary key ASC ," +     //巷道号
                "groups" + " int,"+                        //组数
                "holes" + " int,"+                           //钻孔数
                "detail"+ " nvarchar"+                      //巷道描述
                ")");
        //采集数据表
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                Contants.data_table + "(" +
                "id"+" integer ,"+  //巷道位置
                "idtime"+" integer primary key ASC ,"+  //巷道位置和当前日期组成的id
                "tunnel" + " integer  ," +              //巷道号 “ 7”
                "location" + " nvarchar  ," +   //巷道+组数+钻孔数“7-10-1”
                "tunnelname"+" nvarchar ,"+     //巷道名称
                "potency" + " nvarchar,"+     // 瓦斯浓度
                "pressure" + " nvarchar,"+    // 孔口负压
                "flow" + " nvarchar," +       //混合流量
                "tem" + " nvarchar,"+        //温度
                "person" + " nvarchar,"+      //测量人
                "date" + " nvarchar," +     //日期
                "time" + " nvarchar "+         //时间
                ")");

    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //更新数据库

    }

}
