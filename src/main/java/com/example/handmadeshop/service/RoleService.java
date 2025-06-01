package com.example.handmadeshop.service;

import com.example.handmadeshop.DTO.RoleDTO;
import com.example.handmadeshop.EJB.model.Role;
import com.example.handmadeshop.repository.RoleRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class RoleService {

    @Inject
    private RoleRepository roleRepository;

    // Creare rol nou
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = new Role();
        role.setName(roleDTO.getName());
        roleRepository.create(role);
        return toDTO(role);
    }

    // Obține rol după ID
    public RoleDTO getRoleById(Integer id) {
        Role role = roleRepository.findById(id);
        return (role != null) ? toDTO(role) : null;
    }

    // Listă cu toate rolurile
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Actualizează un rol existent
    public RoleDTO updateRole(Integer id, RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(id);
        if (existingRole != null) {
            existingRole.setName(roleDTO.getName());
            Role updatedRole = roleRepository.update(existingRole);
            return toDTO(updatedRole);
        }
        return null;
    }

    // Șterge un rol
    public void deleteRole(Integer id) {
        roleRepository.delete(id);
    }

    // Conversie entitate -> DTO
    private RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }

    // Conversie DTO -> entitate (dacă e necesar)
    private Role toEntity(RoleDTO dto) {
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        return role;
    }
}