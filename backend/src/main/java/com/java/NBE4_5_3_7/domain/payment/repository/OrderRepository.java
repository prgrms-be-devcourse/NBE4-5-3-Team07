package com.java.NBE4_5_3_7.domain.payment.repository;

import com.java.NBE4_5_3_7.domain.member.entity.Member;
import com.java.NBE4_5_3_7.domain.payment.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByImpUid(String impUid);

    Optional<Order> findByMemberAndStatus(Member member, String cancelled);
}
