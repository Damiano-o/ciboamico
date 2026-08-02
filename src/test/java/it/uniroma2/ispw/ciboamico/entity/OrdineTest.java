package it.uniroma2.ispw.ciboamico.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * T05/T06/T07 — Ordine: totale e transizioni di stato (BR-04).
 */
class OrdineTest {

    private Utente compratore() {
        Utente u = new Utente("Mario", "mario@cibo.it", "hash");
        u.aggiungiRuolo(new RuoloCliente());
        return u;
    }

    private Utente venditore() {
        Utente v = new Utente("Marco", "marco@cibo.it", "hash");
        RuoloVenditore rv = new RuoloVenditore("RM", "tel");
        rv.setStato(StatoVenditoreEnum.APPROVATO);
        v.aggiungiRuolo(rv);
        return v;
    }

    @Test
    void testCalcolaTotale() {
        Prodotto pane = new Prodotto("Pane", 2.50, 10, java.time.LocalDate.now().plusDays(5),
                UnitaEnum.PEZZI, venditore().getRuolo(RuoloVenditore.class));
        Prodotto uovo = new Prodotto("Uovo", 0.50, 10, java.time.LocalDate.now().plusDays(20),
                UnitaEnum.PEZZI, venditore().getRuolo(RuoloVenditore.class));

        Ordine ordine = new Ordine(1L, compratore(), venditore());
        ordine.aggiungiVoce(new VoceOrdine(pane, 1));
        ordine.aggiungiVoce(new VoceOrdine(uovo, 2));

        assertEquals(3.50, ordine.getTotale(), 1e-9);
    }

    @Test
    void testSetStatoValid() {
        Ordine ordine = new Ordine(1L, compratore(), venditore());
        ordine.cambiaStato(StatoOrdineEnum.CONFERMATO);
        assertEquals(StatoOrdineEnum.CONFERMATO, ordine.getStato());
    }

    @Test
    void testSetStatoInvalid() {
        Ordine ordine = new Ordine(1L, compratore(), venditore());
        // Sequenza valida fino a IN_CONSEGNA (BR-04)
        ordine.cambiaStato(StatoOrdineEnum.CONFERMATO);
        ordine.cambiaStato(StatoOrdineEnum.IN_CONSEGNA);
        // IN_CONSEGNA → ANNULLATO non è previsto (solo CONSEGNATO)
        assertThrows(IllegalStateException.class,
                () -> ordine.cambiaStato(StatoOrdineEnum.ANNULLATO));
    }

    @Test
    void testAutoAcquistoVietato() {
        Utente stesso = compratore(); // stesso utente compra il proprio prodotto
        assertThrows(IllegalArgumentException.class,
                () -> new Ordine(1L, stesso, stesso));
    }
}
