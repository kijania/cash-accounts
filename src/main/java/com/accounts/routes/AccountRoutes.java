package com.accounts.routes;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import com.accounts.model.Account;
import com.accounts.model.Accounts;
import com.accounts.persistence.AccountsDataStoreMessages;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class AccountRoutes extends AllDirectives {

    final private ActorRef accountsDataStoreActor;
    final private Duration dataStoreTimeout = Duration.ofSeconds(5l);

    public AccountRoutes(ActorRef accountsDataStoreActor) {
        this.accountsDataStoreActor = accountsDataStoreActor;
    }

    public Route createRoute() {
        return concat(
                path("accounts", () ->
                        route(
                            get(() -> {
                                CompletionStage<Accounts> futureAccounts = Patterns
                                        .ask(accountsDataStoreActor, new AccountsDataStoreMessages.GetAccounts(), dataStoreTimeout)
                                        .thenApply(Accounts.class::cast);
                                return onSuccess(() -> futureAccounts,
                                        accounts -> complete(StatusCodes.OK, accounts, Jackson.marshaller()));
                            }),
                            post(() ->
                                entity(
                                    Jackson.unmarshaller(Account.class),
                                    account -> {
                                        CompletionStage<AccountsDataStoreMessages.Ack> futureAccountCreated = Patterns
                                                .ask(accountsDataStoreActor, new AccountsDataStoreMessages.CreateAccount(account), dataStoreTimeout)
                                                .thenApply(AccountsDataStoreMessages.Ack.class::cast);
                                        return onSuccess(futureAccountCreated, done ->
                                                complete(StatusCodes.CREATED, "Account created"));
                            }))
                        )
                ));
    }
}
