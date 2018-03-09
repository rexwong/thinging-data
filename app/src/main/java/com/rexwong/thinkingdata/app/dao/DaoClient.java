package com.rexwong.thinkingdata.app.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rexwong.thinkingdata.app.utils.RestClientHelper;
import com.rexwong.thinkingdata.app.utils.RestException;
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

    @Value("${http.client.host}")
    private String host;

    @Value("${url.areaTocountry}")
    private String areaTocountry;

    @Value("${url.countryToArea}")
    private String countryToArea;

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ObjectMapper objectMapper;

    public List<String> httpClient(String code) throws RestException {

        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(areaTocountry, String.class, host,code);
        } catch (Exception e) {
            log.error("Calling rest({}) error({})", RestClientHelper.getUrl(areaTocountry, host,code), e.getMessage());
            throw new RestException(e.getMessage());
        }
        return RestClientHelper.extractingResponseJson(objectMapper, response,
                new TypeReference<List<String>>() {
                }, Collections.emptyList());
    }
}
