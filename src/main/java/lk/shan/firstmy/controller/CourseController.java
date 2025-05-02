package lk.shan.firstmy.controller;

import lk.shan.firstmy.dto.CourseDto;
import lk.shan.firstmy.dto.ResponseDto;
import lk.shan.firstmy.entity.Course;
import lk.shan.firstmy.repo.CourseRepo;
import lk.shan.firstmy.service.CourseService;
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

@CrossOrigin
@RestController
@RequestMapping("/v1/course")
public class CourseController {
    @Autowired
    CourseService courseService;
    @Autowired
    CourseRepo courseRepo;
    @Autowired
    ResponseDto responseDto;

    @PostMapping("/addCourse/{useId}")
    public ResponseEntity<CourseDto> courseAdd(@PathVariable String useId, @RequestBody CourseDto courseDto) {
        CourseDto courseDto1 = courseService.addCourse(courseDto, useId);
        return new ResponseEntity<>(courseDto1, HttpStatus.CREATED);
    }

    @PutMapping("/updateCourse")
    public ResponseEntity<ResponseDto> courseUpdate(@RequestBody CourseDto courseDto) {
        try{
            String res = courseService.updateCourse(courseDto);
            if (res.equals ("00")) {
                responseDto.setMessage ("Success to update course..");
                responseDto.setCode (VarList.RSP_SUCCESS);
                responseDto.setContent (courseDto);
                return new ResponseEntity<> (responseDto, HttpStatus.ACCEPTED);
            } else if (res.equals ("01")) {
                responseDto.setMessage ("DUPLICATED job course ...");
                responseDto.setCode (VarList.RSP_DUPLICATED);
                responseDto.setContent (courseDto);
                return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
            } else {
                responseDto.setMessage ("Error  course not update ");
                responseDto.setCode (VarList.RSP_ERROR);
                responseDto.setContent (null);
                return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseDto.setMessage ("Error  course not update ");
            responseDto.setCode (VarList.RSP_ERROR);
            responseDto.setContent (null);
            return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteCourse/{courseId}")
    public ResponseEntity<ResponseDto> courseDelete(@PathVariable int courseId) {
        try {
            String res = courseService.deleteCourse(courseId);
            if (res.equals ("00")) {
                responseDto.setMessage ("Success to delete course..");
                responseDto.setCode (VarList.RSP_SUCCESS);
                responseDto.setContent (courseId);
                return new ResponseEntity<> (responseDto, HttpStatus.ACCEPTED);
            }else {
                responseDto.setMessage ("Error  course not delete ");
                responseDto.setCode (VarList.RSP_ERROR);
                responseDto.setContent (null);
                return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e) {
            responseDto.setMessage ("Error  course not delete ");
            responseDto.setCode (VarList.RSP_ERROR);
            responseDto.setContent (null);
            return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllACourse")
    public ResponseEntity<ResponseDto> getAllCourse(){
        try{
            List<CourseDto> allCourses = courseService.getAllCourses();
            responseDto.setCode (VarList.RSP_SUCCESS);
            responseDto.setMessage("Success to get all course..");
            responseDto.setContent (allCourses);
            return new ResponseEntity<> (responseDto, HttpStatus.ACCEPTED);
        }catch (Exception e) {
            responseDto.setMessage ("Error  course not get ");
            responseDto.setCode (VarList.RSP_ERROR);
            responseDto.setContent (null);
            return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getCourseUserId/{userId}")
    public ResponseEntity<ResponseDto> getCourseUserId(@PathVariable String userId){
        try {
            List<CourseDto> courseUserId = courseService.getCourseUserId(userId);
            responseDto.setCode (VarList.RSP_SUCCESS);
            responseDto.setMessage("Success to get course userId..");
            responseDto.setContent (courseUserId);
            return new ResponseEntity<> (responseDto, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            responseDto.setMessage ("Error  course not get ");
            responseDto.setCode (VarList.RSP_ERROR);
            responseDto.setContent (null);
            return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/uploadImg/{courseId}")
    public ResponseEntity<String> uploadImg(@PathVariable int courseId, @RequestParam("file") MultipartFile file) throws IOException {
        int i = courseService.imageUpload (file, courseId);

        if (i == 1) {
            return new ResponseEntity<String> ("Upload Success !", HttpStatus.CREATED);
        }
        return new ResponseEntity<> ("Upload Failed", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("getImg/{courseId}")
    public ResponseEntity<byte[]> getImg(@PathVariable int courseId){
        Optional<Course> courseOpt = courseRepo.findById (courseId);
        if (!courseOpt.isPresent ()) {
            return new ResponseEntity<> (HttpStatus.NOT_FOUND);
        }

        Course course = courseOpt.get ();
        String imgUrl = course.getImgPath ();
        String fileExtension = getFileExtension (imgUrl);
        if (fileExtension == null || fileExtension.isEmpty ()) {
            return new ResponseEntity<> (HttpStatus.BAD_REQUEST);
        }

        byte[] image;
        try {
            image = courseService.getImage (courseId);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<> (HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<> (HttpStatus.INTERNAL_SERVER_ERROR);
        }

        MediaType mediaType = getMediaTypeForFileExtension (fileExtension);

        HttpHeaders headers = new HttpHeaders ();
        headers.setContentType (mediaType);

        return new ResponseEntity<> (image, headers, HttpStatus.OK);
    }

    private String getFileExtension(String url) {
        if (url == null || !url.contains (".")) {
            return null;
        }
        return url.substring (url.lastIndexOf (".") + 1);
    }

    private MediaType getMediaTypeForFileExtension(String extension) {
        switch (extension.toLowerCase ()) {
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

    @DeleteMapping("/deleteImg/{courseId}")
    public String imageDelete(@PathVariable int courseId) {
        String s = courseService.deleteImage (courseId);
        return s;
    }

    @PutMapping("/updateImg/{courseId}")
    public String updateImage(@PathVariable int courseId, @RequestParam("file") MultipartFile file) throws IOException {
        String s = courseService.updateImage (courseId, file);
        return s;
    }

    @GetMapping("/getById/{courseId}")
    public ResponseEntity<ResponseDto> getById(@PathVariable int courseId) {
        CourseDto course = courseService.getCourse(courseId);
        responseDto.setCode (VarList.RSP_SUCCESS);
        responseDto.setMessage("Success to get course ..");
        responseDto.setContent (course);
        return new ResponseEntity<> (responseDto, HttpStatus.ACCEPTED);
    }



}



