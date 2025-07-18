package com.redmath.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface newsRepository extends JpaRepository<News,Long> {
    List<News> findByTitle(String title);
    List<News> findByTitleStartingWith(String title);
}
