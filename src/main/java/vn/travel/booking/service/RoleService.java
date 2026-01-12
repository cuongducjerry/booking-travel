package vn.travel.booking.service;

import org.springframework.stereotype.Service;
import vn.travel.booking.domain.Role;
import vn.travel.booking.repository.RoleRepository;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role fetchById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findByIdAndActiveTrue(id);
        if(roleOptional.isPresent()){
            return roleOptional.get();
        }
        return null;
    }

}
