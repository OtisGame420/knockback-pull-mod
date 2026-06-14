# Knockback Pull Mod

Fabric mod dla Minecraft 1.21.4. Przyciąga pobliskie entity w stronę gracza po naciśnięciu klawisza **R**.

## Funkcje
- Zasięg: 6 bloków
- Klawisz: `R` (konfigurowalny w ustawieniach klawiszy)
- Cooldown: 0.5 sekundy
- Efekty cząsteczkowe i dźwięk przy użyciu

## Wymagania
- Minecraft 1.21.4
- Fabric Loader ≥ 0.15.0
- Fabric API

## Budowanie

Pobierz `.jar` z zakładki **Actions** na GitHubie (artefakt po każdym pushu).

Lub lokalnie (wymaga JDK 21):
```bash
./gradlew remapJar
```
Plik wyjściowy: `build/libs/knockback-pull-mod-1.0.0.jar`
