package pk.cryptocurrency.notifr.config;

import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitfinex.v2.BitfinexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
public class XchangeConfig {

    @Bean
    @Profile("polling")
    public MarketDataService marketDataService() {
        Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BitfinexExchange.class.getName());
        return exchange.getMarketDataService();
    }

    @Bean
    @Profile("streaming")
    public StreamingExchange streamingExchange() {
        log.info("initializing StreamingExchange");
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(BitfinexStreamingExchange.class.getName());
        exchange.connect().blockingAwait();
        return exchange;
    }

    @Bean
    @Profile("streaming")
    public StreamingMarketDataService streamingMarketDataService(StreamingExchange streamingExchange) {
        return streamingExchange.getStreamingMarketDataService();
    }
}
