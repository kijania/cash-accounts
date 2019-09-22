package com.accounts.persistence;

import com.accounts.model.Account;

import java.io.Serializable;

public interface AccountsDataStoreMessages {
    class GetAccounts implements Serializable {}
    class CreateAccount implements Serializable {
        private final Account account;

        public CreateAccount(Account account) { this.account = account; }

        public Account getAccount() { return  account; }
    }
    class Ack implements Serializable {}
}
