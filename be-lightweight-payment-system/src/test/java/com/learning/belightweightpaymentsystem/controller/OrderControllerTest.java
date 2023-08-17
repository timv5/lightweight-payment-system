package com.learning.belightweightpaymentsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.belightweightpaymentsystem.dto.OrderDetailsDto;
import com.learning.belightweightpaymentsystem.dto.OrderDto;
import com.learning.belightweightpaymentsystem.dto.ResponseWrapper;
import com.learning.belightweightpaymentsystem.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderServiceImpl orderService;

    @Test
    void creteOrder_success() throws Exception {
        OrderDto orderDto = OrderDto.builder()
                .userId(1)
                .productId(1)
                .quantity(3)
                .build();
        ResponseWrapper<OrderDto> expected = new ResponseWrapper<>(orderDto, true);

        when(orderService.createOrder(orderDto)).thenReturn(expected);
        MvcResult result = mockMvc.perform(
                post("/api/order/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(orderDto))
                ).andExpect(status().isOk()).andReturn();

        ResponseWrapper<?> actual = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseWrapper.class);
        assertEquals(objectMapper.writeValueAsString(expected), objectMapper.writeValueAsString(actual));
    }

    @Test
    void creteOrder_bad_request() throws Exception {
        OrderDto orderDto = OrderDto.builder()
                .userId(1)
                .productId(1)
                .quantity(3)
                .build();
        ResponseWrapper<OrderDto> expected = new ResponseWrapper<>(orderDto, false);

        when(orderService.createOrder(orderDto)).thenReturn(expected);
        MvcResult result = mockMvc.perform(
                post("/api/order/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(orderDto))
        ).andExpect(status().isBadRequest()).andReturn();

        ResponseWrapper<?> actual = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseWrapper.class);
        assertEquals(objectMapper.writeValueAsString(expected), objectMapper.writeValueAsString(actual));
    }

    @Test
    void creteOrder_exception() throws Exception {
        OrderDto orderDto = OrderDto.builder()
                .userId(1)
                .productId(1)
                .quantity(3)
                .build();

        when(orderService.createOrder(orderDto)).thenThrow(Exception.class);
        MvcResult result = mockMvc.perform(
                post("/api/order/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(orderDto))
        ).andExpect(status().isInternalServerError()).andReturn();

        int expected = HttpStatus.INTERNAL_SERVER_ERROR.value();
        int actual = result.getResponse().getStatus();
        assertEquals(expected, actual);
    }

    @Test
    void getOrders_success() throws Exception {
        Integer userId = 1;

        List<OrderDetailsDto> orders = new ArrayList<>();
        ResponseWrapper<List<OrderDetailsDto>> expected = new ResponseWrapper<>(orders, true);

        when(orderService.getOrders(userId)).thenReturn(expected);
        MvcResult result = mockMvc.perform(get("/api/order/list").contentType(MediaType.APPLICATION_JSON).param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andReturn();
        ResponseWrapper<?> actual = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseWrapper.class);
        assertEquals(objectMapper.writeValueAsString(expected), objectMapper.writeValueAsString(actual));
    }

    @Test
    void getOrders_bad_request() throws Exception {
        Integer userId = 1;

        List<OrderDetailsDto> orders = new ArrayList<>();
        ResponseWrapper<List<OrderDetailsDto>> expected = new ResponseWrapper<>(orders, false);

        when(orderService.getOrders(userId)).thenReturn(expected);
        MvcResult result = mockMvc.perform(get("/api/order/list").contentType(MediaType.APPLICATION_JSON).param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andReturn();
        ResponseWrapper<?> actual = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseWrapper.class);
        assertEquals(objectMapper.writeValueAsString(expected), objectMapper.writeValueAsString(actual));
    }
}