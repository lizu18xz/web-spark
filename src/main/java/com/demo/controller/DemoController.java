package com.demo.controller;

import com.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dalizu on 2019/11/12.
 * @version v1.0
 * @desc
 */
@RestController
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/write")
    public void demo(@RequestParam("path") String path, @RequestParam("outPut")String outPut){

        demoService.test(path,outPut);
    }


    @GetMapping("/join")
    public void join(@RequestParam("path") String path){

        demoService.join(path);
    }

    @GetMapping("/udf")
    public void udf(){

        demoService.udf();
    }


}
