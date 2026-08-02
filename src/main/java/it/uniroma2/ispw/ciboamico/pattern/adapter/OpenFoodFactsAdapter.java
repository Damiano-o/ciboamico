package it.uniroma2.ispw.ciboamico.pattern.adapter;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;

import java.util.Map;

/**
 * Adapter (GoF) per l'API esterna OpenFoodFacts (barcode).
 * Stub con dati preconfigurati: nessuna chiamata di rete reale (test indipendenti).
 * La Boundary usa questo adapter per popolare un ProdottoBean dalla scansione barcode.
 */
public class OpenFoodFactsAdapter {

    /** Barcode → dati prodotto (stub: catalogo minimo dimostrativo). */
    private static final Map<String, ProdottoBean> CATALOGO_STUB = Map.of(
            "8000500310427", prodotto("Latte Intero", "LITRI"),
            "8076809512190", prodotto("Passata di Pomodoro", "GRAMMI"),
            "8076800195057", prodotto("Farina 00", "GRAMMI")
    );

    private static ProdottoBean prodotto(String nome, String unita) {
        ProdottoBean bean = new ProdottoBean();
        bean.setNome(nome);
        bean.setUnitaMisura(unita);
        return bean;
    }

    /**
     * Restituisce il prodotto corrispondente al codice a barre.
     * Se non trovato nello stub, ritorna null (estensione 3.a: nessun prodotto corrisponde).
     */
    public ProdottoBean findByBarcode(String barcode) {
        if (barcode == null || barcode.isBlank()) {
            throw new IllegalArgumentException("Barcode non valido");
        }
        return CATALOGO_STUB.get(barcode.trim());
    }
}
