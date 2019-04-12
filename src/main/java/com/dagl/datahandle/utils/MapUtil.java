package com.dagl.datahandle.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @className MapUtil
 * @description TODO
 * @date 2019/04/01
 */
public class MapUtil {
    public static Map arrDataToMap(List<String> rowlist, String[] fieldList,String format){
        Map map = new HashMap();
        try {
            if(fieldList.length>0){
                for(int i =0;i<fieldList.length;i++){
                    //日期类处理
                    if(fieldList[i].contains(":")){
                        map.put(fieldList[i].split(":")[0],DateUtil.dateFormat(rowlist.get(i),format));
                    }else {
                        map.put(fieldList[i],rowlist.get(i));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Excel数据转换失败");
        }
        return map;
    }
}
