package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.user.*;
import vn.travel.booking.entity.Role;
import vn.travel.booking.entity.User;

@Component
public class UserMapper {

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());

        ResUserDTO.Role role = new ResUserDTO.Role();
        role.setId(user.getRole().getId());
        role.setName(user.getRole().getName());
        res.setRole(role);

        return res;
    }

    public ResUpdateProfileUserDTO convertToResUpdateProfileUserDTO(User user) {
        ResUpdateProfileUserDTO res = new ResUpdateProfileUserDTO();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setBio(user.getBio());
        res.setUpdatedAt(user.getUpdatedAt());

        return res;
    }

    public ResUpdateAvatarUserDTO convertToResUpdateAvatarUserDTO(User currentUser) {
        ResUpdateAvatarUserDTO res = new ResUpdateAvatarUserDTO();
        res.setUrlImage(currentUser.getAvatarUrl());
        res.setUserId(currentUser.getId());
        res.setUpdatedAt(currentUser.getUpdatedAt());
        return res;
    }

    public ResAssignRoleDTO convertToResAssignRoleDTO(User user, Role role) {
        ResAssignRoleDTO res = new ResAssignRoleDTO();
        res.setIdUser(user.getId());
        res.setRoleName(role.getName());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    public ResUpdateUserStatusDTO convertToResUpdateUserStatusDTO(User user) {
        ResUpdateUserStatusDTO res = new ResUpdateUserStatusDTO();
        res.setUserId(user.getId());
        res.setStatus(user.getStatus());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

}
