import de.tuberlin.bbi.dr.LayoutController;
import de.tuberlin.bbi.dr.Turnout;
import de.tuberlin.bbi.dr.Vehicle;

import static de.tuberlin.bbi.dr.Turnout.Position.CLOSED;
import static de.tuberlin.bbi.dr.Turnout.Position.THROWN;

import java.util.Properties;

public class Main {
    public static int ROTE_LOK = 6;
    public static int BLAUE_LOK = 7;

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("lbserver.ip", "192.168.1.1");
        properties.put("lbserver.port", "1234");

        LayoutController.configureAndSetConnection(properties);

        Vehicle vehicle = LayoutController.vehicleByAddress(ROTE_LOK);
        // The Vehicle class provides methods for setting light, direction, speed and coupling.
        vehicle.setLight(Vehicle.Light.ON);
        vehicle.setDirection(Vehicle.Direction.NORMAL);
        vehicle.setSpeedStep(25);
        vehicle.setCoupling(Vehicle.Coupling.CLOSED);

    }

    public static void weichen_beispiel() {
        var turnout = LayoutController.turnoutByAddress(312);
        turnout.setPosition(CLOSED);
        turnout.setPosition(THROWN);

    }
}
