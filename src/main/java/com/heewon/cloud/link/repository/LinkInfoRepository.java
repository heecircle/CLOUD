package com.heewon.cloud.link.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heewon.cloud.link.domain.Link;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

}
