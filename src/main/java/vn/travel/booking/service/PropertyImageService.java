package vn.travel.booking.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.dto.response.propertyimage.ResPropertyImage;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.PropertyImage;
import vn.travel.booking.entity.PropertyImageDraft;
import vn.travel.booking.entity.User;
import vn.travel.booking.repository.PropertyImageDraftRepository;
import vn.travel.booking.repository.PropertyImageRepository;
import vn.travel.booking.repository.PropertyRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.DraftAction;
import vn.travel.booking.util.constant.PropertyStatus;
import vn.travel.booking.util.error.BusinessException;
import vn.travel.booking.util.error.IdInvalidException;
import vn.travel.booking.util.error.ImageException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyRepository propertyRepository;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final PropertyImageDraftRepository propertyImageDraftRepository;

    public PropertyImageService(
            PropertyImageRepository propertyImageRepository,
            PropertyRepository propertyRepository,
            CloudinaryService cloudinaryService,
            UserService userService,
            PropertyImageDraftRepository propertyImageDraftRepository) {
        this.propertyImageRepository = propertyImageRepository;
        this.propertyRepository = propertyRepository;
        this.cloudinaryService = cloudinaryService;
        this.userService = userService;
        this.propertyImageDraftRepository = propertyImageDraftRepository;
    }

    @Transactional
    public List<ResPropertyImage> uploadImages(Long propertyId, List<MultipartFile> files) {

        Property property = getPropertyAndCheckOwner(propertyId);
        List<ResPropertyImage> result = new ArrayList<>();

        for (MultipartFile file : files) {

            String url = cloudinaryService.uploadPropertyImage(file, propertyId);

            PropertyImageDraft draft;

            // Hiện tại mọi upload đều đi qua draft
            if (property.getStatus() == PropertyStatus.DRAFT
                    || property.getStatus() == PropertyStatus.REJECTED
                    || property.getStatus() == PropertyStatus.APPROVED) {

                draft = saveDraft(property, url, DraftAction.ADD);

            } else {
                throw new BusinessException("Không thể upload ảnh với trạng thái hiện tại");
            }

            result.add(new ResPropertyImage(draft.getId(),draft.getImageUrl()));
        }

        property.setStatus(PropertyStatus.DRAFT);
        return result;
    }

    /* ===================== DELETE ===================== */
    @Transactional
    public void markDelete(Long propertyId, Long imageId) {

        Property property = getPropertyAndCheckOwner(propertyId);

        boolean isFirstDraft = propertyImageRepository.countByProperty_Id(propertyId) == 0;

        // ===== CASE 1: Draft created for the first time -> delete the draft immediately =====
        if (isFirstDraft) {

            PropertyImageDraft draft = propertyImageDraftRepository.findById(imageId)
                    .orElseThrow(() -> new IdInvalidException("Image draft không tồn tại"));

            if (draft.getProperty().getId() != propertyId) {
                throw new BusinessException("Image không thuộc property");
            }

            cloudinaryService.delete(draft.getImageUrl());
            propertyImageDraftRepository.delete(draft);

            return;
        }

        // ===== CASE 2: Update property already exists =====
        PropertyImage image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new IdInvalidException("Image không tồn tại"));

        if (image.getProperty().getId() != propertyId) {
            throw new BusinessException("Image không thuộc property");
        }

        cloudinaryService.delete(image.getImageUrl());
        propertyImageRepository.delete(image);

        // If there's a related draft, delete it as well.
        propertyImageDraftRepository.deleteByProperty_IdAndImageUrl(propertyId, image.getImageUrl());

        property.setStatus(PropertyStatus.DRAFT);
    }

    /* ===================== APPLY WHEN APPROVED ===================== */
    @Transactional
    public void applyDraft(Long propertyId) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IdInvalidException("Property không tồn tại"));

        List<PropertyImageDraft> drafts =
               propertyImageDraftRepository.findByProperty_Id(propertyId);

        for (PropertyImageDraft d : drafts) {

            if (d.getAction() == DraftAction.ADD) {
                saveRealImage(property, d.getImageUrl());
            }

        }
        propertyImageDraftRepository.deleteAll(drafts);
    }

    /* ===================== UTIL ===================== */
    private Property getPropertyAndCheckOwner(Long propertyId) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IdInvalidException("Property không tồn tại"));

        User user = userService.fetchUserById(SecurityUtil.getCurrentUserId());

        if (property.getHost().getId() != user.getId()) {
            throw new AccessDeniedException("Không phải property của bạn");
        }

        if (property.getStatus() == PropertyStatus.PENDING) {
            throw new BusinessException("Property đang chờ duyệt, không thể sửa");
        }

        return property;
    }

    private void saveRealImage(Property property, String url) {
        PropertyImage img = new PropertyImage();
        img.setProperty(property);
        img.setImageUrl(url);
        propertyImageRepository.save(img);
    }

    private PropertyImageDraft saveDraft(Property property, String url, DraftAction action) {
        PropertyImageDraft d = new PropertyImageDraft();
        d.setProperty(property);
        d.setImageUrl(url);
        d.setAction(action);
        return propertyImageDraftRepository.save(d);
    }

}
