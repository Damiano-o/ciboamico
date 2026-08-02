package it.uniroma2.ispw.ciboamico.boundary;

import javafx.scene.Parent;

/**
 * Factory delle Boundary JavaFX — ogni vista costruisce il proprio Parent.
 * Registrata nel Navigator con un nome simbolico.
 */
@FunctionalInterface
public interface ViewFactory {
    Parent build();
}
