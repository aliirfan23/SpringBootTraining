package com.redmath.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long createdUserId;

    @Test
    @Order(1)
    @WithMockUser(roles = "ADMIN")
    public void testCreateUser() throws Exception {
        Users user = new Users();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setRoles("REPORTER");

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("testuser")))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Users createdUser = objectMapper.readValue(response, Users.class);
        createdUserId = createdUser.getUserId();
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "ADMIN")
    public void testGetUserByIdSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("admin")));
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "ADMIN")
    public void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/999999"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(4)
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(5)
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserSuccess() throws Exception {
        Users updatedUser = new Users();
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("newpassword");
        updatedUser.setRoles("EDITOR");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("updateduser")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.is("EDITOR")));
    }

    @Test
    @Order(6)
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/1")
                        .with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
