package raros.plan;

import java.util.List;

public record ShuntingStep(
   String fromTrack,
   String toTrack,

   // The order here is with index 0 being at the locomotive, such that taking a car from the track and coupling it to
   // the cars te be transported will pop and push it at the ends of each list.
   List<String> pickCars,

   // `dropCars` are the same car-IDs as `pickCars`, in the same order, but grouped according to how they should be decoupled.
   // Optionally, the last car already present on the target track will be listed here if the first car in this packet
   // should be coupled to it.
   List<List<String>> dropCars
) { }
