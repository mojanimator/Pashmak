// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.app.pashmak.Model;

import io.realm.RealmObject;

public class Province extends RealmObject {
    private short id;
    private String name;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
