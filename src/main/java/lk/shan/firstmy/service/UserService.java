package lk.shan.firstmy.service;

import lk.shan.firstmy.dto.LoginDto;
import lk.shan.firstmy.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService extends UserDetailsService {
    void register(UserDto userDto);
    String login(LoginDto loginDto);
    UserDto getUser(String username);
    List<UserDto> getAllUsers();
    String updateUser(UserDto userDto);
    String deleteUser(String username);

    int imageUploadProfile(MultipartFile file, String username);
    byte[] getImageProfile(String username) throws IOException;
    String deleteImageProfile(String username);
    String updateImageProfile(String username, MultipartFile file) throws IOException;

    int imageUploadCover(MultipartFile file, String username);
    byte[] getImageCover(String username) throws IOException;
    String deleteImageCover(String username);
    String updateImageCover(String username, MultipartFile file) throws IOException;

}
