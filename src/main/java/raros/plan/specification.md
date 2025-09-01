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

TODO: Kuppeln von Wagen auf dem Zielgleis:
 - Kuppeln des ersten Wagens an einen vorhandenen Wagen durch Bool'schen Wert anzeigen.
 - Entkuppeln von Wagen, die gemeinsam gebracht werden, anzeigen durch wiederholte Schritte vom Typ DROP.

c – Beschränkung der Funktionalität
-----------------------------------

Rangieren mit dem besten Durchsatz wird ja bekanntlich über einen Ablaufberg gemacht, so dass jeder Wagen von seinem Ankunftszug zu seinem Ziel-Gleis rollt. (Nur wenn demnächst kein Zug für diesen Wagen fährt, muss er ggf. später nochmal ablaufen.)

Wenn man hingegen auf nur einer Gleisharfe rangiert und dort beliebige IST und SOLL Kombinationen von Wagenpositionen erlaubt, 
dann kann es passieren, dass zur Herstellung des Zielzustands Wägen mehrmals bewegt werden müssen, wobei der Personalaufwand schnell unbezahlbar wird. 
Auch wird der Planungsalgorithmus dadurch sehr kompliziert. 

Damit der ganze Prozess überschaubar bleibt, schlage ich Folgendes vor:
- Der SOLL-Zustand kann pro Gleis nur einen Zug vorsehen. Innerhalb jedes Zuges im SOLL-Zustand ist die Reihenfolge der Wagen irrelevant. (Die sich ergebende Reihenfolge wird aber im Ergebnis zurück gemeldet.) Nennen wir die Anzahl der Gleise G.
- Die maximale Anzahl von Wagen auf einem Gleis ist für alle Gleise gleich. Nennen wir die Anzahl der Wagen pro Gleis WG.
- Auf dem Ausziehgleis haben auch WG Wagen plus Lokomotive und Sicherheitsabstand zu Weichen Platz.
- Die Anzahl von insgesamt Wagen vorhandenen Wagen ist beschränkt, sodass mindestens ein Gleis völlig frei sein kann. Nennen wir die gesamte Anzahl der Wagen WW, dann soll also WW ≤ (G-1) × WG sein. 


d - Algorithmus
===============

Wir klassifizieren alle Gleise in solche auf denen bereits nur "korrekte Wagen" (im Zielzustand) stehen ("freie Gleise")
und solche auf denen noch andere Wagen stehen ("Eingangsgleise").
Da die Lok ein ganzes Gleis leer ziehen kann, erhalten wir somit mindestens ein freies Gleis.

Dritte Idee:
man könnte es sich fast ganz einfach machen und alle Gleise der Reihe nach ganz herausziehen und Wagen direkt auf das Zielgleis bringen und einfach dort anhängen.
Wenn das Zielgleis noch nicht "frei" ist, stehen die Wagen halt so lange hinter den anderen Wagen, die dann später noch wegrangiert werden. 
Dabei kann es aber leider passieren, dass ein Gleis voll wird... in diesem Fall stellt man einfach die aktuell an der Lok befindlichen Wagen ab und zieht das volle Gleis aus, um damit weiter zu arbeiten.

Fakt ist: in jedem Drop, landet mindestens ein Wagen auf dem richtigen Gleis, sodass jeder Wagen zwar sehr oft mit der Lok hin- und her fahren kann, aber nur ein einziges Mal wirklich gedroppt wird.
Und weil der Algorithmus mit jedem Ausziehen auch mindestens einmal "droppt"

Falle: dieser Algorithmus ist vielleicht schon nah am Optimum, aber wird in dieser Situation fehl schlagen:
Limit pro Gleis: 4 Wagen
Große Buchstaben sind Gleise. Kleine Buchstaben sind Wagen mit Zielgleis.
A b b b b 
B a a a a
C 
Optimale Lösung ist alle Wagen von Gleis A oder B auf C zu schieben, dann kann man in zwei weiteren Schritten alle Wagen richtig einsortieren.
Obiger Algorithmus würde abstürzen, weil er nur von A auf B oder umgekehrt ziehen will.

Man sollte die Algorithmus also abändern: wenn das Zielgleis voll ist, stelle die Wagen nicht auf das Zielgleis, sondern ein beliebiges anderes Gleis.
Man kann sich jetzt wieder ein Beispiel ausdenken, bei dem der neue Algorithmus zur Verteilung von bereits richtig gruppierten Wagen führt:

Limit pro Gleis: 6 Wagen, Gesamt-Limit 18 Wagen
A b b b b 
B a b c a b c
C a a a a 
D c c c c 
Dieses Beispiel ist fies, weil alle Zielgleise im Endzustand voll sind. Man muss also zwangsweise viele Wagen auf Gleis D schieben. 
Und bei Gleis A anzufangen ist besonders schlecht, da Zielgleis B bereits voll ist. 
Eine Lösung:
 - Wagen von C auf A und D verteilen.
 - Dann können alle c Wagen von D nach C.
 - Dann alle b Wagen von A nach D schieben.
