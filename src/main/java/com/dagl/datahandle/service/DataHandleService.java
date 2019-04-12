package com.dagl.datahandle.service;

import com.dagl.datahandle.dao.DataHandleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Administrator
 * @className dataHandleService
 * @description TODO
 * @date 2019/03/22
 */
@Service
public class DataHandleService {
    @Autowired
    private DataHandleDao dataHandleDao;

    @Transactional
    public void  insertTest(){
        dataHandleDao.insertTest();
    }
    public void  selectTest(){
        dataHandleDao.selectTest();
    }
}
