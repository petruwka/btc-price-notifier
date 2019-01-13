package pk.cryptocurrency.notifr.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pk.cryptocurrency.notifr.domain.CurrencyPair;
import pk.cryptocurrency.notifr.domain.Threshold;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Comparator;

class ThresholdServiceTest {
    private DirectProcessor<ThresholdEvent> publisher;
    private Flux<ThresholdEvent> replayPublisher;
    private ThresholdService service;

    @BeforeEach
    public void initService() {
        publisher = DirectProcessor.create();
        replayPublisher = publisher.replay().autoConnect(0);
        service = new ThresholdService(publisher);
    }

    @Test
    void defineThresholdShouldPublishEventToPublisher() {

        service.defineThreshold(threshold("BTC", "USD", 500));

        StepVerifier.create(replayPublisher.take(1))
                .expectNext(new ThresholdEvent(threshold("BTC", "USD", 500), false))
                .verifyComplete();

    }

    @Test
    void removeThresholdShouldPublishThresholdEventWithDeletedFlag() {
        service.removeThreshold(threshold("BTC", "USD", 500));

        StepVerifier.create(replayPublisher.take(1))
                .expectNext(new ThresholdEvent(threshold("BTC", "USD", 500), true))
                .verifyComplete();

    }

    @Test
    void streamThresholdsShouldStreamThresholdEventsFromDefineAndRemove() {
        Flux<ThresholdEvent> thresholds = service.streamThresholds().replay().autoConnect(0);

        service.defineThreshold(threshold("BTC", "USD", 500));
        service.defineThreshold(threshold("BTC", "USD", 501));

        service.removeThreshold(threshold("BTC", "USD", 500));
        service.defineThreshold(threshold("BTC", "USD", 502));

        StepVerifier.create(thresholds.take(4).doOnNext(System.out::println))
                .expectNext(new ThresholdEvent(threshold("BTC", "USD", 500)))
                .expectNext(new ThresholdEvent(threshold("BTC", "USD", 501)))
                .expectNext(new ThresholdEvent(threshold("BTC", "USD", 500), true))
                .expectNext(new ThresholdEvent(threshold("BTC", "USD", 502)))
                .verifyComplete();
    }

    @Test
    void streamThresholdsShouldStreamPreviouslyDefinedActiveThresholds() {

        service.defineThreshold(threshold("BTC", "USD", 500));
        service.defineThreshold(threshold("BTC", "USD", 501));
        service.defineThreshold(threshold("BTC", "USD", 502));

        service.removeThreshold(threshold("BTC", "USD", 500));

        Flux<ThresholdEvent> thresholds = service.streamThresholds();

        StepVerifier.create(thresholds
                .take(Duration.ofSeconds(1))
                .sort(Comparator.comparing(ThresholdEvent::getThreshold, Comparator.comparing(Threshold::getLimit)))
        )
                .expectNext(new ThresholdEvent(threshold("BTC", "USD", 501)))
                .expectNext(new ThresholdEvent(threshold("BTC", "USD", 502)))
                .verifyComplete();
    }

    private Threshold threshold(String base, String counter, int limit) {
        return new Threshold(new CurrencyPair(base, counter), BigDecimal.valueOf(limit));
    }
}