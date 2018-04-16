// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.app.pashmak.Model;

import io.realm.RealmObject;

public class County extends RealmObject {

    private int id;
    private short province_id;
    private String name;

    private Province province;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public short getProvince_id() {
        return province_id;
    }

    public void setProvince_id(short province_id) {
        this.province_id = province_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }


}
