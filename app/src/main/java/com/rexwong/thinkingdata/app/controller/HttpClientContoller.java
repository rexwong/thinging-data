package com.rexwong.thinkingdata.app.controller;

import com.google.common.collect.ImmutableMap;
import com.rexwong.thinkingdata.app.controller.support.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/area/region")
public class HttpClientContoller {

    @RequestMapping(value = "/get-country-code-array", method = RequestMethod.GET)
    public Result areaTocountry(String scopeCode,String regionCode) throws Exception{
        throw new RuntimeException("hahahaha");
//        Result<Map<String,List<String>>> result = new Result<>();
//
//        List<String> data = Arrays.asList("EG","SA","MA","AE","AF","BH","DJ","DZ","IQ","JO","KM","KW","LB","LY","MR","OM","PK","PS","QA","SD","SO","SS","SSD","SY","TN","UG","YE");
//        result.setData( ImmutableMap.of("list",data));
//        return result;
    }

}
