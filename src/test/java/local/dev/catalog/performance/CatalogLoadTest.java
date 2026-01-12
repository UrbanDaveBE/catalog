package local.dev.catalog.performance; // Dein neues Package

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CatalogLoadTest extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json");

    ScenarioBuilder scn = scenario("Catalog Load Test - 100 Users Search")
            .exec(http("Multi-Keyword-Search")
                    // Wir nutzen genau deine Logik: Mehrere Begriffe = AND-Suche
                    .get("/api/books/search?query=clean&query=martin")
                    .check(status().is(200))
                    .check(jsonPath("$[*]").exists()) // Checkt, ob überhaupt Ergebnisse kommen
            );

    {
        setUp(
                scn.injectOpen(atOnceUsers(100)) // Die 100 User aus dem Auftrag
        ).protocols(httpProtocol);
    }
}