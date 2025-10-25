package com.merendero.facil.repository;

import com.merendero.facil.entities.DonationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface DonationRepository extends JpaRepository<DonationEntity, Long>,
        JpaSpecificationExecutor<DonationEntity> {

    List<DonationEntity> findByMerenderoId(Long merenderoId);
    boolean existsByPaymentId(String payment);

    @Query("""
        SELECT d FROM DonationEntity d
        WHERE
        d.donationDate BETWEEN :desde AND :hasta
        AND d.merendero.id = :merenderoId
        """)
    List<DonationEntity> findByDatesAndMerendero(@Param("desde") LocalDateTime desde,
                                                 @Param("hasta")LocalDateTime hasta,
                                                 Long merenderoId);

    @Query("SELECT DISTINCT d.userEmail FROM DonationEntity d WHERE d.donationDate < :periodStartDate" +
            " AND d.merendero.id = :merenderoId")
    Set<String> findPreviousDonorEmails(LocalDateTime periodStartDate, Long merenderoId);
}