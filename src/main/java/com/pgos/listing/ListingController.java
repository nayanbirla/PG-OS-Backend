package com.pgos.listing;

import com.pgos.listing.dto.ListingBedResponse;
import com.pgos.listing.dto.PropertyListingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @GetMapping("/beds")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ListingBedResponse> searchBeds(@RequestParam(required = false) String city,
                                               @RequestParam(required = false) BigDecimal minRent,
                                               @RequestParam(required = false) BigDecimal maxRent) {
        return listingService.searchAvailableBeds(city, minRent, maxRent);
    }

    @GetMapping("/properties/{pid}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public PropertyListingResponse getProperty(@PathVariable Long pid) {
        return listingService.getPropertyListing(pid);
    }
}