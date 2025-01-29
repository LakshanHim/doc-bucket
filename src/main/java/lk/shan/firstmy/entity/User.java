package lk.shan.firstmy.entity;

import jakarta.persistence.*;
import lk.shan.firstmy.utils.UserRoles;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String id;
    @Column(nullable = false,unique = true)
    private String userid;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoles role;
}
