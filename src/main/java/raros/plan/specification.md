a – Format für den Zustand, d.h. Wagen-Positionen vor und nach dem Rangieren (Ist, Soll, und dann wieder Ist-Zustand)
---------------------------------------------------------------------------------------------------------------------

Wagen-IDs sind beliebige Zeichenkombinationen, die zur Funktion des Programms nur innerhalb jeder Beschreibung eindeutig sein sollten;
in der Realität wäre es natürlich am besten, wenn sie weltweit eindeutig sind, indem z.B. jedes Transportunternehmen ein Präfix
reserviert und dann eindeutige Suffixe vergibt.

Im Rangierplan spielt zunächst nur die Reihenfolge der Wagen auf den Gleisen eine Rolle, daher wird nur diese in den 
Schnittstellen übergeben, und zwar einfach durch Listen, wobei das Ausziehgleis am Ende der Liste ist (weil man von dort ja Wagen anfügt und wegnimmt).
Wenn die Lok einen Annäherungssensor (z.B. in Form des Rangierbegleiters) hat, reicht es auch schon, um das Rangieren durchzuführen.
Alternativ können die Zentimeter-genauen Positionen der Wagen noch in einem separaten Datensatz übergeben werden, 
aber eigentlich braucht man doch sowieso den Annäherungssensor bzw. eine Kamera.

Um den Aufwand beim Rangieren zu beschränken, sollte die Reihenfolge der Wagen im Ergebnis nicht zu stark beschränkt sein.
Wir erreichen das, indem eine Zugbeschreibung generell nur als "Menge von Wägen" interpretiert wird, deren Reihenfolge nicht relevant ist. Damit innerhalb eines Gleises trotzdem noch eine Reihenfolge erzeugt werden kann, erlauben wir im Datenmodell,
dass auf einem Gleis mehrere Züge stehen können, deren Reihenfolge wiederum relevant ist. 
So kann ein Zug aus mehreren Teilzügen zusammengestellt werden, welche kurz vor Abfahrt zusammengekuppelt werden.
(Diese Funktion erspart sehr viel Rangierzeit, Software-technisch sind die Auswirkungen jedoch auf den Plan-Teil der Anwendung beschränkt.)

Soll- und Ist-Datenformat sind technisch gleich, aber mit unterschiedlicher Bedeutung:
Im Ist-Zustand ist ja die genaue Position aller Wagen bekannt (und wird auch benötigt),
daher ist die Reihenfolge der Wagen im Zug relevant (Interpretation als geordnete Liste),
während sie im Soll-Zustand nicht relevant ist (Interpretation der Liste als Menge).

Aktuell verlangt unsere Spezifikation, dass ein Soll-Zustand alle Wagen erwähnen muss, die auch im Ist-Zustand vorkommen.
Das heißt, es sollen keine Wagen mit undefinierter Position zurückbleiben. Diese Restriktion könnte man später noch aufheben. 

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
- Es muss für jeden Zug im SOLL-Zustand schon im IST-Zustand ein freies Gleis vorliegen. D.h. alle Ziel-Gleise müssen frei sein und zusätzlich ein weiteres freies Gleis für jeden Zug über dem ersten pro Zielgleis!
- Unter diesen Umständen hat ein Rangier-Plan immer dieselbe Form und jeder Wagen wird höchstens zwei Mal bewegt:
   * einmal auf das Gleis seines Zugs,
   * und am Ende die Züge (außer dem ersten, der sich schon dort befindet) aufs Zielgleis.


((
Randbemerkung dazu: wenn die Ziel-Gleise am Anfang gar nicht alle frei sind, kann man denselben Algorithmus anwenden,
nachdem man sie frei geräumt hat, indem man Wagen auf andere Startgleise verteilt. 
Aber man muss dann die maximale Gleislänge berücksichtigen, was alles komplizierter macht.

Besonders fies wird es, wenn die Wage ursprünglich schon in sehr ähnlicher Position zur Zielposition stehen, 
denn dann sind die Rangier-Schritte eher die Lösung eines Logikrätsels als Abarbeitung eines Algorithmus.
))

