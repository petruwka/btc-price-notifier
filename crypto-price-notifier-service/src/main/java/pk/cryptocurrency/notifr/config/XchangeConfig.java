package pk.cryptocurrency.notifr.config;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitfinex.v2.BitfinexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XchangeConfig {

    @Bean
    public MarketDataService marketDataService() {
        Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BitfinexExchange.class.getName());
        return exchange.getMarketDataService();
    }

}
