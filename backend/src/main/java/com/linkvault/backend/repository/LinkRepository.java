package com.linkvault.backend.repository;

import com.linkvault.backend.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByTitle(String title);
}