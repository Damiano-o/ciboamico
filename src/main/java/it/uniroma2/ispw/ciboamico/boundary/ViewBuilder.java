package it.uniroma2.ispw.ciboamico.boundary;

import javafx.scene.Parent;

/**
 * Builder delle Boundary JavaFX — ogni vista costruisce il proprio Parent.
 * Registrata nel Navigator con un nome simbolico (riferimento a metodo ::build).
 */
@FunctionalInterface
public interface ViewBuilder {
    Parent build();
}
