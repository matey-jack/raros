
a. Zustand der Infrastruktur
----------------------------

Es gibt keinen getrennten Gleisfreimeldeabschnitt für den Weichenbereich, daher kann die Gleisfreimeldung nicht benutzt werden, um den "Kehrpunkt" einer Rangierfahrt zu bestimmen.

Es gibt auch kein Odometer an der Lokomotive und die Geschwindigkeit ist nicht proportional zum per LocoNet eingestellten Geschwindigkeitswert. Eine Ortsbestimmung per Zeit- und Geschwindigkeitsbasiertem Odometer müsste daher die Geschwindigkeit für jede Geschwindigkeitsstufe kalibrieren und als Tabelle vorhalten. 

Bei unserem letzten Test funktionierte die Gleisfreimeldung überhaupt nicht, sodass sie nicht einmal für teil-automatische Kalibrierung des virtuellen Odometers verwendet werden kann.

Schließlich verfügt die Lok über keinen Annäherungssensor, sodass zum Annähern an einen anzukuppelnden Wagen nur das virtuelle Odometer in Verbindung mit einer genau bekannten Position des Wagens verwendet werden kann. Dies ist Fehler-anfällig und führt leicht zu Kollisionen bei maximaler Rangiergeschwindigkeit (25 km/h oder sogar 40 km/h).

Weichen können automatisch gestellt werden und ihr Zustand abgefragt.


b. Realisierbare Automatisierung
--------------------------------

Sowohl für das vorsichtige Anrücken an Wagen zum Kuppeln als auch das Bestimmen des Kehrpunktes auf dem Ausziehgleis ist ein menschlicher Mitarbeiter erforderlich. Dadurch wird die "automatisierte Rangierfahrt" eher zu einer "ferngesteuerten Rangierfahrt", was immerhin einen Mitarbeiter einspart.

Wir schlagen daher folgendes Modell des Rangierprozesses vor:
 - Der Rangierbegleiter führt den Prozess mit einem Tablet aus. (Ersatzweise Steuerung per Laptop im Labor.)
 - Er steuert mit dem Tablet Fahrtrichtung und Geschwindigkeit der Lok.
 - Er sieht auf dem Tablet welche Wagen an- und abzukuppeln sind.
 - Wenn er das Kuppeln abgeschlossen hat bzw. auf dem Ausziehgleis die Weichen alle frei gefahren hat, bestätigt er das auf dem Tablet. Dann werden automatisch die Weichen umgestellt, geprüft und dann die nächste Fahrt freigegeben.
 - Da die Steuerung zentral erfolgt, kann man technisch sicherstellen, dass nicht gefahren wird, während die Weichen umgestellt werden und auch in dieser Zeit nicht losgefahren werden kann.


c. Driver UI
------------

Leertaste = Stop oder Langsamfahrt (vielleicht so 19 aus 127?)
Zifferntasten = Geschwindigkeit 19 + 12 * Ziffer. Also 0 ist genauso schnell wie Leertaste und 9 ist genau 127.
    Alternativ die Buchstaben C V B N M, welche praktisch über der Leertaste liegen. C = Leertastenspeed, M = Max Speed.

Enter = Bestätigung des nächsten Rangier-Schritts. 
Damit man hier nicht versehentlich vorzeitig draufhaut, soll vielleicht vorher die Bedingung bestätigt werden, durch:
 - Anklicken von "Weiche ist frei gefahren" oder
 - Anklicken aller Wagen, die an- oder abgekuppelt wurden.

Mir gefällt, dass für die kritische Operation Maus und Tastatur verwendet werden. Aber halt! 
Das bedeutet ja, dass wir um ein Fensterhaftes UI nicht herum kommen!?!?

d. Anforderungen an Infrastruktur für verbesserte Automatisierung
-----------------------------------------------------------------

Prio 1: Gleisfreimeldeabschnitt für den Weichenbereich – hierdurch könnten Rangierfahrten automatisch auf dem Ausziehgleis kehren, inklusive der Umstellung der Weichen. (Die Rückfahrt muss allerdings manuell überwacht werden, denn bei starker Belegung der Gleise wird der Zug den Weichenbereich nicht frei fahren können, wenn auf einem Gleis der Gleisharfe gekuppelt wird.)

Prio 2: Annäherungssensor für den letzten Wagen (Funkverbindung zur Lok bzw. RaRoS-Zentrale. Muss manuell vom Rbl. umgehängt werden. Erkennt aber automatisch, dass er falsch hängt, wenn beim Fahren Richtung Ausziehgleis der Abstand zum Hindernis nicht größer wird.)
Mit diesem kann automatisch an zu kuppelnde Wagen herangefahren werden. Dann muss der Rbl wirklich nur noch aufpassen und Kuppeln.

Prio 3: Odometer / Umdrehungszähler an der Lokomotive. Dieser ist eher als zusätzliche Sicherheitsebene geeignet. Um zuverlässig zu sein, müsste sie wahrscheinlich anhand der Gleisfreimeldung und ggf. zusätzlicher Marker kalibriert werden.

