package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.entity.Ingrediente;
import it.uniroma2.ispw.ciboamico.entity.ProdottoInventario;
import it.uniroma2.ispw.ciboamico.entity.Ricetta;
import it.uniroma2.ispw.ciboamico.persistence.dao.ProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.RicettaDAO;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Control di UC-03 Gestisci Lista Spesa.
 * Calcola gli ingredienti mancanti per una ricetta (FR-05).
 * Flusso Bean-only: output List<ProdottoBean> (nome+quantità delle mancanze),
 * mai le Entity Ingrediente/Prodotto verso la Boundary.
 */
public class GestisciListaSpesaController {

    private final ProdottoDAO prodottoDAO;
    private final RicettaDAO ricettaDAO;

    public GestisciListaSpesaController(DAOFactory factory) {
        this.prodottoDAO = factory.getProdottoDAO();
        this.ricettaDAO = factory.getRicettaDAO();
    }

    /** Costruttore no-arg (stile 30/30): persistenza dal ServiceLocator. */
    public GestisciListaSpesaController() {
        this(it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager.getInstance().getDAOFactory());
    }

    /**
     * Restituisce gli ingredienti mancanti (con quantità) per la ricetta scelta.
     * 4.a: se non ci sono mancanze, lista vuota.
     */
    public List<ProdottoBean> calcolaMancanze(String utenteEmail, String nomeRicetta) {
        List<ProdottoInventario> inventario = prodottoDAO.findInventario(utenteEmail);
        Ricetta ricetta = ricettaDAO.findAll().stream()
                .filter(r -> r.getNome().equals(nomeRicetta))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ricetta non trovata"));

        List<ProdottoBean> mancanti = new ArrayList<>();
        for (Ingrediente ing : ricetta.getIngredienti()) {
            boolean disponibile = inventario.stream().anyMatch(pi ->
                    pi.getNome().equalsIgnoreCase(ing.getProdotto().getNome())
                            && pi.getQuantita() >= ing.getQuantita());
            if (!disponibile) {
                ProdottoBean bean = new ProdottoBean();
                bean.setNome(ing.getProdotto().getNome());
                bean.setQuantita(ing.getQuantita());
                bean.setUnitaMisura(ing.getUnita().name());
                mancanti.add(bean);
            }
        }
        return mancanti;
    }
}
