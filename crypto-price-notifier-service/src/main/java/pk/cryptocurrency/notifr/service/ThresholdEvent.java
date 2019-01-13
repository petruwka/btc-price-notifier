package pk.cryptocurrency.notifr.service;

import lombok.Getter;
import pk.cryptocurrency.notifr.domain.Threshold;

@Getter
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