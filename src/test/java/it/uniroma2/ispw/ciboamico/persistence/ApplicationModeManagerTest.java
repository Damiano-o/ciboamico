package it.uniroma2.ispw.ciboamico.persistence;

import it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test ApplicationModeManager: switch modalità e factory corretta.
 */
class ApplicationModeManagerTest {

    @Test
    void testDefaultDemo() {
        ApplicationModeManager manager = ApplicationModeManager.getInstance();
        assertEquals(ApplicationModeManager.MODE_DEMO, manager.getActiveMode());
    }

    @Test
    void testSwitchModalita() {
        ApplicationModeManager manager = ApplicationModeManager.getInstance();
        manager.setActiveMode(ApplicationModeManager.MODE_FS);
        assertEquals(ApplicationModeManager.MODE_FS, manager.getActiveMode());
        assertNotNull(manager.getDAOFactory());
    }

    @Test
    void testModalitaNonValida() {
        ApplicationModeManager manager = ApplicationModeManager.getInstance();
        assertThrows(IllegalArgumentException.class, () -> manager.setActiveMode("XYZ"));
    }

    @Test
    void testSingleton() {
        assertSame(ApplicationModeManager.getInstance(), ApplicationModeManager.getInstance());
    }
}
