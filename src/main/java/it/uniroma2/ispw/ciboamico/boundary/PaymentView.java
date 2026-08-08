package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.bean.PaymentInfoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.OrdinaProdottoController;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Boundary JavaFX — Schermata di Pagamento (passo 6 + estensione 6a UC-04).
 * Legge il prodotto in checkout da {@link SessionManager#getOrdineInCorso()},
 * mostra il riepilogo e i dati carta, e su "Paga" invoca
 * {@link OrdinaProdottoController#processaPagamento}. Rispetta la regola
 * bean-only: scambia SOLO {@link PaymentInfoBean} con il controller.
 */
public class PaymentView {

    private final OrdinaProdottoController ordinaController;

    public PaymentView() {
        this.ordinaController = new OrdinaProdottoController();
    }

    public Parent build() {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();
        OrdineBean ordine = SessionManager.getInstance().getOrdineInCorso();

        Label messaggio = new Label(ordine != null
                ? "Riepilogo: " + ordine.getNomeProdotto() + " — " + String.format("%.2f EUR", ordine.getTotale())
                : "Nessun ordine in checkout per il pagamento.");
        messaggio.getStyleClass().add("page-subtitle");
        messaggio.setWrapText(true);

        TextField numeroCarta = new TextField();
        numeroCarta.setId("numCarta");
        numeroCarta.setPromptText("Numero carta");
        numeroCarta.setMaxWidth(Double.MAX_VALUE);

        TextField intestatario = new TextField();
        intestatario.setPromptText("Intestatario");
        intestatario.setMaxWidth(Double.MAX_VALUE);

        TextField scadenza = new TextField();
        scadenza.setPromptText("Scadenza (MM/AA)");
        scadenza.setMaxWidth(Double.MAX_VALUE);

        TextField cvv = new TextField();
        cvv.setId("cvvField");
        cvv.setPromptText("CVV");
        cvv.setMaxWidth(Double.MAX_VALUE);

        Button paga = new Button("Paga");
        paga.setId("btn-paga");
        paga.setMaxWidth(Double.MAX_VALUE);
        Label esito = new Label(" ");
        esito.getStyleClass().add("page-subtitle");
        esito.setWrapText(true);

        paga.setOnAction(e -> {
            try {
                if (ordine == null) {
                    throw new BusinessValidationException("Nessun ordine in checkout.");
                }
                validaCampi(numeroCarta.getText(), intestatario.getText(),
                        scadenza.getText(), cvv.getText());
                PaymentInfoBean payment = new PaymentInfoBean();
                payment.setNumeroCarta(numeroCarta.getText().trim());
                payment.setIntestatario(intestatario.getText().trim());
                payment.setScadenza(scadenza.getText().trim());
                payment.setCvv(cvv.getText().trim());
                // addebita il totale dell'ordine (scontato se è stato applicato un buono)
                payment.setImportoInCent(Math.round(ordine.getTotale() * 100));

                OrdineBean risultato = ordinaController.processaPagamento(ordine, utente, payment);

                esito.setText("Pagamento riuscito ✓ — ordine " + risultato.getStato()
                        + ", totale " + String.format("%.2f EUR", risultato.getTotale()));
                SessionManager.getInstance().setOrdineInCorso(null);
                Navigator.getInstance().switchTo("marketplace");
            } catch (BusinessValidationException ex) {
                esito.setText(ex.getMessage());
            } catch (Exception ex) {
                esito.setText("Problema tecnico: riprovare più tardi.");
            }
        });

        Button annulla = new Button("Annulla");
        annulla.setId("btn-annulla-pagamento");
        annulla.setMaxWidth(Double.MAX_VALUE);
        annulla.setOnAction(e -> {
            SessionManager.getInstance().setOrdineInCorso(null);
            Navigator.getInstance().switchTo("marketplace");
        });

        VBox corpo = new VBox(10,
                messaggio,
                UiKit.field("Numero carta"), numeroCarta,
                UiKit.field("Intestatario"), intestatario,
                UiKit.field("Scadenza"), scadenza,
                UiKit.field("CVV"), cvv,
                esito, paga, annulla);
        corpo.setPadding(new Insets(16, 0, 0, 0));
        corpo.getStyleClass().add("form-panel");
        return UiKit.pagina("Pagamento", "UC-04 · autorizzazione all'addebito", corpo, "marketplace");
    }

    private void validaCampi(String carta, String intestatario, String scadenza, String cvv)
            throws BusinessValidationException {
        if (carta.isBlank() || intestatario.isBlank() || scadenza.isBlank() || cvv.isBlank()) {
            throw new BusinessValidationException("Tutti i campi del pagamento sono obbligatori.");
        }
        if (cvv.trim().length() != 3) {
            throw new BusinessValidationException("Il CVV deve essere di esattamente 3 cifre.");
        }
    }
}