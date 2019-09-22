package com.accounts.routes;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.MediaTypes;
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
    public void testListingNoAccounts() {
        route
                .run(HttpRequest.GET("/accounts"))
                .assertStatusCode(StatusCodes.OK)
                .assertMediaType("application/json")
                .assertEntity("{\"accounts\":[]}");
    }

    @Test
    public void testCreatingAnAccountAndListingSomeAccounts() {
        route
                .run(HttpRequest.POST("/accounts")
                        .withEntity(MediaTypes.APPLICATION_JSON.toContentType(),
                                "{\"ownerName\": \"Michał Kijania\", \"balance\": 10000}"))
                .assertStatusCode(StatusCodes.CREATED)
                .assertMediaType("text/plain")
                .assertEntity("Account created");

        route
                .run(HttpRequest.GET("/accounts"))
                .assertStatusCode(StatusCodes.OK)
                .assertMediaType("application/json")
                .assertEntity("{\"accounts\":[{\"balance\":10000,\"ownerName\":\"Michał Kijania\"}]}");
    }
}
