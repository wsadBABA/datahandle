package com.dagl.datahandle.controller;

import com.dagl.datahandle.service.DataHandleService;
import com.dagl.datahandle.service.ExcelBlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author Administrator
 * @className DataHandleController
 * @description TODO
 * @date 2019/03/22
 */
@Controller
@RequestMapping("/dataHandle")
public class DataHandleController {
    @Autowired
    private DataHandleService dataHandleService;

    @Autowired
    private ExcelBlService excelBlService;
    Logger logger = LoggerFactory.getLogger(DataHandleController.class);

    @RequestMapping("/insert")
    @ResponseBody
    public String insert(){
//        dataHandleService.insertTest();
//        logger.warn("战神呢");
//        FtpClientUtil.getInstance();
       return "hello";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
//        dataHandleService.selectTest();
        return "hello";
    }

    @RequestMapping("/ajHandle")
    @ResponseBody
    public String ajHandle(){
        excelBlService.excelImport("aj");
        logger.error(new Date().toString()+":案卷补录结束");
        return "hello";
    }

    @RequestMapping("/ywHandle")
    @ResponseBody
    public String ywHandle(){
        excelBlService.excelImport("yw");
        logger.error(new Date().toString()+":业务补录结束");
        return "hello";
    }

    @RequestMapping("/yjHandle")
    @ResponseBody
    public String yjHandle(){
        excelBlService.excelImport("yj");
        logger.error(new Date().toString()+":原件补录结束");
        return "hello";
    }

    @RequestMapping("/mixHandle")
    @ResponseBody
    public String mixHandle(){
        excelBlService.excelImport("yj");
        excelBlService.excelImport("yw");
        excelBlService.excelImport("yj");
        logger.error(new Date().toString()+":整体混合补录结束");
        return "hello";
    }

    @RequestMapping("/blDataHandle")
    @ResponseBody
    public String blDataHandle(){
        excelBlService.blDataHandle();
        logger.error(new Date().toString()+":补录处理结束");
        return "hello";
    }
}
