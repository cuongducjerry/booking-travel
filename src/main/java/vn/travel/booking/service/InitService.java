package vn.travel.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.travel.booking.entity.Permission;
import vn.travel.booking.entity.Role;
import vn.travel.booking.entity.User;
import vn.travel.booking.repository.PermissionRepository;
import vn.travel.booking.repository.RoleRepository;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.constant.PermissionConstants;
import vn.travel.booking.util.constant.StatusUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InitService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void init() {
        List<Permission> permissions = initPermissions();
        Role role = initSuperAdminRole(permissions);
        initSuperAdminUser(role);
    }

    // ================= PERMISSION =================
    private List<Permission> initPermissions() {
        List<Permission> result = new ArrayList<>();

        for (String code : PermissionConstants.ALL_PERMISSIONS) {

            Permission permission = permissionRepository
                    .findByCodeIncludeInactive(code)
                    .orElseGet(() -> {
                        Permission p = Permission.builder()
                                .code(code)
                                .build();
                        return permissionRepository.save(p);
                    });

            result.add(permission);
        }

        return result;
    }

    // ================= ROLE =================
    private Role initSuperAdminRole(List<Permission> allPermissions) {

        List<Permission> filteredPermissions = allPermissions.stream()
                .filter(p -> !PermissionConstants.SUPER_ADMIN_EXCLUDED.contains(p.getCode()))
                .collect(Collectors.toList());

        Role role = roleRepository
                .findByNameIncludeInactive("SUPER_ADMIN")
                .orElseGet(() -> {
                    Role r = Role.builder()
                            .name("SUPER_ADMIN")
                            .description("Full quyền hệ thống (đã lọc)")
                            .build();
                    return roleRepository.save(r);
                });

        boolean needUpdate = role.getPermissions() == null ||
                role.getPermissions().size() != filteredPermissions.size() ||
                !role.getPermissions().containsAll(filteredPermissions);

        if (needUpdate) {
            role.setPermissions(new ArrayList<>(filteredPermissions));
            roleRepository.save(role);
        }

        return role;
    }

    // ================= USER =================
    private void initSuperAdminUser(Role role) {

        String email = "superadmin@gmail.com";

        if (userRepository.findByEmailIncludeInactive(email).isEmpty()) {

            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Super Admin")
                    .role(role)
                    .status(StatusUser.APPROVED)
                    .build();

            userRepository.save(user);
        }
    }
}
