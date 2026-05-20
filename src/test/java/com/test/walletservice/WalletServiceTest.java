package com.test.walletservice;

import com.test.walletservice.model.OperationType;
import com.test.walletservice.model.Wallet;
import com.test.walletservice.repository.WalletRepository;
import com.test.walletservice.error.InsufficientFundsException;
import com.test.walletservice.error.WalletNotFoundException;
import com.test.walletservice.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void getWallet_shouldReturnWallet() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setAmount(new BigDecimal("100.00"));

        when(walletRepository.findById(walletId))
                .thenReturn(Optional.of(wallet));

        Optional<Wallet> result = walletService.getWallet(walletId);

        assertTrue(result.isPresent());
        assertEquals(walletId, result.get().getId());
        assertEquals(new BigDecimal("100.00"), result.get().getAmount());
    }

    @Test
    void updateWallet_shouldDepositAmount() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setAmount(new BigDecimal("100.00"));

        when(walletRepository.findById(walletId))
                .thenReturn(Optional.of(wallet));

        walletService.updateWallet(
                walletId,
                OperationType.DEPOSIT,
                new BigDecimal("50.00")
        );

        assertEquals(new BigDecimal("150.00"), wallet.getAmount());
    }

    @Test
    void updateWallet_shouldWithdrawAmount() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setAmount(new BigDecimal("100.00"));

        when(walletRepository.findById(walletId))
                .thenReturn(Optional.of(wallet));

        walletService.updateWallet(
                walletId,
                OperationType.WITHDRAW,
                new BigDecimal("40.00")
        );

        assertEquals(new BigDecimal("60.00"), wallet.getAmount());
    }

    @Test
    void updateWallet_shouldThrowInsufficientFundsException() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setAmount(new BigDecimal("30.00"));

        when(walletRepository.findById(walletId))
                .thenReturn(Optional.of(wallet));

        assertThrows(
                InsufficientFundsException.class,
                () -> walletService.updateWallet(
                        walletId,
                        OperationType.WITHDRAW,
                        new BigDecimal("50.00")
                )
        );

        // Баланс не изменился
        assertEquals(new BigDecimal("30.00"), wallet.getAmount());
    }

    @Test
    void updateWallet_shouldThrowWalletNotFoundException() {
        UUID walletId = UUID.randomUUID();

        when(walletRepository.findById(walletId))
                .thenReturn(Optional.empty());

        assertThrows(
                WalletNotFoundException.class,
                () -> walletService.updateWallet(
                        walletId,
                        OperationType.DEPOSIT,
                        new BigDecimal("50.00")
                )
        );
    }
}

