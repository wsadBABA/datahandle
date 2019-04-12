package com.dagl.datahandle.dao;

import com.dagl.datahandle.config.DaConfig;
import com.dagl.datahandle.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @className SjblDao
 * @description 数据补录处理Dao类
 * @date 2018/10/29
 */
@Repository
public class SjblDao extends BaseDao {

    @Autowired
    private DaConfig daConfig;

    /**
     * 补录日志记录
     *
     * @param isSuccess 0：失败；1：成功
     * @param bllx      yw/yj/aj
     * @param cznr
     */
    public void rzInsert(String isSuccess, String bllx, String cznr) {
        String sql = " INSERT INTO T_SJBL_RZ\n" +
                "  (ID, ISSUCCESS, BLLX, CZNR, CZSJ)\n" +
                "VALUES\n" +
                "  (SEQ_HJYWBL_RZ.NEXTVAL,?,?,?,SYSDATE)\n ";
        excuteSql(sql, isSuccess, bllx, cznr);
    }


    /***
     * 判断指定数据（业务、案卷亦或原件）是否存在
     * @param table
     * @return
     */
    public boolean isDataNotExist(Map map, String fieldName, String table) {
        String sql;
        sql = "SELECT COUNT(1) FROM " + table + " D WHERE 1=1  temCon ";
        List params = new ArrayList();
        String[] fields= fieldName.split(",");
        StringBuilder temCon = new StringBuilder("");
        for(String filed:fields){
            temCon.append(" and  " + fields +" =? ");
            params.add(map.get(filed));
        }
        Object object = queryUniqueResult(sql.replaceAll("temCon",temCon.toString()), params);
        return "0".equals(object.toString());
    }

    /**
     * Excel数据导入
     *
     * @param map
     * @param filedsToSave
     * @param table
     * @param type
     */
    public void insertToZjb(Map map, String filedsToSave, String table, String type) {
        String sql = "INSERT INTO " + table + " (ID,SUCCESS," + ("yj".equals(type) ? "NASWJM," : "") + filedsToSave + ") VALUES\n" +
                "(temSeq,'0'temSql)";
        String[] fieldsArr = filedsToSave.split(",");
        List params = new ArrayList();
        StringBuilder temSql = new StringBuilder();
        String seq;
        if ("yj".equals(type)) {
            temSql.append(",?");
            params.add(map.get("NASFilePath"));
            seq = "SEQ_HJYW_HJXXB_YJBL.NEXTVAL";
        } else {
            seq = "SEQ_HJYW_HJXXB_YWBL.NEXTVAL";
        }
        for (int i = 0; i < fieldsArr.length; i++) {
            temSql.append(",?");
            params.add(map.get(fieldsArr[i]));
        }
        sql = sql.replaceAll("temSql", temSql.toString()).replaceAll("temSeq", seq);
        excuteSql(sql, params);

    }


    /**
     * 获取案卷量
     *
     * @return
     */
    public String getAjNum(String table) {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE SUCCESS = '0'";
        return queryUniqueResult(sql).toString();
    }

    /**
     * 获取业务量
     *
     * @return
     */
    public String getYwNum(String table, String ywAjField, Object ywAjFieldValue) {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE SUCCESS = '0' AND " + ywAjField + " = ?";
        return queryUniqueResult(sql, ywAjFieldValue).toString();
    }

    /**
     * 获取案卷数据
     *
     * @param n
     * @return
     */
    public List<Map<String, Object>> getAjList(String table, String fileds, int n) {
        String sql = "SELECT K.*\n" +
                "  FROM (SELECT " + fileds + ",ID,ROWNUM RN\n" +
                "  FROM  " + table + " D\n" +
                " WHERE D.SUCCESS = '0') K\n" +
                " WHERE K.RN > ? * 10000\n" +
                "   AND K.RN < (? + 1) * 10000";
        return queryMapList(sql, n, n);
    }

    /**
     * 获取业务数据
     *
     * @param n 分批次处理
     * @return
     */
    public List<Map<String, Object>> getYwList(String table, String fileds, String ywAjField, Object ywAjFieldValue, int n) {
        String sql = "SELECT K.*\n" +
                "  FROM (SELECT D.ID,\n" +
                "       " + fileds + ",ROWNUM RN\n" +
                "  FROM " + table + " D\n" +
                " WHERE D.SUCCESS = '0' AND " + ywAjField + " = ?) K\n" +
                " WHERE K.RN > ? * 10000\n" +
                "   AND K.RN < (? + 1) * 10000";
        return queryMapList(sql, ywAjFieldValue, n, n);
    }

