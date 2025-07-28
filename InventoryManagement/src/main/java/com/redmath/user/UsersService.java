package com.redmath.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@SuppressWarnings("EI_EXPOSE_REP2")
public class UsersService implements UserDetailsService {
    final UsersRepository usersRepository;


    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }
//    private final PasswordEncoder passwordEncoder;

//    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
//        this.usersRepository = usersRepository;
//        this.passwordEncoder = passwordEncoder;
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        String authority = "ROLE_" + user.getRoles().toUpperCase();

        return new User(
                user.getUsername(),
                user.getPassword(),
                AuthorityUtils.createAuthorityList(authority)
        );
    }

//    public Users createUser(String username, String rawPassword, String role) {
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//
//        Users user = new Users();
//        user.setUsername(username);
//        user.setPassword(encodedPassword); // Store encoded password
//        user.setRoles(role);
//        user.setCreatedAt(LocalDateTime.now());
//
//        return usersRepository.save(user);
//    }
    public List<Users> findAll() {
        return usersRepository.findAll();
    }
    public Optional<Users> findById(Long id) {
        return usersRepository.findById(id);
    }

    public Users create(Users user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Users newUser = new Users();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setCreatedAt(LocalDateTime.now());
        return usersRepository.save(newUser);
    }
    public Users update(Long id, Users updatedUser) {
        return usersRepository.findById(id).map(user -> {
            if (updatedUser.getUsername() != null) user.setUsername(updatedUser.getUsername());
            if (updatedUser.getRoles() != null) user.setRoles(updatedUser.getRoles());
//            if (updatedUser.getPassword() != null) {
//                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
//            }
            if (updatedUser.getPassword() != null) {
                user.setPassword(updatedUser.getPassword());
            }
            return usersRepository.save(user);
        }).orElse(null);
    }
    public boolean delete(Long id) {
        if (usersRepository.existsById(id)) {
            usersRepository.deleteById(id);
            return true;
        }
        return false;
    }



}
