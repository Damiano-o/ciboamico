package it.uniroma2.ispw.ciboamico.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * T08/T09 — Prodotto: validazioni prezzo (BR-06) e quantità (BR-03).
 */
class ProdottoTest {

    private RuoloVenditore venditoreApprovato() {
        RuoloVenditore v = new RuoloVenditore("RM", "tel");
        v.setStato(StatoVenditoreEnum.APPROVATO);
        return v;
    }

    @Test
    void testValidaPrezzo() {
        LocalDate scadenza = LocalDate.now().plusDays(5);
        RuoloVenditore venditore = venditoreApprovato();
        assertThrows(IllegalArgumentException.class,
                () -> new Prodotto("Pane", -1.50, 10, scadenza, UnitaEnum.PEZZI, venditore));
    }

    @Test
    void testValidaQuantita() {
        LocalDate scadenza = LocalDate.now().plusDays(5);
        RuoloVenditore venditore = venditoreApprovato();
        assertThrows(IllegalArgumentException.class,
                () -> new Prodotto("Pane", 2.0, -5, scadenza, UnitaEnum.PEZZI, venditore));
    }

    @Test
    void testValidaScadenzaPassata() {
        RuoloVenditore venditore = venditoreApprovato();
        assertThrows(IllegalArgumentException.class,
                () -> new Prodotto("Pane", 2.0, 5, LocalDate.now().minusDays(1),
                        UnitaEnum.PEZZI, venditore));
    }

    @Test
    void testRiduciDisponibilita() {
        Prodotto p = new Prodotto("Pane", 2.0, 5, LocalDate.now().plusDays(5),
                UnitaEnum.PEZZI, venditoreApprovato());
        assertTrue(p.riduciDisponibilita(3));
        assertEquals(2, p.getQuantitaDisponibile());
        assertFalse(p.riduciDisponibilita(10));
    }
}
