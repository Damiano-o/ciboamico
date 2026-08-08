package it.uniroma2.ispw.ciboamico.bean;

/**
 * Bean/DTO dei dati di pagamento inseriti nella schermata di checkout
 * (passo 6 + estensione 6a UC-04). Rispetta la regola bean-only ai confini:
 * la Boundary PaymentView scambia SOLO questo bean col controller,
 * senza esporre primitivi sparsi o entity. Per lo stub demo i dati carta
 * sono passati per completezza della firma; l'autorizzazione effettiva
 * valuta solo l'importo.
 */
public class PaymentInfoBean {

    private String numeroCarta;
    private String intestatario;
    private String scadenza;
    private String cvv;
    /** Importo della transazione in centesimi (long) per evitare float su denaro. */
    private long importoInCent;

    public String getNumeroCarta() { return numeroCarta; }
    public void setNumeroCarta(String numeroCarta) { this.numeroCarta = numeroCarta; }

    public String getIntestatario() { return intestatario; }
    public void setIntestatario(String intestatario) { this.intestatario = intestatario; }

    public String getScadenza() { return scadenza; }
    public void setScadenza(String scadenza) { this.scadenza = scadenza; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public long getImportoInCent() { return importoInCent; }
    public void setImportoInCent(long importoInCent) { this.importoInCent = importoInCent; }
}