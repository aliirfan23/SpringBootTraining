package com.redmath.news;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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

    public News findById(Long newsId){
        return newsRepository.findById(newsId).orElseThrow();
    }

    public Optional<News> update(Long newsId, News news) {
        Optional<News> existing = newsRepository.findById(newsId);
        if (existing.isPresent()) {
            existing.get().setTitle(news.getTitle());
            existing.get().setDetails(news.getDetails());

        }
        return Optional.of(newsRepository.save(existing.get()));
    }

}
