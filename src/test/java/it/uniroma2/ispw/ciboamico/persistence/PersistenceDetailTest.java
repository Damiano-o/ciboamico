package it.uniroma2.ispw.ciboamico.persistence;

import it.uniroma2.ispw.ciboamico.entity.*;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import it.uniroma2.ispw.ciboamico.persistence.factory.FSDAOFactory;
import it.uniroma2.ispw.ciboamico.persistence.factory.JDBCDAOFactory;
import it.uniroma2.ispw.ciboamico.persistence.impl.fs.FSProdottoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di dettaglio per FS DAO (findById, inventario) e factory.
 
 * @author Michele Damiano
*/
class PersistenceDetailTest {

    @BeforeEach
    void pulisci() {
        try {
            Path dir = Path.of("data");
            if (Files.exists(dir)) {
                try (var stream = Files.list(dir)) {
                    stream.forEach(f -> { try { Files.deleteIfExists(f); } catch (IOException ignored) { /* pulizia best-effort: se fallisce, il test successivo gestisce */ } });
                }
            }
        } catch (IOException ignored) { /* pulizia best-effort: se fallisce, il test successivo gestisce */ }
    }

    private RuoloVenditore venditore() {
        RuoloVenditore v = new RuoloVenditore("RM", "tel");
        v.setStato(StatoVenditoreEnum.APPROVATO);
        return v;
    }

    @Test
    void testProdottoFindById() throws Exception {
        FSProdottoDAO dao = new FSProdottoDAO();
        Prodotto p = new Prodotto("Pomodori", 2.0, 50, LocalDate.now().plusDays(7),
                UnitaEnum.GRAMMI, venditore());
        dao.save(p);

        Prodotto trovato = dao.findById((long) p.getNome().hashCode());

        assertNotNull(trovato);
        assertEquals("Pomodori", trovato.getNome());}

    @Test
    void testInventarioFSSaveAndFind() throws Exception {
        FSProdottoDAO dao = new FSProdottoDAO();
        ProdottoInventario pi = new ProdottoInventario("Latte", 2,
                LocalDate.now().plusDays(10), "Frigo", UnitaEnum.LITRI, null);
        dao.saveInventario("anna@cibo.it", pi);

        List<ProdottoInventario> inventario = dao.findInventario("anna@cibo.it");

        assertEquals(1, inventario.size());
        assertEquals("Latte", inventario.get(0).getNome());}

    @Test
    void testFactoryConcrete() throws Exception {
        DAOFactory fs = new FSDAOFactory();
        assertNotNull(fs.getUtenteDAO());
        assertNotNull(fs.getProdottoDAO());
        assertNotNull(fs.getRicettaDAO());
        assertNotNull(fs.getOrdineDAO());

        DAOFactory jdbc = new JDBCDAOFactory();
        assertNotNull(jdbc.getUtenteDAO());
        assertNotNull(jdbc.getProdottoDAO());
        assertNotNull(jdbc.getRicettaDAO());
        assertNotNull(jdbc.getOrdineDAO());}

    @Test
    void testStatoEnum() throws Exception {
        assertEquals("IN_ATTESA", StatoVenditoreEnum.IN_ATTESA.name());
        assertEquals("APPROVATA", StatoRicettaEnum.APPROVATA.name());
        assertEquals("DELIVERED", StatoOrdineEnum.DELIVERED.name());}

    @Test
    void testUtenteGetRuoli() throws Exception {
        Utente u = new Utente("Mario", "m@cibo.it", "h");
        u.aggiungiRuolo(new RuoloCliente());
        assertEquals(1, u.getRuoli().size());
        assertNotNull(u.getRuolo(RuoloCliente.class));
        assertNull(u.getRuolo(RuoloVenditore.class));}
}
