package com.demo.service;

import com.demo.ParquetUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author dalizu on 2019/11/12.
 * @version v1.0
 * @desc
 */
@Slf4j
@Service
public class DemoService {

    public void test(String path,String outPut){
        log.info("test~~~");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ParquetUtils parquetUtils=new ParquetUtils();
                parquetUtils.write(path,outPut);
            }
        }).start();

    }

}
