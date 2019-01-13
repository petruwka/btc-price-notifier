package pk.cryptocurrency.notifr.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@ToString
@EqualsAndHashCode
@Builder
public class Trade {
    private long tradeId;
    private Instant timestamp;
    private BigDecimal price;
}