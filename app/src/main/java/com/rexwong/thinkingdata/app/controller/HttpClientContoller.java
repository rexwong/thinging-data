package com.rexwong.thinkingdata.app.controller;

import com.rexwong.thinkingdata.app.controller.support.Result;
import com.rexwong.thinkingdata.app.dao.DaoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/area/region")
public class HttpClientContoller {
    @Autowired
    DaoClient daoClient;
    @RequestMapping(value = "/get-country-code-array", method = RequestMethod.GET)
    public Result areaTocountry(String scopeCode,String regionCode){
        Result<List<String>> result = new Result<>();
        List<String> data = Arrays.asList("EG","SA","MA","AE","AF","BH","DJ","DZ","IQ","JO","KM","KW","LB","LY","MR","OM","PK","PS","QA","SD","SO","SS","SSD","SY","TN","UG","YE");
        result.setData(data);
        return result;
    }
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public Result testArea(String regionCode){
        List<String>  data = daoClient.httpClient("MSL");
        Result<List<String>> result = new Result<>();
        result.setData(data);
        return result;
    }
}
