package com.redmath.news;

import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface newsRepository extends JpaRepository<newsModel,Long> {
    List<newsModel> findByTitle(String title);
    List<newsModel> findByTitleStartingWith(String title);
}
