package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.request.role.ReqAssignPermissionDTO;
import vn.travel.booking.dto.request.role.ReqRoleDTO;
import vn.travel.booking.dto.request.role.ReqRoleUpdateDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.role.ResRoleDTO;
import vn.travel.booking.entity.Permission;
import vn.travel.booking.entity.Role;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.mapper.RoleMapper;
import vn.travel.booking.repository.PermissionRepository;
import vn.travel.booking.repository.RoleRepository;
import vn.travel.booking.util.error.IdInvalidException;
import vn.travel.booking.util.error.NameInvalidException;
import vn.travel.booking.util.error.PermissionNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PaginationMapper paginationMapper;
    private final PermissionRepository permissionRepository;

    public RoleService(
            RoleRepository roleRepository,
            RoleMapper roleMapper,
            PaginationMapper paginationMapper,
            PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.paginationMapper = paginationMapper;
        this.permissionRepository = permissionRepository;
    }

    public Role fetchById(Long id) {
        return this.roleRepository.findById(id)
                .orElseThrow(() ->
                        new IdInvalidException("Role với id = " + id + " không tồn tại")
                );
    }

    @Transactional
    public ResRoleDTO handleCreateRole(ReqRoleDTO reqRoleDTO) {
        if (this.roleRepository.existsByNameIgnoreCase(reqRoleDTO.getName())) {
            throw new NameInvalidException("Role name đã tồn tại");
        }

        Role role = new Role();
        role.setName(reqRoleDTO.getName());
        role.setDescription(reqRoleDTO.getDescription());

        this.roleRepository.save(role);

        return this.roleMapper.convertResRoleDTO(role);

    }

    @Transactional
    public ResRoleDTO handleUpdateRole(ReqRoleUpdateDTO reqRoleUpdateDTO) {
        Role role = fetchById(reqRoleUpdateDTO.getId());

        role.setName(reqRoleUpdateDTO.getName());
        role.setDescription(reqRoleUpdateDTO.getDescription());

        return this.roleMapper.convertResRoleDTO(role);

    }

    @Transactional
    public void handleDeleteRole(Long id) {
        Role role = fetchById(id);
        this.roleRepository.delete(role);
    }

    @Transactional
    public ResRoleDTO handleAssignPermissions(Long roleId, ReqAssignPermissionDTO requestDTO) {
        Role role = fetchById(roleId);

        if ("SUPER_ADMIN".equals(role.getName())) {
            throw new RuntimeException("Không thể thay đổi permission của SUPER_ADMIN");
        }

        List<Permission> permissions = this.permissionRepository.findByIdIn(requestDTO.getPermissionIds());

        if (permissions.size() != requestDTO.getPermissionIds().size()) {
            throw new PermissionNotFoundException("Có permission không tồn tại");
        }

        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);

        return roleMapper.convertResRoleDTO(role);

    }

    public ResultPaginationDTO handleListRole(Specification spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pageRole.getTotalPages();
        long totalElements = pageRole.getTotalElements();

        List<ResRoleDTO> listRole = pageRole.getContent().stream()
                .map(item -> this.roleMapper.convertResRoleDTO(item))
                .collect(Collectors.toList());

        ResultPaginationDTO res = this.paginationMapper.convertToResultPaginationDTO(pageNumber, pageSize, totalPages, totalElements, listRole);

        return res;

    }

    public ResRoleDTO viewRoleById(Long id) {
        Role role = fetchById(id);
        return this.roleMapper.convertResRoleDTO(role);
    }


}
