package it.uniroma2.ispw.ciboamico.persistence.impl.fs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.uniroma2.ispw.ciboamico.entity.BuonoPromozionale;
import it.uniroma2.ispw.ciboamico.entity.RuoloVenditore;
import it.uniroma2.ispw.ciboamico.entity.Utente;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.pattern.strategy.ScontoStrategy;
import it.uniroma2.ispw.ciboamico.pattern.strategy.ScontoStrategyFactory;
import it.uniroma2.ispw.ciboamico.persistence.dao.BuonoDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.UtenteDAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO FS per BuonoPromozionale — persistenza JSON (Gson).
 * Risolve il mismatch documentale/oggetti (De Angelis): serializza un DTO piatto
 * (senza l'interfaccia ScontoStrategy né RuoloVenditore) e, in lettura, ricostruisce
 * la Strategy tramite ScontoStrategyFactory e l'entità tramite UtenteDAO.
 */
public class FSBuonoDAO implements BuonoDAO {

    private static final Path FILE = Path.of("data", "buoni.json");
    private static final Gson GSON = GsonConfig.gson();
    private final UtenteDAO utenteDAO;

    public FSBuonoDAO(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    /** DTO piatto usato da Gson: niente interfaccia Strategy, solo dati primitivi. */
    static class BuonoJsonDTO {
        String codice;
        String venditoreEmail;
        String dataInizio;
        String dataScadenza;
        String tipoSconto;
        double valoreSconto;
    }

    private List<BuonoJsonDTO> carica() {
        try {
            if (!Files.exists(FILE)) {
                return new ArrayList<>();
            }
            return GSON.fromJson(Files.readString(FILE),
                    new TypeToken<List<BuonoJsonDTO>>() { }.getType());
        } catch (IOException e) {
            throw new RuntimeException("Errore lettura buoni.json", e);
        }
    }

    private void salva(List<BuonoJsonDTO> buoni) {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(buoni));
        } catch (IOException e) {
            throw new RuntimeException("Errore scrittura buoni.json", e);
        }
    }

    private Utente findByEmail(String email) {
        Utente u = utenteDAO.findByEmail(email);
        if (u == null) {
            throw new IllegalStateException("Venditore non trovato: " + email);
        }
        return u;
    }

    private RuoloVenditore venditoreDa(String email) {
        RuoloVenditore rv = findByEmail(email).getRuolo(RuoloVenditore.class);
        if (rv == null) {
            throw new IllegalStateException("L'utente " + email + " non è un venditore");
        }
        return rv;
    }

    private BuonoPromozionale aEntita(BuonoJsonDTO dto) {
        ScontoStrategy strategy = ScontoStrategyFactory.createStrategy(dto.tipoSconto, dto.valoreSconto);
        try {
            return new BuonoPromozionale(dto.codice, venditoreDa(dto.venditoreEmail),
                    LocalDate.parse(dto.dataInizio), LocalDate.parse(dto.dataScadenza), strategy);
        } catch (BusinessValidationException e) {
            throw new IllegalStateException("Buono persistito non valido: " + dto.codice, e);
        }
    }

    private BuonoJsonDTO aDto(BuonoPromozionale b) {
        BuonoJsonDTO dto = new BuonoJsonDTO();
        dto.codice = b.getCodice();
        dto.venditoreEmail = b.getVenditore().getUtente() != null
                ? b.getVenditore().getUtente().getEmail()
                : b.getVenditore().getRecapito();
        dto.dataInizio = b.getDataInizio().toString();
        dto.dataScadenza = b.getDataScadenza().toString();
        dto.tipoSconto = b.getStrategiaSconto().getTipo();
        dto.valoreSconto = b.getStrategiaSconto().getValore();
        return dto;
    }

    @Override
    public BuonoPromozionale findByCodice(String codice) {
        return carica().stream()
                .filter(dto -> dto.codice.equals(codice))
                .findFirst()
                .map(this::aEntita)
                .orElse(null);
    }

    @Override
    public List<BuonoPromozionale> findByVenditoreEmail(String venditoreEmail) {
        return carica().stream()
                .filter(dto -> dto.venditoreEmail.equals(venditoreEmail))
                .map(this::aEntita)
                .toList();
    }

    @Override
    public BuonoPromozionale save(BuonoPromozionale buono) {
        List<BuonoJsonDTO> buoni = carica();
        buoni.removeIf(dto -> dto.codice.equals(buono.getCodice()));
        buoni.add(aDto(buono));
        salva(buoni);
        return buono;
    }
}