    /**
     * 案卷数据保存
     *
     * @param map
     */
    public Integer ajInsert(Map map, String table, String fields, String seq, String dxId) {
        Integer ajId = Integer.parseInt(queryUniqueResult("SELECT " + seq + " FROM DUAL").toString());
        String sql = " INSERT INTO " + table + " (ID,DXID,CJSJ,CZBS" +
                "," + fields + ") \n" +
                "VALUES(?,?,SYSDATE,'1'temSql)";
        String[] fieldsArr = fields.split(",");
        List params = new ArrayList();
        StringBuilder temSql = new StringBuilder();
        params.add(ajId);
        params.add(dxId);
        for (int i = 0; i < fieldsArr.length; i++) {
            temSql.append(",?");
            params.add(map.get(fieldsArr[i]));
        }
        excuteSql(sql.replaceAll("temSql",temSql.toString()), params);
        return ajId;
    }

    /**
     * 业务数据保存
     *
     * @param map
     */
    public Integer ywInsert(Map map, Integer ajId,String table, String fields, String seq, String dxId) {
        Integer ywId = Integer.parseInt(queryUniqueResult("SELECT "+ seq+" FROM DUAL").toString());
        String sql = " INSERT INTO " + table +" (ID,DXID,FID,CJSJ,CZBS" +
                "," + fields + ") \n" +
                "VALUES(?,?,?,SYSDATE,'1'temSql) ";
        String[] fieldsArr = fields.split(",");
        List params = new ArrayList();
        StringBuilder temSql = new StringBuilder();
        params.add(ywId);
        params.add(dxId);
        params.add(ajId);
        for (int i = 0; i < fieldsArr.length; i++) {
            temSql.append(",?");
            params.add(map.get(fieldsArr[i]));
        }
        excuteSql(sql.replaceAll("temSql",temSql.toString()), params);
        return ywId;
    }

    /**
     * 获取字典ID
     *
     * @return
     */
    public String getZdId(Object mc, String type) {
        String sql = "SELECT D.ID\n" +
                "\tFROM T_XT_ZD D\n" +
                " WHERE D.CZBS <> '3'\n" +
                "\t AND D.ZDMC = ?\n" +
                " START WITH D.ZDDM = ?\n" +
                "CONNECT BY D.SJZD = PRIOR D.ID";
        try {
            return queryUniqueResult(sql, mc, type).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 中间表数据转入系统结果更新
     *
     * @param tabName
     * @param success 0:未成功；1：成功;
     */
    public void updateSuccessForDataHandle(String tabName, String id, String success) {
        StringBuilder sql = new StringBuilder("UPDATE " + tabName + " D SET D.SUCCESS = ? WHERE D.ID = ?");
        super.excuteSql(sql.toString(), success, id);
    }

    public List<Map> getYjMap(String table, String ywAjField, Object ywAjFieldValue) {
        String sql = "SELECT D.MC, D.NASWJM, D.ID\n" +
                "  FROM "+ table +" D\n" +
                " WHERE D.SUCCESS = '0'\n" +
                "   AND " + ywAjField + " = ? ";
        return queryMapList(sql, ywAjFieldValue);
    }

    /**
     * 原件保存
     *
     * @param map
     * @param ywId
     */
    public void yjInsert(Map map, Integer ywId,String table,String yjSeq,String ywDxId) {
        String sql = "INSERT INTO " + table + "\n" +
                "  (ID, DXID, YWID, MC, NASWJM, SCSJ, CZBS)\n" +
                "VALUES\n" +
                "  (" + yjSeq + ", ?, ?, ?, ?, SYSDATE,'1')\n";
        excuteSql(sql, ywDxId, ywId, map.get("MC"), map.get("NASWJM"));
    }



    public Integer getDataId(String table, String field, Map map) {
        String sql = "select d.id\n" +
                "\tfrom " + table + " d\n" +
                " where d.czbs <> '3'\n" +
                " temCon ";
        List params = new ArrayList();
        String[] fields= field.split(",");
        StringBuilder temCon = new StringBuilder("");
        for(String filed:fields){
            temCon.append(" and  " + fields +" =? ");
            params.add(map.get(filed));
        }
        try {
            return Integer.valueOf(queryUniqueResult(sql.replaceAll("temCon",temCon.toString()),params).toString());
        } catch (Exception e) {
            return null;
        }
    }
}
