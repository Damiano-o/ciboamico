package it.uniroma2.ispw.ciboamico.pattern;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.entity.*;
import it.uniroma2.ispw.ciboamico.pattern.adapter.JakartaMailAdapter;
import it.uniroma2.ispw.ciboamico.pattern.adapter.OpenFoodFactsAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Adapter (GoF): OpenFoodFacts (barcode) e JakartaMail (notifiche).
 
 * @author Michele Damiano
*/
class AdapterTest {

    @Test
    void testBarcodeTrovato() throws Exception {
        OpenFoodFactsAdapter adapter = new OpenFoodFactsAdapter();
        ProdottoBean bean = adapter.findByBarcode("8000500310427");
        assertNotNull(bean);
        assertEquals("Latte Intero", bean.getNome());}

    @Test
    void testBarcodeNonTrovato() throws Exception {
        OpenFoodFactsAdapter adapter = new OpenFoodFactsAdapter();
        assertNull(adapter.findByBarcode("9999999999999"));
    }

    @Test
    void testBarcodeNonValido() throws Exception {
        OpenFoodFactsAdapter adapter = new OpenFoodFactsAdapter();
        assertThrows(IllegalArgumentException.class, () -> adapter.findByBarcode(""));
    }

    @Test
    void testMailInvia() throws Exception {
        JakartaMailAdapter adapter = new JakartaMailAdapter();
        Utente c = new Utente("C", "c@cibo.it", "h");
        Utente v = new Utente("V", "v@cibo.it", "h");
        Ordine ordine = new Ordine(1L, c, v);
        assertTrue(adapter.inviaNotifica(ordine, "v@cibo.it", "Nuovo ordine"));
    }

    @Test
    void testMailDestinatarioNonValido() throws Exception {
        JakartaMailAdapter adapter = new JakartaMailAdapter();
        Utente c = new Utente("C", "c@cibo.it", "h");
        Utente v = new Utente("V", "v@cibo.it", "h");
        Ordine ordine = new Ordine(1L, c, v);
        assertFalse(adapter.inviaNotifica(ordine, "", "test"));
    }
}
