package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.ResUserDTO;
import vn.travel.booking.dto.response.ResUpdateAvatarUserDTO;
import vn.travel.booking.dto.response.ResUpdateProfileUserDTO;
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

    public ResUpdateAvatarUserDTO convertToResUpdateAvatarUserDTO(String avatarUrl, long userId) {
        ResUpdateAvatarUserDTO res = new ResUpdateAvatarUserDTO();
        res.setUrlImage(avatarUrl);
        res.setUserId(userId);
        return res;
    }

}
