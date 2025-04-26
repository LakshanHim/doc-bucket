package lk.shan.firstmy.service;

import lk.shan.firstmy.dto.JobDetailsDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobDetailService {
    void addJob(JobDetailsDto jobDetailsDto, String userId);
    String deleteJob(int jobId);
    JobDetailsDto getJobDetail(int jobId);
    List<JobDetailsDto> getAllJobDetails();

//    int imageUpload(MultipartFile file);
//    byte[] getImage(int jobId);
//    String deleteImage(int jobId);
//    String updateImage(int jobId, MultipartFile file);

    String updateJob(JobDetailsDto jobDetailsDto);
    JobDetailsDto jobsSave(JobDetailsDto jobDetailsDto, String userId);
    List<JobDetailsDto> getAllDetailJob();
    List<JobDetailsDto> jobsFindByUserId(String userId);
}
