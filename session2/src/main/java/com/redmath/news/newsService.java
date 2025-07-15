package com.redmath.news;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class newsService {

    private static final Logger log = LoggerFactory.getLogger(newsService.class);
    private final newsRepository newsRepository;

    public newsService(newsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public news insert(news news) {

        news.setReportedAt(LocalDateTime.now());
        newsRepository.save(news);
        return news;
    }
    public List<news> findByTitleStartingWith(String title){
        String newsList=newsRepository.findByTitleStartingWith(title).getFirst().getTitle();

        log.info("news {}",newsList);
        return newsRepository.findByTitleStartingWith(title);
    }
    public List<news> findAll(){
        return newsRepository.findAll();
    }



}
