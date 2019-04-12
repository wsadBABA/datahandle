package com.dagl.datahandle.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公用工具类
 */
public class StringUtil {
    /**
     * 判断是否是空串
     */
    public static boolean isNotNull(String str) {
        if (str != null && !"".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 判断为空
     * @param str
     * @return
     */
    public static boolean isNull(Object str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是空串
     */
    public static boolean isNotNull(Object str) {
        if (str != null && !"".equals(str.toString().trim())) {
            return true;
        }
        return false;
    }

    /**
     * 判断数组是否为空
     */
    public static boolean arrayIsNotNull(Object[] array) {
        if (array != null && array.length > 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 特殊字符转义，用于Lucene检索词处理
     * @param input
     * @return
     */
    public static String transformLuceneMetacharactor(String input){
        StringBuffer sb = new StringBuffer();
        String regex = "[+\\-&|!(){}\\[\\]^\"~*?:(\\)]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while(matcher.find()){
            matcher.appendReplacement(sb, "\\\\"+matcher.group());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Clob转String
     * @param clob
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public static String ClobToString(Clob clob) throws SQLException, IOException {
        String reString = "";
        Reader is = clob.getCharacterStream();// 得到流
        BufferedReader br = new BufferedReader(is);
        String s = br.readLine();
        StringBuffer sb = new StringBuffer();
        while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
            sb.append(s);
            s = br.readLine();
        }
        reString = sb.toString();
        br.close();
        return reString;
    }

    /**
     *
     * @param str
     * @return
     */
    public static String getNum(String str){
       String result="";
        if(str.length()>0){
            for(int i=0;i<str.length();i++){
                if(str.charAt(i)>= 48 &&str.charAt(i)<=57){
                    result += str.charAt(i);
                }else {
                    break;
                }
            }
        }
       return result;
    }

    /**
     * 返回字符串
     * @param o
     * @return
     */
    public static String objToStr(Object o){
        return isNull(o)?"":o.toString();
    }
}