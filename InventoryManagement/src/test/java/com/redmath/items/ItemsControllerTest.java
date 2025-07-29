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
    private static Long createdItemId=1L;
    private static Long newCreatedItemId=1L; // This will be set after the first test


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
        newCreatedItemId = createdItem.getItemId(); // Save the ID for later use
    }

    @Test
    @Order(2)
    public void testGetItemByIdSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/" + newCreatedItemId).with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemId", Matchers.is(newCreatedItemId.intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", Matchers.is(499.99)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.supplier", Matchers.is("Supplier X")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("Available")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Test Item")));
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

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/"+createdItemId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Updated Item")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", Matchers.is(299.99)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("Out of Stock")));
    }

    @Test
    @Order(7)
    public void testDeleteItemSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/"+createdItemId).with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(10)
    public void testStockInwardSuccess() throws Exception {
        // Reset item to known state (quantity=20)
        resetItem1();

        int inwardQuantity = 10;
        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/inward?quantity=" + inwardQuantity).with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemId", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity", Matchers.is(20 + inwardQuantity)));
    }

    @Test
    @Order(11)
    public void testStockOutwardSuccess() throws Exception {
        // Reset item to known state (quantity=20)
        resetItem1();

        int outwardQuantity = 5;
        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/outward?quantity=" + outwardQuantity).with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemId", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity", Matchers.is(20 - outwardQuantity)));
    }

    @Test
    @Order(12)
    public void testStockReportSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/stock-report"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(3))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].itemId", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Apc Ups")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].quantity", Matchers.is(20)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].itemId", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is("Logitech Mx202 Mouse")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].quantity", Matchers.is(30)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].itemId", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].name", Matchers.is("Headphones (A4Tech Ns202)")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].quantity", Matchers.is(25)));
    }

    @Test
    @Order(13)
    public void testStockInwardNotFound() throws Exception {
        long nonExistingId = 999L;
        mockMvc.perform(MockMvcRequestBuilders.post("/items/" + nonExistingId + "/inward?quantity=10").with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.containsString("Item not found")));
    }

    @Test
    @Order(14)
    public void testStockOutwardNotFound() throws Exception {
        long nonExistingId = 999L;
        mockMvc.perform(MockMvcRequestBuilders.post("/items/" + nonExistingId + "/outward?quantity=5").with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.containsString("Item not found")));
    }

    @Test
    @Order(15)
    public void testStockOutwardInsufficientStock() throws Exception {
        // Reset item to known state (quantity=20)
        resetItem1();

        int outwardQuantity = 30; // More than available
        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/outward?quantity=" + outwardQuantity).with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.containsString("Insufficient stock")));
    }

    // Helper method to reset item with ID=1 to its initial state
    private void resetItem1() throws Exception {
        String resetJson = "{\"name\":\"Apc Ups\",\"price\":40000.00,\"quantity\":20,\"supplier\":\"APC Supplier Official\",\"status\":\"Available\"}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
