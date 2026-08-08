package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.bean.PaymentInfoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.entity.*;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.pattern.factory.OrdineLazyFactory;
import it.uniroma2.ispw.ciboamico.persistence.factory.DemoDAOFactory;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test UC-04 OrdinaProdottoController: verifica che il VENDITORE sia risolto
 * dal prodotto (non dall'utente loggato) — regressione del bug fix 2026-08-02.
 
 * @author Michele Damiano
*/
class OrdinaProdottoControllerTest {

    private DemoDAOFactory factory;
    private OrdinaProdottoController controller;

    @BeforeEach
    void setup() {
        factory = new DemoDAOFactory();
        OrdineLazyFactory.reset();
        OrdineLazyFactory.configure(factory);
        controller = new OrdinaProdottoController(factory);
        // Utente loggato = compratore
        UtenteBean bean = new UtenteBean();
        bean.setUsername("Mario");
        bean.setEmail("mario@cibo.it");
        SessionManager.getInstance().setLoggedUser(bean);
    }

    @AfterEach
    void cleanup() {
        OrdineLazyFactory.reset();
        SessionManager.getInstance().logout();
    }

    private UtenteBean utenteBean() {
        UtenteBean b = new UtenteBean();
        b.setUsername("Mario");
        b.setEmail("mario@cibo.it");
        return b;
    }

    @Test
    void testSubmitOrdineUtenteNull() throws Exception {
        OrdineBean bean = new OrdineBean();
        assertThrows(IllegalStateException.class,
                () -> controller.submitOrdine(bean, null));
    }

    @Test
    void testSubmitOrdineProdottoNonTrovato() throws Exception {
        OrdineBean bean = new OrdineBean();
        bean.setNomeProdotto("ProdottoInesistente");
        assertThrows(IllegalStateException.class, () -> controller.submitOrdine(bean, utenteBean()));
    }

    @Test
    void testSubmitOrdineVenditoreDalProdotto() throws Exception {
        // Venditore con back-reference all'Utente
        Utente utenteVenditore = new Utente("Marco", "marco@cibo.it", "h");
        RuoloVenditore rv = new RuoloVenditore("RM", "tel");
        utenteVenditore.aggiungiRuolo(rv); // setta back-reference

        Prodotto prodotto = new Prodotto("Pomodori", 2.0, 50,
                LocalDate.now().plusDays(7), UnitaEnum.GRAMMI, rv);
        factory.getProdottoDAO().save(prodotto);

        OrdineBean bean = new OrdineBean();
        bean.setNomeProdotto("Pomodori");
        bean.setCompratoreId("mario@cibo.it");

        OrdineBean risultato = controller.submitOrdine(bean, utenteBean());

        assertNotNull(risultato);
        assertNotNull(risultato.getIdOrdine());
        assertEquals("CREATED", risultato.getStato());
        assertEquals(2.0, risultato.getTotale(), 1e-9);}

    @Test
    void testSubmitOrdineRiduceDisponibilita() throws Exception {
        Utente utenteVenditore = new Utente("Marco", "marco@cibo.it", "h");
        RuoloVenditore rv = new RuoloVenditore("RM", "tel");
        utenteVenditore.aggiungiRuolo(rv);

        Prodotto prodotto = new Prodotto("Mele", 1.5, 3,
                LocalDate.now().plusDays(7), UnitaEnum.PEZZI, rv);
        factory.getProdottoDAO().save(prodotto);

        OrdineBean bean = new OrdineBean();
        bean.setNomeProdotto("Mele");
        bean.setCompratoreId("mario@cibo.it");

        controller.submitOrdine(bean, utenteBean());
        assertEquals(2, prodotto.getQuantitaDisponibile());
    }

    @Test
    void testAcquistoQuantitaEccessivaLanciaEccezione() throws Exception {
        Utente utenteVenditore = new Utente("Marco", "marco@cibo.it", "h");
        RuoloVenditore rv = new RuoloVenditore("RM", "tel");
        utenteVenditore.aggiungiRuolo(rv);

        // Un solo pezzo disponibile
        Prodotto prodotto = new Prodotto("Uova", 3.0, 1,
                LocalDate.now().plusDays(7), UnitaEnum.PEZZI, rv);
        factory.getProdottoDAO().save(prodotto);

        // Prima acquisto: consuma l'unico pezzo disponibile
        OrdineBean bean = new OrdineBean();
        bean.setNomeProdotto("Uova");
        bean.setCompratoreId("mario@cibo.it");
        controller.submitOrdine(bean, utenteBean());

        // Secondo acquisto: quantità non più disponibile (estensione 2a)
        OrdineBean bean2 = new OrdineBean();
        bean2.setNomeProdotto("Uova");
        bean2.setCompratoreId("mario@cibo.it");
        assertThrows(BusinessValidationException.class,
                () -> controller.submitOrdine(bean2, utenteBean()));
    }

    @Test
    void testProcessaPagamentoSuccessoCreaOrdine() throws Exception {
        Utente utenteVenditore = new Utente("Marco", "marco@cibo.it", "h");
        RuoloVenditore rv = new RuoloVenditore("RM", "tel");
        utenteVenditore.aggiungiRuolo(rv);
        Prodotto prodotto = new Prodotto("Caffè", 4.50, 10,
                LocalDate.now().plusDays(7), UnitaEnum.PEZZI, rv);
        factory.getProdottoDAO().save(prodotto);

        OrdineBean bean = new OrdineBean();
        bean.setNomeProdotto("Caffè");
        PaymentInfoBean payment = new PaymentInfoBean();
        payment.setNumeroCarta("1111222233334444");
        payment.setCvv("123");
        payment.setImportoInCent(450L); // sotto la soglia dello stub (500 EUR)

        OrdineBean risultato = controller.processaPagamento(bean, utenteBean(), payment);
        assertNotNull(risultato.getIdOrdine());
        assertEquals("CREATED", risultato.getStato());
    }

    @Test
    void testProcessaPagamentoRifiutatoLanciaBusinessValidation() throws Exception {
        Utente utenteVenditore = new Utente("Marco", "marco@cibo.it", "h");
        RuoloVenditore rv = new RuoloVenditore("RM", "tel");
        utenteVenditore.aggiungiRuolo(rv);
        // Prezzo alto: 600 EUR > soglia stub 500 EUR -> estensione 6a
        Prodotto prodotto = new Prodotto("Oro", 600.0, 10,
                LocalDate.now().plusDays(7), UnitaEnum.PEZZI, rv);
        factory.getProdottoDAO().save(prodotto);

        OrdineBean bean = new OrdineBean();
        bean.setNomeProdotto("Oro");
        PaymentInfoBean payment = new PaymentInfoBean();
        payment.setNumeroCarta("1111222233334444");
        payment.setCvv("123");
        payment.setImportoInCent(60_000L); // 600 EUR > soglia

        assertThrows(BusinessValidationException.class,
                () -> controller.processaPagamento(bean, utenteBean(), payment));
    }
}
