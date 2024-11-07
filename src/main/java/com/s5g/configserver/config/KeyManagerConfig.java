package com.s5g.configserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.s5g.configserver.dto.BodyDto;
import com.s5g.configserver.dto.KeyResponseDto;

@Configuration
public class KeyManagerConfig {
    @Value("${key.path}")
    private String keyPath;

    @Value("${repository.id}")
    private String repoKeyId;

    @Value("${encrypt.id}")
    private String encryptKeyId;

    @Bean
    public String loadPrivateKey() {
        String privateKey = retrievePrivateKey(keyPath + repoKeyId);
        String encryptKey = retrievePrivateKey(keyPath + encryptKeyId);

        System.setProperty("config.repository.key", privateKey);
        System.setProperty("encrypt-key", encryptKey);
        return privateKey;
    }

    private String retrievePrivateKey(String keyPath) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KeyResponseDto> response =  restTemplate.getForEntity(keyPath, KeyResponseDto.class);
        BodyDto bodyDto = response.getBody().getBody();
        return bodyDto.getSecret();
    }
}
