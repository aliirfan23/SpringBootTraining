package com.redmath.news;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class newsController {
    private final newsService newsService;

    public newsController(newsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<news>> getByTitleStartingWith(String title) {
        return new ResponseEntity<>(newsService.findByTitleStartingWith(title), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<news> createNews(@RequestBody news news) {
        news insertedNews = newsService.insert(news);
        return new ResponseEntity<>(insertedNews, HttpStatus.CREATED);
    }

}