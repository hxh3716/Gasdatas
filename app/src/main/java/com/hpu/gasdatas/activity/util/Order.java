package com.hpu.gasdatas.activity.util;

import org.json.JSONObject;

/**
 * Created by：何学慧
 * Detail:Model类
 * on 2019/11/4
 */

public class Order {
    /**
     * id               巷道编号
     * location         巷道位置 H1-1-1
     * tunnelnmae       巷道名称
     * tunnelid         巷道编号
     * groups           组数
     * holes            钻孔编号
     * potency          浓度
     * pressure         负压
     * flow             流量
     * tem              温度
     * person           记录人
     * date             日期
     */
    public String id;
    public String location;
    public String tunnelname;
    public  String tunnelid;
    public  String groups;
    public  String holes;
    public String potency;
    public String pressure;
    public String flow;
    public String tem;
    public String person;
    public String date;

    public Order() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTunnelname() {
        return tunnelname;
    }

    public void setTunnelname(String tunnelname) {
        this.tunnelname = tunnelname;
    }

    public String getTunnelid() {
        return tunnelid;
    }

    public void setTunnelid(String tunnelid) {
        this.tunnelid = tunnelid;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getHoles() {
        return holes;
    }

    public void setHoles(String holes) {
        this.holes = holes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPotency() {
        return potency;
    }

    public void setPotency(String potency) {
        this.potency = potency;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
