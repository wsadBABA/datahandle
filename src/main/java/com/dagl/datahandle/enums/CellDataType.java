package com.dagl.datahandle.enums;

public enum CellDataType {
    BOOL("",""), ERROR("",""), FORMULA("",""), INLINESTR("",""), SSTINDEX("",""), NUMBER("",""), DATE("",""), NULL("","");
    private String name;
    private String index;

    CellDataType(String name, String index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}