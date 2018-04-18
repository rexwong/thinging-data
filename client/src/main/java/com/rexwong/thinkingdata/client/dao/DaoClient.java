package com.rexwong.thinkingdata.client.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rexwong.thinkingdata.client.rest.RestException;
import com.rexwong.thinkingdata.client.utils.RestClientHelper;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class DaoClient {

    @Value("${url.areaTocountry}")
    private String areaTocountry;

    @Value("${url.countryToArea}")
    private String countryToArea;

    @Resource
    private RestTemplate restTemplate;

    public Country2AreaResult httpClient(String code) throws RestException {
        try {
            ResponseEntity<Country2AreaResult> responseEntity = restTemplate.getForEntity(areaTocountry,Country2AreaResult.class,code);
            return responseEntity.getBody();
        } catch (Exception e) {
            log.error("Calling rest({}) error({})", RestClientHelper.getUrl(areaTocountry, code), e.getMessage());
            throw new RestException(e);
        }

    }
    @Data
    @ToString
    public static class Area2CountryResult {
        int errcode;
        String errmsg;
        Data data;

        @lombok.Data
        @lombok.ToString
        public static class Data {
            List<String> list;
        }
    }

    @Data
    @ToString
    public static class Country2AreaResult {
        int errcode;
        String errmsg;
        Data data;

        @lombok.Data
        @lombok.ToString
        public static class Data {
            List<String> area;
        }
    }
}
