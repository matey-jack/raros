package raros;

import de.tuberlin.bbi.dr.ConfiguredConnection;
import de.tuberlin.bbi.dr.LayoutController;
import de.tuberlin.bbi.dr.TrackSection;
import de.tuberlin.bbi.dr.Vehicle;
import de.tuberlin.bbi.dr.impl.BaseDeviceImpl;
import loconet.LocoNetHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import raros.drive.Infrastruktur_SG;

import static de.tuberlin.bbi.dr.Turnout.Position.CLOSED;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static int ROTE_LOK = 6;
    public static int BLAUE_LOK = 7;

    static void logTrackState(int track, TrackSection.State state) {
        System.out.println("Track " + track + " is now " + state.toString() + ".");
    }

    public static ConfiguredConnection configureController() {

//        Logger.getLogger(BaseDeviceImpl.class).setLevel(Level.INFO);
//        Logger.getLogger(LayoutController.class).setLevel(Level.INFO);
//        Logger.getLogger("de.tuberlin.bbi.dr").setLevel(Level.INFO);
//        Logger.getLogger(LocoNetHandler.class).setLevel(Level.INFO);

        Properties properties = new Properties();
        properties.put("lbserver.ip", "192.168.188.47");
        properties.put("lbserver.port", "1234");

        return LayoutController.configureAndSetConnection(properties);
    }

    public static void main(String[] args) {
        ConfiguredConnection conn = null;
        try {
            conn = configureController();
            if (conn.getHandler().isConnected()) {
                LayoutController.addTrackSectionStateListener(Main::logTrackState);
                //lokTest();
                logAllTrackStates();

                sleepSeconds(2);
            }
        } finally {
            if (conn != null) conn.getHandler().close();
        }
    }

    private static void logAllTrackStates() {
        var sections = Infrastruktur_SG.allTrackSections
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        LayoutController::trackSectionByAddress
                ));
        var states = new HashMap<Integer, TrackSection.State>();
        Infrastruktur_SG.allTrackSections.forEach((sec) -> {
            var state = sections.get(sec).getTrackSectionState();
            System.out.println("Section " + sec + ": " + state.toString());
            states.put(sec, state);
        });
        do {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {

            }
            Infrastruktur_SG.allTrackSections.forEach((sec) -> {
                var state = sections.get(sec).getTrackSectionState();
                if (state != states.get(sec)) {
                    System.out.println("Section " + sec + " changed to " + state.toString() + ".");
                }
            });
        } while (true);
    }

    /*
        Linke Seite vom Puffer bis zum Stoß im Holz.
        Speed 30/128 FWD, Zeit 47 Sek.
        Speed 30/128 REV, Zeit 49 s.
        Speed 60/128 REV, Zeit 19,8 s.
        Speed 60/128 FWD, Zeit 20,1 s.

        Doppelte Strecke bis zum zweiten Stoß
        Speed 127 FWD 9,6 s, REV 9,5 s
        Speed 80 FWD 25,6 s, REV 25,9 s
        Speed 96 FWD 18,1 s, REV ca. 18 s
     */
    static void calibrate() {
        var start = System.currentTimeMillis();
        Vehicle vehicle = LayoutController.vehicleByAddress(BLAUE_LOK);
        vehicle.setDirection(Vehicle.Direction.NORMAL);
        vehicle.setSpeedStep(96);
        var elapsed = System.currentTimeMillis() - start;
        vehicle.setSpeedStep(0);
        System.out.println("Elapsed time in milli-seconds " + elapsed);
    }

    private static void lokTest() {
        Vehicle vehicle = LayoutController.vehicleByAddress(BLAUE_LOK);
        // The Vehicle class provides methods for setting light, direction, speed and coupling.
        //vehicle.setLight(Vehicle.Light.ON);
        vehicle.setDirection(Vehicle.Direction.REVERSE);
        vehicle.setSpeedStep(96);
        sleepSeconds(30);
        vehicle.setSpeedStep(0);
    }

    public static void weichenTest() {
        var point = LayoutController.turnoutByAddress(Infrastruktur_SG.W1);
        point.setPosition(CLOSED);
        System.out.println("closed");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {

        }
        //point.setPosition(THROWN);
    }


    public static void sleepSeconds(int secs) {
        if (secs < 0) return;
        var trackSection = LayoutController.trackSectionByAddress(Infrastruktur_SG.MIDDLE_TRACK_SECTION);
        var oldState = trackSection.getTrackSectionState();
        System.out.println("Initial middle section state: " + oldState.toString());
        try {
            for (int i = 0; i < secs * 10; i++) {
                TimeUnit.MILLISECONDS.sleep(100);
                var newState = trackSection.getTrackSectionState();
                if (newState != oldState) {
                    String time = i / 10 + "," + (i % 10) + " s";
                    System.out.println("[" + time + "] Changed middle section state: " + oldState.toString());
                    oldState = newState;
                }
            }
        } catch (InterruptedException e) {

        }
    }
}
