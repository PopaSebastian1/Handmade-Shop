package com.example.handmadeshop.service;

import com.example.handmadeshop.DTO.ModeMapper;
import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.EJB.model.User;
import com.example.handmadeshop.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class UserService {
    @Inject
    private UserRepository userRepository;

    public UserDTO createUser(UserDTO userDTO) {
        User user = ModeMapper.toUser(userDTO);
        userRepository.create(user);
        return ModeMapper.toUserDTO(user);
    }

    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id);
        return user != null ? ModeMapper.toUserDTO(user) : null;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(ModeMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id);
        if (existingUser != null) {
            existingUser.setName(userDTO.getName());
            existingUser.setSurname(userDTO.getSurname());
            existingUser.setEmail(userDTO.getEmail());
            // Only update password if it's provided (not null)
            if (userDTO.getPassword() != null) {
                existingUser.setPassword(userDTO.getPassword());
            }
            existingUser.setRole(userDTO.getRole());

            User updatedUser = userRepository.update(existingUser);
            return ModeMapper.toUserDTO(updatedUser);
        }
        return null;
    }

    public void deleteUser(Integer id) {
        userRepository.delete(id);
    }

    public void addProductToUser(Integer userId, Integer productId) {
        userRepository.addProductToUser(userId, productId);
    }

    public List<UserDTO> searchUsers(String keyword) {
        return userRepository.findAll().stream()
                .filter(u -> u.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        u.getSurname().toLowerCase().contains(keyword.toLowerCase()) ||
                        u.getEmail().toLowerCase().contains(keyword.toLowerCase()))
                .map(ModeMapper::toUserDTO)
                .collect(Collectors.toList());
    }
}