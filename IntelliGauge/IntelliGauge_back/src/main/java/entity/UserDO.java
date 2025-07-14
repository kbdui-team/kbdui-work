package entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class UserDO {
    @Id
    private Long id;
    private String username;
    private String password;
    private String role;
}
