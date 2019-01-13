package pk.cryptocurrency.notifr.service;

import org.springframework.stereotype.Service;
import pk.cryptocurrency.notifr.domain.Alert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;

@Service
public class AlertService {

    private FluxProcessor<Alert, Alert> publisher;
    private Flux<Alert> alerts;

    public AlertService(FluxProcessor<Alert, Alert> alertsPublisher) {
        this.publisher = alertsPublisher;
        this.alerts = publisher.publish().autoConnect();
    }

    public void publishAlert(Alert alert) {
        publisher.onNext(alert);
    }

    public Flux<Alert> streamAlerts() {
        return alerts;
    }
}
