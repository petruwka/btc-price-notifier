package pk.cryptocurrency.notifr.service;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Service;
import pk.cryptocurrency.notifr.domain.CurrencyPair;
import pk.cryptocurrency.notifr.domain.Trade;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TradeService {
    private MarketDataService marketDataService;

    public TradeService(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    public Flux<Trade> streamTrades(CurrencyPair pair) {
        return Flux.create(new TradePollingGenerator(pair));
    }

    private class TradePollingGenerator implements Consumer<FluxSink<Trade>> {
        private CurrencyPair pair;
        private long fromTimestamp;
        private int pollinIntervalSeconds = 5;

        public TradePollingGenerator(CurrencyPair pair) {
            this.pair = pair;
            this.fromTimestamp = Instant.now().toEpochMilli();
        }

        @Override
        public void accept(FluxSink<Trade> tFluxSink) {
            Flux.generate(() -> fromTimestamp, this::fetchTrades)
                    .retryBackoff(10, Duration.ofSeconds(10))
                    .subscribe(new BaseSubscriber<List<Trade>>() {
                        @Override
                        protected void hookOnSubscribe(Subscription subscription) {
                            subscription.request(1);
                            tFluxSink.onDispose(this);
                        }

                        @Override
                        protected void hookOnNext(List<Trade> value) {
                            value.forEach(tFluxSink::next);
                            Mono.delay(Duration.ofSeconds(pollinIntervalSeconds)).subscribe((Long v) -> request(1));
                        }

                        @Override
                        protected void hookOnCancel() {
                            log.info("Unsubscribed from polling trades: {}", pair);
                        }
                    });
        }

        private long fetchTrades(Long fromTimestamp, SynchronousSink<List<Trade>> sink) {
            try {
                int limit = 1000;
                org.knowm.xchange.currency.CurrencyPair currencyPair = new org.knowm.xchange.currency.CurrencyPair(pair.getBase(), pair.getCounter());
                log.debug("Fetching trades: {} {} {}", pair, limit, fromTimestamp);
                Trades trades = marketDataService.getTrades(currencyPair, limit, fromTimestamp);
                List<Trade> tradesList = trades.getTrades().stream().map(this::convertTrade).collect(Collectors.toList());
                sink.next(tradesList);
                return tradesList.stream()
                        .max(Comparator.comparing(Trade::getTimestamp))
                        .map(Trade::getTimestamp)
                        .map(Instant::toEpochMilli)
                        .orElse(fromTimestamp);
            } catch (IOException e) {
                throw new RuntimeException("Can't fetch trades", e);
            }
        }

        private Trade convertTrade(org.knowm.xchange.dto.marketdata.Trade trade) {
            return Trade.builder()
                    .tradeId(Long.parseLong(trade.getId()))
                    .price(trade.getPrice())
                    .timestamp(trade.getTimestamp().toInstant())
                    .build();
        }

    }

}