package it.uniroma2.ispw.ciboamico.persistence.impl.demo;

import it.uniroma2.ispw.ciboamico.entity.Ricetta;
import it.uniroma2.ispw.ciboamico.entity.StatoRicettaEnum;
import it.uniroma2.ispw.ciboamico.persistence.dao.RicettaDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO Demo in-memory per ricette.
 */
public class DemoRicettaDAO implements RicettaDAO {

    private final List<Ricetta> memoria = new ArrayList<>();

    @Override
    public List<Ricetta> findAll() { return new ArrayList<>(memoria); }

    @Override
    public List<Ricetta> findByStato(String stato) {
        return memoria.stream()
                .filter(r -> r.getStato() == StatoRicettaEnum.valueOf(stato))
                .toList();
    }

    @Override
    public Ricetta save(Ricetta ricetta) {
        memoria.add(ricetta);
        return ricetta;
    }
}
