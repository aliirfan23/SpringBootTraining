package com.redmath.news;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        Optional<News> news = newsService.findById(newsId);
        if (news.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(news.get());
    }
}