package lk.shan.firstmy.service;

import lk.shan.firstmy.dto.CourseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CourseService {
    CourseDto addCourse(CourseDto courseDto,String userId);
    String deleteCourse(int courseId);
    String updateCourse(CourseDto courseDto);
    List<CourseDto> getAllCourses();
    CourseDto getCourse(int courseId);

    List<CourseDto> getCoursesByName(String courseTitle);
    List<CourseDto> getCourseUserId(String userId);

    int imageUpload(MultipartFile file, int courseId) throws IOException;
    byte[] getImage(int courseId) throws IOException;
    String deleteImage(int courseId);
    String updateImage(int courseId, MultipartFile file) throws IOException;

}
