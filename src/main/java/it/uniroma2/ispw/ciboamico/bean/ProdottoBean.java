package it.uniroma2.ispw.ciboamico.bean;

import java.time.LocalDate;

/**
 * Bean/DTO: unico canale Boundary→Control per i dati di inventario.
 * Puro trasporto dati + validazione sintattica; zero logica di business.
 */
public class ProdottoBean {

    private String nome;
    private Double quantita;
    private Double prezzo;
    private LocalDate scadenza;
    private String posizione;
    private String unitaMisura;

    public ProdottoBean() { /* costruttore no-arg richiesto dai DAO/Gson */ }

    public boolean datiObbligatoriPresenti() {
        return nome != null && !nome.isBlank()
                && quantita != null
                && scadenza != null
                && posizione != null
                && unitaMisura != null;
    }

    public Double getPrezzo() { return prezzo; }
    public void setPrezzo(Double prezzo) { this.prezzo = prezzo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Double getQuantita() { return quantita; }
    public void setQuantita(Double quantita) { this.quantita = quantita; }
    public LocalDate getScadenza() { return scadenza; }
    public void setScadenza(LocalDate scadenza) { this.scadenza = scadenza; }
    public String getPosizione() { return posizione; }
    public void setPosizione(String posizione) { this.posizione = posizione; }
    public String getUnitaMisura() { return unitaMisura; }
    public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }
}
