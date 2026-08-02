package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.entity.Ricetta;
import it.uniroma2.ispw.ciboamico.entity.StatoRicettaEnum;
import it.uniroma2.ispw.ciboamico.entity.StatoVenditoreEnum;
import it.uniroma2.ispw.ciboamico.entity.Utente;
import it.uniroma2.ispw.ciboamico.entity.RuoloVenditore;
import it.uniroma2.ispw.ciboamico.persistence.dao.RicettaDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.UtenteDAO;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

import java.util.List;

/**
 * Control di UC-08/09 — Approva Venditore e Approva Ricetta (Amministratore).
 * Flusso Bean-only: output List<RicettaBean> per le ricette in attesa.
 */
public class ApprovazioneController {

    private final UtenteDAO utenteDAO;
    private final RicettaDAO ricettaDAO;

    public ApprovazioneController(DAOFactory factory) {
        this.utenteDAO = factory.getUtenteDAO();
        this.ricettaDAO = factory.getRicettaDAO();
    }

    /** UC-08: approva/sospende un venditore (cambia stato ruolo). */
    public void approvaVenditore(String emailVenditore, boolean approva) {
        Utente utente = utenteDAO.findByEmail(emailVenditore);
        if (utente == null) {
            throw new IllegalArgumentException("Venditore non trovato");
        }
        RuoloVenditore ruolo = utente.getRuolo(RuoloVenditore.class);
        if (ruolo == null) {
            throw new IllegalArgumentException("Venditore non trovato");
        }
        ruolo.setStato(approva ? StatoVenditoreEnum.APPROVATO : StatoVenditoreEnum.SOSPESO);
        utenteDAO.save(utente);
    }

    /** UC-09: ricette in stato PROPOSTA (Bean-only in output). */
    public List<RicettaBean> ricetteInAttesa() {
        return ricettaDAO.findByStato(StatoRicettaEnum.PROPOSTA.name()).stream()
                .map(this::aBean)
                .toList();
    }

    /** UC-09: approva o rifiuta una ricetta. */
    public void approvaRicetta(String nomeRicetta, boolean approva) {
        Ricetta ricetta = ricettaDAO.findAll().stream()
                .filter(r -> r.getNome().equals(nomeRicetta))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ricetta non trovata"));
        ricetta.setStato(approva ? StatoRicettaEnum.APPROVATA : StatoRicettaEnum.RIFIUTATA);
        ricettaDAO.save(ricetta);
    }

    /** Mapping Entity → Bean (mai esposta alla Boundary). */
    private RicettaBean aBean(Ricetta ricetta) {
        RicettaBean bean = new RicettaBean();
        bean.setNome(ricetta.getNome());
        bean.setIstruzioni(ricetta.getIstruzioni());
        bean.setStato(ricetta.getStato().name());
        return bean;
    }
}
