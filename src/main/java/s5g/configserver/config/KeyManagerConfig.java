package s5g.configserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import s5g.configserver.dto.BodyDto;
import s5g.configserver.dto.KeyResponseDto;

@Configuration
public class KeyManagerConfig {
    @Value("${key.path}")
    private String keyPath;

    @Value("${repository.id}")
    private String repoKeyId;

    @Value("${password.id}")
    private String passwordKeyId;

    @Value("${encrypt.id}")
    private String encryptKeyId;

    @Bean
    public String loadPrivateKey() {
        String privateKey = retrievePrivateKey(keyPath + repoKeyId);
        String password = retrievePrivateKey(keyPath + passwordKeyId);
        String encryptKey = retrievePrivateKey(keyPath + encryptKeyId);

        System.setProperty("config.repository.key", privateKey);
        System.setProperty("config.user.password", password);
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
