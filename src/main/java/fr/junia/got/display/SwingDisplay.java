package fr.junia.got.display;

import fr.junia.got.entity.Master;
import fr.junia.got.simulation.Config;
import fr.junia.got.simulation.Simulation;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Affichage graphique dans une fenêtre Swing. Comme ConsoleDisplay, il implémente Display :
 * la simulation ne sait pas quel affichage elle utilise. C'est aussi la fenêtre qui règle
 * le rythme de la partie : pause, pas à pas et vitesse.
 */
public class SwingDisplay implements Display {

    /** Verrou partagé entre la simulation (qui attend) et les boutons (qui la relancent). */
    private final Object lock = new Object();
    private volatile int delayMs = Config.DELAY_MS;
    private boolean paused;
    private boolean stepRequested;

    private WorldView view;
    private JButton pauseButton;
    private JButton stepButton;

    @Override
    public void showIntro(Simulation simulation) {
        runOnSwingThread(() -> buildWindow(simulation));
        publish(StepSnapshot.of(simulation, null));
    }

    @Override
    public void render(Simulation simulation) {
        publish(StepSnapshot.of(simulation, null));
        waitBeforeNextStep();
    }

    @Override
    public void showResult(Simulation simulation) {
        publish(StepSnapshot.of(simulation, resultText(simulation)));
        SwingUtilities.invokeLater(() -> {
            pauseButton.setEnabled(false);
            stepButton.setEnabled(false);
        });
    }

    /** Crée la fenêtre : la vue au centre, les commandes en bas. */
    private void buildWindow(Simulation simulation) {
        view = new WorldView(simulation.getMap().getWidth(), simulation.getMap().getHeight());

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(event -> togglePause());
        stepButton = new JButton("Pas suivant");
        stepButton.setEnabled(false);
        stepButton.addActionListener(event -> requestStep());
        JSlider speed = new JSlider(0, 600, delayMs);
        speed.addChangeListener(event -> delayMs = speed.getValue());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        controls.add(pauseButton);
        controls.add(stepButton);
        controls.add(new JLabel("Pause entre deux pas (ms) :"));
        controls.add(speed);

        JFrame frame = new JFrame("GameOfThrones MAS");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(view, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /** Envoie un état à la vue, dans le thread de Swing. */
    private void publish(StepSnapshot snapshot) {
        SwingUtilities.invokeLater(() -> view.update(snapshot));
    }

    /** Attend le délai choisi, puis bloque la simulation tant que la partie est en pause. */
    private void waitBeforeNextStep() {
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        synchronized (lock) {
            while (paused && !stepRequested) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            stepRequested = false;
        }
    }

    private void togglePause() {
        boolean nowPaused;
        synchronized (lock) {
            paused = !paused;
            nowPaused = paused;
            lock.notifyAll();
        }
        pauseButton.setText(nowPaused ? "Reprendre" : "Pause");
        stepButton.setEnabled(nowPaused);
    }

    private void requestStep() {
        synchronized (lock) {
            stepRequested = true;
            lock.notifyAll();
        }
    }

    private static String resultText(Simulation simulation) {
        Master winner = simulation.getWinner();
        if (winner == null) {
            return "Match nul\nPlusieurs Maîtres ont le meilleur score · " + simulation.getStep() + " pas";
        }
        return "Vainqueur : " + winner.getHouse().getDisplayName() + "\n" + winner.getName() + " réunit "
                + winner.getScore() + " messages sur " + simulation.getTotalMessages() + " · "
                + simulation.getStep() + " pas";
    }

    private static void runOnSwingThread(Runnable task) {
        try {
            SwingUtilities.invokeAndWait(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Impossible d'ouvrir la fenêtre", e.getCause());
        }
    }
}
