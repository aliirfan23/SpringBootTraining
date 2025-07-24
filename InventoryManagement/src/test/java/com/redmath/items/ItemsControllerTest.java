package com.redmath.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class ItemsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long createdItemId;

    @Test
    @Order(1)
    public void testCreateItem() throws Exception {
        Items item = new Items();
        item.setName("Test Item");
        item.setPrice(499.99);
        item.setSupplier("Supplier X");
        item.setStatus("Available");

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/items").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Test Item")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", Matchers.is(499.99)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.supplier", Matchers.is("Supplier X")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("Available")))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Items createdItem = objectMapper.readValue(response, Items.class);
        createdItemId = createdItem.getItemId(); // Save the ID for later use
    }

    @Test
    @Order(2)
    public void testGetItemByIdSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/" + 123).with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemId", Matchers.is(123)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", Matchers.is(999.99)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Sample Item")));
    }

    @Test
    @Order(3)
    public void testGetItemByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/999999").with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(4)
    public void testGetAllItems() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/items"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(5)
    public void testUpdateItemSuccess() throws Exception {
        Items updatedItem = new Items();
        updatedItem.setName("Updated Item");
        updatedItem.setPrice(299.99);
        updatedItem.setSupplier("Supplier Y");
        updatedItem.setStatus("Out of Stock");

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/123").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Updated Item")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", Matchers.is(299.99)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("Out of Stock")));
    }

//    @Test
//    @Order(6)
//    public void testUpdateItemNotFound() throws Exception {
//        Items updatedItem = new Items();
//        updatedItem.setName("Ghost");
//        updatedItem.setPrice(0.0);
//        updatedItem.setSupplier("None");
//        updatedItem.setStatus("Unavailable");
//
//        mockMvc.perform(MockMvcRequestBuilders.patch("/items/999999")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedItem)))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//    }

    @Test
    @Order(7)
    public void testDeleteItemSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/123").with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

//    @Test
//    @Order(8)
//    public void testDeleteItemNotFound() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/items/999999"))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
}
