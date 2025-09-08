package raros.drive;

import de.tuberlin.bbi.dr.LayoutController;
import de.tuberlin.bbi.dr.Turnout;
import javafx.application.Platform;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Switcher {
    final Gleisharfe infrastruktur;
    boolean dryRun = true;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "switcher");
        t.setDaemon(true);
        return t;
    });

    public Switcher(Gleisharfe infrastruktur) {
        this.infrastruktur = infrastruktur;
    }

    public void setRealRun() {
        dryRun = false;
    }

    public void switchTo(String track, Consumer<List<String>> reportStatus, Runnable reportDone) {
        executor.submit(() -> {
            try {
                var points = infrastruktur.fahrwegKonfiguration.get(Integer.parseInt(track));
                final var switches = points.stream().map(point -> {
                    Turnout turnout = dryRun
                            ? new FakeTurnout(point.turnoutPosition())
                            : LayoutController.turnoutByAddress(point.turnoutId());
                    return new Switch(turnout, point);
                }).toList();

                boolean done;
                do {
                    Platform.runLater(() -> reportStatus.accept(createStatus(switches)));
                    switches.forEach(s -> {
                        System.out.println("Setting " + s.weiche().turnoutId() + " to " + s.desiredPosition() + ".");
                        s.turnout().setPosition(s.desiredPosition());
                    });
                    Thread.sleep(1000);
                    done = switches.stream().allMatch(Switch::isDone);
                } while (!done);
                Platform.runLater(() -> reportStatus.accept(createStatus(switches)));
                Platform.runLater(reportDone);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private List<String> createStatus(List<Switch> switches) {
        return switches.stream()
                .sorted(Comparator.comparing(s -> s.weiche().weichenbezeichnung()))
                .map(Switch::toString).toList();
    }

    record FakeTurnout(Position position) implements Turnout {
        @Override
        public Turnout setPosition(Position position) {
            return this;
        }

        @Override
        public Position getPosition() {
            return position;
        }

        @Override
        public int getDeviceAddress() {
            return 0;
        }
    }

}
