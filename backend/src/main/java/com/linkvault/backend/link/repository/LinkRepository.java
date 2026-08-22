package com.linkvault.backend.link.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkvault.backend.link.model.Link;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Page<Link> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}