package vn.travel.booking.service;

import org.springframework.stereotype.Service;
import vn.travel.booking.domain.Role;
import vn.travel.booking.domain.User;
import vn.travel.booking.domain.response.ResCreateUserDTO;
import vn.travel.booking.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserService(
            UserRepository userRepository,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {

        // check role
        if(user.getRole() != null) {
            Role r = this.roleService.fetchById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }

        return userRepository.save(user);
    }

    public User handleGetUserByUsername(String username){
        return this.userRepository.findByEmailAndActiveTrue(username);
    }

    public boolean isEmailExist(String email){
        return this.userRepository.existsByEmailAndActiveTrue(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());

        return res;
    }

}
