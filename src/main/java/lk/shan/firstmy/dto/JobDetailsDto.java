package lk.shan.firstmy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class JobDetailsDto {
    private int jobId;
    private String jobTitle;
    private String jobDescription;
    private String qualification;
    private String jobClosingDate;
    private String imgPath;
    private String userId;

    public JobDetailsDto(int jobId, String jobTitle, String jobDescription, String qualification, String jobClosingDate, String jobOpenDate, String userId) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.qualification = qualification;
        this.jobClosingDate = jobClosingDate;
        this.userId = userId;

    }

    public JobDetailsDto(int jobId, String jobTitle, String jobDescription, String qualification, String jobClosingDate, String username) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.qualification = qualification;
        this.jobClosingDate = jobClosingDate;
        this.userId = username;

    }

    public JobDetailsDto(int jobId, String jobTitle, String jobDescription, String qualification, String jobClosingDate) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.qualification = qualification;
        this.jobClosingDate = jobClosingDate;
    }
}
