package it.uniroma2.ispw.ciboamico.entity;

// Author: Michele Damiano

import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.exception.InvalidStateTransitionException;
import it.uniroma2.ispw.ciboamico.pattern.strategy.ScontoPercentualeStrategy;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Test di dominio: Ordine, Prodotto, Utente, BuonoPromozionale, RuoloVenditore

class EntityTest {

    private Utente compratore() {
        Utente u = new Utente("Mario", "mario@cibo.it", "hash");
        u.aggiungiRuolo(new RuoloCliente());
        return u;
    }

    private Utente venditore() {
        Utente v = new Utente("Marco", "marco@cibo.it", "hash");
        v.aggiungiRuolo(new RuoloVenditore("RM", "tel"));
        return v;
    }

    private RuoloVenditore venditoreApprovato() {
        RuoloVenditore v = new RuoloVenditore("RM", "tel");
        v.setStato(StatoVenditoreEnum.APPROVATO);
        return v;
    }

    @Test
    void ordineCalcolaTotaleDelleVoci() throws Exception {
        Prodotto pane = new Prodotto("Pane", 2.50, 10, LocalDate.now().plusDays(5),
                UnitaEnum.PEZZI, venditore().getRuolo(RuoloVenditore.class));
        Prodotto uovo = new Prodotto("Uovo", 0.50, 10, LocalDate.now().plusDays(20),
                UnitaEnum.PEZZI, venditore().getRuolo(RuoloVenditore.class));
        Ordine ordine = new Ordine(1L, compratore(), venditore());
        ordine.aggiungiVoce(new VoceOrdine(pane, 1));
        ordine.aggiungiVoce(new VoceOrdine(uovo, 2));
        assertEquals(3.50, ordine.getTotale(), 1e-9);
    }

    @Test
    void ordineRifiutaAutoacquisto() {
        Utente stesso = compratore();
        assertThrows(BusinessValidationException.class, () -> new Ordine(1L, stesso, stesso));
    }

    @Test
    void ordineRifiutaTransizioneNonValida() throws Exception {
        Ordine ordine = new Ordine(1L, compratore(), venditore());
        ordine.cambiaStato(StatoOrdineEnum.CONFIRMED);
        ordine.cambiaStato(StatoOrdineEnum.IN_DELIVERY);
        assertThrows(InvalidStateTransitionException.class,
                () -> ordine.cambiaStato(StatoOrdineEnum.ANNULLED));
    }

    @Test
    void prodottoRifiutaPrezzoNonPositivo() {
        assertThrows(BusinessValidationException.class, () -> new Prodotto("Pane", -1.0, 10,
                LocalDate.now().plusDays(5), UnitaEnum.PEZZI, venditoreApprovato()));
    }

    @Test
    void prodottoRifiutaScadenzaPassata() {
        assertThrows(BusinessValidationException.class, () -> new Prodotto("Pane", 2.0, 10,
                LocalDate.now().minusDays(1), UnitaEnum.PEZZI, venditoreApprovato()));
    }

    @Test
    void prodottoRiduceDisponibilita() throws Exception {
        Prodotto p = new Prodotto("Pane", 2.0, 5, LocalDate.now().plusDays(5),
                UnitaEnum.PEZZI, venditoreApprovato());
        p.riduciDisponibilita(3);
        assertEquals(2, p.getQuantitaDisponibile());
    }

    @Test
    void buonoApplicaScontoPercentuale() throws Exception {
        RuoloVenditore rv = venditoreApprovato();
        BuonoPromozionale buono = new BuonoPromozionale("SCONTO20", rv,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(10),
                new ScontoPercentualeStrategy(0.20));
        assertEquals(0.8, buono.applicaSconto(1.0), 0.001);
    }

    @Test
    void buonoNonValidoFuoriFinestra() throws Exception {
        RuoloVenditore rv = venditoreApprovato();
        BuonoPromozionale buono = new BuonoPromozionale("SCADUTO", rv,
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(1),
                new ScontoPercentualeStrategy(0.20));
        assertFalse(buono.isValido());
    }

    @Test
    void utenteRegistraBuonoMonouso() {
        Utente u = compratore();
        u.registraBuonoUtilizzato("X1");
        assertTrue(u.haUsatoBuono("X1"));
    }

    @Test
    void ruoloVenditoreApprovato() {
        RuoloVenditore v = new RuoloVenditore("RM", "tel");
        v.approva();
        assertEquals(StatoVenditoreEnum.APPROVATO, v.getStato());
    }
}
