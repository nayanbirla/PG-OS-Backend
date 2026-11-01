package com.pgos.booking;

import com.pgos.booking.dto.BookingResponse;
import com.pgos.booking.dto.CreateBookingRequest;
import com.pgos.property.Bed;
import com.pgos.property.BedRepository;
import com.pgos.property.PropertyAccessService;
import com.pgos.tenancy.Tenancy;
import com.pgos.tenancy.TenancyRepository;
import com.pgos.user.User;
import com.pgos.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.pgos.booking.BookingRequest.Status.*;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRequestRepository bookingRepository;
    private final BedRepository bedRepository;
    private final TenancyRepository tenancyRepository;
    private final UserRepository userRepository;
    private final PropertyAccessService access;

    // ---------- Customer ----------

    @Transactional
    public BookingResponse create(Long customerId, CreateBookingRequest req) {
        Bed bed = bedRepository.findById(req.bedId())
                .orElseThrow(() -> notFound("Bed not found"));
        if (bed.getStatus() != Bed.BedStatus.AVAILABLE) {
            throw conflict("Bed is not available");
        }
        if (bookingRepository.existsByCustomerIdAndBedIdAndStatus(customerId, bed.getId(), PENDING)) {
            throw conflict("You already have a pending request for this bed");
        }
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> notFound("Customer not found"));

        BookingRequest booking = new BookingRequest(customer, bed, req.requestedStartDate(), req.message());
        return BookingResponse.from(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> myRequests(Long customerId) {
        return bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional
    public BookingResponse cancel(Long customerId, Long bookingId) {
        BookingRequest booking = findBooking(bookingId);
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your booking request");
        }
        if (booking.getStatus() != PENDING) {
            throw conflict("Only pending requests can be cancelled");
        }
        booking.setStatus(CANCELLED);
        return BookingResponse.from(booking);
    }

    // ---------- Owner ----------

    @Transactional(readOnly = true)
    public List<BookingResponse> listForProperty(Long ownerId, Long propertyId, BookingRequest.Status status) {
        access.requireOwner(ownerId, propertyId);
        return bookingRepository.findByBedRoomPropertyIdAndStatus(propertyId, status).stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional
    public BookingResponse approve(Long ownerId, Long bookingId) {
        BookingRequest booking = findBooking(bookingId);
        Long propertyId = booking.getBed().getRoom().getProperty().getId();
        access.requireOwner(ownerId, propertyId);

        if (booking.getStatus() != PENDING) {
            throw conflict("Only pending requests can be approved");
        }
        Bed bed = booking.getBed();
        if (bed.getStatus() != Bed.BedStatus.AVAILABLE
                || tenancyRepository.existsByBedIdAndStatus(bed.getId(), Tenancy.Status.ACTIVE)) {
            throw conflict("Bed is already occupied");
        }

        booking.setStatus(APPROVED);
        bed.setStatus(Bed.BedStatus.OCCUPIED);
        tenancyRepository.save(new Tenancy(
                bed,
                booking.getCustomer(),
                booking.getRequestedStartDate(),
                bed.getMonthlyRent()));

        // The bed is now taken, so every other pending request for it is closed.
        bookingRepository.findByBedIdAndStatus(bed.getId(), PENDING).stream()
                .filter(other -> !other.getId().equals(booking.getId()))
                .forEach(other -> other.setStatus(REJECTED));

        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse reject(Long ownerId, Long bookingId) {
        BookingRequest booking = findBooking(bookingId);
        Long propertyId = booking.getBed().getRoom().getProperty().getId();
        access.requireOwner(ownerId, propertyId);

        if (booking.getStatus() != PENDING) {
            throw conflict("Only pending requests can be rejected");
        }
        booking.setStatus(REJECTED);
        return BookingResponse.from(booking);
    }

    // ---------- Helpers ----------

    private BookingRequest findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> notFound("Booking request not found"));
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}