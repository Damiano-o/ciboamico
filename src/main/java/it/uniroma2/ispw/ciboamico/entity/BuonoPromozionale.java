package it.uniroma2.ispw.ciboamico.entity;

import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.pattern.strategy.ScontoStrategy;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Buono promozionale associato a uno specifico Venditore.
 * Il buono è valido solo all'interno dell'intervallo [dataInizio, dataScadenza]
 * e solo se applicato a un ordine del medesimo Venditore.
 * Lo sconto è calcolato tramite il pattern Strategy (ScontoStrategy),
 * consentendo sconti percentuali o a importo fisso senza modificare l'entità Ordine.
 */
public class BuonoPromozionale {

    private final String codice;
    private final RuoloVenditore venditore;
    private final LocalDate dataInizio;
    private final LocalDate dataScadenza;
    private final ScontoStrategy strategiaSconto;

    public BuonoPromozionale(String codice, RuoloVenditore venditore,
                             LocalDate dataInizio, LocalDate dataScadenza,
                             ScontoStrategy strategiaSconto)
            throws BusinessValidationException {
        if (codice == null || codice.isBlank()) {
            throw new BusinessValidationException("Il codice del buono è obbligatorio");
        }
        if (venditore == null) {
            throw new BusinessValidationException("Il buono deve essere associato a un Venditore");
        }
        if (dataInizio.isAfter(dataScadenza)) {
            throw new BusinessValidationException(
                    "La data di inizio non può essere successiva alla scadenza del buono");
        }
        if (strategiaSconto == null) {
            throw new BusinessValidationException("Il buono deve definire una strategia di sconto");
        }
        this.codice = codice;
        this.venditore = venditore;
        this.dataInizio = dataInizio;
        this.dataScadenza = dataScadenza;
        this.strategiaSconto = strategiaSconto;
    }

    /** Verifica che il buono sia attivo alla data corrente di sistema. */
    public boolean isValido(LocalDate dataCorrente) {
        return !dataCorrente.isBefore(dataInizio) && !dataCorrente.isAfter(dataScadenza);
    }

    public boolean isValido() {
        return isValido(LocalDate.now(ZoneId.systemDefault()));
    }

    /** Applica lo sconto all'importo lordo usando la strategia incapsulata. */
    public double applicaSconto(double subtotale) {
        return strategiaSconto.applicaSconto(subtotale);
    }

    public String getCodice() { return codice; }
    public RuoloVenditore getVenditore() { return venditore; }
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataScadenza() { return dataScadenza; }
    public ScontoStrategy getStrategiaSconto() { return strategiaSconto; }
}
