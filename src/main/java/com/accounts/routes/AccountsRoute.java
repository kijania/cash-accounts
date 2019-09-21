package com.accounts.routes;

import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

public class AccountsRoute extends AllDirectives {

    public Route createRoute() {
        return concat(
                path("accounts", () ->
                        get(() ->
                                complete("No cash accounts"))));
    }
}
