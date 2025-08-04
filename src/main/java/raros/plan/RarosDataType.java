package raros.plan;

import com.fasterxml.jackson.core.type.TypeReference;

public enum RarosDataType {
    // TODO: put generic type instances here instead. (field will need a different type
    YARD_TRACKS_CAR_ORDER_GIVEN("yard-tracks-car-order/given", new TypeReference<Tracks<TrainState>>() {}),
    YARD_TRACKS_CAR_ORDER_TARGET("yard-tracks-car-order/target", new TypeReference<Tracks<TrainRequest>>() {}),
    SHUNTING_PLAN("raros-shunting-plan", new TypeReference<ShuntingPlan>() {});

    final String value;
    final TypeReference typeRef;

    RarosDataType(String value, TypeReference typeRef) {
        this.value = value;
        this.typeRef = typeRef;
    }

    public static TypeReference getClassFor(String type) {
        for (var v : RarosDataType.values()) {
            if (v.value.equals(type)) return v.typeRef;
        }
        return null;
    }
}
