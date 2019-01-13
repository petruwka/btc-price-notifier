package pk.cryptocurrency.notifr.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class CurrencyPair {
    private final String base;
    private final String counter;

    public CurrencyPair(String base, String counter) {
        this.base = base;
        this.counter = counter;
    }
}
