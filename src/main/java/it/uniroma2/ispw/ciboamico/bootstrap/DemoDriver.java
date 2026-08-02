package it.uniroma2.ispw.ciboamico.bootstrap;

import it.uniroma2.ispw.ciboamico.boundary.Navigator;
import it.uniroma2.ispw.ciboamico.control.AutenticazioneController;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Driver della demo video: naviga le schermate registrate nel Navigator,
 * attiva i bottoni reali via lookup per ID, e salva uno snapshot PNG per
 * ogni passo. Nessun input esterno: tutto avviene dentro la JVM JavaFX.
 * Output: <project>/temp/demo-frames/<NNN>-<nome>.png
 */
public final class DemoDriver {

    private DemoDriver() { }

    private static int step = 0;
    private static final String OUT_DIR = "temp/demo-frames";

    public static void run() {
        new File(OUT_DIR).mkdirs();
        demo();
    }

    /** Esegue il flusso demo con snapshot ad ogni passo. */
    private static void demo() {
        autenticazione();
        home();
        inventario();
        ricette();
        listaSpesa();
        marketplace();
        admin();
        Platform.exit();
    }

    // ------------------------------------------------------------
    private static void autenticazione() {
        switchView("login");
        pause();
        // compila i campi email/password della LoginView
        fillTextIndex(1, "mario@cibo.it");
        fillTextIndex(2, "password123");
        pause();
        snap("01-login");
        click("login", "btn-login");
        pause();
    }

    private static void home() {
        snap("02-home");
        pause();
    }

    private static void inventario() {
        click("home", "btn-inventario");
        pause();
        click("inventario", "btn-aggiorna-inventario");
        pause();
        snap("03-inventario");
        back("inventario");
    }

    private static void ricette() {
        click("home", "btn-ricette");
        pause();
        click("ricette", "btn-trova");
        pause();
        snap("04-ricette");
        back("ricette");
    }

    private static void listaSpesa() {
        click("home", "btn-lista");
        pause();
        fillText("lista-spesa", null, "Pasta al pomodoro");
        click("lista-spesa", "btn-calcola");
        pause();
        snap("05-lista-spesa");
        back("lista-spesa");
    }

    private static void marketplace() {
        click("home", "btn-market");
        pause();
        click("marketplace", "btn-catalogo");
        pause();
        snap("06-marketplace");
        fillText("marketplace", null, "Miele locale");
        click("marketplace", "btn-ordina");
        pause();
        snap("07-ordine");
        back("marketplace");
    }

    private static void admin() {
        // logout e login come admin
        click("home", null); // no-op per sicurezza
        switchView("login");
        pause();
        fillTextIndex(1, "admin@cibo.it");
        fillTextIndex(2, "password123");
        click("login", "btn-login");
        pause();
        snap("08-admin-home");
        switchView("admin");
        pause();
        click("admin", "btn-attesa");
        pause();
        snap("09-admin");
    }

    // ------------------------------------------------------------
    private static void click(String viewName, String buttonId) {
        if (buttonId == null) return;
        runOnFx(() -> {
            Parent root = Navigator.getInstance().getScene().getRoot();
            Node node = root.lookup("#" + buttonId);
            if (node instanceof Button b) {
                b.fire();
            }
        });
        pause();
    }

    private static void fillText(String viewName, String fieldId, String text) {
        runOnFx(() -> {
            Parent root = Navigator.getInstance().getScene().getRoot();
            Node node = fieldId != null ? root.lookup("#" + fieldId)
                    : findFirstTextInput(root);
            if (node instanceof TextField tf) {
                tf.setText(text);
            }
        });
        pause();
    }

    /** Riempie l'ennesimo campo di testo (1-based) del container corrente. */
    private static void fillTextIndex(int index, String text) {
        runOnFx(() -> {
            Parent root = Navigator.getInstance().getScene().getRoot();
            int count = 0;
            TextField target = null;
            for (Node n : collectTextInputs(root)) {
                count++;
                if (count == index) { target = (TextField) n; break; }
            }
            if (target != null) target.setText(text);
        });
        pause();
    }

    private static java.util.List<Node> collectTextInputs(Node root) {
        java.util.List<Node> result = new java.util.ArrayList<>();
        if (root instanceof TextField || root instanceof javafx.scene.control.PasswordField) {
            result.add(root);
            return result;
        }
        if (root instanceof javafx.scene.Parent p) {
            for (Node n : p.getChildrenUnmodifiable()) {
                result.addAll(collectTextInputs(n));
            }
        }
        return result;
    }

    private static Node findFirstTextInput(Node root) {
        if (root instanceof TextField) return root;
        if (root instanceof javafx.scene.Parent p) {
            for (Node n : p.getChildrenUnmodifiable()) {
                Node r = findFirstTextInput(n);
                if (r != null) return r;
            }
        }
        return null;
    }

    private static void back(String viewName) {
        click(viewName, null);
        switchView("home");
        pause();
    }

    /** Cambia schermata sul FX thread (mai da un thread demo). */
    private static void switchView(String viewName) {
        runOnFx(() -> Navigator.getInstance().switchTo(viewName));
        pause();
    }

    private static void snap(String nome) {
        runOnFx(() -> {
            try {
                WritableImage img = Navigator.getInstance().getScene().snapshot(null);
                File out = new File(OUT_DIR, String.format("%02d-%s.png", step++, nome));
                ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", out);
                System.out.println("[DEMO] snapshot: " + out.getName());
            } catch (Exception e) {
                System.err.println("[DEMO] snapshot fallito: " + e.getMessage());
            }
        });
    }

    private static void pause() {
        try { Thread.sleep(2200); } catch (InterruptedException ignored) { }
    }

    /** Esegue un'azione sul FX thread e attende il completamento. */
    private static void runOnFx(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try { action.run(); } finally { latch.countDown(); }
        });
        try { latch.await(); } catch (InterruptedException ignored) { }
    }
}
