# Graphite Plate — AI session context

## Current goal

Hand-drawn-style Jetpack Compose calorie tracker (Standard scope:
calories + macros + meal categories + history + weight + settings).
UI is hybrid pencil-sketch: procedural Canvas strokes over a procedural
paper background.

## Status

Scaffold complete (2026-05-24). Project root: `/home/ycs/disk6T/samule/kiro-cli_ground`.
30 Kotlin files, 10 XML files, full Gradle build setup.

## Decisions made

- AGP 8.5.2 + Kotlin 2.0.20 (compose plugin handled via
  `org.jetbrains.kotlin.plugin.compose`).
- KSP for Room; no Hilt (manual DI through `GraphitePlateApp`).
- DataStore Preferences for the small set of user prefs (calorie goal,
  macro split). No Proto.
- Min SDK 26, target SDK 34.
- Single activity (`MainActivity`) + Compose Navigation.
- All UI drawing primitives live in `ui/sketch/SketchStroke.kt`. They
  use deterministic seeds derived from stable keys so the sketch
  doesn't shimmer between recompositions.

## Module layout

```
app/src/main/java/com/graphiteplate/
  GraphitePlateApp.kt   Application class (DI container)
  MainActivity.kt       Single activity + Scaffold + PaperBackground + NavGraph
  data/
    CalorieRepository.kt
    local/{AppDatabase, Entities, FoodEntryDao, WeightEntryDao, Mappers}.kt
    prefs/UserPreferences.kt
  domain/model/Models.kt
  ui/
    nav/GraphitePlateNavGraph.kt
    screen/{Home, AddFood, History, Weight, Settings}Screen.kt
    sketch/{SketchStroke, SketchCard, SketchButton, SketchProgress,
            SketchTextField, SketchAccents}.kt
    theme/{SketchColors, SketchTypography, SketchTheme, PaperBackground}.kt
    vm/{HomeViewModel, AddFoodViewModel, SecondaryViewModels (History/Weight/Settings),
        ViewModelFactory}.kt
```

## Known gaps / next steps

- Cannot compile in this environment (no JDK/Gradle/Android SDK on the
  ground machine). User must open in Android Studio Iguana+ and run
  the first sync, which generates `gradlew` + the wrapper jar.
- No automated tests yet — the natural first ones would be:
  - `CalorieRepositoryTest` covering insert/observe round-trip.
  - `AddFoodViewModelTest` covering `canSave` + sanitize behavior.
- No food database / barcode / cloud sync (out of scope, see README).
- Dark theme intentionally omitted (a "pencil on slate" variant was not
  designed).

## Audit notes (post-write)

- `ViewModelFactory.kt` uses plain `@Composable inline reified` with
  `noinline build` → no `crossinline` clash.
- `SketchTextField.decorationBox` wraps placeholder + inner in a `Box`
  so they overlay rather than stacking with no layout.
- All `DrawScope` extensions defined inside `object SketchStroke` are
  imported by member name at every call site (verified via grep).
- `MealChip` uses `Modifier.clickable(onClick = ...)` for tap handling.
- No trailing whitespace, all UTF-8 LF.
