package it.uniroma2.ispw.ciboamico.persistence.impl.fs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.uniroma2.ispw.ciboamico.entity.Ricetta;
import it.uniroma2.ispw.ciboamico.persistence.dao.RicettaDAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO FS per Ricetta — JSON persistente.
 */
public class FSRicettaDAO implements RicettaDAO {

    private static final Path FILE = Path.of("data", "ricette.json");
    private static final Gson GSON = GsonConfig.gson();

    private List<Ricetta> carica() {
        try {
            if (!Files.exists(FILE)) return new ArrayList<>();
            return GSON.fromJson(Files.readString(FILE), new TypeToken<List<Ricetta>>() { }.getType());
        } catch (IOException e) {
            throw new RuntimeException("Errore lettura ricette.json", e);
        }
    }

    private void salva(List<Ricetta> ricette) {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(ricette));
        } catch (IOException e) {
            throw new RuntimeException("Errore scrittura ricette.json", e);
        }
    }

    @Override
    public List<Ricetta> findAll() { return carica(); }

    @Override
    public List<Ricetta> findByStato(String stato) {
        return carica().stream().filter(r -> r.getStato().name().equals(stato)).toList();
    }

    @Override
    public Ricetta save(Ricetta ricetta) {
        List<Ricetta> ricette = carica();
        ricette.add(ricetta);
        salva(ricette);
        return ricetta;
    }
}
