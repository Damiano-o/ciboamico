package it.uniroma2.ispw.ciboamico.pattern.facade;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.entity.ProdottoInventario;
import it.uniroma2.ispw.ciboamico.entity.Ricetta;
import it.uniroma2.ispw.ciboamico.pattern.strategy.MatchingStrategy;
import it.uniroma2.ispw.ciboamico.pattern.strategy.SubstitutionMatchingStrategy;

import java.util.List;

/**
 * Facade (GoF): interfaccia unica al motore di matching ricette.
 * La Boundary chiama UN metodo e riceve Bean — non conosce Strategy né Entity.
 */
public class RicettaMatchingFacade {

    private final MatchingStrategy strategy;

    public RicettaMatchingFacade(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    /** Factory interna: default = Substitution (elemento innovativo). */
    public static RicettaMatchingFacade conSostituzione() {
        return new RicettaMatchingFacade(new SubstitutionMatchingStrategy());
    }

    public List<RicettaBean> getRecipes(List<ProdottoInventario> inventario, List<Ricetta> ricette) {
        return strategy.match(inventario, ricette).stream()
                .map(this::toBean)
                .toList();
    }

    private RicettaBean toBean(Ricetta r) {
        RicettaBean bean = new RicettaBean();
        bean.setNome(r.getNome());
        bean.setIstruzioni(r.getIstruzioni());
        bean.setStato(r.getStato().name());
        bean.setIngredientiNomi(r.getIngredienti().stream()
                .map(i -> i.getProdotto().getNome()).toList());
        bean.setDosi(r.getIngredienti().stream()
                .map(i -> i.getQuantita()).toList());
        return bean;
    }
}
