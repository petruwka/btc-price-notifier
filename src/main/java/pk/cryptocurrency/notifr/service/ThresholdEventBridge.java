package pk.cryptocurrency.notifr.service;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Processor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import pk.cryptocurrency.notifr.domain.Alert;
import pk.cryptocurrency.notifr.domain.Threshold;
import pk.cryptocurrency.notifr.domain.Trade;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ThresholdEventBridge extends BaseSubscriber<ThresholdEvent> implements InitializingBean {

    private final Map<Threshold, Disposable> subscriptions = new ConcurrentHashMap<>();

    private TradeService tradeService;
    private AlertService alertService;
    private Processor<ThresholdEvent, ThresholdEvent> thresholdsPublisher;

    public ThresholdEventBridge(TradeService tradeService,
                                AlertService alertService,
                                Processor<ThresholdEvent, ThresholdEvent> thresholdsPublisher) {
        this.tradeService = tradeService;
        this.alertService = alertService;
        this.thresholdsPublisher = thresholdsPublisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        thresholdsPublisher.subscribe(this);
    }

    @Override
    protected void hookOnNext(ThresholdEvent event) {
        Threshold threshold = event.getThreshold();
        if (event.isDeleted()) {
            Optional.ofNullable(subscriptions.remove(threshold)).ifPresent(Disposable::dispose);
            return;
        }

        subscriptions.computeIfAbsent(threshold, this::subscribeToTrades);
    }

    private Disposable subscribeToTrades(Threshold threshold) {
        Instant fromTimestamp = Instant.now();
        return tradeService.streamTrades(threshold.getPair())
                .filter(trade -> trade.getTimestamp().compareTo(fromTimestamp) >= 0)
                .filter(trade -> trade.getPrice().compareTo(threshold.getLimit()) >= 0)
                .take(1)
                .subscribe(
                        trade -> broadcastAlert(threshold, trade),
                        e -> log.warn("trades stream error", e),
                        () -> unsubscribe(threshold)
                );
    }


    private void unsubscribe(Threshold threshold) {
        log.info("Unsubscribed from threshold: {}", threshold);
        thresholdsPublisher.onNext(new ThresholdEvent(threshold, true));
    }

    private void broadcastAlert(Threshold threshold, Trade trade) {
        log.info("Send alert: {}", trade);
        alertService.publishAlert(new Alert(threshold, trade));
    }
}
