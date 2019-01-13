package pk.cryptocurrency.notifr.service;

import org.reactivestreams.Processor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import pk.cryptocurrency.notifr.domain.Threshold;
import reactor.core.publisher.BaseSubscriber;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class ThresholdService extends BaseSubscriber<ThresholdEvent> implements DisposableBean {

    private final Set<Threshold> thresholds = new CopyOnWriteArraySet<>();
    private Processor<ThresholdEvent, ThresholdEvent> thresholdsPublisher;

    public ThresholdService(Processor<ThresholdEvent, ThresholdEvent> thresholdsPublisher) {
        this.thresholdsPublisher = thresholdsPublisher;
        this.thresholdsPublisher.subscribe(this);
    }

    public void defineThreshold(Threshold threshold) {
        thresholdsPublisher.onNext(new ThresholdEvent(threshold));
    }

    public void removeThreshold(Threshold threshold) {
        thresholdsPublisher.onNext(new ThresholdEvent(threshold, true));
    }

    @Override
    protected void hookOnNext(ThresholdEvent value) {
        if (!value.isDeleted()) {
            thresholds.add(value.getThreshold());
        } else {
            thresholds.remove(value.getThreshold());
        }
    }

    @Override
    public void destroy() throws Exception {
        cancel();
    }
}
