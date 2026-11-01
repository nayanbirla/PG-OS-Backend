package com.pgos.admin;

import com.pgos.admin.dto.OwnerSummary;
import com.pgos.user.OwnerVerificationStatus;
import com.pgos.user.Role;
import com.pgos.user.User;
import com.pgos.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<OwnerSummary> listOwners(OwnerVerificationStatus status) {
        return userRepository.findByRoleAndOwnerVerificationStatus(Role.OWNER, status).stream()
                .map(OwnerSummary::from)
                .toList();
    }

    @Transactional
    public OwnerSummary verify(Long ownerId) {
        User owner = findOwner(ownerId);
        if (owner.getOwnerVerificationStatus() == OwnerVerificationStatus.VERIFIED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Owner is already verified");
        }
        owner.setOwnerVerificationStatus(OwnerVerificationStatus.VERIFIED);
        return OwnerSummary.from(owner);
    }

    @Transactional
    public OwnerSummary reject(Long ownerId) {
        User owner = findOwner(ownerId);
        if (owner.getOwnerVerificationStatus() == OwnerVerificationStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Owner is already rejected");
        }
        owner.setOwnerVerificationStatus(OwnerVerificationStatus.REJECTED);
        return OwnerSummary.from(owner);
    }

    private User findOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        if (owner.getRole() != Role.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an owner");
        }
        return owner;
    }
}