package com.accounts.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Accounts {
    private final List<Account> accounts;

    @JsonCreator
    public Accounts(@JsonProperty("accounts") List<Account> accounts) { this.accounts = accounts; }

    public List<Account> getAccounts() { return accounts; }

}