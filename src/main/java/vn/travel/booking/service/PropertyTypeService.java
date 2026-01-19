package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.request.propertytype.ReqCreatePropertyTypeDTO;
import vn.travel.booking.dto.request.propertytype.ReqUpdatePropertyTypeDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.propertytype.ResPropertyTypeDTO;
import vn.travel.booking.entity.PropertyType;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.mapper.PropertyTypeMapper;
import vn.travel.booking.repository.PropertyTypeRepository;
import vn.travel.booking.util.error.IdInvalidException;
import vn.travel.booking.util.error.NameInvalidException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyTypeService {

    private final PropertyTypeRepository propertyTypeRepository;
    private final PropertyTypeMapper propertyTypeMapper;
    private final PaginationMapper paginationMapper;

    public PropertyTypeService(
            PropertyTypeRepository propertyTypeRepository,
            PropertyTypeMapper propertyTypeMapper,
            PaginationMapper paginationMapper) {
        this.propertyTypeRepository = propertyTypeRepository;
        this.propertyTypeMapper = propertyTypeMapper;
        this.paginationMapper = paginationMapper;
    }

    public PropertyType fetchById(long id) {
        return this.propertyTypeRepository.findById(id).orElseThrow(() -> new IdInvalidException("Property với id = " + id + " không tồn tại"));
    }

    @Transactional
    public ResPropertyTypeDTO handleCreatePropertyType(ReqCreatePropertyTypeDTO dto) {
        if(this.propertyTypeRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new NameInvalidException("Property type name đã tồn tại");
        }

        PropertyType propertyType = new PropertyType();
        propertyType.setName(dto.getName());
        this.propertyTypeRepository.save(propertyType);

        return this.propertyTypeMapper.convertToResPropertyTypeDTO(propertyType);

    }

    @Transactional
    public ResPropertyTypeDTO handleUpdatePropertyType(ReqUpdatePropertyTypeDTO dto) {
        PropertyType propertyType = fetchById(dto.getId());
        propertyType.setName(dto.getName());

        return this.propertyTypeMapper.convertToResPropertyTypeDTO(propertyType);
    }

    public ResultPaginationDTO handleListPropertyType(Specification spec, Pageable pageable) {
        Page<PropertyType> pagePropertyType = this.propertyTypeRepository.findAll(spec, pageable);
        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pagePropertyType.getTotalPages();
        long totalElements = pagePropertyType.getTotalElements();

        List<ResPropertyTypeDTO> listPropertyType = pagePropertyType.getContent().stream()
                .map(item -> this.propertyTypeMapper.convertToResPropertyTypeDTO(item))
                .collect(Collectors.toList());

        ResultPaginationDTO res = this.paginationMapper.convertToResultPaginationDTO(pageNumber, pageSize, totalPages, totalElements, listPropertyType);

        return res;
    }

    public ResPropertyTypeDTO viewPropertyTypeById(long id) {
        PropertyType propertyType = fetchById(id);
        return this.propertyTypeMapper.convertToResPropertyTypeDTO(propertyType);
    }

    @Transactional
    public void handleDeletePropertyType(long id) {
        PropertyType propertyType = fetchById(id);
        this.propertyTypeRepository.delete(propertyType);
    }

}
