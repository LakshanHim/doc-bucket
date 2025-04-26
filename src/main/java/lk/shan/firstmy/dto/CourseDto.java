package lk.shan.firstmy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CourseDto {
    private int courseId;
    private String courseTitle;
    private String courseDescription;
    private String courseQualification;
    private String courseContent;
    private String courseStartDate;
    private String imgPath;
    private String userId;

    public CourseDto(int courseId, String courseTitle, String courseContent, String courseQualification, String courseStartDate, String courseDescription) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseContent = courseContent;
        this.courseQualification = courseQualification;
        this.courseStartDate = courseStartDate;
        this.courseDescription = courseDescription;
    }

    public CourseDto(int courseId, String courseTitle, String courseContent, String courseQualification, String courseStartDate, String courseDescription, String id) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseContent = courseContent;
        this.courseQualification = courseQualification;
        this.courseStartDate = courseStartDate;
        this.courseDescription = courseDescription;
        this.userId = id;
    }
}
