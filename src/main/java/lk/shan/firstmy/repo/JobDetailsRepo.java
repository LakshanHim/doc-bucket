package lk.shan.firstmy.repo;

import lk.shan.firstmy.entity.JobDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface JobDetailsRepo extends JpaRepository<JobDetails, Integer> {
    @Query("SELECT j FROM JobDetails j ORDER BY j.jobOpenDate DESC")
    List<JobDetails> findAllOrderedByDateUpload();

    @Query("SELECT j FROM JobDetails j WHERE j.user.id = :userId ORDER BY j.jobOpenDate DESC")
    List<JobDetails> findAllByUserIdOrdered(@Param("userId") String userId);
}
