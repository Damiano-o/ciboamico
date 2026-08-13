package it.uniroma2.ispw.ciboamico.pattern;

// Author: Michele Damiano

import it.uniroma2.ispw.ciboamico.pattern.observer.OrdineEvent;
import it.uniroma2.ispw.ciboamico.pattern.observer.OrdineEventPublisher;
import it.uniroma2.ispw.ciboamico.exception.PaymentRejectedException;
import it.uniroma2.ispw.ciboamico.pattern.payment.PaymentGateway;
import it.uniroma2.ispw.ciboamico.pattern.payment.PaymentGatewayFactory;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.pattern.strategy.ScontoImportoFissoStrategy;
import it.uniroma2.ispw.ciboamico.pattern.strategy.ScontoPercentualeStrategy;
import it.uniroma2.ispw.ciboamico.pattern.strategy.ScontoStrategyFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Test dei pattern: Observer, Singleton, Strategy, Payment gateway

class PatternTest {

    @Test
    void observerNotificaListenerRegistrato() {
        OrdineEventPublisher publisher = OrdineEventPublisher.getInstance();
        publisher.clearListeners();
        final int[] count = {0};
        publisher.addListener(e -> count[0]++);
        publisher.notifyOrdineConfermato(new OrdineEvent(1L, "mario@cibo.it", "marco@cibo.it", 5.5));
        assertEquals(1, count[0]);
        publisher.clearListeners();
    }

    @Test
    void observerDTOConValoriCorretti() {
        OrdineEvent event = new OrdineEvent(5L, "mario@cibo.it", "marco@cibo.it", 4.5);
        assertEquals("mario@cibo.it", event.getClienteId());
    }

    @Test
    void sessionManagerSingletonStessaIstanza() {
        assertSame(SessionManager.getInstance(), SessionManager.getInstance());
    }

    @Test
    void scontoPercentualeApplicaValore() {
        assertEquals(8.0, new ScontoPercentualeStrategy(0.20).applicaSconto(10.0), 1e-9);
    }

    @Test
    void scontoImportoFissoMaiNegativo() {
        assertEquals(0.0, new ScontoImportoFissoStrategy(50.0).applicaSconto(10.0), 1e-9);
    }

    @Test
    void scontoStrategyFactoryCreaPercentuale() {
        assertInstanceOf(ScontoPercentualeStrategy.class,
                ScontoStrategyFactory.createStrategy(ScontoStrategyFactory.TIPO_PERCENTUALE, 0.1));
    }

    @Test
    void paymentGatewayAutorizzaSottoSoglia() throws Exception {
        PaymentGateway gateway = PaymentGatewayFactory.createGateway();
        assertTrue(gateway.autorizza(499_00L));
    }

    @Test
    void paymentGatewayRifiutaOltreSoglia() {
        PaymentGateway gateway = PaymentGatewayFactory.createGateway();
        assertThrows(PaymentRejectedException.class, () -> gateway.autorizza(501_00L));
    }
}
