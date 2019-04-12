package com.dagl.datahandle.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @className dablConfig
 * @description TODO
 * @date 2019/04/01
 */
@Component
@ConfigurationProperties(prefix = "dabl")
public class DaConfig {
    private String offAjTable;
    private String offYwTable;
    private String offYjTable;
    private String ajDxId;
    private String ywDxId;
    private String ajTemTable;
    private String ywTemTable;
    private String yjTemTable;

    private String ajSeq;
    private String ywSeq;
    private String yjSeq;

    private String ajExcelFields;
    private String ywExcelFields;
    private String yjExcelFields;
    private String ajTableFields;
    private String ywTableFields;
    private String yjTableFields;

    private String ajUniqueField;
    private String ywUniqueField;
    private String yjUniqueField;

    private String dateForm;
    private String ywAjField;
    private String yjYwField;
    private String yjName;


    public String getOffAjTable() {
        return offAjTable;
    }

    public void setOffAjTable(String offAjTable) {
        this.offAjTable = offAjTable;
    }

    public String getOffYwTable() {
        return offYwTable;
    }

    public void setOffYwTable(String offYwTable) {
        this.offYwTable = offYwTable;
    }

    public String getOffYjTable() {
        return offYjTable;
    }

    public void setOffYjTable(String offYjTable) {
        this.offYjTable = offYjTable;
    }

    public String getAjDxId() {
        return ajDxId;
    }

    public void setAjDxId(String ajDxId) {
        this.ajDxId = ajDxId;
    }

    public String getYwDxId() {
        return ywDxId;
    }

    public void setYwDxId(String ywDxId) {
        this.ywDxId = ywDxId;
    }

    public String getAjTemTable() {
        return ajTemTable;
    }

    public void setAjTemTable(String ajTemTable) {
        this.ajTemTable = ajTemTable;
    }

    public String getYwTemTable() {
        return ywTemTable;
    }

    public void setYwTemTable(String ywTemTable) {
        this.ywTemTable = ywTemTable;
    }

    public String getYjTemTable() {
        return yjTemTable;
    }

    public void setYjTemTable(String yjTemTable) {
        this.yjTemTable = yjTemTable;
    }

    public String getAjExcelFields() {
        return ajExcelFields;
    }

    public void setAjExcelFields(String ajExcelFields) {
        this.ajExcelFields = ajExcelFields;
    }

    public String getYwExcelFields() {
        return ywExcelFields;
    }

    public void setYwExcelFields(String ywExcelFields) {
        this.ywExcelFields = ywExcelFields;
    }

    public String getYjExcelFields() {
        return yjExcelFields;
    }

    public void setYjExcelFields(String yjExcelFields) {
        this.yjExcelFields = yjExcelFields;
    }

    public String getAjTableFields() {
        return ajTableFields;
    }

    public void setAjTableFields(String ajTableFields) {
        this.ajTableFields = ajTableFields;
    }

    public String getYwTableFields() {
        return ywTableFields;
    }

    public void setYwTableFields(String ywTableFields) {
        this.ywTableFields = ywTableFields;
    }

    public String getYjTableFields() {
        return yjTableFields;
    }

    public void setYjTableFields(String yjTableFields) {
        this.yjTableFields = yjTableFields;
    }

    public String getAjUniqueField() {
        return ajUniqueField;
    }

    public void setAjUniqueField(String ajUniqueField) {
        this.ajUniqueField = ajUniqueField;
    }

    public String getYwUniqueField() {
        return ywUniqueField;
    }

    public void setYwUniqueField(String ywUniqueField) {
        this.ywUniqueField = ywUniqueField;
    }

    public String getYjUniqueField() {
        return yjUniqueField;
    }

    public void setYjUniqueField(String yjUniqueField) {
        this.yjUniqueField = yjUniqueField;
    }

    public String getDateForm() {
        return dateForm;
    }

    public void setDateForm(String dateForm) {
        this.dateForm = dateForm;
    }

    public String getYwAjField() {
        return ywAjField;
    }

    public void setYwAjField(String ywAjField) {
        this.ywAjField = ywAjField;
    }

    public String getYjYwField() {
        return yjYwField;
    }

    public void setYjYwField(String yjYwField) {
        this.yjYwField = yjYwField;
    }

    public String getYjName() {
        return yjName;
    }

    public void setYjName(String yjName) {
        this.yjName = yjName;
    }

    public String getAjSeq() {
        return ajSeq;
    }

    public void setAjSeq(String ajSeq) {
        this.ajSeq = ajSeq;
    }

    public String getYwSeq() {
        return ywSeq;
    }

    public void setYwSeq(String ywSeq) {
        this.ywSeq = ywSeq;
    }

    public String getYjSeq() {
        return yjSeq;
    }

    public void setYjSeq(String yjSeq) {
        this.yjSeq = yjSeq;
    }
}
