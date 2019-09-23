package com.accounts.routes;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import com.accounts.model.Account;
import com.accounts.model.Accounts;
import com.accounts.model.MoneyTransfer;
import com.accounts.persistence.AccountsDataStoreMessages;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class AccountRoutes extends AllDirectives {

    final private ActorRef accountsDataStoreActor;
    final private Duration dataStoreTimeout = Duration.ofSeconds(5l);

    public AccountRoutes(ActorRef accountsDataStoreActor) {
        this.accountsDataStoreActor = accountsDataStoreActor;
    }

    public Route routes() {
        return route(pathPrefix("accounts", () ->
                route(
                        pathEnd(this::getAccounts),
                        pathEnd(this::createAccount),
                        path("transfer", this::transferMoney)
                )
        ));
    }

    private Route getAccounts() {
        return get(() -> {
            CompletionStage<Accounts> futureAccounts = Patterns
                    .ask(accountsDataStoreActor, new AccountsDataStoreMessages.GetAccounts(), dataStoreTimeout)
                    .thenApply(Accounts.class::cast);
            return onSuccess(() -> futureAccounts,
                    accounts -> complete(StatusCodes.OK, accounts, Jackson.marshaller()));
        });
    }

    private Route createAccount() {
        return post(() ->
                entity(
                        Jackson.unmarshaller(Account.class),
                        account -> {
                            CompletionStage<AccountsDataStoreMessages.CreationResult> futureCreationResult = Patterns
                                    .ask(accountsDataStoreActor, new AccountsDataStoreMessages.CreateAccount(account), dataStoreTimeout)
                                    .thenApply(AccountsDataStoreMessages.CreationResult.class::cast);
                            return onSuccess(() -> futureCreationResult,
                                    creationResult -> {
                                        if (creationResult.getIsCreated())
                                            return complete(StatusCodes.CREATED, creationResult.getDescription());
                                        else
                                            return complete(StatusCodes.FORBIDDEN, creationResult.getDescription());

                                    });
                        })
        );
    }

    private Route transferMoney() {
        return post(() ->
                entity(
                        Jackson.unmarshaller(MoneyTransfer.class),
                        moneyTransfer -> {
                            CompletionStage<AccountsDataStoreMessages.EitherFailureOrAccountAfterTransfer> futureEitherFailureOrAccountAfterTransfer = Patterns
                                    .ask(accountsDataStoreActor, new AccountsDataStoreMessages.TransferMoney(moneyTransfer), dataStoreTimeout)
                                    .thenApply(AccountsDataStoreMessages.EitherFailureOrAccountAfterTransfer.class::cast);
                            return onSuccess(() -> futureEitherFailureOrAccountAfterTransfer,
                                    eitherFailureOrAccountAfterTransfer -> {
                                        if (eitherFailureOrAccountAfterTransfer.getAccountAfterTransferOption().isPresent())
                                            return complete(StatusCodes.OK, eitherFailureOrAccountAfterTransfer.getAccountAfterTransferOption().get(), Jackson.marshaller());
                                        else if (eitherFailureOrAccountAfterTransfer.getFailureDescriptionOption().isPresent())
                                            return complete(StatusCodes.FORBIDDEN, eitherFailureOrAccountAfterTransfer.getFailureDescriptionOption().get());
                                        else
                                            return complete(StatusCodes.INTERNAL_SERVER_ERROR);
                                    });
                        })
        );
    }
}
