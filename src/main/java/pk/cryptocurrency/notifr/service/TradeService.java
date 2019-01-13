package pk.cryptocurrency.notifr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pk.cryptocurrency.notifr.domain.CurrencyPair;
import pk.cryptocurrency.notifr.domain.Trade;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
public class TradeService {


    public Flux<Trade> streamTrades(CurrencyPair pair) {
        // TODO: call external service
        return Flux.just(Trade.builder().price(BigDecimal.valueOf(500)).timestamp(Instant.now()).build());
    }

}
