package it.uniroma2.ispw.ciboamico.bean;

/**
 * Bean/DTO per l'ordine — scambiato tra MarketplaceView e OrdinaProdottoController.
 */
public class OrdineBean {

    private Long idOrdine;
    private String nomeProdotto;
    private Double totale;
    private String stato;
    private String compratoreId;
    private String venditoreId;
    /** Codice del buono promozionale applicato (opzionale, estensione 4a UC-04). */
    private String codiceBuono;

    public Long getIdOrdine() { return idOrdine; }
    public void setIdOrdine(Long idOrdine) { this.idOrdine = idOrdine; }
    /** Prodotto selezionato dalla boundary (chiave di lookup per UC-04). */
    public String getNomeProdotto() { return nomeProdotto; }
    public void setNomeProdotto(String nomeProdotto) { this.nomeProdotto = nomeProdotto; }
    public Double getTotale() { return totale; }
    public void setTotale(Double totale) { this.totale = totale; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    public String getCompratoreId() { return compratoreId; }
    public void setCompratoreId(String compratoreId) { this.compratoreId = compratoreId; }
    public String getVenditoreId() { return venditoreId; }
    public void setVenditoreId(String venditoreId) { this.venditoreId = venditoreId; }
    public String getCodiceBuono() { return codiceBuono; }
    public void setCodiceBuono(String codiceBuono) { this.codiceBuono = codiceBuono; }
}
