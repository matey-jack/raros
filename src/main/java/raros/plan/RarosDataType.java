package raros.plan;

public enum RarosDataType {
    YARD_TRACKS_CAR_ORDER_GIVEN("yard-tracks-car-order/given"),
    YARD_TRACKS_CAR_ORDER_TARGET("yard-tracks-car-order/target"),
    SHUNTING_PLAN("raros-shunting-plan");

    final String value;
    RarosDataType(String value) {
        this.value = value;
    }
}
