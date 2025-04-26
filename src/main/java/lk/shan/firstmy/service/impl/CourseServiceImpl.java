package lk.shan.firstmy.service.impl;

import lk.shan.firstmy.dto.CourseDto;
import lk.shan.firstmy.entity.Course;
import lk.shan.firstmy.entity.User;
import lk.shan.firstmy.repo.CourseRepo;
import lk.shan.firstmy.repo.UserRepo;
import lk.shan.firstmy.service.CourseService;
import lk.shan.firstmy.utils.VarList;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.bytebuddy.description.method.MethodDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private UserRepo userRepo;

    @Override
    public CourseDto addCourse(CourseDto courseDto, String userId) {
        User userFound = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Course course = new Course(
                courseDto.getCourseId(),
                courseDto.getCourseTitle(),
                courseDto.getCourseQualification(),
                courseDto.getCourseStartDate(),
                courseDto.getCourseDescription(),
                courseDto.getCourseContent()
        );
        course.setUser(userFound);
        Course save = courseRepo.save(course);
        return new CourseDto(save.getCourseId(),save.getCourseTitle(),save.getCourseContent(),save.getCourseQualification(),save.getCourseStartDate(),save.getCourseDescription(),save.getUser().getId());
    }

    @Override
    public String deleteCourse(int courseId) {
        if (courseRepo.existsById(courseId)) {
            courseRepo.deleteById(courseId);
            return VarList.RSP_SUCCESS;
        }else {
            return VarList.RSP_FAIL;
        }
    }

    @Override
    public String updateCourse(CourseDto courseDto) {
        if (courseRepo.existsById(courseDto.getCourseId())) {
            Course course = courseRepo.findById(courseDto.getCourseId()).orElseThrow(() -> new RuntimeException("Course Not Found"));

            if (course != null) {
                course.setCourseTitle(courseDto.getCourseTitle());
                course.setCourseQualification(courseDto.getCourseQualification());
                course.setCourseStartDate(courseDto.getCourseStartDate());
                course.setCourseDescription(courseDto.getCourseDescription());
                course.setCourseContent(courseDto.getCourseContent());

                User user = userRepo.findById(courseDto.getUserId()).orElseThrow(() -> new RuntimeException("User Not Found"));
                if (user != null) {
                    course.setUser(user);
                }else {
                    return VarList.RSP_ERROR;
                }
                courseRepo.save(course);
                return VarList.RSP_SUCCESS;
            }
        }
        return VarList.RSP_ERROR;
    }

    @Override
    public List<CourseDto> getAllCourses() {
        List<Course> all = courseRepo.findAll();
        return modelMapper.map(all,new TypeToken<List<CourseDto>>(){
        }.getType());

    }

    @Override
    public CourseDto getCourse(int courseId) {
        return null;
    }

    @Override
    public List<CourseDto> getCoursesByName(String courseTitle) {
        return List.of();
    }

    @Override
    public List<CourseDto> getCourseUserId(String userId) {
        List<Course> allByUserIdOrdered = courseRepo.findAllByUserIdOrdered(userId);
        return modelMapper.map(allByUserIdOrdered,new TypeToken<List<CourseDto>>(){}.getType());
    }

    @Override
    public int imageUpload(MultipartFile file, int courseId) throws IOException {
        String fileName = file.getOriginalFilename ();

        Path uploadPath = Paths.get ("upload/", fileName);

        Files.createDirectories (uploadPath.getParent ());

        Files.write (uploadPath, file.getBytes ());

        String fileUrl = "http://localhost:8080/upload/" + fileName;

        Course course = courseRepo.findById (courseId).orElseThrow (() -> new RuntimeException ("Course  not found with ID: " + courseId));

        course.setImgPath (fileUrl);

        Course saveCourseImg = courseRepo.save (course);

        return 1;
    }

    @Override
    public byte[] getImage(int courseId) throws IOException {
        Course course = courseRepo.findById (courseId).orElseThrow (() -> new RuntimeException ("Course not found with id: " + courseId));

        String imgUrl = course.getImgPath ();
        System.out.println ("image url :" + imgUrl);

        String fileName = imgUrl.substring (imgUrl.lastIndexOf ("/") + 1);
        System.out.println ("file name :" + fileName);

        Path imgPath = Paths.get ("upload/", fileName);
        System.out.println ("image path :" + imgPath);

        if (!Files.exists (imgPath)) {
            throw new FileNotFoundException("Image not found for Course id: " + courseId);
        }

        return Files.readAllBytes (imgPath);
    }

    @Override
    public String deleteImage(int courseId) {
        if (courseRepo.existsById (courseId)) {
            Optional<Course> byId = courseRepo.findById (courseId);
            byId.get ().setImgPath (null);
            courseRepo.save (byId.get ());
        }
        return "Image deleted successfully";
    }

    @Override
    public String updateImage(int courseId, MultipartFile file) throws IOException {
        if (courseRepo.existsById (courseId)) {
            Course course = courseRepo.findById (courseId).get ();

            String fileName = file.getOriginalFilename ();

            Path uploadPath = Paths.get ("upload/", fileName);

            Files.createDirectories (uploadPath.getParent ());

            Files.write (uploadPath, file.getBytes ());

            String fileUrl = "http://localhost:8080/upload/" + fileName;

            course.setImgPath (fileUrl);

            Course save = courseRepo.save (course);

            return "Image updated successfully";
        }

        return "Image not updated";
    }
}
