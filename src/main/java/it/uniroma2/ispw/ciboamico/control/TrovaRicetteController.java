package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.entity.ProdottoInventario;
import it.uniroma2.ispw.ciboamico.entity.Ricetta;
import it.uniroma2.ispw.ciboamico.pattern.facade.RicettaMatchingFacade;
import it.uniroma2.ispw.ciboamico.persistence.dao.ProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.RicettaDAO;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

import java.util.List;

/**
 * Control di UC-02 Trova Ricette Compatibili.
 * Recupera inventario+ricette approvate e delega alla Facade (Strategy).
 */
public class TrovaRicetteController {

    private final ProdottoDAO prodottoDAO;
    private final RicettaDAO ricettaDAO;
    private final RicettaMatchingFacade facade;

    public TrovaRicetteController(DAOFactory factory, RicettaMatchingFacade facade) {
        this.prodottoDAO = factory.getProdottoDAO();
        this.ricettaDAO = factory.getRicettaDAO();
        this.facade = facade;
    }

    public List<RicettaBean> findCompatible(String utenteEmail) {
        List<ProdottoInventario> inventario = prodottoDAO.findInventario(utenteEmail);
        List<Ricetta> ricetteApprovate = ricettaDAO.findByStato("APPROVATA");
        return facade.getRecipes(inventario, ricetteApprovate);
    }
}
