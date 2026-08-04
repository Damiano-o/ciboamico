package it.uniroma2.ispw.ciboamico.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * T16 — ProdottoInventario: scadenza entro 3 giorni (FR-03) e ordinamento (FR-02).
 
 * @author Michele Damiano
*/
class ProdottoInventarioTest {

    private ProdottoInventario prodotto(String nome, LocalDate scadenza) {
        return new ProdottoInventario(nome, 2, scadenza, "Frigo",
                UnitaEnum.PEZZI, null);
    }

    @Test
    void testVerificaScadenzaEntroTreGiorni() throws Exception {
        ProdottoInventario p = prodotto("Latte", LocalDate.now().plusDays(2));
        assertTrue(p.inScadenza());
    }

    @Test
    void testScadenzaLontana() throws Exception {
        ProdottoInventario p = prodotto("Pasta", LocalDate.now().plusDays(200));
        assertFalse(p.inScadenza());
    }

    @Test
    void testOrdinamentoPerScadenza() throws Exception {
        List<ProdottoInventario> lista = List.of(
                prodotto("Pasta", LocalDate.now().plusDays(200)),
                prodotto("Latte", LocalDate.now().plusDays(2)));
        List<ProdottoInventario> ordinata = lista.stream().sorted().toList();
        assertEquals("Latte", ordinata.get(0).getNome());
    }

    @Test
    void testQuantitaNonValida() throws Exception {
        LocalDate scadenza = LocalDate.now().plusDays(5);
        assertThrows(IllegalArgumentException.class,
                () -> new ProdottoInventario("Latte", 0, scadenza,
                        "Frigo", UnitaEnum.PEZZI, null));
    }

    @Test
    void testEqualsStessoContenuto() throws Exception {
        LocalDate scadenza = LocalDate.now().plusDays(5);
        ProdottoInventario a = new ProdottoInventario("Latte", 2, scadenza, "Frigo", UnitaEnum.PEZZI, null);
        ProdottoInventario b = new ProdottoInventario("Latte", 5, scadenza, "Frigo", UnitaEnum.PEZZI, null);
        assertEquals(a, a);            // branch this == o
        assertEquals(a, b);            // identità = nome+scadenza+posizione
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, "non-un-prodotto");}

    @Test
    void testEqualsDiverso() throws Exception {
        ProdottoInventario a = prodotto("Latte", LocalDate.now().plusDays(5));
        ProdottoInventario b = prodotto("Pasta", LocalDate.now().plusDays(5));
        assertNotEquals(a, b);
    }
}
