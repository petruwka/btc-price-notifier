package pk.cryptocurrency.notifr.service;

import pk.cryptocurrency.notifr.domain.CurrencyPair;
import pk.cryptocurrency.notifr.domain.Trade;
import reactor.core.publisher.Flux;

public interface TradeService {

    Flux<Trade> streamTrades(CurrencyPair pair);

    default Trade convertTrade(org.knowm.xchange.dto.marketdata.Trade trade) {
        return Trade.builder()
                .tradeId(Long.parseLong(trade.getId()))
                .price(trade.getPrice())
                .timestamp(trade.getTimestamp().toInstant())
                .build();
    }

}