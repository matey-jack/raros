package raros.plan;

import java.util.List;

public record ShuntingStep(
   String fromTrack,
   String toTrack,

   // The order here is with the highest index being at the locomotive.
   // This keeps the same sequence as in the track state and makes it easy to read.
   List<String> pickCars,

   // `dropCars` are the same car-IDs as `pickCars`, in the same order, but grouped according to how they should be decoupled.
   // Optionally, the last car already present on the target track will be listed here if the first car in this packet
   // should be coupled to it.
   List<List<String>> dropCars
) { }
