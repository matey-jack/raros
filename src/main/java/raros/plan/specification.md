a – Format für den Zustand, d.h. Wagen-Positionen vor und nach dem Rangieren (Ist, Soll, und dann wieder Ist-Zustand)
---------------------------------------------------------------------------------------------------------------------

Wagen-IDs sind beliebige Zeichenkombinationen, die zur Funktion des Programms nur innerhalb jeder Beschreibung eindeutig sein sollten;
in der Realität wäre es natürlich am besten, wenn sie weltweit eindeutig sind, indem z.B. jedes Transportunternehmen ein Präfix
reserviert und dann eindeutige Suffixe vergibt.

Im Rangierplan spielt zunächst nur die Reihenfolge der Wagen auf den Gleisen eine Rolle, daher wird nur diese in den 
Schnittstellen übergeben, und zwar einfach durch Listen, immer von vorn (Seite des Ausziehgleises) nach hinten.
Wenn die Lok einen Annäherungssensor (z.B. in Form des Rangierbegleiters) hat, reicht es auch schon, um das Rangieren durchzuführen.
Alternativ können die Zentimeter-genauen Positionen der Wagen noch in einem separaten Datensatz übergeben werden, 
aber eigentlich braucht man doch sowieso den Annäherungssensor bzw. eine Kamera.

Um den Aufwand beim Rangieren zu beschränken, sollte die Reihenfolge der Wagen im Ergebnis nicht zu stark beschränkt sein.
Wir schlagen daher als Datenmodell vor, dass der Soll-Zustand der zu erstellenden Züge durch "Wagenpakete" beschrieben wird.
Alle Wagen eines Pakets hängen ununterbrochen zusammen, innerhalb des Pakets jedoch ist die Reihenfolge frei.
So kann der Zug am Zwischenbahnhof leicht getrennt werden.
(Diese Funktion erspart sehr viel Rangierzeit, Software-technisch sind die Auswirkungen jedoch auf den Plan-Teil der Anwendung beschränkt.)

Dieses Datenmodell ist sehr flexibel: ein Zug ohne definierte Wagenreihenfolge lässt sich durch ein einziges Wagenpaket abbilden
und ein Zug mit vollständig fest definierter Wagenreihenfolge lässt sich durch ein-Wagen-Pakete abbilden.

Soll- und Ist-Datenformat sind technisch gleich, aber mit unterschiedlicher Bedeutung:
Im Ist-Zustand ist ja die genaue Position aller Wagen bekannt (und wird auch benötigt),
daher ist die Reihenfolge der Elemente in einem Paket relevant (Interpretation als geordnete Liste),
während sie im Soll-Zustand nicht relevant ist (Interpretation der Liste als Menge).
Als Konvention verwenden wir in der Ist-Beschreibung nur ein Paket pro Zug.

Für die genaue Beschreibung siehe die Java-records "Envelope" und dort verwendete, sowie die Beispiele in src/test/resources.


b – Format für die Beschreibung des Rangierplans
------------------------------------------------

Ein "Rangierschritt" beginnt immer mit der Lok auf dem Ausziehgleis und endet auch dort.
Das muss im Plan nicht beschrieben werden.
Benötigt wird nur:
 - von welchem Gleis
 - sollen welche Wagen
 - auf welches Gleis befördert werden
 
Der Plan muss garantieren, dass die zu holenden Wagen alle am richtigen Ende des Quell-Gleises stehen.

Für die Lok ist relevant:
 - Die beiden Gleise, auf die gefahren werden soll.
 - Ob Wagen auf dem Quellgleis zum Kuppeln leicht zusammen geschoben werden sollen. 
   (Wobei der Rbl hier manuell steuern sollte, wann es reicht.)
 - Das Auseinanderziehen nach dem Entkoppeln sollte nicht nötig sein, könnte aber gewünscht sein.

Für den Rbl. ist wichtig, 
 - welche bzw. wie viele Wagen auf dem Quell-Gleis anzukoppeln sind und
 - was auf dem Ziel-Gleis an- und abgekuppelt werden soll.

Für die genaue Beschreibung siehe Source Code und Beispiele in src/test/resources.

c – Beschränkung der Funktionalität
-----------------------------------

Rangieren mit dem besten Durchsatz wird ja bekanntlich über einen Ablaufberg gemacht, so dass jeder Wagen von seinem Ankunftszug zu seinem Ziel-Gleis rollt. (Nur wenn demnächst kein Zug für diesen Wagen fährt, muss er ggf. später nochmal ablaufen.)

Wenn man hingegen auf nur einer Gleisharfe rangiert und dort beliebige IST und SOLL Kombinationen von Wagenpositionen erlaubt, 
dann kann es passieren, dass zur Herstellung des Zielzustands Wägen mehrmals bewegt werden müssen, wobei der Personalaufwand schnell unbezahlbar wird. 
Auch wird der Planungsalgorithmus dadurch sehr kompliziert. 

Damit der ganze Prozess überschaubar bleibt, schlage ich Folgendes vor:
- es muss für jedes Wagen-Paket im SOLL-Zustand schon im IST-Zustand ein freies Gleis vorliegen. D.h. alle Ziel-Gleise müssen frei sein und zusätzlich ein weiteres freies Gleis für jedes Wagenpaket über dem ersten pro Zielgleis!
- unter diesen Umständen hat ein Rangier-Plan immer dieselbe Form und jeder Wagen wird höchstens zwei Mal bewegt:
   * einmal auf das Gleis seines Wagen-Pakets
   * und dann dieses ganze Paket auf's Zielgleis


((
Randbemerkung dazu: Am einfachsten ist das Rangieren, wenn alle Zielgleise schon leer sind und es zusätzlich ein freies Gleis zum Zwischenparken gibt, denn dann kann man alle Wagen direkt von ihrer Position schieben, oder erst auf's Zwischengleis, wenn die Reihenfolge auf den Zielgleisen eingeschränkt ist. Bei starker Einschränkung der Reihenfolge kann danach noch die nun frei gewordenen Start-gleise benutzen, um einzelne Teilpakete zu gruppieren. Im schlimmsten Fall hat man aber nur ein Gleis jeder Sorte und eine fest-definierte Reihenfolge im Zielgleis, was dann zu sehr viel Hin- und Her-Fahren führt.

Noch schlimmer wird das Ganze, wenn die Ziel-Gleise am Anfang gar nicht alle frei sind. 
Man kann zwar dann versuchen, sie freizuräumen, indem man Wagen auf andere Startgleise verteilt, 
aber muss dann ja eigentlich auch maximale Gleislänge berücksichtigen, was alles komplizierter macht.

Besonders fies wird es, wenn die Wage ursprünglich schon in sehr ähnlicher Position zur Zielposition stehen, 
denn dann sind die Rangier-Schritte eher die Lösung eines Logikrätsels als Abarbeitung eines Algorithmus.
))

