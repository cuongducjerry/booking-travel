package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.travel.booking.dto.response.permission.ResPermissionDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.entity.Permission;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.mapper.PermissionMapper;
import vn.travel.booking.mapper.UserMapper;
import vn.travel.booking.repository.PermissionRepository;
import vn.travel.booking.util.error.IdInvalidException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final PaginationMapper paginationMapper;

    public PermissionService(
            PermissionRepository permissionRepository,
            PermissionMapper permissionMapper,
            PaginationMapper paginationMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
        this.paginationMapper = paginationMapper;
    }

    public ResultPaginationDTO handleListPermission(Specification spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pagePermission.getTotalPages();
        long totalElements = pagePermission.getTotalElements();

        List<ResPermissionDTO> listPermission = pagePermission.getContent().stream()
                .map(item -> this.permissionMapper.convertToResPermissionDTO(item))
                .collect(Collectors.toList());

        ResultPaginationDTO res = this.paginationMapper.convertToResultPaginationDTO(pageNumber, pageSize, totalPages, totalElements, listPermission);

        return res;

    }

    public ResPermissionDTO getPermissionById(long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() ->
                        new IdInvalidException("Permission not found with id = " + id));
        return this.permissionMapper.convertToResPermissionDTO(permission);
    }

}
