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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Order(0)
    public void testSuccessfulLogin() throws Exception {

        // Prepare form data
        String username = "admin";
        String password = "admin123";
        String formData = String.format("username=%s&password=%s", username, password);

        // Perform login request
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formData)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.expires_in").value(3600))
                .andReturn();

        // Print the response for debugging
        String response = result.getResponse().getContentAsString();
        System.out.println("Login Response: " + response);
    }

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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("testuser")))
                .andReturn();
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "ADMIN")
    public void testGetUserByIdSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("admin")));
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "ADMIN")
    public void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/999999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isOk())
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("updateduser")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.is("EDITOR")));
    }

    @Test
    @Order(6)
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @Order(6)
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserSuccessWithException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/del/2")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserNotFound() throws Exception {
        Users updatedUser = new Users();
        updatedUser.setUsername("nonexistent");
        updatedUser.setPassword("somepass");
        updatedUser.setRoles("REPORTER");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/999999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("")); // Expecting empty response body
    }

    @Test
    @Order(8)
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/999999")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @Order(9)
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserNotFound_TriggersExceptionHandler() throws Exception {
        Users user = new Users();
        user.setUsername("ghost");
        user.setPassword("irrelevant");
        user.setRoles("ADMIN");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/del/99999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.issue").value("User not found with id: 99999"));
    }

}
