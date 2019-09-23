package com.accounts.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class MoneyTransfer {
    private final String senderName;
    private final String recipientName;
    private final BigDecimal transferAmount;

    @JsonCreator
    public MoneyTransfer(@JsonProperty("senderName") String senderName,
                         @JsonProperty("recipientName") String recipientName,
                         @JsonProperty("transferAmount") BigDecimal transferAmount) {
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.transferAmount = transferAmount;
    }

    public String getSenderName() { return senderName; }
    public String getRecipientName() { return recipientName; }
    public BigDecimal getTransferAmount() { return transferAmount; }
}
