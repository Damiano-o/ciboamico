package it.uniroma2.ispw.ciboamico.control;

// Author: Michele Damiano

import it.uniroma2.ispw.ciboamico.bean.AutenticazioneBean;
import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.bean.PaymentInfoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.facade.OrdinaProdottoFacade;
import it.uniroma2.ispw.ciboamico.exception.AutenticazioneException;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.pattern.factory.OrdineLazyFactory;
import it.uniroma2.ispw.ciboamico.pattern.strategy.ScontoPercentualeStrategy;
import it.uniroma2.ispw.ciboamico.persistence.factory.DemoDAOFactory;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Test dei controller applicativi e delle facade (UC-04, UC-11)

class ControllerTest {

    private DemoDAOFactory factory;
    private OrdinaProdottoController ordinaController;
    private ApplicaBuonoPromozionaleController buonoController;
    private PagamentoController pagamentoController;
    private OrdinaProdottoFacade facade;

    @BeforeEach
    void setup() throws Exception {
        factory = new DemoDAOFactory();
        factory.seedDemoData();
        OrdineLazyFactory.reset();
        OrdineLazyFactory.configure(factory);
        ordinaController = new OrdinaProdottoController(factory);
        buonoController = new ApplicaBuonoPromozionaleController(factory);
        pagamentoController = new PagamentoController(factory);
        facade = new OrdinaProdottoFacade(ordinaController, pagamentoController, buonoController);
    }

    @AfterEach
    void cleanup() {
        OrdineLazyFactory.reset();
    }

    private UtenteBean utenteCliente() throws Exception {
        UtenteBean b = new UtenteBean();
        b.setUsername("Mario");
        b.setEmail("mario@cibo.it");
        return b;
    }

    @Test
    void loginConCredenzialiValideRestituisceUtente() throws Exception {
        AutenticazioneController auth = new AutenticazioneController(factory);
        UtenteBean utente = auth.login(
                AutenticazioneBean.fromCredenziali("mario@cibo.it", "123"));
        assertNotNull(utente);
    }

    @Test
    void loginConCredenzialiErrateLancia() throws Exception {
        AutenticazioneController auth = new AutenticazioneController(factory);
        assertThrows(AutenticazioneException.class, () -> auth.login(
                AutenticazioneBean.fromCredenziali("mario@cibo.it", "sbagliata")));
    }

    @Test
    void checkoutAvviatoConTotaleProdotto() throws Exception {
        OrdineBean inCorso = ordinaController.avviaCheckout(OrdineBean.fromCheckout("Miele locale"));
        assertEquals(6.50, inCorso.getTotale(), 1e-9);
    }

    @Test
    void checkoutProdottoInesistenteLancia() throws Exception {
        assertThrows(BusinessValidationException.class,
                () -> ordinaController.avviaCheckout(OrdineBean.fromCheckout("Assente")));
    }

    @Test
    void applicaBuonoValidoScontaIlTotale() throws Exception {
        OrdineBean bean = OrdineBean.fromCheckout("Miele locale");
        OrdineBean ris = buonoController.applicaBuonoPromozionale("SALUTI20", bean, utenteCliente());
        assertEquals(5.20, ris.getTotale(), 1e-9);
    }

    @Test
    void applicaBuonoInesistenteLancia() throws Exception {
        OrdineBean bean = OrdineBean.fromCheckout("Miele locale");
        assertThrows(BusinessValidationException.class,
                () -> buonoController.applicaBuonoPromozionale("INESISTENTE", bean, utenteCliente()));
    }

    @Test
    void pagamentoAutorizzatoCreaOrdine() throws Exception {
        OrdineBean bean = OrdineBean.fromCheckout("Miele locale");
        PaymentInfoBean payment = PaymentInfoBean.fromCardData(
                "1111222233334444", "Mario", "12/29", "123", 6.50);
        OrdineBean esito = pagamentoController.processaPagamento(bean, utenteCliente(), payment);
        assertEquals("CREATED", esito.getStato());
    }

    @Test
    void pagamentoRifiutatoLancia() throws Exception {
        OrdineBean bean = OrdineBean.fromCheckout("Oro");
        PaymentInfoBean payment = PaymentInfoBean.fromCardData(
                "1111222233334444", "Mario", "12/29", "123", 600.0);
        assertThrows(BusinessValidationException.class,
                () -> pagamentoController.processaPagamento(bean, utenteCliente(), payment));
    }

    @Test
    void facadeEsponeCatalogo() throws Exception {
        assertFalse(facade.getProdottiDisponibili().isEmpty());
    }

    @Test
    void facadeAvviaCheckoutESalvaInSessione() throws Exception {
        facade.avviaCheckout(OrdineBean.fromCheckout("Miele locale"));
        assertNotNull(it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager
                .getInstance().getOrdineInCorso());
    }
}
