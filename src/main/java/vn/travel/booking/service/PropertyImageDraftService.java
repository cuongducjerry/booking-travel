package vn.travel.booking.service;

import org.springframework.stereotype.Service;
import vn.travel.booking.dto.response.propertyimage.ResPropertyImage;
import vn.travel.booking.entity.PropertyImageDraft;
import vn.travel.booking.repository.PropertyImageDraftRepository;

import java.util.List;

@Service
public class PropertyImageDraftService {

    private final PropertyImageDraftRepository propertyImageDraftRepository;

    public PropertyImageDraftService(PropertyImageDraftRepository propertyImageDraftRepository) {
        this.propertyImageDraftRepository = propertyImageDraftRepository;
    }

    public List<ResPropertyImage> handleListPropertyDraftImage(Long id) {
        List<PropertyImageDraft> listDraft = this.propertyImageDraftRepository.findByProperty_Id(id);

        return listDraft.stream()
                .map(draft -> new ResPropertyImage(
                        draft.getId(),
                        draft.getImageUrl()
                ))
                .toList();
    }



}
