package com.accounts.wiring;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.accounts.persistence.AccountsDataStoreActor;
import com.accounts.routes.AccountRoutes;

public enum ProductionModule {
    INSTANCE;

    public final ActorSystem actorSystem = ActorSystem.create("cash-accounts");

    public final ActorRef accountsDataStoreActor = actorSystem.actorOf(AccountsDataStoreActor.props(), "accountsDataStoreActor");

    public final AccountRoutes accountRoutes = new AccountRoutes(accountsDataStoreActor);
}
