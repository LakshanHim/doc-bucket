package lk.shan.firstmy.repo;

import lk.shan.firstmy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {
    boolean existsByUserid(String userid);
    Optional<User> findByUserid(String userid);
}
