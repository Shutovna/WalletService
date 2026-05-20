package com.test.walletservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.walletservice.model.OperationType;
import com.test.walletservice.model.Wallet;
import com.test.walletservice.payload.WalletRequest;
import com.test.walletservice.service.IWalletService;
import com.test.walletservice.error.InsufficientFundsException;
import com.test.walletservice.error.WalletNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IWalletService walletService;

    // ================= GET =================

    @Test
    void getWalletAmount_shouldReturn200() throws Exception {
        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setAmount(new BigDecimal("100.00"));

        when(walletService.getWallet(walletId))
                .thenReturn(Optional.of(wallet));

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void getWalletAmount_shouldReturn404() throws Exception {
        UUID walletId = UUID.randomUUID();

        when(walletService.getWallet(walletId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isNotFound());
    }

    // ================= POST =================

    @Test
    void updateWallet_shouldReturn200() throws Exception {
        WalletRequest request = new WalletRequest();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(new BigDecimal("50.00"));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(walletService).updateWallet(
                request.getWalletId(),
                request.getOperationType(),
                request.getAmount()
        );
    }

    @Test
    void updateWallet_shouldReturn404_whenWalletNotFound() throws Exception {
        WalletRequest request = new WalletRequest();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(new BigDecimal("50.00"));

        doThrow(new WalletNotFoundException())
                .when(walletService)
                .updateWallet(
                        request.getWalletId(),
                        request.getOperationType(),
                        request.getAmount()
                );

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateWallet_shouldReturn400_whenInsufficientFunds() throws Exception {
        WalletRequest request = new WalletRequest();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("999.00"));

        doThrow(new InsufficientFundsException())
                .when(walletService)
                .updateWallet(
                        request.getWalletId(),
                        request.getOperationType(),
                        request.getAmount()
                );

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}