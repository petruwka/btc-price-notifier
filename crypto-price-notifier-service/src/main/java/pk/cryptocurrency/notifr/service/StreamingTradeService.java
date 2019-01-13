package pk.cryptocurrency.notifr.service;

import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.BackpressureStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pk.cryptocurrency.notifr.domain.CurrencyPair;
import pk.cryptocurrency.notifr.domain.Trade;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Profile("streaming")
public class StreamingTradeService implements TradeService {

    private Map<CurrencyPair, Flux<Trade>> streams = new ConcurrentHashMap<>();

    private StreamingMarketDataService streamingMarketDataService;

    public StreamingTradeService(@Lazy StreamingMarketDataService streamingMarketDataService) {
        this.streamingMarketDataService = streamingMarketDataService;
    }

    @Override
    public Flux<Trade> streamTrades(CurrencyPair pair) {
        return streams.computeIfAbsent(pair, p -> {
            org.knowm.xchange.currency.CurrencyPair xchangePair = new org.knowm.xchange.currency.CurrencyPair(p.getBase(), p.getCounter());
            return RxJava2Adapter.observableToFlux(streamingMarketDataService.getTrades(xchangePair), BackpressureStrategy.BUFFER)
                    .map(this::convertTrade)
                    .doOnNext(t -> log.info("Trade received: {}", t))
                    .share();
        });

    }
}
