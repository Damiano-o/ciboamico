package it.uniroma2.ispw.ciboamico.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean/DTO per le ricette mostrate alle Boundary.
 */
public class RicettaBean {

    private String nome;
    private String istruzioni;
    private List<String> ingredientiNomi = new ArrayList<>();
    private List<Double> dosi = new ArrayList<>();
    private String stato;

    public RicettaBean() { /* costruttore no-arg richiesto dai DAO/Gson */ }

    public boolean haAlmenoDueIngredienti() {
        return ingredientiNomi != null && ingredientiNomi.size() >= 2;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getIstruzioni() { return istruzioni; }
    public void setIstruzioni(String istruzioni) { this.istruzioni = istruzioni; }
    public List<String> getIngredientiNomi() { return ingredientiNomi; }
    public void setIngredientiNomi(List<String> ingredientiNomi) { this.ingredientiNomi = ingredientiNomi; }
    public List<Double> getDosi() { return dosi; }
    public void setDosi(List<Double> dosi) { this.dosi = dosi; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
}
