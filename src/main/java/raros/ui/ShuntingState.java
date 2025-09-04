package raros.ui;

public enum ShuntingState {
    DRIVING_IN, COUPLING, DRIVING_OUT, SWITCHING_POINTS;

    boolean isDriving() {
        return this == DRIVING_IN || this == DRIVING_OUT;
    }
}
