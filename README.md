# crypto-price-notifier

crypto-price-notifier provides the following functionality. User can:
- define a threshold for an alert when cryptocurrency price goes above the defined limit (PUT /alert?pair=xxx-xxx&limit=100)
- see alerts for the defined thresholds (websockets /alerts)
- see his thresholds (websockets /thresholds)
- delete an alert threshold (DELETE /alert?pair=xxx-xxx&limit=100)

User is notified about the threshold only once. It means that when the alert happened for some threshold, user is notified, and this threshold is cancelled.
User can define as many alerts as he wishes.
The application fetches trades from Bitfines cryptocurrency exchange (with the help of xchange library). 

The application comes with web UI (simple one but still user friendly).


# how to build

requirements:
- java 8+
- maven 3+

Run in the root of the project:
`mvn clean install -DskipTests`

The desired artefact will be built here: `./crypto-price-notifier-service/target/crypto-price-notifier-service-0.0.1-SNAPSHOT.jar`

# how to run

requirements:
- java 8+
- browser that supports websockets (optional, needed to use web-application)
 
Run the command: `java -jar crypto-price-notifier-service-0.0.1-SNAPSHOT.jar`

By default application will run on localhost 9090. You can supply custom port (e.g 9095) in the following way:
`java -jar crypto-price-notifier-service-0.0.1-SNAPSHOT.jar --server.port=9095`

# technology stack

Server side:
- Java 8
- Spring Boot
- Spring WebFlux, reactive streams (reactor)
- xchange library to fetch cryptocurrency stock (Bitfinex) trades
- JUnit 5

Client side:
- angular 7
- angular material
- RxJs

Communication:
- Rest API/http
- websockets

Tests:
test folder of crypto-price-notifier-service has the following tests:
 - AlertsWebSocketIntegrationTest.java - 1 integration test
 - AlertEndpointTest.java - 2 web flux tests
 - AlertServiceTest.java - unit tests for AlertService