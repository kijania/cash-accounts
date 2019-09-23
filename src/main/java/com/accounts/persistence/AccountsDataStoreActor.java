package com.accounts.persistence;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.accounts.model.Account;
import com.accounts.model.Accounts;
import com.accounts.model.MoneyTransfer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountsDataStoreActor extends AbstractActor {

    private Map<String, Account> accounts = new HashMap<>();

    public static Props props() {
        return Props.create(AccountsDataStoreActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AccountsDataStoreMessages.GetAccounts.class,
                        getAccounts -> getSender().tell(new Accounts(new ArrayList<>(accounts.values())), getSelf()))
                .match(AccountsDataStoreMessages.CreateAccount.class, this::onCreateAccount)
                .match(AccountsDataStoreMessages.TransferMoney.class, this::onTransferMoney)
                .build();
    }

    private void onCreateAccount(AccountsDataStoreMessages.CreateAccount createAccount) {
        if (accounts.get(createAccount.getAccount().getOwnerName()) == null) {
            accounts.put(createAccount.getAccount().getOwnerName(), createAccount.getAccount());
            getSender().tell(new AccountsDataStoreMessages.CreationResult("Account created", true), getSelf());
        } else
            getSender().tell(new AccountsDataStoreMessages.CreationResult(
                    "Account could not be created, because it already exists", false), getSelf());
    }

    private void onTransferMoney(AccountsDataStoreMessages.TransferMoney transferMoney) {
        final MoneyTransfer moneyTransfer = transferMoney.getMoneyTransfer();
        final String ownerName = moneyTransfer.getSenderName();
        final String recipientName = moneyTransfer.getRecipientName();
        final BigDecimal transferAmount = moneyTransfer.getTransferAmount();

        final Optional<String> failureDescriptionOption;
        final Optional<Account> accountAfterTransferOption;

        if (transferAmount.compareTo(BigDecimal.valueOf(0)) < 1) {
            failureDescriptionOption = Optional.of("The transfer failed. Transfer amount cannot be negative or 0");
            accountAfterTransferOption = Optional.empty();
        } else {
            final Account ownerAccount = accounts.get(ownerName);
            final Account recipientAccount = accounts.get(recipientName);

            if (ownerAccount == null) {
                failureDescriptionOption = Optional.of("The transfer failed. Owner account does not exist");
                accountAfterTransferOption = Optional.empty();
            } else if (recipientAccount == null) {
                failureDescriptionOption = Optional.of("The transfer failed. Recipient account does not exist");
                accountAfterTransferOption = Optional.empty();
            } else if (ownerAccount.getBalance().compareTo(transferAmount) < 0) {
                failureDescriptionOption = Optional.of("The transfer failed. Not sufficient account balance");
                accountAfterTransferOption = Optional.empty();
            } else {
                Account accountAfterTransfer = ownerAccount.withdraw(transferAmount);
                accounts.put(ownerName, accountAfterTransfer);
                accounts.put(recipientName, recipientAccount.deposit(transferAmount));
                failureDescriptionOption = Optional.empty();
                accountAfterTransferOption = Optional.of(accountAfterTransfer);
            }
        }
        getSender().tell(new AccountsDataStoreMessages.EitherFailureOrAccountAfterTransfer(
                failureDescriptionOption, accountAfterTransferOption), getSelf());
    }
}
