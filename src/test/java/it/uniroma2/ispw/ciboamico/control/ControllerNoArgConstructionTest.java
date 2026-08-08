package it.uniroma2.ispw.ciboamico.control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Copertura dei costruttori no-arg dei controller applicativi (stile 30/30):
 * la persistenza è risolta dal ServiceLocator (ApplicationModeManager) in
 * modalità DEMO. Verifica che ogni controller sia istanziabile senza che la
 * View debba iniettare la DAOFactory (BCE: boundary non conosce la persistenza).
 */
class ControllerNoArgConstructionTest {

    @Test
    void tuttiIControllerSiCostruisconoConNoArg() {
        assertNotNull(new OrdinaProdottoController());
        assertNotNull(new ApprovazioneController());
        assertNotNull(new AutenticazioneController());
        assertNotNull(new GestisciCatalogoVenditoreController());
        assertNotNull(new GestisciInventarioController());
        assertNotNull(new GestisciListaSpesaController());
        assertNotNull(new GestisciOrdiniRicevutiController());
        assertNotNull(new GestisciRicetteNutrizionistaController());
        assertNotNull(new TrovaRicetteController());
    }
}
