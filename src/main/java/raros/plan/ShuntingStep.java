package raros.plan;

public record ShuntingStep(
   String fromTrack,
   String toTrack,
   String[] pickCars,

   // `dropCars` are the same car-IDs as `pickCars`, in the same order, but grouped according to how they should be decoupled.
   // Optionally, the last car already present on the target track will be listed here if the first car in this packet
   // should be coupled to it.
   String[][] dropCars
) { }
