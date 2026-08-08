package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.entity.Ricetta;
import it.uniroma2.ispw.ciboamico.pattern.facade.RicettaMatchingFacade;
import it.uniroma2.ispw.ciboamico.persistence.factory.DemoDAOFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Copertura di TrovaRicetteController (UC-02): verifica che findByStato
 * e la delega alla Facade (Strategy) non lancino eccezioni anche con stato vuoto.
 */
class TrovaRicetteControllerTest {

    private DemoDAOFactory factory;
    private TrovaRicetteController controller;

    @BeforeEach
    void setup() {
        factory = new DemoDAOFactory();
        controller = new TrovaRicetteController(factory, RicettaMatchingFacade.conSostituzione());
    }

    @Test
    void trovaRicetteConInventarioVuoto() {
        // inventario e ricette vuoti: nessuna eccezione, lista (vuota) di bean
        List<RicettaBean> risultato = controller.findCompatible("mario@cibo.it");
        assertNotNull(risultato);
    }
}
