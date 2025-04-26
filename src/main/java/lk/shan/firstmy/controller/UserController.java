package lk.shan.firstmy.controller;

import lk.shan.firstmy.dto.LoginDto;
import lk.shan.firstmy.dto.ResponseDto;
import lk.shan.firstmy.dto.UserDto;
import lk.shan.firstmy.entity.User;
import lk.shan.firstmy.repo.UserRepo;
import lk.shan.firstmy.service.UserService;
import lk.shan.firstmy.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@CrossOrigin
@RequestMapping("/v1/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ResponseDto responseDto;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto) {
        try{
            userService.register(userDto);
            return new ResponseEntity<>(userDto.getUsername()+" user registered..!!",HttpStatus.CREATED);
        }catch (Exception exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
        try{
            return new ResponseEntity<>(userService.login(loginDto),HttpStatus.CREATED);
        }catch (Exception exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getUser/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username){
        try{
            UserDto user = userService.getUser(username);
            return new ResponseEntity<>(user,HttpStatus.OK);
        }catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getUserId/{userId}")
    public ResponseEntity<UserDto> getUserId(@PathVariable String userId){
        try{
            UserDto userBYId = userService.getUserBYId(userId);
            return ResponseEntity.ok(userBYId);
        }catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        try{
            return new ResponseEntity<>(userService.getAllUsers(),HttpStatus.OK);
        }catch (Exception exception){
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<ResponseDto> updateUser(@RequestBody UserDto userDto){
        ResponseDto responseDto = new ResponseDto();
        try {
            String response = userService.updateUser(userDto);

            if(VarList.RSP_SUCCESS.equals(response)){
                responseDto.setMessage("User updated successfully");
                responseDto.setCode(VarList.RSP_SUCCESS);
                responseDto.setContent(response);
                return new ResponseEntity<>(responseDto,HttpStatus.OK);
            } else if (VarList.RSP_DUPLICATED.equals(response)) {
                responseDto.setMessage("User updated duplicated");
                return new ResponseEntity<>(responseDto,HttpStatus.CONFLICT);
            } else {
                responseDto.setMessage("User update failed");
                return new ResponseEntity<>(responseDto,HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseDto.setMessage("User update failed");
            return new ResponseEntity<>(responseDto,HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteUser/{userName}")
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable String userName){
        try {
            String res = userService.deleteUser(userName);
            if (res.equals("00")){
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            }else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/uploadProfileImg/{userName}")
    public ResponseEntity<String> imageUploadProfile(@PathVariable String userName, @RequestParam("file") MultipartFile file){
        int i = userService.imageUploadProfile(file, userName);
        if (i ==1){
            return new ResponseEntity<>("Upload suceess",HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("Upload failed",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get/profileImg/{userName}")
    public ResponseEntity<byte[]> getProfileImg(@PathVariable String userName){
        Optional<User> byUsername = userRepo.findByUsername(userName);
        if (!byUsername.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = byUsername.get();
        String imgPathProfile = user.getImgPathProfile();
        String fileExtension = getFileExtension(imgPathProfile);

        if(fileExtension == null || fileExtension.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        byte[] img;
        try {
             img = userService.getImageProfile(userName);
        } catch (FileNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }

        MediaType mediaType = getMediaTypeForFileExtension(fileExtension);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return new ResponseEntity<>(img, headers, HttpStatus.OK);
    }
    private String getFileExtension(String url) {
        if (url == null || !url.contains (".")) {
            return null;
        }
        return url.substring (url.lastIndexOf (".") + 1);
    }

    private MediaType getMediaTypeForFileExtension(String extension) {
        switch (extension.toLowerCase()) {
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "webp":
                return MediaType.valueOf ("image/webp");
            case "bmp":
                return MediaType.valueOf ("image/bmp");
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    @DeleteMapping("/delete/imageProfile/{username}")
    public String deleteImageProfile(@PathVariable String username) throws IOException {
        String del = userService.deleteImageProfile(username);
        return del;
    }

    @PutMapping("/update/imageProfile/{username}")
    public String updateImageProfile(@PathVariable String username, @RequestParam("file") MultipartFile file) throws IOException {
        String up = userService.updateImageProfile(username, file);
        return up;

    }

    @PostMapping("/uploadCoverImg/{username}")
    public ResponseEntity<String> uploadCoverImg(@PathVariable String username, @RequestParam("file") MultipartFile file) throws IOException {
        int i = userService.imageUploadCover(file, username);
        if (i ==1){
            return new ResponseEntity<>("Upload suceess",HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("Upload failed",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getImageCover/{username}")
    public ResponseEntity<byte[]> getImageCover(@PathVariable String username) throws IOException {
        Optional<User> byUsername = userRepo.findByUsername(username);
        if (!byUsername.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = byUsername.get();
        String imgPathCover = user.getImgPathCover();

        String fileExtension = getFileExtension(imgPathCover);
        if(fileExtension == null || fileExtension.isEmpty() ){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        byte[] img;
        try{
            img = userService.getImageCover(username);
        }catch (FileNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (IOException e){
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
        MediaType mediaTypeForFileExtension = getMediaTypeForFileExtension(fileExtension);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaTypeForFileExtension);
        return new ResponseEntity<>(img, headers, HttpStatus.OK);
    }

    @DeleteMapping("/delete/imageCover/{username}")
    public String deleteImageCover(@PathVariable String username) throws IOException {
        String del = userService.deleteImageCover(username);
        return del;
    }

    @PutMapping("/update/imageCover/{username}")
    public String updateImageCover(@PathVariable String username, @RequestParam("file") MultipartFile file) throws IOException {
        String up = userService.updateImageCover(username, file);
        return up;
    }

    @GetMapping("/getAllUsersOrderBy")
    public List<UserDto> getAllUsersOrderBy(){
        return userService.getAllOrderByUsers();
    }
}
