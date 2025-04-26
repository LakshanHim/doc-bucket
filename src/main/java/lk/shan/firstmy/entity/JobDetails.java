package lk.shan.firstmy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class JobDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private int jobId;
    private String jobTitle;
    private String jobDescription;
    private String qualification;
    private String jobClosingDate;
    private String imgPath;
    private String jobOpenDate;

//    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    public JobDetails(int jobId, String jobTitle, String jobDescription, String qualification, String jobClosingDate) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.qualification = qualification;
        this.jobClosingDate = jobClosingDate;
    }

    @PrePersist
    public void prePersist(){
        this.jobOpenDate = LocalDate.now().toString();
    }

}
