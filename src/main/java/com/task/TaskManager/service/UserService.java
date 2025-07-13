package com.task.TaskManager.service;

import com.task.TaskManager.model.Role;
import com.task.TaskManager.model.Role.ERole;
import com.task.TaskManager.model.User;
import com.task.TaskManager.repository.RoleRepository;
import com.task.TaskManager.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String email, String password, Set<String> strRoles) {
        if (userRepository.existsByUserName(username)) {
            throw new IllegalArgumentException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role defaultRole = roleRepository.findByName(ERole.ROLE_TEAM_MEMBER)
                    .orElseThrow(() -> new RuntimeException("Error: Role 'TEAM_MEMBER' not found."));
            roles.add(defaultRole);
        } else {
            for (String role : strRoles) {
                switch (role.toLowerCase()) {
                    case "manager" -> {
                        Role managerRole = roleRepository.findByName(ERole.ROLE_PROJECT_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role 'MANAGER' not found."));
                        roles.add(managerRole);
                    }
                    case "member" -> {
                        Role memberRole = roleRepository.findByName(ERole.ROLE_TEAM_MEMBER)
                                .orElseThrow(() -> new RuntimeException("Error: Role 'MEMBER' not found."));
                        roles.add(memberRole);
                    }
                    default -> throw new RuntimeException("Error: Role '" + role + "' is not supported.");
                }
            }
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUserRoles(Long userId, Set<String> newRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Set<Role> roles = new HashSet<>();

        for (String roleName : newRoles) {
            ERole eRole = switch (roleName.toLowerCase()) {
                case "manager" -> ERole.ROLE_PROJECT_MANAGER;
                case "member" -> ERole.ROLE_TEAM_MEMBER;
                default -> throw new IllegalArgumentException("Role '" + roleName + "' is not supported.");
            };

            Role role = roleRepository.findByName(eRole)
                    .orElseThrow(() -> new RuntimeException("Error: Role '" + roleName + "' not found."));
            roles.add(role);
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            return Optional.empty();
        }

        return userRepository.findByUserName(username);
    }
}
