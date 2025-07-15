package com.redmath.news;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class newsService {

    private final newsRepository newsRepository;

    public newsService(newsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public newsModel insert(newsModel newsModel) {

        newsModel.setReportedAt(LocalDateTime.now());
        newsRepository.save(newsModel);
        return newsModel;
    }
    public List<newsModel> findByTitleStartingWith(String title){
        return newsRepository.findByTitleStartingWith(title);
    }
    public List<newsModel> findAll(){
        return newsRepository.findAll();
    }



}
