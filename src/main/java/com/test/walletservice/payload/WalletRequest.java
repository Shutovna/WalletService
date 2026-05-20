package com.test.walletservice.payload;

import com.test.walletservice.model.OperationType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequest {
    @NotNull
    private UUID walletId;

    private OperationType operationType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @DecimalMax(value = "999999.99", inclusive = true,
            message = "Amount cannot exceed 999999.99")
    @Digits(integer = 19, fraction = 2,
            message = "Amount must have up to 6 integer and 2 fractional digits")
    private BigDecimal amount;
}
