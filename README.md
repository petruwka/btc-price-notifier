# crypto-price-notifier

crypto-price-notifier provides the following functionality. User can:
- define a threshold for an alert when cryptocurrency price goes above the defined limit (PUT /alert?pair=xxx-xxx&limit=100)
- see alerts for the defined thresholds (websockets /alerts)
- view defined thresholds (websockets /thresholds)
- delete an alert threshold (DELETE /alert?pair=xxx-xxx&limit=100)

User is notified about the threshold only once. It means that when the alert happened for some threshold, user is notified, and this threshold is cancelled.
User can define as many alert thresholds as he wishes, but he cannot setup the same thresholds (when pair and limit are the same). 
He also can cancel his alert (button 'Cancel' near the threshold in the list of My Thresholds').
The application fetches trades from Bitfines cryptocurrency exchange (with the help of xchange library - https://github.com/knowm/XChange, and its streaming enhancement - https://github.com/bitrich-info/xchange-stream ). 

The application comes with web UI (simple one but still user friendly).

# validation/error handling
- currency pair has to be in format `base-counter` (e.g. BTC-USD)
- no validation that currency is valid or exists
- user won't be notified if error occurs
- in case if 'polling' profile active (see below), if remote exchange is unavailable, system will try to fetch indefinitely (or until user cancels the threshold) with delay >=10s (.retryBackoff(Long.MAX_VALUE, Duration.ofSeconds(10)))

# polling vs streaming spring profiles
By default, 'streaming' spring profile is active. It uses StremingMarketDataService (from xchange-stream library). 
But the application can be started with 'polling' profile (--spring.profiles.active=polling) to use trade service which will use polling of Bitfinex exchange.
'polling' mechanism is considered outdated.

# how to build

requirements:
- java 8+
- maven 3+

Run in the root of the project:
`mvn clean install`

or skipping tests:
`mvn clean install -DskipTests`

The desired artifact will be built here: `./crypto-price-notifier-service/target/crypto-price-notifier-service-0.0.1-SNAPSHOT.jar`

# how to run

requirements:
- java 8+
- browser that supports websockets (optional, needed to use web-application)
 
Run the command from folder `./crypto-price-notifier-service/target/`: 

`java -jar crypto-price-notifier-service-0.0.1-SNAPSHOT.jar`

Open browser: http://localhost:9090/

By default application will run on localhost 9090. You can supply custom port (e.g 9095) in the following way:
`java -jar crypto-price-notifier-service-0.0.1-SNAPSHOT.jar --server.port=9095`

# technology stack

Server side:
- Java 8
- Spring Boot
- Spring WebFlux, reactive streams (reactor)
- knowm/XChange library to fetch cryptocurrency stock (Bitfinex) trades
- JUnit 5

Client side:
- angular 7
- angular material
- rxjs

Communication:
- Rest API/http
- websockets

Tests:
test folder of crypto-price-notifier-service has the following tests:
 - AlertsWebSocketIntegrationTest.java - 1 integration test
 - AlertEndpointTest.java - 2 web flux tests
 - AlertServiceTest.java - unit tests for AlertService
 - ThresholdServiceTest.java - unit tests for ThresholdService
 - class ThresholdEventBridge was not covered by unit tests