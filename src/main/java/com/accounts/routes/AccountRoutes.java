package com.accounts.routes;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import com.accounts.model.Accounts;
import com.accounts.persistence.AccountsDataStoreMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class AccountRoutes extends AllDirectives {

    final private ActorRef accountsDataStoreActor;
    final private Duration dataStoreTimeout = Duration.ofSeconds(5l);
    final private ObjectMapper objectMapper = new ObjectMapper();

    public AccountRoutes(ActorRef accountsDataStoreActor) {
        this.accountsDataStoreActor = accountsDataStoreActor;
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public Route createRoute() {
        return concat(
                path("accounts", () ->
                        get(() -> {
                            CompletionStage<Accounts> futureAccounts = Patterns
                                    .ask(accountsDataStoreActor, new AccountsDataStoreMessages.GetAccounts(), dataStoreTimeout)
                                    .thenApply(Accounts.class::cast);
                            return onSuccess(() -> futureAccounts,
                                    accounts -> complete(StatusCodes.OK, accounts, Jackson.marshaller(objectMapper)));
                        })
                ));
    }
}
