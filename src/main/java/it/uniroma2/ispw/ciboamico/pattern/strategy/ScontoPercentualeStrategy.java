package it.uniroma2.ispw.ciboamico.pattern.strategy;


/**
 * ConcreteStrategy: sconto percentuale sull'importo lordo.
 * Esempio: -20% su un totale di 10.00 € -> 8.00 €.
 * Il risultato non scende mai sotto zero.
 */
public class ScontoPercentualeStrategy implements ScontoStrategy {

    /** Percentuale di sconto in [0,1] (es. 0.20 = 20%). */
    private final double percentuale;

    public ScontoPercentualeStrategy(double percentuale) {
        if (percentuale < 0 || percentuale > 1) {
            throw new IllegalArgumentException("Percentuale di sconto fuori intervallo: " + percentuale);
        }
        this.percentuale = percentuale;
    }

    @Override
    public double applicaSconto(double subtotale) {
        double scontato = subtotale * (1 - percentuale);
        return Math.max(0.0, scontato);
    }

    @Override
    public String descrizione() {
        return "-" + Math.round(percentuale * 100) + "%";
    }

    @Override
    public String getTipo() { return "PERCENTUALE"; }

    @Override
    public double getValore() { return percentuale; }
}
