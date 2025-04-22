package com.example.handmadeshop.service;

import com.example.handmadeshop.DTO.ModeMapper;
import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.EJB.model.*;
import com.example.handmadeshop.repository.RoleRepository;
import com.example.handmadeshop.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class UserService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager em;

    public UserDTO createUser(UserDTO userDTO) {
        User user = ModeMapper.toUser(userDTO);
        userRepository.create(user);

        // Adăugăm rolurile
        if (userDTO.getRoles() != null) {
            for (String roleName : userDTO.getRoles()) {
                Role role = roleRepository.findByName(roleName);
                if (role != null) {
                    userRepository.addRoleToUser(user.getId(), role.getId());
                }
            }
        }

        User freshUser = userRepository.findById(user.getId());
        return toDTO(freshUser);
    }


    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());
        dto.setClientId(user.getClientid());

        // Map roles
        if (user.getUserRoles() != null) {
            dto.setRoles(user.getUserRoles().stream()
                    .map(ur -> ur.getRoleid().getName())
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public UserDTO getUserById(Integer id) {
        User user = userRepository.findByIdWithRoles(id);
        return user != null ? toDTO(user) : null;
    }

    public UserDTO findByEmail(String email) {
        KmsEncryptionService kmsEncryptionService = new KmsEncryptionService();
        List<User> allUsers = userRepository.findAll();
        User matchedUser = null;

        for (User user : allUsers) {
            String decryptedEmail = kmsEncryptionService.decrypt(user.getEmail());
            if (decryptedEmail.equals(email)) {
                matchedUser = user;
                break;
            }
        }
        if (matchedUser != null) {
            return ModeMapper.toUserDTO(matchedUser);
        } else {
            return null;
        }
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id);
        if (existingUser != null) {
            existingUser.setName(userDTO.getName());
            existingUser.setSurname(userDTO.getSurname());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setClientid(userDTO.getClientId());
            existingUser.setClientsecret(userDTO.getClientSecret());

            if (userDTO.getPassword() != null) {
                existingUser.setPassword(userDTO.getPassword());
            }

            userRepository.removeAllRoles(id); // ștergem rolurile vechi
            if (userDTO.getRoles() != null) {
                for (String roleName : userDTO.getRoles()) {
                    Role role = roleRepository.findByName(roleName);
                    if (role != null) {
                        userRepository.addRoleToUser(id, role.getId()); // adăugăm noile roluri
                    }
                }
            }

            em.flush();
            em.clear();

            User updatedUser = userRepository.update(existingUser);
            return toDTO(updatedUser);
        }
        return null;
    }


    public void deleteUser(Integer id) {
        userRepository.delete(id);
    }

    public UserDTO updateUserRoles(Integer userId, List<String> roleNames) {
        // Start by getting user without roles to avoid caching issues
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Clear existing roles
        userRepository.removeAllRoles(userId);

        // Add new roles
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role != null) {
                userRepository.addRoleToUser(userId, role.getId());
            }
        }

        em.flush();
        em.clear();

        // Get fresh data with roles
        User updatedUser = userRepository.findByIdWithRoles(userId);
        return toDTO(updatedUser);
    }
}