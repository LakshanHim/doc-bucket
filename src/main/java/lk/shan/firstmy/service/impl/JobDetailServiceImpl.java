package lk.shan.firstmy.service.impl;

import lk.shan.firstmy.dto.JobDetailsDto;
import lk.shan.firstmy.entity.JobDetails;
import lk.shan.firstmy.entity.User;
import lk.shan.firstmy.repo.JobDetailsRepo;
import lk.shan.firstmy.repo.UserRepo;
import lk.shan.firstmy.service.JobDetailService;
import lk.shan.firstmy.utils.Converter;
import lk.shan.firstmy.utils.VarList;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JobDetailServiceImpl implements JobDetailService {
    @Autowired
    private Converter converter;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private JobDetailsRepo jobDetailsRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public void addJob(JobDetailsDto jobDetailsDto, String userId) {
        User userid = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        JobDetails jobDetails = new JobDetails();
        jobDetails.setJobTitle(jobDetailsDto.getJobTitle());
        jobDetails.setJobDescription(jobDetailsDto.getJobDescription());
        jobDetails.setQualification(jobDetailsDto.getQualification());
        jobDetails.setJobClosingDate(jobDetailsDto.getJobClosingDate());
        jobDetails.setUser(userid);

        jobDetailsRepo.save(jobDetails);


    }

    @Override
    public String deleteJob(int jobId) {
        if (jobDetailsRepo.existsById(jobId)) {
            jobDetailsRepo.deleteById(jobId);
            return VarList.RSP_SUCCESS;
        }else {
            return VarList.RSP_ERROR;
        }
    }

    @Override
    public JobDetailsDto getJobDetail(int jobId) {
        if (jobDetailsRepo.existsById(jobId)) {
            JobDetails jobDetails = jobDetailsRepo.findById(jobId).orElse(null);
            return modelMapper.map(jobDetails, JobDetailsDto.class);
        }else {
            return null;
        }
    }

    @Override
    public List<JobDetailsDto> getAllJobDetails() {
        List<JobDetails> all = jobDetailsRepo.findAll();
        return modelMapper.map(all,new TypeToken<List<JobDetailsDto>>(){
        }.getType());
        
    }

//    @Override
//    public int imageUpload(MultipartFile file) {
//        return 0;
//    }
//
//    @Override
//    public byte[] getImage(int jobId) {
//        return new byte[0];
//    }
//
//    @Override
//    public String deleteImage(int jobId) {
//        return "";
//    }
//
//    @Override
//    public String updateImage(int jobId, MultipartFile file) {
//        return "";
//    }

    @Override
    public String updateJob(JobDetailsDto jobDetailsDto) {
        if (jobDetailsRepo.existsById(jobDetailsDto.getJobId())){
            JobDetails jobDetails = jobDetailsRepo.findById(jobDetailsDto.getJobId()).orElseThrow(() -> new RuntimeException("Job not found"));

            if(jobDetails != null){
                jobDetails.setJobTitle(jobDetailsDto.getJobTitle());
                jobDetails.setJobDescription(jobDetailsDto.getJobDescription());
                jobDetails.setQualification(jobDetailsDto.getQualification());
                jobDetails.setJobClosingDate(jobDetailsDto.getJobClosingDate());

                jobDetailsRepo.save(jobDetails);
                return VarList.RSP_SUCCESS;

            }

        }
        return VarList.RSP_DUPLICATED;
    }

    @Override
    public JobDetailsDto jobsSave(JobDetailsDto jobDetailsDto, String userId) {
        User userFound = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobDetails jobDetails = new JobDetails(
                jobDetailsDto.getJobId(),
                jobDetailsDto.getJobTitle(),
                jobDetailsDto.getJobDescription(),
                jobDetailsDto.getQualification(),
                jobDetailsDto.getJobClosingDate()
        );
        jobDetails.setUser(userFound);
        JobDetails save = jobDetailsRepo.save(jobDetails);
        return new JobDetailsDto(
                save.getJobId(),
                save.getJobTitle(),
                save.getJobDescription(),
                save.getQualification(),
                save.getJobClosingDate()
                );
    }

    @Override
    public List<JobDetailsDto> getAllDetailJob() {
        List<JobDetails> allOrderedByDateUpload = jobDetailsRepo.findAllOrderedByDateUpload();
        List<JobDetailsDto> allDetailJobsDto = new ArrayList<>();

        for (JobDetails jobDetails : allOrderedByDateUpload) {
            String userId = jobDetails.getUser() != null ? jobDetails.getUser().getId() : "";

            JobDetailsDto jobDetailsDto = new JobDetailsDto(
                    jobDetails.getJobId(),
                    jobDetails.getJobTitle(),
                    jobDetails.getJobDescription(),
                    jobDetails.getQualification(),
                    jobDetails.getJobClosingDate(),
                    jobDetails.getJobOpenDate(),
                    userId
            );

            allDetailJobsDto.add(jobDetailsDto);

        }
        return allDetailJobsDto;

    }

    @Override
    public List<JobDetailsDto> jobsFindByUserId(String userId) {
        Optional<User> byId = userRepo.findById(userId);

        if(byId.isEmpty()){
            return Collections.emptyList();
        }
        List<JobDetails> allByUserIdOrdered = jobDetailsRepo.findAllByUserIdOrdered(userId);
        List<JobDetailsDto> allDetailJobsDto = new ArrayList<>();

        for (JobDetails jobDetails : allByUserIdOrdered) {
            if (jobDetails.getUser() != null && jobDetails.getUser().getId().equals(userId)) {
                JobDetailsDto jobDetailsDto = new JobDetailsDto(
                        jobDetails.getJobId(),
                        jobDetails.getJobTitle(),
                        jobDetails.getJobDescription(),
                        jobDetails.getQualification(),
                        jobDetails.getJobClosingDate(),
                        jobDetails.getUser().getUsername()
                );
                allDetailJobsDto.add(jobDetailsDto);
            }
        }
        return allDetailJobsDto;

    }
}
