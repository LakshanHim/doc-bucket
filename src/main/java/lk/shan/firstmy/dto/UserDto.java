package lk.shan.firstmy.dto;

import lk.shan.firstmy.utils.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    private String userId;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private String imgPathProfile;
    private String imgPathCover;
    private UserRoles role;
    private String registerDate;
}
