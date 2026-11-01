package com.pgos.listing;

import com.pgos.listing.dto.ListingBedResponse;
import com.pgos.listing.dto.PropertyListingResponse;
import com.pgos.property.Bed;
import com.pgos.property.BedRepository;
import com.pgos.property.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final BedRepository bedRepository;
    private final PropertyRepository propertyRepository;

    @Transactional(readOnly = true)
    public List<ListingBedResponse> searchAvailableBeds(String city, BigDecimal minRent, BigDecimal maxRent) {
        String cityFilter = (city == null || city.isBlank()) ? null : city.trim();

        return bedRepository.findWithPropertyByStatus(Bed.BedStatus.AVAILABLE).stream()
                .filter(b -> cityFilter == null
                        || b.getRoom().getProperty().getCity().equalsIgnoreCase(cityFilter))
                .filter(b -> minRent == null || b.getMonthlyRent().compareTo(minRent) >= 0)
                .filter(b -> maxRent == null || b.getMonthlyRent().compareTo(maxRent) <= 0)
                .map(ListingBedResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PropertyListingResponse getPropertyListing(Long propertyId) {
        var property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        List<PropertyListingResponse.AvailableBed> beds = bedRepository
                .findByPropertyIdAndStatus(propertyId, Bed.BedStatus.AVAILABLE).stream()
                .map(PropertyListingResponse.AvailableBed::from)
                .toList();

        return PropertyListingResponse.of(property, beds);
    }
}