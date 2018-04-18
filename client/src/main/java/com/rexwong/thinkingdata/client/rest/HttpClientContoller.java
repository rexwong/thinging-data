package com.rexwong.thinkingdata.client.rest;

import com.rexwong.thinkingdata.client.dao.DaoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/area/region")
public class HttpClientContoller {
    @Autowired
    DaoClient daoClient;
    @GetMapping(value = "/test")
    public Object testArea(String regionCode){
        Object data = daoClient.httpClient("MSL");
        return data;
    }
}
