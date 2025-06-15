package com.example.handmadeshop.services;

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

    private final KmsEncryptionService kmsEncryptionService;

    @PersistenceContext
    private EntityManager em;

    public UserService() {
        this.kmsEncryptionService = new KmsEncryptionService();
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(kmsEncryptionService.encrypt(userDTO.getName()));
        user.setSurname(kmsEncryptionService.encrypt(userDTO.getSurname()));
        user.setEmail(kmsEncryptionService.encrypt(userDTO.getEmail()));
        user.setPassword(kmsEncryptionService.encrypt(userDTO.getPassword()));

        if (userDTO.getClientId() != null) {
            user.setClientid(kmsEncryptionService.encrypt(userDTO.getClientId()));
        }
        if (userDTO.getClientSecret() != null) {
            user.setClientsecret(kmsEncryptionService.encrypt(userDTO.getClientSecret()));
        }

        userRepository.create(user);

        if (userDTO.getRoles() != null) {
            for (String roleName : userDTO.getRoles()) {
                Role role = roleRepository.findByName(roleName);
                if (role != null) {
                    userRepository.addRoleToUser(user.getId(), role.getId());
                }
            }
        }

        User freshUser = userRepository.findByIdWithRoles(user.getId());
        return toDTO(freshUser);
    }

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());

        dto.setName(kmsEncryptionService.decrypt(user.getName()));
        dto.setSurname(kmsEncryptionService.decrypt(user.getSurname()));
        dto.setEmail(kmsEncryptionService.decrypt(user.getEmail()));
        dto.setPassword(kmsEncryptionService.decrypt(user.getPassword()));

        if (user.getClientid() != null) {
            dto.setClientId(kmsEncryptionService.decrypt(user.getClientid()));
        }
        if (user.getClientsecret() != null) {
            dto.setClientSecret(kmsEncryptionService.decrypt(user.getClientsecret()));
        }

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
            return toDTO(matchedUser);
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
        try {
            if (existingUser != null) {

                String currentDecryptedEmail = kmsEncryptionService.decrypt(existingUser.getEmail());
                String newEmail = userDTO.getEmail();

                if (!currentDecryptedEmail.equals(newEmail)) {
                    List<User> allUsers = userRepository.findAll();
                    for (User user : allUsers) {
                        if (user.getId().equals(id)) continue;

                        String decryptedEmail = kmsEncryptionService.decrypt(user.getEmail());
                        if (decryptedEmail.equals(newEmail)) {
                            throw new RuntimeException("Email is already in use by another user");
                        }
                    }
                }


                existingUser.setName(kmsEncryptionService.encrypt(userDTO.getName()));
                existingUser.setSurname(kmsEncryptionService.encrypt(userDTO.getSurname()));
                existingUser.setEmail(kmsEncryptionService.encrypt(userDTO.getEmail()));

                if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                    existingUser.setPassword(kmsEncryptionService.encrypt(userDTO.getPassword()));
                }

                if (userDTO.getClientId() != null && !userDTO.getClientId().isEmpty()) {
                    existingUser.setClientid(kmsEncryptionService.encrypt(userDTO.getClientId()));
                }


                if (userDTO.getClientSecret() != null || !userDTO.getClientId().isEmpty()) {
                    existingUser.setClientsecret(kmsEncryptionService.encrypt(userDTO.getClientSecret()));
                }

                userRepository.removeAllRoles(id);
                if (userDTO.getRoles() != null) {
                    for (String roleName : userDTO.getRoles()) {
                        Role role = roleRepository.findByName(roleName);
                        if (role != null) {
                            userRepository.addRoleToUser(id, role.getId());
                        }
                    }
                }

                em.flush();
                em.clear();

                User updatedUser = userRepository.update(existingUser);
                return toDTO(updatedUser);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
        return null;
    }

    public void deleteUser(Integer id) {
        userRepository.delete(id);
    }

    public UserDTO updateUserRoles(Integer userId, List<String> roleNames) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        userRepository.removeAllRoles(userId);

        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role != null) {
                userRepository.addRoleToUser(userId, role.getId());
            }
        }

        em.flush();
        em.clear();

        User updatedUser = userRepository.findByIdWithRoles(userId);
        return toDTO(updatedUser);
    }

    public void clearUserCart(Integer userId) {
        userRepository.clearUserProducts(userId);
    }
}