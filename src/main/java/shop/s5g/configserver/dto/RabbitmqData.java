package shop.s5g.configserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RabbitmqData {
    private String host;
    private String port;
    private String username;
    private String password;
}
