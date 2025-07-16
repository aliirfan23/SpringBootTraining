package com.redmath.news;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class newsService {

    private final newsRepository newsRepository;

    public newsService(newsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public News insert(News news) {

        news.setReportedAt(LocalDateTime.now());
        newsRepository.save(news);
        return news;
    }
    public List<News> findByTitleStartingWith(String title){
        String newsList=newsRepository.findByTitleStartingWith(title).getFirst().getTitle();

        log.info("news {}",newsList);
        return newsRepository.findByTitleStartingWith(title);
    }
    public List<News> findAll(){
        return newsRepository.findAll();
    }

    public Optional<News> findById(Long newsId){
        return newsRepository.findById(newsId);
    }



}
