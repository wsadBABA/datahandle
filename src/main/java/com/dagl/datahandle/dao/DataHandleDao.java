package com.dagl.datahandle.dao;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @className DataHandleDao
 * @description TODO
 * @date 2019/03/22
 */
@Repository
public class DataHandleDao extends BaseDao{
    public void  insertTest(){
        Map map = new HashMap();
        String sql = "insert into t_scho_student(id,name,czbz,czsj) " +
                "values(seq_scho_student.nextval,?0,'1',sysdate) ";
        map.put("name","123232332");
        super.excuteSql(sql,"ada");
    }
    public void  selectTest(){
        String sql = "select seq_scho_student.nextval from dual  ";
    }
}
