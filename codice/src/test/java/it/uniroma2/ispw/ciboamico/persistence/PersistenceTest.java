package it.uniroma2.ispw.ciboamico.persistence;

// Author: Michele Damiano

import it.uniroma2.ispw.ciboamico.entity.Ordine;
import it.uniroma2.ispw.ciboamico.entity.Prodotto;
import it.uniroma2.ispw.ciboamico.entity.RuoloVenditore;
import it.uniroma2.ispw.ciboamico.entity.StatoVenditoreEnum;
import it.uniroma2.ispw.ciboamico.entity.UnitaEnum;
import it.uniroma2.ispw.ciboamico.entity.Utente;
import it.uniroma2.ispw.ciboamico.pattern.strategy.ScontoPercentualeStrategy;
import it.uniroma2.ispw.ciboamico.persistence.factory.DemoDAOFactory;
import it.uniroma2.ispw.ciboamico.persistence.factory.FSDAOFactory;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoOrdineDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoUtenteDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.fs.FSProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.fs.FSUtenteDAO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Test dei layer di persistenza: Demo (in-memory) e File System (JSON)

class PersistenceTest {

    private RuoloVenditore venditore() {
        RuoloVenditore v = new RuoloVenditore("RM", "tel");
        v.setStato(StatoVenditoreEnum.APPROVATO);
        return v;
    }

    @Test
    void demoUtenteRoundtrip() {
        DemoUtenteDAO dao = new DemoUtenteDAO();
        dao.save(new Utente("Mario", "mario@cibo.it", "hash"));
        assertEquals("Mario", dao.findByEmail("mario@cibo.it").getNome());
    }

    @Test
    void demoProdottoFindByNomeCaseInsensitive() throws Exception {
        DemoProdottoDAO dao = new DemoProdottoDAO();
        dao.save(new Prodotto("Pane", 2.0, 10, LocalDate.now().plusDays(5),
                UnitaEnum.PEZZI, venditore()));
        assertNotNull(dao.findByNome("pane"));
    }

    @Test
    void demoOrdineFindByVenditore() throws Exception {
        DemoOrdineDAO dao = new DemoOrdineDAO();
        Long id = dao.getNextId();
        dao.save(new Ordine(id, new Utente("C", "c@cibo.it", "h"),
                new Utente("V", "v@cibo.it", "h")));
        assertEquals(1, dao.findByVenditore("v@cibo.it").size());
    }

    @Test
    void seedDemoContieneUtenti() throws Exception {
        DemoDAOFactory factory = new DemoDAOFactory();
        factory.seedDemoData();
        assertNotNull(factory.getUtenteDAO().findByEmail("mario@cibo.it"));
    }

    @Test
    void fsUtenteRoundtrip() throws Exception {
        FSUtenteDAO dao = new FSUtenteDAO();
        Utente v = new Utente("Marco", "marco@cibo.it", "h");
        v.aggiungiRuolo(new RuoloVenditore("RM", "tel"));
        dao.save(v);
        assertTrue(dao.findByEmail("marco@cibo.it").haRuolo(RuoloVenditore.class));
    }

    @Test
    void fsProdottoFindByNome() throws Exception {
        FSProdottoDAO dao = new FSProdottoDAO();
        dao.save(new Prodotto("Pomodori", 2.0, 50, LocalDate.now().plusDays(7),
                UnitaEnum.GRAMMI, venditore()));
        assertNotNull(dao.findByNome("Pomodori"));
    }

    @Test
    void factoryFSEsponeUtenteDao() {
        assertNotNull(new FSDAOFactory().getUtenteDAO());
    }

    @Test
    void factoryFSEsponeProdottoDao() {
        assertNotNull(new FSDAOFactory().getProdottoDAO());
    }

    @Test
    void factoryFSEsponeOrdineDao() {
        assertNotNull(new FSDAOFactory().getOrdineDAO());
    }

    @Test
    void factoryFSEsponeBuonoDao() {
        assertNotNull(new FSDAOFactory().getBuonoDAO());
    }
}
