package raros.plan;

import com.fasterxml.jackson.core.type.TypeReference;

public enum RarosDataType {
    // TODO: put generic type instances here instead. (field will need a different type
    YARD_TRACKS_CAR_ORDER_GIVEN("yard-tracks-car-order/given", Tracks.class),
    YARD_TRACKS_CAR_ORDER_TARGET("yard-tracks-car-order/target", ShuntingTask.class),
    SHUNTING_PLAN("raros-shunting-plan", ShuntingPlan.class);

    final String value;
    final Class dataClass;

    RarosDataType(String value, Class dataClass) {
        this.value = value;
        this.dataClass = dataClass;
    }

    public static Class getClassFor(String type) {
        for (var v : RarosDataType.values()) {
            if (v.value.equals(type)) return v.dataClass;
        }
        return null;
    }
}
