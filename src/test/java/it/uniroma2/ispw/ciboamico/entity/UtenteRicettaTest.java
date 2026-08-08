package it.uniroma2.ispw.ciboamico.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Utente + Ruoli (whole-part, metamorfosi) e Ricetta (BR-05).
 
 * @author Michele Damiano
*/
class UtenteRicettaTest {

    @Test
    void testMetamorfosiRuolo() throws Exception {
        Utente u = new Utente("Mario", "mario@cibo.it", "hash");
        assertFalse(u.haRuolo(RuoloVenditore.class));

        RuoloVenditore rv = new RuoloVenditore("RM", "tel");
        rv.setStato(StatoVenditoreEnum.APPROVATO);
        u.aggiungiRuolo(rv);

        assertTrue(u.haRuolo(RuoloVenditore.class));
        assertTrue(u.isVenditoreApprovato());}

    @Test
    void testVenditoreNonApprovato() throws Exception {
        Utente u = new Utente("Mario", "mario@cibo.it", "hash");
        u.aggiungiRuolo(new RuoloVenditore("RM", "tel")); // stato IN_ATTESA
        assertFalse(u.isVenditoreApprovato());
    }

    @Test
    void testNomeRuoliConcreti() throws Exception {
        assertEquals("AMMINISTRATORE", new RuoloAmministratore().getNomeRuolo());
        assertEquals("CLIENTE", new RuoloCliente().getNomeRuolo());
        assertEquals("NUTRIZIONISTA", new RuoloNutrizionista().getNomeRuolo());
        assertEquals("VENDITORE", new RuoloVenditore("RM", "tel").getNomeRuolo());}

    @Test
    void testRicettaHaAlmenoDueIngredienti() throws Exception {
        RuoloNutrizionista nutrizionista = new RuoloNutrizionista();
        Ricetta r = new Ricetta("Pasta al pomodoro", "bollire", nutrizionista);
        assertFalse(r.haAlmenoDueIngredienti()); // BR-05: serve >= 2

        Prodotto pasta = new Prodotto("Pasta", 2.0, 10, java.time.LocalDate.now().plusDays(100),
                UnitaEnum.GRAMMI, null);
        Prodotto pomodoro = new Prodotto("Pomodoro", 1.0, 10, java.time.LocalDate.now().plusDays(10),
                UnitaEnum.PEZZI, null);
        r.aggiungiIngrediente(new Ingrediente(pasta, 500, UnitaEnum.GRAMMI));
        r.aggiungiIngrediente(new Ingrediente(pomodoro, 3, UnitaEnum.PEZZI));

        assertTrue(r.haAlmenoDueIngredienti());}

    @Test
    void testCheckPasswordCorretta() {
        Utente u = new Utente("Mario", "mario@cibo.it", Utente.hashPassword("segreta"));
        assertTrue(u.checkPassword("segreta"));
    }

    @Test
    void testCheckPasswordErrata() {
        Utente u = new Utente("Mario", "mario@cibo.it", Utente.hashPassword("segreta"));
        assertFalse(u.checkPassword("sbagliata"));
    }

    @Test
    void testHashPasswordDeterministico() {
        assertEquals(Utente.hashPassword("abc"), Utente.hashPassword("abc"));
        assertNotEquals(Utente.hashPassword("abc"), Utente.hashPassword("abd"));
    }
}
