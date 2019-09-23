package com.accounts.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Account {
    private final String ownerName;
    private final BigDecimal balance;

    @JsonCreator
    public Account(@JsonProperty("ownerName") String ownerName,
                   @JsonProperty("balance") BigDecimal balance) {
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public String getOwnerName() { return ownerName; }

    public BigDecimal getBalance() { return balance; }

    public Account deposit(BigDecimal amount) { return new Account(ownerName, balance.add(amount)); }

    public Account withdraw(BigDecimal amount) { return new Account(ownerName, balance.subtract(amount)); }
}
