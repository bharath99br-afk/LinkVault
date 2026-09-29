package com.linkvault.backend.offer.repository;

import com.linkvault.backend.offer.model.Offer;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.linkvault.backend.offer.model.DiscountType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

        Page<Offer> findAllByOrderByStartDateDesc(
                        Pageable pageable);

        Page<Offer> findByTitleContainingIgnoreCase(
                        String title,
                        Pageable pageable);

        List<Offer> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        LocalDate today1,
                        LocalDate today2);

        long countByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        LocalDate today1,
                        LocalDate today2);

        Page<Offer> findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
                        LocalDate startDate,
                        LocalDate endDate,
                        Pageable pageable);

        Page<Offer> findByStartDateAfterOrderByStartDateAsc(
                        LocalDate date,
                        Pageable pageable);

        Page<Offer> findByEndDateBeforeOrderByEndDateDesc(
                        LocalDate date,
                        Pageable pageable);

        Page<Offer> findByTitleContainingIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
                        String title,
                        LocalDate startDate,
                        LocalDate endDate,
                        Pageable pageable);

        Page<Offer> findByTitleContainingIgnoreCaseAndStartDateAfterOrderByStartDateAsc(
                        String title,
                        LocalDate date,
                        Pageable pageable);

        Page<Offer> findByTitleContainingIgnoreCaseAndEndDateBeforeOrderByEndDateDesc(
                        String title,
                        LocalDate date,
                        Pageable pageable);

        @Query("""
                        SELECT o
                        FROM Offer o
                        WHERE
                            o.title = :title
                            AND o.discountType = :discountType
                            AND o.discountValue = :discountValue
                            AND (
                                (:maxDiscount IS NULL AND o.maxDiscount IS NULL)
                                OR o.maxDiscount = :maxDiscount
                            )
                            AND (
                                (:minTransactionAmount IS NULL AND o.minTransactionAmount IS NULL)
                                OR o.minTransactionAmount = :minTransactionAmount
                            )
                            AND o.startDate = :startDate
                            AND o.endDate = :endDate
                            AND (
                                (:globalMerchantId IS NULL AND o.globalMerchant IS NULL)
                                OR o.globalMerchant.id = :globalMerchantId
                            )
                        """)
        Optional<Offer> findDuplicate(
                        @Param("title") String title,
                        @Param("discountType") DiscountType discountType,
                        @Param("discountValue") BigDecimal discountValue,
                        @Param("maxDiscount") BigDecimal maxDiscount,
                        @Param("minTransactionAmount") BigDecimal minTransactionAmount,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("globalMerchantId") Long globalMerchantId);

}