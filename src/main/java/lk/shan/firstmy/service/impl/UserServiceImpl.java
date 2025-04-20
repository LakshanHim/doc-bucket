package lk.shan.firstmy.service.impl;

import lk.shan.firstmy.dto.LoginDto;
import lk.shan.firstmy.dto.UserDto;
import lk.shan.firstmy.entity.User;
import lk.shan.firstmy.repo.UserRepo;
import lk.shan.firstmy.service.UserService;
import lk.shan.firstmy.service.auth.JwtService;
import lk.shan.firstmy.utils.Converter;
import lk.shan.firstmy.utils.VarList;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private Converter converter;
    @Autowired
    @Lazy
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void register(UserDto userDto) {
        if(userRepo.existsByUsername(userDto.getUsername()))
            throw new UsernameNotFoundException(userDto.getUsername()+"Username is already taken...!");
        userRepo.save(converter.userDtoToEntity(userDto));
    }

    @Override
    public String login(LoginDto loginDto) {
        AuthenticationManager authenticationManager = new ProviderManager(authenticationProvider);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(loadUserByUsername(loginDto.username()));
        }
        throw new RuntimeException("Bad Request");
    }

    @Override
    public UserDto getUser(String username) {
        return userRepo.findByUsername(username)
                .map(user -> modelMapper.map(user,UserDto.class))
                .orElseThrow(() -> new RuntimeException("User not found: " + username)
        );
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user,UserDto.class)).collect(Collectors.toList());
    }

    @Override
    public String updateUser(UserDto userDto) {
        Optional<User> user = userRepo.findByUsername(userDto.getUsername());
        if(user.isPresent()) {
            User user1 = user.get();
            user1.setUsername(userDto.getUsername());
            user1.setPassword(userDto.getPassword());
            user1.setEmail(userDto.getEmail());
            user1.setImgPathCover(userDto.getImgPathCover());
            user1.setImgPathProfile(userDto.getImgPathProfile());
            user1.setRole(userDto.getRole());
            userRepo.save(user1);
            return VarList.RSP_SUCCESS;
        }
        else {
            return VarList.RSP_DUPLICATED;
        }
    }

    @Override
    public String deleteUser(String username) {
        if(userRepo.existsByUsername(username)){
            Optional<User> byUsername = userRepo.findByUsername(username);
            if(byUsername.isPresent()) {
                userRepo.delete(byUsername.get());
                return VarList.RSP_SUCCESS;
            }
        }
        return VarList.RSP_NO_DATA_FOUND;
    }

    @SneakyThrows
    @Override
    public int imageUploadProfile(MultipartFile file, String username) {
        String originalFilename = file.getOriginalFilename();
        Path path = Paths.get("upload/", originalFilename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        String fileUrl = "http://localhost:8080/upload/" +originalFilename;
        User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found" + username));
        user.setImgPathProfile(fileUrl);
        User save = userRepo.save(user);
        return 1;

    }

    @Override
    public byte[] getImageProfile(String username) throws IOException {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found" + username));
        String imgPathProfile = user.getImgPathProfile();
        String substring = imgPathProfile.substring(imgPathProfile.lastIndexOf("/") + 1);
        Path path = Paths.get("upload/", substring);
        if(!Files.exists(path)) {
            throw new RuntimeException("User not found");
        }
        return Files.readAllBytes(path);

    }

    @Override
    public String deleteImageProfile(String username) {
        if(userRepo.existsByUsername(username)){
            Optional<User> byUsername = userRepo.findByUsername(username);
            byUsername.get().setImgPathProfile(null);
            userRepo.save(byUsername.get());
        }
        return VarList.RSP_SUCCESS;
    }

    @Override
    public String updateImageProfile(String username, MultipartFile file) throws IOException {
        if(userRepo.existsByUsername(username)){
            User user = userRepo.findByUsername(username).get();
            String originalFilename = file.getOriginalFilename();
            Path path = Paths.get("upload/", originalFilename);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            String fileUrl = "http://localhost:8080/upload/" +originalFilename;
            user.setImgPathProfile(fileUrl);
            User save = userRepo.save(user);
            return VarList.RSP_SUCCESS;
        }
        return VarList.RSP_ERROR;
    }

    @SneakyThrows
    @Override
    public int imageUploadCover(MultipartFile file, String username) {
        String originalFilename = file.getOriginalFilename();
        Path path = Paths.get("upload/", originalFilename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        String fileUrl = "http://localhost:8080/upload/" +originalFilename;
        User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found" + username));
        user.setImgPathCover(fileUrl);
        User save = userRepo.save(user);
        return 1;
    }

    @Override
    public byte[] getImageCover(String username) throws IOException {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found" + username));
        String imgPathProfile = user.getImgPathProfile();
        String substring = imgPathProfile.substring(imgPathProfile.lastIndexOf("/") + 1);
        Path path = Paths.get("upload/", substring);
        if(!Files.exists(path)) {
            throw new RuntimeException("image not found");
        }
        return Files.readAllBytes(path);
    }

    @Override
    public String deleteImageCover(String username) {
        if (userRepo.existsByUsername(username)){
            Optional<User> byUsername = userRepo.findByUsername(username);
            byUsername.get().setImgPathCover(null);
            userRepo.save(byUsername.get());
        }
        return VarList.RSP_SUCCESS;
    }

    @Override
    public String updateImageCover(String username, MultipartFile file) throws IOException {
        if(userRepo.existsByUsername(username)){
            User user = userRepo.findByUsername(username).get();
            String originalFilename = file.getOriginalFilename();
            Path path = Paths.get("upload/", originalFilename);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            String fileUrl = "http://localhost:8080/upload/" +originalFilename;
            user.setImgPathCover(fileUrl);
            User save = userRepo.save(user);
            return VarList.RSP_SUCCESS;
        }
        return VarList.RSP_ERROR;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent()) {
            User use = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(use.getUsername())
                    .password(use.getPassword())
                    .roles(String.valueOf(use.getRole()))
                    .build();
        }
        else
            throw new UsernameNotFoundException(username);
    }
}
