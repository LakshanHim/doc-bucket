package lk.shan.firstmy.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lk.shan.firstmy.utils.UserRoles;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false,unique = true)
    private String email;
    private String firstname;
    private String lastname;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoles role;
    private String imgPathProfile;
    private String imgPathCover;
    private String registerDate;

//    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE ,orphanRemoval = true)
    private List<JobDetails> jobs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Course> courses;

    @PrePersist
    public void prePersist(){
        this.registerDate = LocalDate.now().toString();
    }
}
