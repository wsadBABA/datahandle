package com.dagl.datahandle.service;


import com.dagl.datahandle.config.DaConfig;
import com.dagl.datahandle.dao.SjblDao;
import com.dagl.datahandle.utils.SpringContextUtil;
import com.dagl.datahandle.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @className ExcelBlService
 * @description TODO
 * @date 2018/10/29
 */
@Service
public class ExcelBlService {
    @Autowired
    private SjblDao sjblDao;
    @Autowired
    private DaConfig daConfig;
    Logger logger = LoggerFactory.getLogger(ExcelBlService.class);

    @Value("${ajExcelPath}")
    private String ajExcelPath;
    @Value("${ywExcelPath}")
    private String ywExcelPath;
    @Value("${yjExcelpath}")
    private String yjExcelpath;
    @Value("${yjPath}")
    private String yjPath;
    @Value("${blTitle}")
    private String blTitle;
    @Value("${nasPath}")
    private String nasPath;

    /**
     * 处理Excel数据导入
     *
     * @return
     * @throws IOException
     */
    public void excelImport(String type) {
        Map map = new HashMap();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
        Date date = new Date();
        String dateStr = formatter.format(date);
        if("aj".equals(type)){
            map.put("aj", excelImport("D:\\aj" + dateStr + ".csv", "rId1",blTitle,
                ajExcelPath,nasPath,yjPath, "aj"));
        }else if("yw".equals(type)){
            map.put("yw", excelImport("D:\\yw" + dateStr + ".csv", "rId1", blTitle,
                ywExcelPath, nasPath, yjPath, "yw"));
        }else if("yj".equals(type)){
            String[] yjExcelPaths = yjExcelpath.split(",");
            for(String excelPath:yjExcelPaths){
                if(StringUtil.isNotNull(excelPath)){
                    String flieName = excelPath.split("\\\\")[excelPath.split("\\\\").length-1].split("\\.")[0];
                    map.put("yj", excelImport("D:\\yj" + dateStr + flieName + ".csv", "rId1",blTitle,
                            excelPath, nasPath, yjPath, "yj"));
                }
            }
        }
    }

    /**
     * excel导入处理方法
     *
     * @param logPath     错误数据记录文件路径 eg. "D:\\yw" + dateStr + ".csv"
     * @param rId         excel之sheet值  eg. "rId1"
     * @param title       处理日志描述 eg. "外事档案"
     * @param excelPath   excel存储路径 eg."H:\\档案数据\\白茆派出所1\\340207490000-1.xlsx"
     * @param nasPath     nas附件存储路径 eg. "/dagl/hjyjbl"
     * @param localFjPath 本地附件存储路径 "H:\\档案数据\\白茆派出所1\\"
     * @param type        导入类型（yw、yj、aj）
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public Map excelImport(String logPath, String rId, String title, String excelPath, String nasPath,
                           String localFjPath, String type) {
        Map map = new HashMap();
        ExcelService excelUtil = SpringContextUtil.getBean("excelService");
        try {
            BufferedWriter defWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logPath)));
            excelUtil.excelServiceInit(localFjPath, nasPath, rId, defWriter,type);
            excelUtil.processOneSheet(excelPath);
            sjblDao.rzInsert("1", type, title + type +
                    "补录结果:成功--" + excelUtil.sucNum + ",失败--" + excelUtil.defNum+";excelPath:"+excelPath);
        } catch (Exception e) {
            e.printStackTrace();
            sjblDao.rzInsert("0", type, title + type +
                    "失败;成功/失败" + excelUtil.sucNum + "/" + excelUtil.defNum + ";失败原因:" + e.getLocalizedMessage()
                    +";excelPath:"+excelPath);
        }
        map.put("sucNum", excelUtil.sucNum);
        map.put("defNum", excelUtil.defNum);
        return map;
    }

    /**
     * 补录数据经过处理转换存储到正式业务表
     */
    @Transactional(rollbackFor = Exception.class)
    public void blDataHandle() {
        long ajNum = Long.parseLong(sjblDao.getAjNum(daConfig.getAjTemTable()));
        //防止数据量过大，分批处理
        if (ajNum > 0) {
            long num = ajNum / 10000;
            List<Map<String, Object>> ajList;
            for (int n = 0; n <= num; n++) {
                ajList = sjblDao.getAjList(daConfig.getAjTemTable(),daConfig.getAjTableFields(),n);
                Map map;//案卷数据
                Integer ajId;
                for (int i = 0; i < ajList.size(); i++) {
                    map = ajList.get(i);
                    try {
                        ajId = sjblDao.getDataId(daConfig.getAjTemTable(),daConfig.getAjUniqueField(),map);
                        if (StringUtil.isNull(ajId)) {
                            ajId = sjblDao.ajInsert(map,daConfig.getOffAjTable(),
                                    daConfig.getAjTableFields(),daConfig.getAjSeq(),daConfig.getAjDxId());
                            //更新处理状态
                            sjblDao.updateSuccessForDataHandle(daConfig.getAjTemTable(), map.get("ID").toString(), "1");
                        }
                        ywInsert(ajId, daConfig.getYwAjField(),map.get(daConfig.getYwAjField()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
    }


    public void ywInsert(Integer ajId,String ywAjField, Object ywAjFieldValue) {
        long ywNum = Long.parseLong(sjblDao.getYwNum(daConfig.getYwTemTable(),ywAjField,ywAjFieldValue));
        //防止数据量过大，分批处理
        if (ywNum > 0) {
            long num = ywNum / 10000;
            List<Map<String, Object>> ywList;
            for (int n = 0; n <= num; n++) {
                ywList = sjblDao.getYwList(daConfig.getYwTemTable(),daConfig.getYwTableFields(),ywAjField,ywAjFieldValue,n);
                Map map;//案卷数据
                Integer ywId;
                for (int i = 0; i < ywList.size(); i++) {
                    map = ywList.get(i);
                    try {
                        ywId = sjblDao.getDataId(daConfig.getYwTemTable(),daConfig.getYwUniqueField(),map);
                        if(StringUtil.isNull(ywId)){
                            //字典值替换
                            if(daConfig.getYwTableFields().contains("XB")){
                                map.put("XB", sjblDao.getZdId(map.get("XB"), "XB"));
                            }
                            ywId = sjblDao.ywInsert(map, ajId,daConfig.getOffYwTable(),
                                    daConfig.getYwTableFields(),daConfig.getYwSeq(),daConfig.getYwDxId());
                            //更新处理状态
                            sjblDao.updateSuccessForDataHandle(daConfig.getYwTemTable(), map.get("ID").toString(), "1");
                        }
                        yjInsert(ywId,daConfig.getYjYwField(), map.get(daConfig.getYjYwField()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
    }

    public void yjInsert(Integer ywId,String ywAjField, Object ywAjFieldValue) {
        List<Map> yjList = sjblDao.getYjMap(daConfig.getYjTemTable(),ywAjField,ywAjFieldValue);
        try {
            if (!yjList.isEmpty()) {
                for (Map map : yjList) {
                    if(StringUtil.isNull(sjblDao.getDataId(daConfig.getYjTemTable(),daConfig.getYjUniqueField(),map))){
                        sjblDao.yjInsert(map, ywId,daConfig.getOffYjTable(),daConfig.getYjSeq(),daConfig.getYwDxId());
                        sjblDao.updateSuccessForDataHandle(daConfig.getYjTemTable(), map.get("ID").toString(), "1");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
