package com.pgos.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoomRequest(
        @NotBlank @Size(max = 20) String roomNumber,
        Integer floor,
        @Size(max = 500) String description
) {}