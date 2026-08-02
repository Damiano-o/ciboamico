package it.uniroma2.ispw.ciboamico.persistence.factory;

import it.uniroma2.ispw.ciboamico.persistence.dao.OrdineDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.ProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.RicettaDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.UtenteDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoOrdineDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoRicettaDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoUtenteDAO;

/**
 * Factory DEMO: DAO in-memory — milestone M1 (senza salvataggio) e test veloci.
 * Riutilizza le stesse istanze DAO: lo spazio dati è condiviso tra chiamate
 * della stessa factory (ma isolato tra factory diverse — test indipendenti).
 */
public class DemoDAOFactory extends DAOFactory {

    private final UtenteDAO utenteDAO = new DemoUtenteDAO();
    private final ProdottoDAO prodottoDAO = new DemoProdottoDAO();
    private final RicettaDAO ricettaDAO = new DemoRicettaDAO();
    private final OrdineDAO ordineDAO = new DemoOrdineDAO();

    @Override
    public UtenteDAO getUtenteDAO() { return utenteDAO; }

    @Override
    public ProdottoDAO getProdottoDAO() { return prodottoDAO; }

    @Override
    public RicettaDAO getRicettaDAO() { return ricettaDAO; }

    @Override
    public OrdineDAO getOrdineDAO() { return ordineDAO; }
}
