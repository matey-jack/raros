# raros
Rangier Roboter Systemanalyse und Konzeption, Abteilung Entity-Relationship Diagramme

```mermaid
---
title: Entities & Relationships für den Gleisplan
---
erDiagram
    WEICHE {
        %% Unveränderliche Eigenschaften %%
        %% Liegt die Weiche mit der Spitze in aufsteigender Richtung der Kilometrierung?
        bool spitze_aufsteigend
        
        %% Aktueller Zustand %%
        bool befahrbar
        bool verstellbar
        %% Aktuelle Lage der Weiche. Wenn nicht 'gerade' dann 'abzweigend'.
        bool liegt_gerade
    }
    WEICHE many(0)..1 GLEIS_ABSCHNITT : Spitze
    WEICHE many(0)..1 GLEIS_ABSCHNITT : Gerade
    WEICHE many(0)..1 GLEIS_ABSCHNITT : Abzweig

    GLEIS_ABSCHNITT {
        int start_in_metern
        int ende_in_metern
        %% Optionaler Wert; leer bei Zwischenabschnitten auf denen keine Wagen stehen gelassen werden
        string nummer
    }
```


```mermaid
---
title: Entities & Relationships für die Rangieraufgabe
---
erDiagram
    GLEIS {
        string nummer
    }
    WAGEN {
        string nummer        
    }
    WAGEN many(0)..1 GLEIS 
```