package pk.cryptocurrency.notifr.service;

import org.junit.jupiter.api.Test;
import pk.cryptocurrency.notifr.domain.Alert;
import pk.cryptocurrency.notifr.domain.CurrencyPair;
import pk.cryptocurrency.notifr.domain.Threshold;
import pk.cryptocurrency.notifr.domain.Trade;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;

class AlertServiceTest {

    @Test
    void publishAlertShouldPublishToPublisher() {
        DirectProcessor<Alert> publisher = DirectProcessor.create();
        Flux<Alert> sharedFlux = publisher.replay().autoConnect(0);

        AlertService alertService = new AlertService(publisher);

        alertService.publishAlert(firstAlert());
        alertService.publishAlert(secondAlert());

        publisher.onComplete();

        StepVerifier.create(sharedFlux)
                .expectNext(firstAlert())
                .expectNext(secondAlert())
                .verifyComplete();

    }

    @Test
    void streamAlertsShouldStreamFutureAlerts() {
        DirectProcessor<Alert> publisher = DirectProcessor.create();

        AlertService alertService = new AlertService(publisher);

        publisher.onNext(pastAlert());

        // get flux at this point (should not emit pastAlert
        Flux<Alert> subscribed = alertService.streamAlerts().replay().autoConnect(0);

        publisher.onNext(firstAlert());
        publisher.onNext(secondAlert());
        publisher.onComplete();

        StepVerifier.create(subscribed)
                .expectNext(firstAlert())
                .expectNext(secondAlert())
                .verifyComplete();

    }

    private Alert firstAlert() {
        return new Alert(new Threshold(new CurrencyPair("BTC", "USD"), BigDecimal.valueOf(500)), Trade.builder().price(BigDecimal.valueOf(1000)).timestamp(Instant.ofEpochMilli(1000)).build());
    }

    private Alert secondAlert() {
        return new Alert(new Threshold(new CurrencyPair("BTC", "EUR"), BigDecimal.valueOf(500)), Trade.builder().price(BigDecimal.valueOf(1000)).timestamp(Instant.ofEpochMilli(1000)).build());
    }

    private Alert pastAlert() {
        return new Alert(new Threshold(new CurrencyPair("BTC", "PLN"), BigDecimal.valueOf(500)), Trade.builder().price(BigDecimal.valueOf(1000)).timestamp(Instant.ofEpochMilli(1000)).build());
    }


}