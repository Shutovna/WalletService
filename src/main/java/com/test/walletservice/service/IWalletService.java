package com.test.walletservice.service;

import com.test.walletservice.model.OperationType;
import com.test.walletservice.model.Wallet;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface IWalletService {
    Optional<Wallet> getWallet(UUID id);

    void updateWallet(UUID id, OperationType operationType, BigDecimal amount);
}
