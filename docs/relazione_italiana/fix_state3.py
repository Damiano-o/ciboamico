p = r"chapters/03_design.tex"
t = open(p, encoding="utf-8").read()

start = t.index("\\subsection{State Diagram (Ordina un Prodotto, navigazione UI)}")
end = t.index("\\section{Buono Promozionale", start + 1)

intro = (
"\\subsection{State Diagram (Ordina un Prodotto, navigazione UI)}\n"
"In linea con la modellazione proposta nelle relazioni di riferimento "
"(Sporty, Cardify), lo State Diagram del caso d'uso modella le\n"
"\\emph{schermate} della UI e le transizioni nel formato\n"
"\\texttt{trigger [guardia] / azione}. La modellazione mappa fedelmente la\n"
"natura \\emph{single-view} della \\texttt{MarketplaceView}: non si introducono\n"
"view fittizie (es. schermate di checkout esterne o dialoghi modali), bens\u00ec\n"
"stati ``logici'' per l'evoluzione testuale della \\texttt{Label}\n"
"\\texttt{messaggio} a runtime, conservando la consistenza con il codice reale.\n"
"\n"
)

items = (
"\\begin{itemize}\n"
"    \\item \\texttt{CatalogAdIniziale} \\texttt{clickAggiorna() / getProdottiDisponibili()} \\texttt{CatalogoPronto}: il caricamento popola catalogo e selettore.\n"
"    \\item \\texttt{CatalogoPronto} \\texttt{selectComboBox(prodotto) / null} \\texttt{ProdottoSelezionato}: selezione del prodotto da ordinare.\n"
"    \\item \\texttt{ProdottoSelezionato} \\texttt{clickApplicaBuono(codice) [codice non vuoto] / applicaBuonoPromozionale()} \\texttt{ProdottoSelezionato}: auto-transizione dell'\\textbf{estensione 4a} (buono promozionale) che ricalcola il totale inline.\n"
"    \\item \\texttt{ProdottoSelezionato} \\texttt{clickOrdinaProdotto() [successoInviaOrdine] / submitOrdine()} \\texttt{OrdineCreato} oppure \\texttt{[non successo] / submitOrdine()} \\texttt{ErroreMostrato}: guardie mutuamente esclusive sull'esito.\n"
"    \\item \\texttt{OrdineCreato} e \\texttt{ErroreMostrato} \\texttt{clickAggiorna() / resetForm()} \\texttt{CatalogoPronto}: riavvio del flusso dopo l'esito.\n"
"\\end{itemize}\n"
"\n"
)

figure = (
"\\begin{figure}[H]\n"
"    \\centering\n"
"    \\includegraphics[width=0.62\\textwidth]{figures/state_uc04.png}\n"
"    \\caption{State diagram (navigazione UI) del caso d'uso Ordina un Prodotto: schermate e transizioni \\texttt {trigger [guardia] / azione}, incluse l'estensione 4a (buono promozionale) come auto-transizione, fedele alla \\texttt{MarketplaceView}.}\n"
"    \\label{fig:state}\n"
"\\end{figure}\n"
"\n"
)
t = t[:start] + intro + items + final + t[end:]
open(p, "w", encoding="utf-8").write(t)
print("OK")