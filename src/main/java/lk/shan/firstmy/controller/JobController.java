package lk.shan.firstmy.controller;

import lk.shan.firstmy.dto.JobDetailsDto;
import lk.shan.firstmy.dto.ResponseDto;
import lk.shan.firstmy.repo.JobDetailsRepo;
import lk.shan.firstmy.service.JobDetailService;
import lk.shan.firstmy.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/v1/job_details")
public class JobController {
    @Autowired
    ResponseDto responseDto;
    @Autowired
    JobDetailService jobDetailService;
    @Autowired
    JobDetailsRepo jobDetailsRepo;

    @PostMapping("/addJob/{userId}")
    public ResponseEntity<String> addJob(@RequestBody JobDetailsDto jobDetailsDto, @PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isEmployee = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_EMPLOYEES"));

        if (!isEmployee) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only EMPLOYEES can add jobs.");
        }
        jobDetailService.addJob(jobDetailsDto, userId);
        return ResponseEntity.ok("Job added successfully.");
    }

    @PutMapping("/updateJob")
    public ResponseEntity<ResponseDto> updateJob(@RequestBody JobDetailsDto jobDetailsDto) {
        try {
            String res = jobDetailService.updateJob(jobDetailsDto);

            if (res.equals ("00")) {
                responseDto.setMessage ("Success to update job..");
                responseDto.setCode (VarList.RSP_SUCCESS);
                responseDto.setContent (jobDetailsDto);
                return new ResponseEntity<> (responseDto, HttpStatus.ACCEPTED);
            } else if (res.equals ("01")) {
                responseDto.setMessage ("DUPLICATED job Saved ...");
                responseDto.setCode (VarList.RSP_DUPLICATED);
                responseDto.setContent (jobDetailsDto);
                return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
            } else {
                responseDto.setMessage ("Error  job not update ");
                responseDto.setCode (VarList.RSP_ERROR);
                responseDto.setContent (null);
                return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseDto.setMessage("Error job not updated.");
            responseDto.setCode(VarList.RSP_ERROR);
            responseDto.setMessage(e.getMessage());
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteJob/{jobId}")
    public ResponseEntity<ResponseDto> deleteJob(@PathVariable int jobId) {
        try{
            String s = jobDetailService.deleteJob(jobId);
            if (s.equals ("00")) {
                responseDto.setMessage ("Success to delete job..");
                responseDto.setCode (VarList.RSP_SUCCESS);
                responseDto.setContent (jobId);
                return new ResponseEntity<> (responseDto, HttpStatus.ACCEPTED);
            }else {
                responseDto.setMessage ("Error  job not delete ");
                responseDto.setCode (VarList.RSP_ERROR);
                responseDto.setContent (null);
                return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            responseDto.setMessage ("Error  not delete  job ");
            responseDto.setCode (VarList.RSP_ERROR);
            responseDto.setContent (null);
            return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/searchJob/{jobId}")
    public ResponseEntity<ResponseDto> searchJob(@PathVariable int jobId) {
        try{
            JobDetailsDto jobDetail = jobDetailService.getJobDetail(jobId);
            if (jobDetail!=null) {
                responseDto.setMessage ("Success to search job..");
                responseDto.setCode (VarList.RSP_SUCCESS);
                responseDto.setContent (jobDetail);
                return new ResponseEntity<> (responseDto, HttpStatus.OK);
            }else {
                responseDto.setMessage ("Error  user not job ");
                responseDto.setCode (VarList.RSP_ERROR);
                responseDto.setContent (null);
                return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e) {
            responseDto.setMessage ("Error  user not job ");
            responseDto.setCode (VarList.RSP_ERROR);
            responseDto.setContent (null);
            return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);

        }
    }

    @GetMapping("/getAllJobs")
    public ResponseEntity<ResponseDto> getAllJobs() {
        try{
            List<JobDetailsDto> allJobDetails = jobDetailService.getAllJobDetails();
            responseDto.setMessage ("Success to get all jobs...");
            responseDto.setCode (VarList.RSP_SUCCESS);
            responseDto.setContent (allJobDetails);
            return new ResponseEntity<> (responseDto, HttpStatus.OK);
        }catch (Exception e) {
            responseDto.setMessage ("Error  getAllJobs  ");
            responseDto.setCode (VarList.RSP_ERROR);
            responseDto.setContent (null);
            return new ResponseEntity<> (responseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllDetailJob")
    public ResponseEntity<List<JobDetailsDto>> getAllDetailJob() {
        List<JobDetailsDto> allDetailJob = jobDetailService.getAllDetailJob();

        if(allDetailJob.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allDetailJob);

    }

    @GetMapping("/getJobsUserId/{userId}")
    public ResponseEntity<List<JobDetailsDto>> getJobsUserId(@PathVariable String userId) {
        List<JobDetailsDto> jobDetailsDtos = jobDetailService.jobsFindByUserId(userId);
        if(jobDetailsDtos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(jobDetailsDtos);
    }

    @PostMapping("/saveJobs/{userId}")
    public ResponseEntity<JobDetailsDto> saveJob(@PathVariable String userId, @RequestBody JobDetailsDto jobDetailsDto) {
        JobDetailsDto jobDetailsDto1 = jobDetailService.jobsSave(jobDetailsDto, userId);
        return new ResponseEntity<>(jobDetailsDto1,HttpStatus.CREATED);
    }

}
