package it.uniroma2.ispw.ciboamico.entity;

/**
 * Ruolo Venditore (contadino/fornitore locale).
 * Solo con stato APPROVATO può pubblicare prodotti (BR-02).
 * Mantiene il back-reference all'Utente proprietario (whole-part bidirezionale).
 */
public class RuoloVenditore extends Ruolo {

    private final String zona;
    private final String recapito;
    private StatoVenditoreEnum stato;
    private Utente utente;

    public RuoloVenditore(String zona, String recapito) {
        this.zona = zona;
        this.recapito = recapito;
        this.stato = StatoVenditoreEnum.IN_ATTESA;
    }

    public String getZona() { return zona; }
    public String getRecapito() { return recapito; }
    public StatoVenditoreEnum getStato() { return stato; }

    @Override
    public String getNomeRuolo() { return "VENDITORE"; }

    /** Utente proprietario di questo ruolo (set quando il ruolo è aggiunto all'Utente). */
    public Utente getUtente() { return utente; }
    void setUtente(Utente utente) { this.utente = utente; }

    public void setStato(StatoVenditoreEnum stato) { this.stato = stato; }
}
