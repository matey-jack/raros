# RaRoS - Rangier Roboter System

Rangierplaner und Rangiersteuerung sind zwei völlig unabhängige Systeme. 
Die Kommunikation erfolgt lediglich über den abgespeicherten Rangierplan im Json-Format.

## Rangierplaner

Spezifikation: [src/main/java/raros/plan/specification.md](src/main/java/raros/plan/specification.md)

Ausführung: [src/test/java/raros/plan/PlannerTest.java](src/test/java/raros/plan/PlannerTest.java)

Eine Main-Klasse für den Planer kann man leicht erstellen, aber sie hilft nicht zum Test oder Demonstration, 
daher beschränken wir uns auf Unit-Tests.

## Rangiersteuerung

Spezifikation: [src/main/java/raros/drive/specification.md](src/main/java/raros/drive/specification.md)

Ausführung: `./gradlew run`

Man kann den vollständigen Rangiervorgang auch ohne Loconet durchspielen.
Das Highlight sind die automatisch gewählten Geschwindigkeiten bei der Rückfahrt auf das Ausziehgleis. 
Denkt dran, den "Fahren" Button immer gedrückt zu halten! (Geht auch mit Leertaste. Geschwindikeit nur über Tastatur wählbar.)

