package com.kobylchak.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.model.Role;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.model.enums.UserRole;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    
    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                          .webAppContextSetup(webApplicationContext)
                          .apply(springSecurity())
                          .build();
    }
    
    @Test
    @SneakyThrows
    @Sql(
            scripts = "classpath:database/payment/insert-two-payments.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/payment/delete-two-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/payment/insert-rental-for-getPaymets-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    public void getPayments_ValidParams_ShouldReturnListPaymentDto() {
        int expected = 2;
        User customerUser = getCustomerUser();
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/payments/?user_id=" + customerUser.getId())
                                .with(user(customerUser))
                                .contentType(MediaType.APPLICATION_JSON)
                                             )
                                      .andExpect(status().isOk())
                                      .andReturn();
        List<PaymentDto> result = Arrays.stream(
                        mapper.readValue(mvcResult.getResponse().getContentAsString(),
                                         PaymentDto[].class))
                                          .toList();
        assertFalse(result.isEmpty());
        assertEquals(expected, result.size());
    }
    
    private User getCustomerUser() {
        User user = new User();
        Role role = new Role();
        role.setName(UserRole.CUSTOMER);
        user.setRole(role);
        user.setId(1L);
        return user;
    }
    
    private User getManagerUser() {
        User user = new User();
        Role role = new Role();
        role.setName(UserRole.MANAGER);
        user.setRole(role);
        user.setId(1L);
        return user;
    }
}
