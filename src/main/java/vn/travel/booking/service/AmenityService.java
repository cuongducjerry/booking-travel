package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.request.amenity.ReqCreateAmenityDTO;
import vn.travel.booking.dto.request.amenity.ReqUpdateAmenityDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.amenity.ResAmenityDTO;
import vn.travel.booking.entity.Amenity;
import vn.travel.booking.mapper.AmenityMapper;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.repository.AmenityRepository;
import vn.travel.booking.util.error.IdInvalidException;
import vn.travel.booking.util.error.NameInvalidException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmenityService {

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;
    private final PaginationMapper paginationMapper;

    public AmenityService(
            AmenityRepository amenityRepository,
            AmenityMapper amenityMapper,
            PaginationMapper paginationMapper) {
        this.amenityRepository = amenityRepository;
        this.amenityMapper = amenityMapper;
        this.paginationMapper = paginationMapper;
    }

    @Transactional
    public ResAmenityDTO handleCreateAmenity(ReqCreateAmenityDTO req) {

        if (this.amenityRepository.existsByNameIgnoreCase(req.getName())) {
            throw new NameInvalidException("Amenity đã tồn tại");
        }

        Amenity amenity = new Amenity();
        amenity.setName(req.getName());
        amenity.setIcon(req.getIcon());
        this.amenityRepository.save(amenity);

        return this.amenityMapper.convertToResAmenityDTO(amenity);
    }

    @Transactional
    public ResAmenityDTO handleUpdateAmenity(ReqUpdateAmenityDTO req) {
        Amenity amenity = fetchById(req.getId());
        amenity.setName(req.getName());
        amenity.setIcon(req.getIcon());

        return this.amenityMapper.convertToResAmenityDTO(amenity);
    }

    @Transactional
    public void handleDeleteAmenity(long id) {
        Amenity amenity = fetchById(id);
        this.amenityRepository.delete(amenity);
    }

    public ResultPaginationDTO handleListAmenity(Specification spec, Pageable pageable) {
        Page<Amenity> pageAmenity = this.amenityRepository.findAll(spec, pageable);
        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pageAmenity.getTotalPages();
        long totalElements = pageAmenity.getTotalElements();

        List<ResAmenityDTO> listAmenity = pageAmenity.getContent().stream()
                .map(item -> this.amenityMapper.convertToResAmenityDTO(item))
                .collect(Collectors.toList());

        ResultPaginationDTO res = this.paginationMapper.convertToResultPaginationDTO(pageNumber, pageSize, totalPages, totalElements, listAmenity);

        return res;
    }

    public ResAmenityDTO viewAmenityById(Long id) {
        Amenity amenity = fetchById(id);
        return this.amenityMapper.convertToResAmenityDTO(amenity);
    }

    public Amenity fetchById(Long id) {
        return this.amenityRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Amenity với id = " + id + " không tồn tại"));
    }

}
