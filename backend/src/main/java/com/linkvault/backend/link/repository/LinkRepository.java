package com.linkvault.backend.link.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.linkvault.backend.link.model.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Page<Link> findByUserId(Long userId, Pageable pageable);

    Page<Link> findByUserIdAndTitleContainingIgnoreCase(
            Long userId,
            String title,
            Pageable pageable);

    Optional<Link> findByIdAndUserId(Long id, Long userId);
}