package com.pgos.property;

import com.pgos.property.dto.*;
import com.pgos.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final UserRepository userRepository;
    private final PropertyAccessService access;

    // ---------- Properties ----------

    @Transactional
    public PropertyResponse createProperty(Long ownerId, CreatePropertyRequest req) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> notFound("Owner not found"));
        if (owner.getOwnerVerificationStatus() != OwnerVerificationStatus.VERIFIED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Owner account is not verified yet");
        }
        Property property = new Property(owner, req.name(), req.addressLine(),
                req.city(), req.pincode(), req.description());
        return PropertyResponse.from(propertyRepository.save(property));
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> listOwnerProperties(Long ownerId) {
        return propertyRepository.findByOwnerId(ownerId).stream()
                .map(PropertyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PropertyResponse getProperty(Long userId, Long propertyId) {
        return PropertyResponse.from(access.requireAccess(userId, propertyId));
    }

    // ---------- Rooms ----------

    @Transactional
    public RoomResponse addRoom(Long userId, Long propertyId, CreateRoomRequest req) {
        Property property = access.requireOwner(userId, propertyId);
        if (roomRepository.existsByPropertyIdAndRoomNumber(propertyId, req.roomNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room number already exists");
        }
        Room room = new Room(property, req.roomNumber(), req.floor(), req.description());
        return RoomResponse.from(roomRepository.save(room));
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> listRooms(Long userId, Long propertyId) {
        access.requireAccess(userId, propertyId);
        return roomRepository.findByPropertyId(propertyId).stream()
                .map(RoomResponse::from)
                .toList();
    }

    // ---------- Beds ----------

    @Transactional
    public BedResponse addBed(Long userId, Long propertyId, Long roomId, CreateBedRequest req) {
        access.requireOwner(userId, propertyId);
        Room room = roomRepository.findByIdAndPropertyId(roomId, propertyId)
                .orElseThrow(() -> notFound("Room not found in this property"));
        if (bedRepository.existsByRoomIdAndBedNumber(roomId, req.bedNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bed number already exists in this room");
        }
        Bed bed = new Bed(room, req.bedNumber(), req.monthlyRent());
        return BedResponse.from(bedRepository.save(bed));
    }

    @Transactional(readOnly = true)
    public List<BedResponse> listBeds(Long userId, Long propertyId, Long roomId) {
        access.requireAccess(userId, propertyId);
        roomRepository.findByIdAndPropertyId(roomId, propertyId)
                .orElseThrow(() -> notFound("Room not found in this property"));
        return bedRepository.findByRoomId(roomId).stream()
                .map(BedResponse::from)
                .toList();
    }

    // ---------- Staff members ----------

    @Transactional
    public MemberResponse addMember(Long ownerId, Long propertyId, AddMemberRequest req) {
        Property property = access.requireOwner(ownerId, propertyId);

        User staff = userRepository.findByPhone(req.phone())
                .orElseThrow(() -> notFound("No user with this phone number"));
        if (staff.getRole() != Role.STAFF) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a STAFF account");
        }
        if (staff.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff account is not active");
        }
        if (memberRepository.existsByPropertyIdAndUserId(propertyId, staff.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already a member of this property");
        }
        PropertyMember member = new PropertyMember(property, staff, req.memberRole());
        return MemberResponse.from(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers(Long ownerId, Long propertyId) {
        access.requireOwner(ownerId, propertyId);
        return memberRepository.findByPropertyId(propertyId).stream()
                .map(MemberResponse::from)
                .toList();
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}