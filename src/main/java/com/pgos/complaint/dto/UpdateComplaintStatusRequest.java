package com.pgos.complaint.dto;

import com.pgos.complaint.Complaint;
import jakarta.validation.constraints.NotNull;

public record UpdateComplaintStatusRequest(@NotNull Complaint.Status status) {}