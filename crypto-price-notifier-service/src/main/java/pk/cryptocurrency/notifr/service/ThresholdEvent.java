package pk.cryptocurrency.notifr.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pk.cryptocurrency.notifr.domain.Threshold;

@Getter
@EqualsAndHashCode
@ToString
public class ThresholdEvent {
    private final Threshold threshold;
    private final boolean deleted;

    public ThresholdEvent(Threshold threshold) {
        this(threshold, false);
    }

    public ThresholdEvent(Threshold threshold, boolean deleted) {
        this.threshold = threshold;
        this.deleted = deleted;
    }
}