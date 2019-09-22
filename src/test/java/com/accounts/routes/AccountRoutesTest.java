package com.accounts.routes;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import com.accounts.persistence.AccountsDataStoreActor;
import org.junit.Before;
import org.junit.Test;

public class AccountRoutesTest extends JUnitRouteTest {
    private TestRoute route;

    @Before
    public void initClass() {
        ActorSystem system = ActorSystem.create("AccountRoutesTest");
        ActorRef accountsDataStoreActor = system.actorOf(AccountsDataStoreActor.props(), "accountsDataStoreActor");
        AccountRoutes accountRoutes = new AccountRoutes(accountsDataStoreActor);
        route = testRoute(accountRoutes.createRoute());
    }

    @Test
    public void testGettingNoAccounts() {
        route
                .run(HttpRequest.GET("/accounts"))
                .assertStatusCode(StatusCodes.OK)
                .assertMediaType("application/json")
                .assertEntity("{}");
    }
}
