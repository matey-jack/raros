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

Die letzte Bedingung ist ziemlich willkürlich. Ohne formalen Beweis zu liefern, vermute ich, dass ein einziger freier Platz ausreicht, um in jedem Fall einen Rangierplan zu erzeugen. Allerdings wird die Anzahl der Schritte viel länger sein, da in jedem Schritt viele Wagen aus einem Gleis gezogen werden, aber nur einer davon auf ein anderes Gleis gestellt wird. Mit mehr Platz, geht alles schneller: im Idealfall wird immer ein ganzes Gleis leer gezogen und alle Wagen direkt auf die Zielgleise verteilt.

d - Algorithmus
===============

Kurz gesagt, wird der Rangierplan wie folgt erstellt:
 - Man zieht alle Wagen aus einem Gleis heraus und verteilt diese auf ihre Gleise. Wagen, die auf dem gerade gezogenen Gleis schon richtig waren, werden auch wieder dort abgestellt. (Bzw. wenn sie am hinteren Ende standen, gar nicht erst mit ausgezogen.)
 - Ist dabei ein Zielgleis schon voll (weil dort noch Wagen stehen, die woanders hin gehören), so werden die betreffenden Wagen auf ein anderes Gleis zwischengeparkt. (Dies kann auch das Gleis sein, das wir gerade ausgezogen haben.)

Man kann sich überlegen, dass dieser Algorithmus immer ein Ende finden wird, wenn man die Anzahl der noch nicht im Zielgleis befindlichen Wagen (genannt N) betrachtet. Da wir richtige stehende Wagen (zwar manchmal ausziehen, aber) niemals auf einem anderen Gleis abstellen, wird N niemals größer werden. Wenn wir also in jedem Schritt mindestens einen Wagen vom falschen auf ein richtiges Gleis abstellen, wird N in jedem Schritt um einen ganzzahligen Betrag reduziert und somit wird N=0 und ein erfolgreiches Ende sicher erreicht werden.

Um dies zu erreichen, passieren zwei Dinge:
 1. Um das Gleis zu wählen, welches ausgezogen werden soll, zählen wir auf allen Gleisen die noch nicht richtig stehenden Wagen und wie viele davon freien Platz auf dem Zielgleis haben. Dann ziehen wir das Gleis, auf dem diese Zahl am höchsten ist (opportunistisch optimiert) – es wird unser N also in jedem Schritt (lokal / opportunistisch) maximal vermindert.

 2. Es kann passieren, dass noch Wagen falsch stehen, aber alle Zielgleise besetzt sind. Es ist eine Art Deadlock und kommt vor, wenn mindestens zwei Gleise sowohl im Ziel- als auch im Ausgangs-Zustand voll sind UND Wagen wechselseitig auf das andere Gleis sollen. (So eine zirkuläre Abhängigkeit kann auch mehrere Gleise betreffen.) In diesem Fall reicht es, irgendeins der betroffenen Gleise herauszuziehen und die Wagen auf irgendeinem freien Gleis zwischenzuparken. Im nächsten Schritt findet dann Methode 1 wieder ein Gleis, dessen Wagen platziert werden können. (Das kann man beweisen, da im Fall 2 jedes Gleis auf dem noch falsche Wagen stehen, genauso viele Wagen auf anderen Gleisen hat, die dort noch hingehören.) Im übernächsten Schritt (und bei größeren Deadlock-Kreisen) findet Methode 1 übrigens auch garantiert wieder korrekt verschiebbare Wagen, da irgendwann die zwischengeparkten Wagen auf das letzte Gleis im Kreis geschoben werden können.

Man kann auch im Schritt optimieren, indem man zuerst das Gleis auszieht, auf dem die meisten falschen Wagen stehen. Da ein Gleis Teil von mehreren Deadlock-Kreisen sein kann, können auch mehrere solche mit einem Zug aufgebrochen werden. Aber genauso wie jene im Schritt 1 ist es nur eine heuristische Optimierung.

Damit ist bewiesen, dass das Rangieren nicht ewig dauern wird, und zwar höchstens so lange wie die Anzahl der Wagen im System plus die Anzahl der Deadlock-Kreise. Letztere kann zwar sehr hoch sein, aber in der Praxis ist sie das wohl eher nicht und die Heuristik ist auch oft schneller als ihre obere Schranke. Im Übrigen kann man an den Test-Beispielen sehen, dass die Heuristik sehr gut freie Plätze im System ausnutzt. Je weniger Wagen vorhanden sind, desto schneller wird alles gehen. Es werden dann auch automatisch bereits zusammenhängende Gruppen von Wagen zusammen verschoben, was viel Kuppelarbeit spart.


### Noch ein paar Überlegungen zum Deadlock

Ein Deadlock tritt auf, wenn die Zielgleise aller Wagen, die noch nicht richtig stehen, besetzt sind.

Daraus folgt:
 - Gleise mit freien Plätzen haben auch im Zielzustand freie Plätze, da ja dort keine Wagen mehr hinzukommen.
 - Auf den voll-besetzten Gleisen befinden sich nur Wagen, die schon richtig stehen oder auf ein anderes voll-besetztes Gleis versetzt werden sollen. Damit befinden sich auf den vollbesetzten Gleisen insgesamt alle Wagen, die auf voll-besetzte Gleise sollen... folglich kann auf den nicht voll-besetzten Gleisen kein solcher Wagen mehr stehen.
 - Daraus folgt, dass die nicht voll besetzten Gleise schon vollständig korrekt sortiert sein müssen, wenn der Deadlock entsteht. 
 - Folglich wird es ausreichen, nur ein paar Wagen von einem voll-besetzten Gleis auf die nicht voll-besetzten Gleise zu verteilen. Dies können wir "Zwischenparken" nennen. Es ist der einzige Rangierschritt, bei dem sich die Anzahl schon korrekt stehender Wagen nicht erhöht. 
 - Nach dem Zwischenparken werden zumindest ein paar Wagen auf das nun freie Gleis geschoben und ein erneuter Deadlock kann erst entstehen, wenn alle Zwischengeparkten Wagen wieder auf voll-besetzte Gleise gestellt wurden. Folglich gibt es nach jedem Zwischenparken mindestens zwei Rangierschritte (Ausziehen und verteilen aller Wagen eines Gleises)  
 - Vermutlich ist der Worstcase, wenn Wagen auf vollen Gleisen paarweise vertauscht werden müssen. Generell ist einmal "Zwischenparken" pro Deadlock-Zyklus notwendig, daher ist der worst-case bei der maximalen Anzahl von Zyklen.
 - In jedem Fall gibt es aber mindestens in zwei Schritte Fortschritt pro "Zwischenparken" (ein Wagen mehr steht auf dem korrekten Zielgleis als vorher), so dass der Algorithmus garantiert immer terminieren wird.


### Schriftlich-lautes Nachdenken


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

