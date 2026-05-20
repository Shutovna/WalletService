package com.test.walletservice.controller;

import com.test.walletservice.model.Wallet;
import com.test.walletservice.payload.WalletRequest;
import com.test.walletservice.payload.WalletResponse;
import com.test.walletservice.service.IWalletService;
import com.test.walletservice.error.InsufficientFundsException;
import com.test.walletservice.error.WalletNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class WalletController {
    @Autowired
    private IWalletService walletService;

    @GetMapping("/api/v1/wallets/{walletId}")
    public ResponseEntity<?> getWalletAmount(@PathVariable UUID walletId) {
        Optional<Wallet> wallet = walletService.getWallet(walletId);
        if (wallet.isPresent()) {
            return ResponseEntity.ok().body(new WalletResponse(wallet.get().getAmount()));
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/v1/wallet")
    public ResponseEntity<?> updateWallet(@Valid @RequestBody WalletRequest walletRequest) {
        try {
            walletService.updateWallet(
                    walletRequest.getWalletId(),
                    walletRequest.getOperationType(),
                    walletRequest.getAmount());

        } catch (WalletNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InsufficientFundsException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
