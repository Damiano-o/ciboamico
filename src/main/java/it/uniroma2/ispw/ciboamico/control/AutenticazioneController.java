package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.AutenticazioneBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.entity.Utente;
import it.uniroma2.ispw.ciboamico.exception.AutenticazioneException;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.persistence.dao.UtenteDAO;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Control di UC-11 Autenticazione (incluso dagli altri UC).
 * Validazione email, hash password, sessione in SessionManager.
 */
public class AutenticazioneController {

    private final UtenteDAO utenteDAO;

    public AutenticazioneController(DAOFactory factory) {
        this.utenteDAO = factory.getUtenteDAO();
    }

    public UtenteBean login(AutenticazioneBean bean) throws AutenticazioneException {
        if (bean == null || bean.getEmail() == null || !bean.isEmailValida()) {
            throw new AutenticazioneException("Email non valida");
        }
        return login(bean.getEmail(), bean.getPassword());
    }

    public UtenteBean login(String email, String password) throws AutenticazioneException {
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
            throw new AutenticazioneException("Email non valida");
        }
        Utente utente = utenteDAO.findByEmail(email);
        if (utente == null || !utente.getPasswordHash().equals(hash(password))) {
            throw new AutenticazioneException("Credenziali non valide");
        }

        UtenteBean bean = new UtenteBean();
        bean.setUsername(utente.getNome());
        bean.setEmail(utente.getEmail());
        bean.setRuoloAttivo(utente.getRuoli().isEmpty() ? "CLIENTE" : utente.getRuoli().get(0).getClass().getSimpleName());
        SessionManager.getInstance().setLoggedUser(bean);
        return bean;
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }

    /** Hash SHA-256 con salt fisso (NFR-03). */
    private String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(("ciboamico-salt" + password).getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
