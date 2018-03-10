package com.rexwong.thinkingdata.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * @author rexwong
 */
@SpringBootConfiguration
@PropertySources({@PropertySource(value = "classpath:rest.properties")})
public class ApplicationConfig {
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }
}
