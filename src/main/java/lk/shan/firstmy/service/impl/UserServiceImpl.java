package lk.shan.firstmy.service.impl;

import lk.shan.firstmy.dto.LoginDto;
import lk.shan.firstmy.dto.UserDto;
import lk.shan.firstmy.entity.User;
import lk.shan.firstmy.repo.UserRepo;
import lk.shan.firstmy.service.UserService;
import lk.shan.firstmy.service.auth.JwtService;
import lk.shan.firstmy.utils.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private Converter converter;
    @Autowired
    @Lazy
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JwtService jwtService;

    @Override
    public void register(UserDto userDto) {
        if(userRepo.existsByUserid(userDto.getUserID()))
            throw new UsernameNotFoundException(userDto.getUserID()+"Username is already taken...!");
        userRepo.save(converter.userDtoToEntity(userDto));
    }

    @Override
    public String login(LoginDto loginDto) {
        AuthenticationManager authenticationManager = new ProviderManager(authenticationProvider);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.userid(), loginDto.password()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(loadUserByUsername(loginDto.userid()));
        }
        throw new RuntimeException("Bad Request");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByUserid(username);
        if (user.isPresent()) {
            User use = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(use.getUserid())
                    .password(use.getPassword())
                    .roles(String.valueOf(use.getRole()))
                    .build();
        }
        else
            throw new UsernameNotFoundException(username);
    }
}
