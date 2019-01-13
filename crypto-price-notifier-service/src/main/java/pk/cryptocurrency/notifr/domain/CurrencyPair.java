package pk.cryptocurrency.notifr.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@EqualsAndHashCode
@ToString
public class CurrencyPair {
    private final String base;
    private final String counter;

    public CurrencyPair(String base, String counter) {
        Objects.requireNonNull(base, () -> "base currency must not be null");
        Objects.requireNonNull(counter, () -> "counter currency must not be null");
        this.base = base.toUpperCase();
        this.counter = counter.toUpperCase();
    }
}
