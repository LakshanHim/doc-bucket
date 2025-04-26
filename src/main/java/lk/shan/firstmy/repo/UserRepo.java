package lk.shan.firstmy.repo;

import lk.shan.firstmy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u ORDER BY u.registerDate DESC")
    List<User> findAllDetails();

    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}
