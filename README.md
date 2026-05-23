# Graphite Plate

A Jetpack Compose calorie tracker styled like a mechanical-engineering
student's pencil notebook: warm cream paper, jittery graphite strokes,
cross-hatch shading, and a single sanguine accent for over-the-line
moments.

The look is procedural — every border, progress bar, and gauge is drawn
with `Canvas` using a small library of pencil primitives (`SketchStroke`)
that produce deterministic, hand-drawn lines from stable seeds, so the
UI doesn't shimmer between recompositions. The paper texture is
generated once at app start (fibers + flecks + faint vignette) so no
asset bundling is required.

## Features

- Daily calorie tracking with macro breakdown (protein / carbs / fat)
- Meal categories (Breakfast / Lunch / Dinner / Snack)
- Multi-day history with per-day totals and progress bars
- Body-weight log with running history
- Configurable daily goal and macro split (Settings)
- All data is local: Room database + Preferences DataStore

## Project layout

```
app/src/main/java/com/graphiteplate/
├── GraphitePlateApp.kt            Application class — DI container
├── MainActivity.kt                 Single activity host, edge-to-edge
├── data/
│   ├── CalorieRepository.kt        Façade over Room + DataStore
│   ├── local/                      Room (entities, DAOs, mappers, db)
│   └── prefs/UserPreferences.kt    DataStore for goal + macro split
├── domain/model/Models.kt          FoodEntry, WeightEntry, MealType…
└── ui/
    ├── nav/GraphitePlateNavGraph.kt   Routes + NavHost
    ├── screen/                        HomeScreen, AddFood, History, Weight, Settings
    ├── sketch/                        Sketch* components + SketchStroke primitives
    ├── theme/                         SketchTheme, SketchColors, PaperBackground
    └── vm/                            ViewModels + factory
```

## Requirements

- Android Studio Iguana or newer (Hedgehog and earlier won't accept
  Kotlin 2.0 + AGP 8.5)
- JDK 17 (Android Studio bundles one — point Gradle to it under
  *Settings → Build, Execution, Deployment → Build Tools → Gradle*)
- Android SDK platform 34
- A device or emulator running Android 8.0 (API 26) or newer

## Build and run

1. Open the project root (`graphite-plate/` or whatever you cloned it
   as) in Android Studio. The IDE will offer to "Trust Project" and
   download Gradle 8.9 and the missing SDK pieces — accept.
2. The first sync will create `gradlew` and the wrapper jar
   automatically (Android Studio invokes `gradle wrapper` for you).
   On the command line you can do the same once Gradle 8.5+ is on
   `$PATH`:

   ```sh
   gradle wrapper --gradle-version 8.9
   ```

3. Build and install on the connected device:

   ```sh
   ./gradlew :app:installDebug
   ```

4. Or run from Android Studio (`Run > Run 'app'`).

## Theming the sketch look

The visual style lives in three small files you can tweak without
touching the rest of the app:

- `ui/theme/SketchColors.kt` — graphite tones, paper tones, the single
  sanguine accent. Replace the hex values to retune the palette.
- `ui/theme/SketchTypography.kt` — serif body, sans-serif labels.
  Swap `FontFamily.Serif` for a custom font resource if you want a
  specific notebook hand.
- `ui/sketch/SketchStroke.kt` — primitives for jittery lines, arcs,
  cross-hatch fills, smudges. Bumping `jitter` or `passes` makes every
  border feel rougher; reducing them tightens the look.

## Scope notes

This is a single-module local-only app. There is no food database
lookup, no barcode scanning, and no cloud sync; the tracker is the
notebook itself, not a client to a remote service. Adding any of those
would require new permissions and a network layer — see the data
package for the natural extension point (a `RemoteFoodSource` that
`CalorieRepository` can fall back to).
