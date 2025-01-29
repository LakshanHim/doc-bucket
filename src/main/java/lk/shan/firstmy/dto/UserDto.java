package lk.shan.firstmy.dto;

import lk.shan.firstmy.utils.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    private String userID;
    private String password;
    private UserRoles role;
}