Zwischenstand:
A a a
B a b c a b c
C c c c c 
D a a b b b b
Dann geht es direkt weiter:
 - Alle Wagen von B können jetzt verteilt werden.
 - Schließlich alle Wagen von D verteilen.

Resultat: es wurden nur jene 6 Wagen zwei Mal abgestellt, die im Zwischenzustand in D waren.

Erkenntnis: Wegen der Symmetrie des Beispiels wäre das Vorgehen und der Resultierende Aufwand bei Beginn mit Gleis A der gleiche!
Besser wäre es, mit Gleis B zu beginnen: dort können tatsächlich alle Wagen direkt verteilt werden.
Zwischenstand:
A b b b b a a 
B b b 
C a a a a c c 
D c c c c 

Jetzt kann man im nächsten Schritt die Wagen von Gleis A alle verteilen, dann von Gleis C, dann von Gleis D.
Resultat: es wurde jeder Wagen nur einmal abgestellt!

In diesem Fall ist also der originale, banale Algorithmus (direkt zum Zielgleis schieben) wieder optimal.
Also erstmal diesen implementieren und später optimieren.

Frage: 
 - Was ist die optimale Reihenfolge, in der Gleise herausgezogen werden? Gibt es immer eine Reihenfolge, bei der jedes Gleis nur einmal gezogen wird?
 - Da man jetzt das Gleis nicht mehr frei machen muss, hat man einen Vorteil, wenn man erstmal mit der Verteilung der "vorderen" Wagen beginnt?
   - Statt Wagen, die auf dem Gleis bleiben sollen, gleich mit heraus zu ziehen, kann man diese Wage erstmal auf dem Gleis belassen und statt sie dann wieder auf dem Gleis abzustellen, erst dann herausziehen, wenn man an die nächsten umzuschiebenden Wagen heran will. Dann sammeln sich quasi die "richtigen" Wagen des aktuellen Arbeitsgleises an der Lok und nicht auf dem Gleis selbst.
     ==> in anderen Worten, statt am Anfang schon sehr viele Wagen an der Lok zu haben, führt das dazu, dass man am Ende sehr viele Wagen an der Lok hat.


--------------------------------------------------------------------------------------------------------------------------------
Zweite Idee:

Der Algorithmus kann immer gleich vorgehen, egal ob alle Zielgleise schon frei sind:
 - Markiere alle Zielgleise, die schon frei sind. (= Speichern in einer Variable)
 - Optionale Optimierung: bestimme für jedes Eingangsgleis, welches auch Zielgleis ist, wie viele Wagen von diesem Gleis auf schon freie Gleise (inkl. ihm selbst) gehören. 
    beginne dann zuerst mit dem Gleis, welches die geringste Anzahl sonstiger Wagen hat. (Weil diese nämlich mehrmals bewegt werden müssen!)
 - Ziehe ein Eingangsgleis leer, welches auch Zielgleis ist. (Oder ein anderes, wenn alle Zielgleise schon "frei" sind.)
    * Markiere dieses Gleis als "frei".
    * Verteile die Wagen dieses Gleises auf die Zielgleise, wenn sie dorthin gehören, und sonstige Wagen auf die restlichen Eingangsgleise.
    * In letzterem Fall bevorzuge Eingangsgleise, an deren Ende schon ein Wagen mit demselben Ziel-Gleis steht.

--------------------------------------------------------------------------------------------------------------------------------
Erste Idee:

1. Räume so viele Ziel-Gleise wie möglich frei, indem Wagen von dort auf anderen Gleisen zusammen gefasst werden.
    - Falls einige Gleise keine Ziel-Gleise sind, werden Wagen dort bevorzugt hingefahren.
    - Alle Gleise, auf denen die Wagen dann stehen, nennen wir "Arbeitsgleise".
    - Am Ende des Prozesses bleiben WG Wagen an der Lok hängen und können direkt im nächsten Schritt verwendet werden.
    - Wenn alle Zielgleise jetzt schon leer sind, springe direkt zu Schritt 3.
   
2. Verteile Wagen auf die bisher freien Zielgleise. 
   - Ziehe dazu aus den Arbeitsgleisen Teilzüge, an deren freiem Ende sich Wagen befinden, die auf die bisher freien Zielgleise sollen. Wagen, deren Zielgleis noch nicht frei ist, bleiben dabei an der Lok.
   - Räume dabei zunächst die anderen Zielgleise bevorzugt frei. Dass heißt, sobald die gesamte Anzahl der Wagen auf ein Gleis weniger passt, fasse sie dort zusammen. 

3. Wenn alle Zielgleise frei sind (dass heißt, nur noch Wagen, die dort sein SOLLen, sind auch dort), kann die Verteilung schneller erfolgen:
    - Alle Wagen werden gesamt aus einem Arbeitsgleis gezogen und die jeweils vorderen auf das richtige Zielgleis abgesetzt.

