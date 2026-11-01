package com.pgos.property;

import com.pgos.property.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/properties/{pid}")
@RequiredArgsConstructor
public class RoomController {

    private final PropertyService propertyService;

    @PostMapping("/rooms")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse addRoom(@AuthenticationPrincipal Long userId,
                                @PathVariable Long pid,
                                @Valid @RequestBody CreateRoomRequest request) {
        return propertyService.addRoom(userId, pid, request);
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    public List<RoomResponse> listRooms(@AuthenticationPrincipal Long userId, @PathVariable Long pid) {
        return propertyService.listRooms(userId, pid);
    }

    @PostMapping("/rooms/{rid}/beds")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.CREATED)
    public BedResponse addBed(@AuthenticationPrincipal Long userId,
                              @PathVariable Long pid,
                              @PathVariable Long rid,
                              @Valid @RequestBody CreateBedRequest request) {
        return propertyService.addBed(userId, pid, rid, request);
    }

    @GetMapping("/rooms/{rid}/beds")
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    public List<BedResponse> listBeds(@AuthenticationPrincipal Long userId,
                                      @PathVariable Long pid,
                                      @PathVariable Long rid) {
        return propertyService.listBeds(userId, pid, rid);
    }
}