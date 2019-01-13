package pk.cryptocurrency.notifr.service;

import org.springframework.stereotype.Service;
import pk.cryptocurrency.notifr.domain.Alert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@Service
public class AlertService {

    private UnicastProcessor<Alert> publisher = UnicastProcessor.create();
    private Flux<Alert> alerts = publisher.publish().autoConnect();

    public void sendAlert(Alert alert) {
        publisher.onNext(alert);
    }

    public Flux<Alert> streamAlerts() {
        return alerts;
    }
}
