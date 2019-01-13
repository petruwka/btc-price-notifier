package pk.cryptocurrency.notifr;

import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.CurrencyPair;

@Slf4j
public class XchangeStreamFetchTest {

    @Test
    @Disabled
    public void testStreamingXchange() throws Exception {
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(BitfinexStreamingExchange.class.getName());

        // Connect to the Exchange WebSocket API. Blocking wait for the connection.
        exchange.connect().blockingAwait();

        // Subscribe to live trades update.
        exchange.getStreamingMarketDataService()
                .getTrades(CurrencyPair.BTC_USD)
                .subscribe(trade -> {
                    log.info("Incoming trade: {}", trade);
                }, throwable -> {
                    log.error("Error in subscribing trades.", throwable);
                });


        Thread.sleep(120_000);
    }
}

