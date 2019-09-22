package com.accounts.persistence;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.accounts.model.Account;
import com.accounts.model.Accounts;

import java.util.ArrayList;
import java.util.List;

public class AccountsDataStoreActor extends AbstractActor {

    private final List<Account> accounts = new ArrayList<Account>();

    public static Props props() {
        return Props.create(AccountsDataStoreActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AccountsDataStoreMessages.GetAccounts.class,
                        getAccounts -> getSender().tell(new Accounts(accounts), getSelf()))
                .build();
    }
}
