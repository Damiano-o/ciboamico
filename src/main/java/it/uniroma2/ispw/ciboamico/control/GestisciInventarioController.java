package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.entity.ProdottoInventario;
import it.uniroma2.ispw.ciboamico.entity.UnitaEnum;
import it.uniroma2.ispw.ciboamico.persistence.dao.ProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

import java.util.Comparator;
import java.util.List;

/**
 * Control di UC-01 Gestisci Inventario.
 * Aggiunta prodotto (FR-01), ordinamento per scadenza (FR-02), avvisi (FR-03).
 * Flusso Bean-only: input ProdottoBean, output List<ProdottoBean>
 * (compartimento stagno BCE: la Boundary non vede mai le Entity).
 */
public class GestisciInventarioController {

    private final ProdottoDAO prodottoDAO;

    public GestisciInventarioController(DAOFactory factory) {
        this.prodottoDAO = factory.getProdottoDAO();
    }

    /** Costruttore no-arg (stile 30/30): persistenza dal ServiceLocator. */
    public GestisciInventarioController() {
        this(it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager.getInstance().getDAOFactory());
    }

    /** UC-01 MSS: valida dati, memorizza, conferma. */
    public ProdottoBean aggiungiProdotto(ProdottoBean bean, String utenteEmail) {
        if (!bean.datiObbligatoriPresenti()) {
            throw new IllegalArgumentException("Dati obbligatori mancanti (3.a)");
        }
        if (bean.getQuantita() <= 0) {
            throw new IllegalArgumentException("Quantità non valida (3.b)");
        }
        ProdottoInventario prodotto = new ProdottoInventario(
                bean.getNome(),
                bean.getQuantita().intValue(),
                bean.getScadenza(),
                bean.getPosizione(),
                UnitaEnum.valueOf(bean.getUnitaMisura()),
                null);
        prodottoDAO.saveInventario(utenteEmail, prodotto);
        return bean;
    }

    /** FR-02: inventario ordinato per scadenza crescente (Bean-only in output). */
    public List<ProdottoBean> visualizzaInventarioOrdinato(String utenteEmail) {
        return prodottoDAO.findInventario(utenteEmail).stream()
                .sorted(Comparator.naturalOrder())
                .map(this::aBean)
                .toList();
    }

    /** FR-03: solo prodotti in scadenza entro 3 giorni (Bean-only in output). */
    public List<ProdottoBean> prodottiInScadenza(String utenteEmail) {
        return prodottoDAO.findInventario(utenteEmail).stream()
                .filter(ProdottoInventario::inScadenza)
                .map(this::aBean)
                .toList();
    }

    /** Mapping Entity → Bean (mai esposta alla Boundary). */
    private ProdottoBean aBean(ProdottoInventario p) {
        ProdottoBean bean = new ProdottoBean();
        bean.setNome(p.getNome());
        bean.setQuantita((double) p.getQuantita());
        bean.setScadenza(p.getScadenza());
        bean.setPosizione(p.getPosizione());
        bean.setUnitaMisura(p.getUnita().name());
        return bean;
    }
}
