package com.pgos.auth;

import com.pgos.auth.dto.MeResponse;
import com.pgos.property.Property;
import com.pgos.property.PropertyMember;
import com.pgos.property.PropertyMemberRepository;
import com.pgos.property.PropertyRepository;
import com.pgos.tenancy.Tenancy;
import com.pgos.tenancy.TenancyRepository;
import com.pgos.user.Role;
import com.pgos.user.User;
import com.pgos.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyMemberRepository memberRepository;
    private final TenancyRepository tenancyRepository;

    @Transactional(readOnly = true)
    public MeResponse me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        List<MeResponse.PropertyAccess> properties = new ArrayList<>();

        if (user.getRole() == Role.OWNER) {
            for (Property p : propertyRepository.findByOwnerId(userId)) {
                properties.add(new MeResponse.PropertyAccess(p.getId(), p.getName(), p.getCity(), "OWNER"));
            }
        }
        for (PropertyMember m : memberRepository.findByUserId(userId)) {
            Property p = m.getProperty();
            properties.add(new MeResponse.PropertyAccess(p.getId(), p.getName(), p.getCity(),
                    m.getMemberRole().name()));
        }

        List<MeResponse.ActiveTenancy> tenancies = tenancyRepository
                .findByTenantIdAndStatus(userId, Tenancy.Status.ACTIVE).stream()
                .map(t -> new MeResponse.ActiveTenancy(
                        t.getId(),
                        t.getBed().getRoom().getProperty().getId(),
                        t.getBed().getRoom().getRoomNumber(),
                        t.getBed().getBedNumber(),
                        t.getStartDate()))
                .toList();

        return new MeResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name(),
                user.getAccountStatus().name(),
                user.getOwnerVerificationStatus() == null ? null : user.getOwnerVerificationStatus().name(),
                properties,
                tenancies);
    }
}