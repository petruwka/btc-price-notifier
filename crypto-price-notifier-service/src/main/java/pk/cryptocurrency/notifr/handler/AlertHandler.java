package pk.cryptocurrency.notifr.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pk.cryptocurrency.notifr.domain.CurrencyPair;
import pk.cryptocurrency.notifr.domain.Threshold;
import pk.cryptocurrency.notifr.service.ThresholdService;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Supplier;

@Component
public class AlertHandler {

    private ThresholdService thresholdService;

    @Autowired
    public AlertHandler(ThresholdService thresholdService) {
        this.thresholdService = thresholdService;
    }

    public Mono<ServerResponse> defineAlert(ServerRequest serverRequest) {
        CurrencyPair pair = currencyPair(serverRequest);
        BigDecimal priceLimit = limit(serverRequest);
        thresholdService.defineThreshold(new Threshold(pair, priceLimit));
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> removeAlert(ServerRequest serverRequest) {
        CurrencyPair pair = currencyPair(serverRequest);
        BigDecimal priceLimit = limit(serverRequest);
        thresholdService.removeThreshold(new Threshold(pair, priceLimit));
        return ServerResponse.ok().build();
    }

    private BigDecimal limit(ServerRequest serverRequest) {
        String limit = serverRequest.queryParam("limit").orElseThrow(paramRequiredException("limit"));
        return new BigDecimal(limit);
    }

    private CurrencyPair currencyPair(ServerRequest serverRequest) {
        String pair = serverRequest.queryParam("pair").orElseThrow(paramRequiredException("pair"));
        String[] split = pair.split("-");
        if (split.length != 2) {
            throw new IllegalArgumentException("Parameter 'pair' must have format XXX-XXX");
        }
        return new CurrencyPair(split[0], split[1]);
    }

    private Supplier<IllegalArgumentException> paramRequiredException(String param) {
        return () -> new IllegalArgumentException(String.format("Request param '%s' is required", param));
    }
}
