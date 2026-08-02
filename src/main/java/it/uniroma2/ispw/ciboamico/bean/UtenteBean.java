package it.uniroma2.ispw.ciboamico.bean;

/**
 * Bean/DTO per la sessione utente — tenuto da SessionManager.
 */
public class UtenteBean {

    private String username;
    private String email;
    private String ruoloAttivo;

    public UtenteBean() { }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRuoloAttivo() { return ruoloAttivo; }
    public void setRuoloAttivo(String ruoloAttivo) { this.ruoloAttivo = ruoloAttivo; }
}
