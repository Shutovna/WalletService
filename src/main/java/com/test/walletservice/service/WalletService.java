package com.test.walletservice.service;

import com.test.walletservice.error.InsufficientFundsException;
import com.test.walletservice.error.WalletNotFoundException;
import com.test.walletservice.model.OperationType;
import com.test.walletservice.model.Wallet;
import com.test.walletservice.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletService implements IWalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Optional<Wallet> getWallet(UUID id) {
        return walletRepository.findById(id);
    }

    @Transactional
    @Override
    public void updateWallet(UUID id, OperationType operationType, BigDecimal amount) {
        Wallet wallet = getWallet(id).orElseThrow(WalletNotFoundException::new);
        switch (operationType) {
            case DEPOSIT -> {
                wallet.setAmount(wallet.getAmount().add(amount));
            }

            case WITHDRAW -> {
                BigDecimal diff = wallet.getAmount().subtract(amount);
                if(diff.compareTo(BigDecimal.ZERO) < 0) {
                    throw new InsufficientFundsException();
                }

                wallet.setAmount(diff);
            }
        }
    }
}
