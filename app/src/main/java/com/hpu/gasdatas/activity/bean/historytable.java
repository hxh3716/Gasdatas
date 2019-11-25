package com.hpu.gasdatas.activity.bean;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

/**
 * Created by：何学慧
 * Detail:历史记录表格
 * on 2019/10/29
 */
@SmartTable(name="查看记录")
public class historytable {
    public historytable() {

    }

    public historytable(Integer id, String location, String potency, String pressure, String flow, String tem, String people, String time) {
        this.id = id;
        this.location = location;
        this.potency = potency;
        this.pressure = pressure;
        this.flow = flow;
        this.tem = tem;
        this.people = people;
        this.time = time;
    }

    @SmartColumn(id = 0, name = "序号")
    private Integer id;
    @SmartColumn(id = 1, name = "钻孔位置",fixed = true)
    private String location;
    @SmartColumn(id = 2, name = "瓦斯浓度")
    private String potency;
    @SmartColumn(id = 3, name = "孔口负压")
    private String pressure;
    @SmartColumn(id = 4, name = "混合流量")
    private String flow;
    @SmartColumn(id = 5, name = "温度")
    private String tem;
    @SmartColumn(id = 6, name = "记录人")
    private String people;
    @SmartColumn(id = 7, name = "记录时间")
    private String time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
