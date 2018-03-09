package com.rexwong.thinkingdata.app.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriTemplate;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class RestClientHelper {

    private static Logger logger = LoggerFactory.getLogger(RestClientHelper.class);

    public static Map<String, String> convertObjectToMap(Object obj) {
        Map<String, String> result = Maps.newHashMapWithExpectedSize(24);
        try {
            BeanInfo info = Introspector.getBeanInfo(obj.getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {

                Method reader = pd.getReadMethod();
                if (reader != null) {
                    Object target = reader.invoke(obj);
                    String val = "";
                    if (target != null && (target.getClass() != Class.class)) {
                        if (target instanceof List) {
                            val = ((List) target).stream().map(o -> o.toString()).collect(joining(",")).toString();
                        } else if (target instanceof Date) {
                            val = String.valueOf(((Date) reader.invoke(obj)).getTime());
                        } else {
                            val = reader.invoke(obj).toString();
                        }
                    }
                    if (StringUtils.hasLength(val)) {
                        result.put(pd.getName(), val);
                    }
                }
            }
        } catch (Throwable e) {
            logger.error("convert map to object " + Throwables.getStackTraceAsString(e));
        }
        return result;
    }

    public static String query(Map<String, String> queryMap) {
        if (queryMap != null && queryMap.size() > 0)
            return Joiner.on("&").withKeyValueSeparator("=").join(queryMap);
        return null;
    }

    public static String query(Object obj) {
        return query(convertObjectToMap(obj));
    }

    public static String mapToString(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : map.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            String[] value = (String[]) map.get(key);
            try {
                stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
                stringBuilder.append("=");
                stringBuilder.append(value != null ? URLEncoder.encode(value[0], "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }
        return stringBuilder.toString();
    }


    public static URI getUrl(String url, Object... urlVariables) {
        UriTemplate uriTemplate = new UriTemplate(url);
        URI expanded = uriTemplate.expand(urlVariables);
        return expanded;
    }

    public static <T> T extractingResponseJson(ObjectMapper objectMapper, ResponseEntity<String> response,
                                               TypeReference<? extends T> typeRefer,
                                               T defaults) {
        String json = response.getBody();
        if (json == null){
            return defaults;
        }
        try {
            return objectMapper.readValue(json.getBytes("UTF-8"), typeRefer);
        } catch (UnsupportedEncodingException | JsonParseException | JsonMappingException e) {
            logger.error("Json Exception {}", e.getMessage());
        } catch (IOException e) {
            logger.error("IO exception {}", e.getMessage());
        }
        return defaults;
    }

    public static <T> T extractingResponseJson(ObjectMapper objectMapper, String json,
                                               TypeReference<? extends T> typeRefer,
                                               T defaults) {
        try {
            return objectMapper.readValue(json.getBytes("UTF-8"), typeRefer);
        } catch (UnsupportedEncodingException | JsonParseException | JsonMappingException e) {
            logger.error("Json Exception {}", e.getMessage());
        } catch (IOException e) {
            logger.error("IO exception {}", e.getMessage());
        }
        return defaults;
    }

}
