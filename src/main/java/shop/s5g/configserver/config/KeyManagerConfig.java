package shop.s5g.configserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import shop.s5g.configserver.dto.BodyDto;
import shop.s5g.configserver.dto.KeyResponseDto;
import shop.s5g.configserver.dto.RabbitmqData;

@Configuration
public class KeyManagerConfig {
    @Value("${key.path}")
    private String keyPath;

    @Value("${repository.id}")
    private String repoKeyId;

    @Value("${encrypt.id}")
    private String encryptKeyId;

    @Value("${rabbitmq.id}")
    private String rabbitmqKeyId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public String loadPrivateKey() {
        String privateKey = retrievePrivateKey(keyPath + repoKeyId);
        String encryptKey = retrievePrivateKey(keyPath + encryptKeyId);
        RabbitmqData rabbitmqData = retrieveRabbitmqData(keyPath + rabbitmqKeyId);

        System.setProperty("config.repository.key", privateKey);
        System.setProperty("encrypt-key", encryptKey);
        System.setProperty("spring.rabbitmq.host", rabbitmqData.getHost());
        System.setProperty("spring.rabbitmq.port", rabbitmqData.getPort());
        System.setProperty("spring.rabbitmq.username", rabbitmqData.getUsername());
        System.setProperty("spring.rabbitmq.password", rabbitmqData.getPassword());
        return privateKey;
    }

    private String retrievePrivateKey(String keyPath) {
        ResponseEntity<KeyResponseDto> response = restTemplate.getForEntity(keyPath, KeyResponseDto.class);
        BodyDto bodyDto = response.getBody().getBody();
        return bodyDto.getSecret();
    }

    private RabbitmqData retrieveRabbitmqData(String keyPath) {
        ResponseEntity<KeyResponseDto> response = restTemplate.getForEntity(keyPath, KeyResponseDto.class);

        try {
            return objectMapper.readValue(response.getBody().getBody().getSecret(),
                RabbitmqData.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse RabbitmqData", e);
        }
    }
}
