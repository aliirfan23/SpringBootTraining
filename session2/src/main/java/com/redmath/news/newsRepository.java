package com.redmath.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface newsRepository extends JpaRepository<news,Long> {
    List<news> findByTitle(String title);
    List<news> findByTitleStartingWith(String title);
}
