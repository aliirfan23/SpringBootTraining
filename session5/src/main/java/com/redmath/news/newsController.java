package com.redmath.news;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class newsController {
    private final newsService newsService;

    public newsController(newsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public ResponseEntity<List<News>> getByTitleStartingWith(String title) {
        return new ResponseEntity<>(newsService.findByTitleStartingWith(title), HttpStatus.OK);
    }

    @PostMapping("/news")
    public ResponseEntity<News> createNews(@RequestBody News news) {
        News insertedNews = newsService.insert(news);
        return new ResponseEntity<>(insertedNews, HttpStatus.CREATED);
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<News> get(@PathVariable("newsId") Long newsId) {
        News news = newsService.findById(newsId);
        return ResponseEntity.ok(news);
    }

    @Transactional
    @PostMapping("{newsId}")
    public ResponseEntity<News> update(@PathVariable("newsId") Long newsId, @RequestBody News news) {
        Optional<News> saved = newsService.update(newsId, news);
        if (saved.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(saved.get());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(NoSuchElementException e) {
        log.warn("News not found: {}", e.getMessage(), e);
        return Map.of("issue", e.getMessage());
    }
}