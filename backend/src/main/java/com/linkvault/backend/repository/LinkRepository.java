package com.linkvault.backend.repository;

import com.linkvault.backend.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Page<Link> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}