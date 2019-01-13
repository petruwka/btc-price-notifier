package pk.cryptocurrency.notifr.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
@ToString
public class Threshold {
    private CurrencyPair pair;
    private BigDecimal limit;

    public Threshold(CurrencyPair pair, BigDecimal limit) {
        this.pair = pair;
        this.limit = limit;
    }
}
