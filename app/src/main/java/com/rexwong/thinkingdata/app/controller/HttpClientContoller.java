package com.rexwong.thinkingdata.app.controller;

import com.google.common.collect.ImmutableMap;
import com.rexwong.thinkingdata.app.config.KafkaProperties;
import com.rexwong.thinkingdata.app.controller.support.Result;
import kafka.utils.Json;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scala.util.parsing.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/area/region")
public class HttpClientContoller {
    @Autowired
    KafkaProducer producer;
    @RequestMapping(value = "/get-country-code-array", method = RequestMethod.GET)
    public Result areaTocountry(String scopeCode,String regionCode) throws Exception{
//        throw new RuntimeException("hahahaha");
        Result<Map<String,List<String>>> result = new Result<>();

        List<String> data = Arrays.asList(regionCode);
        result.setData( ImmutableMap.of("area",data));
        producer.send(new ProducerRecord<>(KafkaProperties.TOPIC, "1",result.toString() ),(recordMetadata, e)->{
            System.out.println("message send to:["+recordMetadata.partition()+"],offset:["+recordMetadata.offset()+"]");
        });
        return result;
    }

}
