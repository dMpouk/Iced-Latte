package com.zufar.icedlatte.payment.repository;

import com.zufar.icedlatte.payment.entity.Payment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, String> {

    @Modifying
    @Query(value = "UPDATE payment SET status = :payment_status, description = :payment_description WHERE session_id = :session_id",
            nativeQuery = true)
    void updateStatusAndDescriptionInPayment(@Param("session_id") String sessionId,
                                                          @Param("payment_status") String paymentStatus,
                                                          @Param("payment_description") String paymentDescription);

    Payment findBySessionId(String id);
}
