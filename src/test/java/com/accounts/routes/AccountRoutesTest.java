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
        route = testRoute(accountRoutes.routes());
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

    @Test
    public void testTransferingMoneyBetweenAccounts() {

        route
                .run(HttpRequest.POST("/accounts")
                        .withEntity(MediaTypes.APPLICATION_JSON.toContentType(),
                                "{\"ownerName\": \"Michał Kijania\", \"balance\": 10000}"))
                .assertStatusCode(StatusCodes.CREATED)
                .assertMediaType("text/plain")
                .assertEntity("Account created");

        route
                .run(HttpRequest.POST("/accounts")
                        .withEntity(MediaTypes.APPLICATION_JSON.toContentType(),
                                "{\"ownerName\": \"Wanda Kijania\", \"balance\": 0}"))
                .assertStatusCode(StatusCodes.CREATED)
                .assertMediaType("text/plain")
                .assertEntity("Account created");

        route
                .run(HttpRequest.POST("/accounts/transfer")
                        .withEntity(MediaTypes.APPLICATION_JSON.toContentType(),
                                "{\"senderName\":\"Franciszek Kijania\",\"recipientName\":\"Wanda Kijania\",\"transferAmount\":100}"))
                .assertStatusCode(StatusCodes.FORBIDDEN)
                .assertMediaType("text/plain")
                .assertEntity("The transfer failed. Owner account does not exist");

        route
                .run(HttpRequest.POST("/accounts/transfer")
                        .withEntity(MediaTypes.APPLICATION_JSON.toContentType(),
                                "{\"senderName\":\"Michał Kijania\",\"recipientName\":\"Franciszek Kijania\",\"transferAmount\":100}"))
                .assertStatusCode(StatusCodes.FORBIDDEN)
                .assertMediaType("text/plain")
                .assertEntity("The transfer failed. Recipient account does not exist");

        route
                .run(HttpRequest.POST("/accounts/transfer")
                        .withEntity(MediaTypes.APPLICATION_JSON.toContentType(),
                                "{\"senderName\":\"Michał Kijania\",\"recipientName\":\"Wanda Kijania\",\"transferAmount\":-100}"))
                .assertStatusCode(StatusCodes.FORBIDDEN)
                .assertMediaType("text/plain")
                .assertEntity("The transfer failed. Transfer amount cannot be negative or 0");

        route
                .run(HttpRequest.POST("/accounts/transfer")
                        .withEntity(MediaTypes.APPLICATION_JSON.toContentType(),
                                "{\"senderName\":\"Michał Kijania\",\"recipientName\":\"Wanda Kijania\",\"transferAmount\":20000}"))
                .assertStatusCode(StatusCodes.FORBIDDEN)
                .assertMediaType("text/plain")
                .assertEntity("The transfer failed. Not sufficient account balance");

        route
                .run(HttpRequest.POST("/accounts/transfer")
                        .withEntity(MediaTypes.APPLICATION_JSON.toContentType(),
                                "{\"senderName\":\"Michał Kijania\",\"recipientName\":\"Wanda Kijania\",\"transferAmount\":6777.86}"))
                .assertStatusCode(StatusCodes.OK)
                .assertMediaType("application/json")
                .assertEntity("{\"balance\":3222.14,\"ownerName\":\"Michał Kijania\"}");

        route
                .run(HttpRequest.GET("/accounts"))
                .assertStatusCode(StatusCodes.OK)
                .assertMediaType("application/json")
                .assertEntity("{\"accounts\":[{\"balance\":6777.86,\"ownerName\":\"Wanda Kijania\"}," +
                        "{\"balance\":3222.14,\"ownerName\":\"Michał Kijania\"}]}");
    }
}
