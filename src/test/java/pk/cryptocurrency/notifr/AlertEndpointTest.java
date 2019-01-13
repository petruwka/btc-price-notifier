package pk.cryptocurrency.notifr;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import pk.cryptocurrency.notifr.config.AlertEndpointConfiguration;
import pk.cryptocurrency.notifr.domain.CurrencyPair;
import pk.cryptocurrency.notifr.domain.Threshold;
import pk.cryptocurrency.notifr.handler.AlertHandler;
import pk.cryptocurrency.notifr.service.ThresholdService;

import java.math.BigDecimal;


@WebFluxTest
@Import({AlertEndpointConfiguration.class, AlertHandler.class})
public class AlertEndpointTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private ThresholdService thresholdService;

    @Test
    public void testPutAlert() {
        client.put()
                .uri("/alert?pair=BTC-USD&limit=500")
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(thresholdService).defineThreshold(new Threshold(new CurrencyPair("BTC", "USD"), BigDecimal.valueOf(500)));
    }

    @Test
    public void testDeleteAlert() {
        client.delete()
                .uri("/alert?pair=BTC-USD&limit=500")
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(thresholdService).removeThreshold(new Threshold(new CurrencyPair("BTC", "USD"), BigDecimal.valueOf(500)));
    }
}
