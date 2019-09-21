package com.accounts.wiring;

import com.accounts.routes.AccountsRoute;

public enum ProductionModule {
    INSTANCE;

    public final AccountsRoute accountsRoute = new AccountsRoute();
}
