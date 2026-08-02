package it.uniroma2.ispw.ciboamico.bean;

/**
 * Bean/DTO per il login (UC-11) — incapsula e validà sintatticamente
 * email e password PRIMA di raggiungere il controller (disaccoppiamento
 * presentazione/model, Bean-only).
 */
public class AutenticazioneBean {

    private String email;
    private String password;

    public AutenticazioneBean() { }

    /** Validazione sintattica dell'email (regex, formato standard). */
    private static final String EMAIL_REGEX = "^[\\w.+-]+@[\\w-]+\\.[\\w.]+$";

    /** Valida la forma dell'email; non garantisce l'esistenza dell'account. */
    public boolean isEmailValida() {
        return email != null && email.matches(EMAIL_REGEX);
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}