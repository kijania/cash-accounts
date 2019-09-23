package com.accounts.persistence;

import com.accounts.model.Account;
import com.accounts.model.MoneyTransfer;

import java.io.Serializable;
import java.util.Optional;

public interface AccountsDataStoreMessages {

    class GetAccounts implements Serializable {}

    class CreateAccount implements Serializable {
        private final Account account;

        public CreateAccount(Account account) { this.account = account; }

        public Account getAccount() { return account; }
    }

    class CreationResult implements Serializable {
        private final String description;
        private final Boolean isCreated;

        public CreationResult(String description, Boolean isCreated) {
            this.description = description;
            this.isCreated = isCreated;
        }

        public String getDescription() { return description; }
        public Boolean getIsCreated() { return isCreated; }
    }

    class TransferMoney implements Serializable {
        private final MoneyTransfer moneyTransfer;

        public TransferMoney(MoneyTransfer moneyTransfer) { this.moneyTransfer = moneyTransfer; }

        public MoneyTransfer getMoneyTransfer() { return moneyTransfer; }
    }

    // TODO replace it with something excluding possibility of two nulls or zero nulls
    class EitherFailureOrAccountAfterTransfer implements Serializable {
        private final Optional<String> failureDescriptionOption;
        private final Optional<Account> accountAfterTransferOption;

        public EitherFailureOrAccountAfterTransfer(Optional<String> failureDescriptionOption,
                                                   Optional<Account> accountAfterTransferOption) {
            this.failureDescriptionOption = failureDescriptionOption;
            this.accountAfterTransferOption = accountAfterTransferOption;
        }

        public Optional<String> getFailureDescriptionOption() { return failureDescriptionOption; }
        public Optional<Account> getAccountAfterTransferOption() { return accountAfterTransferOption; }
    }
}
