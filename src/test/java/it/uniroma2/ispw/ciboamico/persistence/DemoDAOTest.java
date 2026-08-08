package it.uniroma2.ispw.ciboamico.persistence;

import it.uniroma2.ispw.ciboamico.entity.Prodotto;
import it.uniroma2.ispw.ciboamico.entity.RuoloVenditore;
import it.uniroma2.ispw.ciboamico.entity.UnitaEnum;
import it.uniroma2.ispw.ciboamico.entity.Utente;
import it.uniroma2.ispw.ciboamico.persistence.factory.DemoDAOFactory;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoUtenteDAO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test DAO Demo: roundtrip utente e prodotto.
 
 * @author Michele Damiano
*/
class DemoDAOTest {

    @Test
    void testUtenteRoundtrip() throws Exception {
        DemoUtenteDAO dao = new DemoUtenteDAO();
        Utente u = new Utente("Mario", "mario@cibo.it", "hash");
        dao.save(u);

        Utente trovato = dao.findByEmail("mario@cibo.it");

        assertNotNull(trovato);
        assertEquals("Mario", trovato.getNome());}

    @Test
    void testProdottoSaveAndFind() throws Exception {
        DemoProdottoDAO dao = new DemoProdottoDAO();
        RuoloVenditore venditore = new RuoloVenditore("RM", "tel");
        Prodotto p = new Prodotto("Pane", 2.0, 10, LocalDate.now().plusDays(5),
                UnitaEnum.PEZZI, venditore);
        dao.save(p);

        List<Prodotto> tutti = dao.findAll();

        assertFalse(tutti.isEmpty());
    }

    @Test
    void testProdottoFindByNome() throws Exception {
        DemoProdottoDAO dao = new DemoProdottoDAO();
        RuoloVenditore venditore = new RuoloVenditore("RM", "tel");
        dao.save(new Prodotto("Pane", 2.0, 10, LocalDate.now().plusDays(5),
                UnitaEnum.PEZZI, venditore));

        Prodotto trovato = dao.findByNome("Pane");

        assertNotNull(trovato);
        assertEquals("Pane", trovato.getNome());
    }

    @Test
    void testProdottoFindByNomeCaseInsensitive() throws Exception {
        DemoProdottoDAO dao = new DemoProdottoDAO();
        RuoloVenditore venditore = new RuoloVenditore("RM", "tel");
        dao.save(new Prodotto("Pane", 2.0, 10, LocalDate.now().plusDays(5),
                UnitaEnum.PEZZI, venditore));

        Prodotto trovato = dao.findByNome("pane");

        assertNotNull(trovato);
    }

    @Test
    void testSeedDemoDataCreaUtentiERicettaApprovata() throws Exception {
        DemoDAOFactory factory = new DemoDAOFactory();
        factory.seedDemoData();

        // 4 utenti demo (Mario, Marco, Anna, Admin)
        assertNotNull(factory.getUtenteDAO().findByEmail("mario@cibo.it"));
        assertNotNull(factory.getUtenteDAO().findByEmail("marco@cibo.it"));
        assertNotNull(factory.getUtenteDAO().findByEmail("anna@cibo.it"));
        assertNotNull(factory.getUtenteDAO().findByEmail("admin@cibo.it"));

        // la ricetta demo deve essere APPROVATA (visibile in Ricette compatibili)
        var ricette = factory.getRicettaDAO().findAll();
        assertEquals(1, ricette.size());
        assertEquals("Pasta al pomodoro", ricette.get(0).getNome());
        assertEquals("APPROVATA", ricette.get(0).getStato().name());

        // l'inventario di Mario è popolato (8 prodotti)
        var inventario = factory.getProdottoDAO().findInventario("mario@cibo.it");
        assertEquals(8, inventario.size());

        // il venditore demo è approvato → i suoi prodotti sono sul marketplace
        var catalogo = factory.getProdottoDAO().findAll();
        assertFalse(catalogo.isEmpty());
    }

    @Test
    void testSeedIdempotenteNonDuplica() throws Exception {
        DemoDAOFactory factory = new DemoDAOFactory();
        factory.seedDemoData();
        factory.seedDemoData(); // seconda chiamata: deve restare invariato
        factory.seedDemoData();

        // le ricette non devono duplicare (era il bug: ricetta pubblicata 2 volte)
        assertEquals(1, factory.getRicettaDAO().findAll().size());

        // anche utenti e inventario restano 1 sola copia
        assertNotNull(factory.getUtenteDAO().findByEmail("mario@cibo.it"));
        assertEquals(8, factory.getProdottoDAO().findInventario("mario@cibo.it").size());
    }
}
