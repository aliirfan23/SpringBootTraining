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

    @PostMapping
    public ResponseEntity<newsModel> createNews(@RequestBody newsModel newsModel) {
        newsModel insertedNews = newsService.insert(newsModel);
        return new ResponseEntity<>(insertedNews, HttpStatus.CREATED);

    }

//    @GetMapping
//    public ResponseEntity<List<newsModel>> getByTitle() {
//        return new ResponseEntity<>(newsService.findAll(), HttpStatus.OK);
//    }


    @GetMapping
    public ResponseEntity<List<newsModel>> getByTitleStartingWith(String title) {
        return new ResponseEntity<>(newsService.findByTitleStartingWith(title), HttpStatus.OK);
    }
}