package pk.cryptocurrency.notifr;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitfinex.v2.BitfinexExchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;

public class XchangeFetchTest {

    @Test
    @Disabled
    public void testTicketFetch() throws IOException {
        Exchange exchange = ExchangeFactory.INSTANCE.createExchange(BitfinexExchange.class.getName());
        MarketDataService marketDataService = exchange.getMarketDataService();
        Ticker ticker = marketDataService.getTicker(new CurrencyPair("BTC", "USD"));
        System.out.println(ticker.toString());
//        System.out.println(marketDataService.getTrades(new CurrencyPair(Currency.BTC, Currency.USD), null, "asc"));
        System.out.println(marketDataService.getTrades(new CurrencyPair(Currency.BTC, Currency.USD)));
        Assertions.assertThat(ticker).isNotNull();
    }
}
