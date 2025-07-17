package com.redmath.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class newsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    // GET /api/v1/123
    @Test
    @Order(1)
    public void testGetNewsByIdSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/123"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.newsId", Matchers.is(123)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("title 123")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details", Matchers.is("details 123")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reportedBy", Matchers.is("reporter 123")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reportedAt", Matchers.notNullValue()));
    }
    // GET /api/v1/{newsId} - Negative case
    @Test
    @Order(2)
    public void testGetNewsByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/999999"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // GET /api/v1/news?title=title
    @Test
    @Order(3)
    public void testGetByTitleStartingWith() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/news")
                        .param("title", "title"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(Matchers.greaterThan(0))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.startsWith("title")));
    }

    // POST /api/v1/news
    @Test
    @Order(4)
    public void testCreateNews() throws Exception {
        News newNews = new News();
        newNews.setTitle("Test Title");
        newNews.setDetails("Test details");
        newNews.setReportedBy("Test Reporter");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newNews)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Test Title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details", Matchers.is("Test details")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reportedBy", Matchers.is("Test Reporter")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reportedAt", Matchers.notNullValue()));
    }

    // POST /api/v1/{newsId}
    @Test
    @Order(5)
    public void testUpdateNews() throws Exception {
        News updatedNews = new News();
        updatedNews.setTitle("Updated Title");
        updatedNews.setDetails("Updated details");
        updatedNews.setReportedBy("Updated Reporter");
        updatedNews.setReportedAt(LocalDateTime.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedNews)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Updated Title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details", Matchers.is("Updated details")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reportedBy", Matchers.is("Updated Reporter")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reportedAt", Matchers.notNullValue()));
    }
    // POST /api/v1/{newsId} - Negative case
    @Test
    @Order(6)
    public void testUpdateNewsNotFound() throws Exception {
        News updatedNews = new News();
        updatedNews.setTitle("Updated Title");
        updatedNews.setDetails("Updated details");
        updatedNews.setReportedBy("Updated Reporter");
        updatedNews.setReportedAt(LocalDateTime.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedNews)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // Exception handling test
    @Test
    @Order(7)
    public void testHandleNoSuchElementException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/999999"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.issue", Matchers.is("No value present")));
    }
}
