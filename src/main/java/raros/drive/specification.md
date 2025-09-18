
a. Zustand der Infrastruktur
----------------------------

Es gibt keinen getrennten Gleisfreimeldeabschnitt für den Weichenbereich, daher kann die Gleisfreimeldung nicht benutzt werden, um den "Kehrpunkt" einer Rangierfahrt zu bestimmen.

Es gibt auch kein Odometer an der Lokomotive und die Geschwindigkeit ist nicht proportional zum per LocoNet eingestellten Geschwindigkeitswert. Eine Ortsbestimmung per Zeit- und Geschwindigkeitsbasiertem Odometer müsste daher die Geschwindigkeit für jede Geschwindigkeitsstufe kalibrieren und als Tabelle vorhalten. 

Bei unserem letzten Test funktionierte die Gleisfreimeldung überhaupt nicht, sodass sie nicht einmal für teil-automatische Kalibrierung des virtuellen Odometers verwendet werden kann.

Schließlich verfügt die Lok über keinen Annäherungssensor, sodass zum Annähern an einen anzukuppelnden Wagen nur das virtuelle Odometer in Verbindung mit einer genau bekannten Position des Wagens verwendet werden kann. Dies ist Fehler-anfällig und führt leicht zu Kollisionen bei maximaler Rangiergeschwindigkeit (25 km/h oder sogar 40 km/h).

Weichen können automatisch gestellt werden und ihr Zustand abgefragt.


b. Realisierbare Automatisierung
--------------------------------

Sowohl für das vorsichtige Anrücken an Wagen zum Kuppeln als auch das Bestimmen des Kehrpunktes auf dem Ausziehgleis ist ein menschlicher Mitarbeiter erforderlich. 
Dadurch wird die "automatisierte Rangierfahrt" eher zu einer "ferngesteuerten Rangierfahrt", was immerhin einen Mitarbeiter einspart.

Wir schlagen daher folgendes Modell des Rangierprozesses vor:
 - Der Rangierbegleiter führt den Prozess mit einem Tablet aus. (Ersatzweise Steuerung per Laptop im Labor.)
 - Er steuert mit dem Tablet die Geschwindigkeit der Lok.
   * Bei der Fahrt ins Abstellgleis erfolgt dabei die Fahrt vollständig manuell, da die genaue Position der Wagen auf dem Gleis nicht bekannt ist. 
   * Die Fahrt zurück ins Ausziehgleis erfolgt zunächst automatisch: Es wird so weit herausgefahren, wie zuvor hineingefahren wurde. Wegen der zusätzlichen Wagen an der Lok (und nicht-konstanter Geschwindigkeit, die zu signifikanten Fehlern in der Pseudo-Odometrie führt) muss aber ein Mensch prüfen, dass die Weichen frei gefahren wurden und bei Bedarf noch etwas weiter fahren.
 - Er sieht auf dem Tablet welche Wagen an- und abzukuppeln sind.
 - Wenn er das Kuppeln abgeschlossen hat bzw. auf dem Ausziehgleis die Weichen alle frei gefahren hat, bestätigt er das auf dem Tablet. Dann werden automatisch die Weichen umgestellt, geprüft und dann die nächste Fahrt freigegeben.
 - Die Fahrtrichtung der Lok wird dabei vollständig automatisch bestimmt. Nur beim Kuppeln kann manuell langsam etwas vor- oder zurückgesetzt werden.
 - Da die Steuerung zentral erfolgt, kann man technisch sicherstellen, dass nicht gefahren wird, während die Weichen umgestellt werden und auch in dieser Zeit nicht losgefahren werden kann.

Durch das automatische Zurücksetzen erspart man sich das Kalibrieren eines virtuellen Odometers.

c. Driver UI
------------

Leertaste = Stop oder Langsamfahrt (vielleicht so 19 aus 127?)
Zur Präsenzprüfung des Rbl muss die Taste (Leertaste oder Touchscreenbutton) gedrückt bleiben oder die Lok hält an.
Die Geschwindigkeit kann ebenso nur verändert werden, wenn die Taste dabei gedrückt bleibt. 
Dazu dienen die Tasten direkt über der Leertaste: C - Creep/Kriechgang, N - Normal, M - Maximal.

Enter = Bestätigung des nächsten Rangier-Schritts. 
Damit man hier nicht versehentlich vorzeitig draufhaut, soll vielleicht vorher die Bedingung bestätigt werden, durch:
 - Anklicken von "Weiche ist frei gefahren" oder
 - Anklicken aller Wagen, die an- oder abgekuppelt wurden.

d. Anforderungen an Infrastruktur für verbesserte Automatisierung
-----------------------------------------------------------------

Prio 1: Gleisfreimeldeabschnitt für den Weichenbereich – hierdurch könnten Rangierfahrten automatisch auf dem Ausziehgleis kehren, inklusive der Umstellung der Weichen. (Die Rückfahrt muss allerdings manuell überwacht werden, denn bei starker Belegung der Gleise wird der Zug den Weichenbereich nicht frei fahren können, wenn auf einem Gleis der Gleisharfe gekuppelt wird.)

Prio 2: Annäherungssensor für den letzten Wagen (Funkverbindung zur Lok bzw. RaRoS-Zentrale. Muss manuell vom Rbl. umgehängt werden. Erkennt aber automatisch, dass er falsch hängt, wenn beim Fahren Richtung Ausziehgleis der Abstand zum Hindernis nicht größer wird.)
Mit diesem kann automatisch an zu kuppelnde Wagen herangefahren werden. Dann muss der Rbl wirklich nur noch aufpassen und Kuppeln.

Prio 3: Odometer / Umdrehungszähler an der Lokomotive. Dieser ist eher als zusätzliche Sicherheitsebene geeignet. Um zuverlässig zu sein, müsste sie wahrscheinlich anhand der Gleisfreimeldung und ggf. zusätzlicher Marker kalibriert werden.

