package com.pgos.property;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class PropertyAccessService {

    private final PropertyRepository propertyRepository;
    private final PropertyMemberRepository memberRepository;

    /** Property must exist and belong to this owner. */
    public Property requireOwner(Long userId, Long propertyId) {
        Property property = findOrThrow(propertyId);
        if (!property.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your property");
        }
        return property;
    }

    /** Owner of the property, or a staff member assigned to it. */
    public Property requireAccess(Long userId, Long propertyId) {
        Property property = findOrThrow(propertyId);
        if (property.getOwner().getId().equals(userId)) {
            return property;
        }
        if (memberRepository.existsByPropertyIdAndUserId(propertyId, userId)) {
            return property;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access to this property");
    }

    private Property findOrThrow(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
    }
}