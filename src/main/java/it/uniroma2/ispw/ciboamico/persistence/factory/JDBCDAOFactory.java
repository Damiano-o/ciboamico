package it.uniroma2.ispw.ciboamico.persistence.factory;

import it.uniroma2.ispw.ciboamico.persistence.dao.OrdineDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.ProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.RicettaDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.UtenteDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.jdbc.JDBCOrdineDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.jdbc.JDBCProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.jdbc.JDBCRicettaDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.jdbc.JDBCUtenteDAO;

/**
 * Factory JDBC: persistenza su MySQL con PreparedStatement (anti SQL-injection).
 */
public class JDBCDAOFactory extends DAOFactory {

    @Override
    public UtenteDAO getUtenteDAO() { return new JDBCUtenteDAO(); }

    @Override
    public ProdottoDAO getProdottoDAO() { return new JDBCProdottoDAO(); }

    @Override
    public RicettaDAO getRicettaDAO() { return new JDBCRicettaDAO(); }

    @Override
    public OrdineDAO getOrdineDAO() { return new JDBCOrdineDAO(); }
}
