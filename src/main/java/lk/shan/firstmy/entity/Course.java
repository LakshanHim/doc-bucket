package lk.shan.firstmy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int courseId;
    private String courseTitle;
    private String courseDescription;
    private String courseQualification;
    private String courseContent;
    private String courseStartDate;
    private String imgPath;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    public Course(int courseId, String courseTitle, String courseQualification, String courseStartDate, String courseDescription, String courseContent) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseQualification = courseQualification;
        this.courseContent = courseContent;
        this.courseStartDate = courseStartDate;
        this.courseDescription = courseDescription;
    }
}
