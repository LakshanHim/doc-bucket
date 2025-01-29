package lk.shan.firstmy.service;

import lk.shan.firstmy.dto.LoginDto;
import lk.shan.firstmy.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void register(UserDto userDto);
    String login(LoginDto loginDto);

}
