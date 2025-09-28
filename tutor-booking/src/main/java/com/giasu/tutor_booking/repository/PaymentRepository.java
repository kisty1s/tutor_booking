package com.giasu.tutor_booking.repository;

import com.giasu.tutor_booking.domain.payment.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByReferenceCode(String referenceCode);
}
