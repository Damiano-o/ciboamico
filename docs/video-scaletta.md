# CiboAmico — Video Dimostrativo (scaletta da NotebookLM)

Durata totale: **~5 minuti**, 1080p, audio pulito, cursore visibile.
Tagliare i tempi morti in montaggio (build, login).

## Blocchi di registrazione

| Tempo | Blocco | Cosa mostrare |
|-------|--------|----------------|
| 0:00–0:45 | **Intro & Login (UC-11)** | Presentazione app, login con i 4 ruoli demo (mario/marco/anna/admin @ cibo.it, password `password123`). Evidenziare la tripla persistenza (JDBC/FS/DEMO) |
| 0:45–1:15 | **Home & ruolo** | Dashboard con navigazione, ruolo utente dalla sessione |
| 1:15–2:00 | **Dispensa & Lista Spesa (UC-01, UC-03)** | Inventario ordinato per scadenza, alert prodotti ≤3 giorni, calcolo ingredienti mancanti |
| 2:00–2:45 | **Trova Ricette (UC-02)** | Ricerca ricette compatibili con **sostituzione ingredienti** (elemento innovativo D-01) |
| 2:45–3:30 | **Marketplace & Ordine (UC-04)** | Catalogo venditore, ordine singolo diretto (D-03), conferma con totale |
| 3:30–4:15 | **Ordini Ricevuti & Nutrizionista (UC-06, UC-07)** | Cambio stato ordine (BR-04: CREATO→CONFERMATO→IN_CONSEGNA→CONSEGNATO), creazione ricetta (BR-05: min 2 ingredienti) |
| 4:15–5:00 | **Admin (UC-08 + Qualità** | Approvazione venditore/ricetta, poi terminale: 82 test JUnit verdi + SonarCloud Quality Gate **Passed** (0 bug, 0 smell, coverage >80%) |

## Script di chiusura (da NotebookLM)

> "Concludiamo mostrando l'affidabilità ingegneristica di CiboAmico. La test
> suite JUnit 5 conta 82 test d'unità e d'integrazione, tutti superati con
> successo. Grazie a JaCoCo, la copertura del codice di business supera l'80%,
> escludendo giustamente le classi di front-end. Come confermato da
> SonarCloud, il Quality Gate è superato con zero bug, zero vulnerabilità e
> zero code smell. Grazie per l'attenzione."

## Credenziali demo

| Ruolo | Email | Password |
|-------|-------|----------|
| Client | mario@cibo.it | password123 |
| Venditore | marco@cibo.it | password123 |
| Nutrizionista | anna@cibo.it | password123 |
| Admin | admin@cibo.it | password123 |
