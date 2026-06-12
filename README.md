# FlexingActivities

A native Android app that ranks four outdoor/indoor activities — **Skiing, Surfing, Outdoor Sightseeing, Indoor Sightseeing** — for any location using real-time weather forecasts from the Open-Meteo API.

https://github.com/user-attachments/assets/da6769bb-ef81-4bd4-945d-cb33786b85e6

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Platform and Tooling](#2-platform-and-tooling)
3. [Architecture and Technical Decisions](#3-architecture-and-technical-decisions)
4. [How to Build and Run](#4-how-to-build-and-run)
5. [Testing Strategy and How to Run Tests](#5-testing-strategy-and-how-to-run-tests)
6. [API Usage Notes](#6-api-usage-notes)
7. [Activity Recommendation Logic](#7-activity-recommendation-logic)
8. [Assumptions](#8-assumptions)
9. [Trade-offs and Omissions](#9-trade-offs-and-omissions)
10. [Production-Readiness Notes](#10-production-readiness-notes)
11. [Cross-Platform Delivery Notes](#11-cross-platform-delivery-notes)
12. [AI Usage Disclosure](#12-ai-usage-disclosure)

---

## 1. Project Overview

FlexingActivities tells you which activities are worth doing at a given location today, and for each of the next seven days.

**Three user-facing screens:**

- **Search** — find a city by name (geocoding) or enter coordinates manually.
- **Favorites** — pinned locations each showing a live score card for all four activities.
- **Details / Ranking** — full per-activity ranking with an overall score and a 7-day daily breakdown; includes a save-to-favorites toggle.

Scores are computed entirely on-device from the raw hourly weather forecast; no backend ranking service is involved.

---

## 2. Platform and Tooling

| Concern | Choice |
|---|---|
| Platform | Android (minSdk 28, targetSdk 37, compileSdk 37) |
| Language | Kotlin 2.3 |
| UI toolkit | Jetpack Compose (BOM 2026.05.01) + Material3 |
| Navigation | Navigation Compose 2.x (type-safe `@Serializable` routes) |
| Networking | Ktor 3.5 (OkHttp engine, ContentNegotiation, kotlinx-serialization) |
| Local storage | Room 2.8 |
| Dependency injection | Koin 4.2 |
| Build system | Gradle (Kotlin DSL) + `build-logic` included build with convention plugins |
| Code generation | KSP (Room, Koin annotations) |
| Test libraries | kotlinx-coroutines-test, Turbine, Compose UI test |

**Why Ktor over Retrofit?** Ktor is a Kotlin-first, multiplatform-ready HTTP client. Switching to a KMP target later requires no networking rewrite — only an engine swap (OkHttp → Darwin/CIO).

**Why Koin over Hilt?** Koin requires no annotation processing, keeps compile times shorter across 14 modules, and is fully KMP-compatible.

---

## 3. Architecture and Technical Decisions

### Module graph

```
:app
├── :core-ui                  Design system (AppTheme, AppButton, AppCard, ScoreBadge, …)
│
├── :domain:core              Resource<T>, ResourceError, executeUseCase, dispatcher Koin module
├── :domain:geocoding         GeoLocation model, GeocodingRepository (interface), SearchLocationsUseCase
├── :domain:weather           HourlyWeather, ActivityRanking, 4 resolvers, GetLocationRankingUseCase
├── :domain:favorites         FavoriteLocation, FavoritesRepository (interface), CRUD + refresh use cases
│
├── :data:core                Shared Ktor HttpClient (Koin module)
├── :data:geocoding           GeocodingRemoteDataSource, GeocodingRepositoryImpl (internal), DTOs
├── :data:weather             WeatherRemoteDataSource, WeatherRepositoryImpl (internal), DTOs
├── :data:favorites           Room DB + DAO, FavoritesRepositoryImpl (internal)
│
├── :feature:core             MVI infrastructure (delegates, interfaces), ObservableSideEffect
├── :feature:search           Search screen + ViewModel + Presentation
├── :feature:favorites        Favorites screen + ViewModel + Presentation
└── :feature:details          Details screen + ViewModel + Presentation
```

**Dependency rules:**
- `:domain:X` depends only on `:domain:core`.
- `:data:X` depends on `:data:core` + `:domain:X` + `:domain:core`.
- `:feature:X` depends on `:feature:core` + `:core-ui` + `:domain:X` only.
- `:app` is the single module that aggregates all Koin modules and owns the NavHost.
- Feature modules never depend on each other or on any `:data:*` module.

### MVI presentation layer

Each screen follows a consistent MVI pattern provided by `:feature:core`:

- **State** — `MutableStateFlow` guarded by a `Mutex`, exposed as `StateFlow`.
- **Intent** — sealed interfaces dispatched through `handleIntent`.
- **Event** — one-shot side effects delivered via a conflated `Channel` and collected in the composable via `ObservableSideEffect`.
- State reducers are free functions (`MviStateReducer<S>`) defined alongside the state in a `*Presentation.kt` file, keeping transformation logic out of the ViewModel.

### Build infrastructure

A `build-logic` included build defines reusable convention plugins:

| Plugin | Applied to |
|---|---|
| `flexing.android.application` | `:app` |
| `flexing.android.library` | Android library modules |
| `flexing.android.library.compose` | Modules with Compose UI |
| `flexing.kotlin.library` | Pure-JVM domain modules |
| `flexing.android.room` | `:data:favorites` |
| `flexing.koin` | All modules using Koin |
| `flexing.ktor` | `:data:core`, `:data:geocoding`, `:data:weather` |
| `flexing.test.unit` | Unit test setup |
| `flexing.test.instrumented` | Instrumented (Compose UI) test setup |

This eliminates repetitive `android {}` blocks across 14 modules.

### Design system

`AppTheme` wraps Material3 with a custom game-like aesthetic:
- Dark mode follows system settings exclusively (`isSystemInDarkTheme()`). Dynamic color is disabled so the game palette is always applied consistently.
- Rounded corner shapes via `RoundedCornerShape` in `Shape.kt`.
- `AppButton` includes a pressed-state "push down" animation.
- `ScoreBadge` maps 0–100 scores to a color gradient (red → amber → green).
- All design components ship `@PreviewLightDark` previews.

---

## 4. How to Build and Run

### Prerequisites

- Android Studio Quali (2024.3) or newer.
- JDK 11 (set via Android Studio or `JAVA_HOME`).
- No API keys required — Open-Meteo is free and keyless.

### Build

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requires signing config)
./gradlew assembleRelease
```

### Run on a device or emulator

```bash
./gradlew installDebug
```

Or use the **Run** button in Android Studio with any connected device or AVD (API 28+).

### Verify the Koin DI graph at startup

The app calls `startKoin` in `Application.onCreate`. Any missing binding will throw at launch rather than silently at runtime. A Koin `checkModules` test can also be wired in CI — see [Production-Readiness Notes](#10-production-readiness-notes).

---

## 5. Testing Strategy and How to Run Tests

### Layers tested

| Layer | What is tested | Tools |
|---|---|---|
| `:domain:core` | `executeUseCase` error-mapping (`IOException → NoNetwork`, HTTP 404 → `NotFound`, 5xx → `Server`) | JUnit4, kotlinx-coroutines-test |
| `:domain:weather` | All four resolvers: scoring math, hard-penalty zeros, daily/overall aggregation, Indoor inversion | JUnit4, fake `HourlyWeather` data |
| `:domain:geocoding` / `:domain:favorites` | Use cases with fake repositories; `Resource.Success/Error` paths and dispatcher forwarding | JUnit4, Turbine, fake repositories via `testFixtures` |
| Feature ViewModels | Intent → state reduction, emitted side effects | JUnit4, Turbine, `UnconfinedTestDispatcher`, MockK / hand-written fakes |
| Compose screens | UI rendering, state transitions, click delegation | `createComposeRule()`, Espresso |

### Run all unit tests

```bash
./gradlew :app:allUnitTests
```

This aggregator task (registered in `:app`) depends on `testDebugUnitTest` across every module, so a single command covers all layers.

### Run all instrumented (Compose UI) tests

```bash
./gradlew :app:allUiTests
```

Same pattern — delegates to `connectedDebugAndroidTest` in every module that has instrumented tests. Requires a connected device or running emulator.

### Fake repositories

Domain modules expose test fixtures via `testFixtures` source sets (`FakeGeocodingRepository`, `FakeWeatherRepository`, `FakeFavoritesRepository`). These are real implementations backed by in-memory state rather than mocks, which prevents the mock/reality divergence that mocked-repository tests are prone to.

---

## 6. API Usage Notes

### Open-Meteo Geocoding API

```
GET https://geocoding-api.open-meteo.com/v1/search
  ?name=<query>
  &count=10
  &language=en
```

- Returns up to 10 candidate locations with `id`, `name`, `country`, `latitude`, `longitude`, `admin1`, `timezone`.
- Empty or `null` results are mapped to `ResourceError.NotFound`.
- No authentication required.

### Open-Meteo Forecast API

```
GET https://api.open-meteo.com/v1/forecast
  ?latitude=<lat>
  &longitude=<lon>
  &hourly=temperature_2m,apparent_temperature,relative_humidity_2m,
          precipitation_probability,precipitation,rain,showers,snowfall,snow_depth,
          pressure_msl,cloud_cover,visibility,wind_speed_10m,wind_gusts_10m,
          wind_direction_10m,weather_code
  &wind_speed_unit=ms
  &forecast_days=7
```

- The `wind_speed_unit=ms` parameter requests m/s directly; no client-side km/h conversion is needed.
- Snow depth is returned in metres and converted to centimetres (×100) during DTO mapping.
- Visibility is returned in metres and kept in metres internally; resolvers convert to km at scoring time.
- The response includes `timezone` and `utc_offset_seconds`, used to group hourly points into local calendar days.
- No authentication required. Rate limits are generous for personal/low-volume use; see Open-Meteo terms for commercial thresholds.

### Error handling

All network calls are wrapped in `executeUseCase`, which maps:

| Exception | `ResourceError` |
|---|---|
| `IOException` | `NoNetwork` |
| Ktor `ResponseException` 404 | `NotFound` |
| Ktor `ResponseException` 5xx | `Server` |
| Any other `Exception` | `Custom(message)` |

---

## 7. Activity Recommendation Logic

Full parameter definitions, weights, ideal ranges, hard-penalty thresholds, and rationale are documented in [`ACTIVITIES_RANKING.md`](ACTIVITIES_RANKING.md). This section summarises the implementation.

### Scoring pipeline

1. **Fetch** — `WeatherRemoteDataSource` retrieves 7 days of hourly forecasts.
2. **Group** — hourly points are grouped by local calendar date using the API-supplied timezone.
3. **Aggregate** — each day is reduced to a `DailyWeather`:
   - Averages (temperature, wind speed, humidity, visibility, pressure, cloud cover, precipitation probability) are computed over **daytime hours (06:00–20:00 local time)** only. Night readings are excluded so scores reflect the hours when activities actually occur.
   - Precipitation totals (rain, showers, snowfall, precipitation) use the **full 24-hour sum** so overnight events are not missed.
   - Snow depth uses the **daily maximum**; weather code uses the **worst (highest-severity) code** of the day.
4. **Score parameters** — a shared `score(value, idealLow, idealHigh, hardLow?, hardHigh?)` function returns 0–100 using piecewise-linear interpolation:
   - Inside the ideal range → 100.
   - At or beyond a hard threshold → 0.
   - Between ideal edge and hard threshold → linear interpolation.
   - When no hard threshold is supplied → tapers to 0 over a distance equal to the ideal range width.
5. **Weighted sum** — parameter scores are multiplied by their weights and summed → daily score (0–100).
6. **Overall score** — mean of the daily scores.

### Per-activity weights

| Parameter | Skiing | Surfing | Outdoor | Indoor |
|---|---|---|---|---|
| Snow depth | 25% | — | — | — |
| Snowfall | 20% | — | — | — |
| Wind speed | 15% | 50% | 5% | 20% |
| Visibility | 15% | 10% | 10% | 10% |
| Temperature | 10% | 15% | — | 25% (discomfort) |
| Rain / Precipitation | 10% | 10% | 20% | 35% |
| Cloud cover | 5% | — | 5% | — |
| Apparent temperature | — | — | 30% | — |
| Precip. probability | — | — | 25% | — |
| Relative humidity | — | — | 5% | — |
| Sea-level pressure | — | 15% | — | — |
| Weather code severity | — | — | — | 10% |

### Special scoring rules

- **Cloud cover** uses a soft floor of 30 (never 0) because no extreme is a hard deal-breaker.
- **Indoor Sightseeing** is inverted: bad outdoor weather scores high. There is no hard-zero condition — severe weather only raises the score.
- **Temperature discomfort** (Indoor) is a custom function: comfortable range 18–25 °C → 0; freezing (≤ −10 °C) or extreme heat (≥ 40 °C) → 100.
- **WMO weather code severity** only scores codes representing hazards not already captured by other parameters (fog, freezing precipitation, heavy snow, thunderstorms). Rain and drizzle codes are skipped to avoid double-counting with the precipitation parameter.

---

## 8. Assumptions
- **No geogrpahical common sense checking.** The scoring system doesn't include geographical checks for activities because of the API limitations. For example, you cannot do surfing in Berlin as there are no major water resources around it - thus, the Surfing score should be 0, but it actually isn't. 
- **No wave data.** A production surf ranking would use swell height, swell period, swell direction, and tides. These are not available from Open-Meteo. The current surfing score is a wind-and-comfort proxy only.
- **No coastline orientation.** Wind direction's contribution to surf quality depends on the angle between wind and coastline, which is not derivable without a coastline dataset. Wind direction was therefore dropped from the surfing model, and its weight (30%) was redistributed proportionally across the remaining parameters (resulting in wind speed carrying 50%).
- **Single location per Details request.** Rankings are always computed for a single (lat, lon) pair. Area-wide comparison across multiple cities is not implemented.
- **Daytime window 06:00–20:00.** This is assumed appropriate for all four activities globally. Near polar latitudes in winter, the fallback to all-day averaging applies automatically.
- **Scores are advisory, not safety guidance.** The hard-penalty thresholds (e.g. wind > 20 m/s → 0 for skiing) reflect typical operational limits, not safety certifications.
- **Cached scores are point-in-time snapshots.** Favorites store the scores computed at the time of the last refresh; they do not auto-refresh in the background.

---

## 9. Trade-offs and Omissions

| Omission / Trade-off | Reason |
|---|---|
| No DataStore / settings screen | Nothing to persist in v1 (theme follows system; no unit preferences). Re-introduce when a real persisted preference is needed. |
| No Google Maps SDK | Manual lat/lon entry covers the use case. The search input layer is designed so a map picker can be added later without changing the ViewModel. |
| Wind direction excluded from surfing | Coastline-relative bearing not available in the dataset. Documented explicitly in `ACTIVITIES_RANKING.md`. |
| No background score refresh | Favorites are refreshed on demand (pull-to-refresh or per-card). Background refresh would require WorkManager and a periodic job, which adds complexity without clear user benefit for an infrequently opened app. |
| No dynamic color | Disabled intentionally so the game palette is always applied consistently regardless of Android 12+ wallpaper-derived colors. |
| No wave / swell data | Open-Meteo does not provide swell parameters in its free forecast API. |
| `weatherCode` not used in skiing / surfing / outdoor scoring | The WMO code component was designed for Indoor Sightseeing where it captures qualitative hazards (fog, ice, thunderstorms) not covered by other parameters. Adding it to other activities would double-count effects already captured by precipitation and visibility. |

---

## 10. Production-Readiness Notes

**What is production-ready:**
- Clean module boundaries prevent accidental data-layer coupling in features.
- `executeUseCase` provides a single, consistent error-mapping layer across all use cases.
- Repository implementations are `internal` — the only public surface is the domain interface.
- Unit tests cover the scoring engine end-to-end, including hard-penalty zeros and edge cases.
- Compose UI tests verify each screen renders correctly across states.

**What would need attention before shipping:**
- **API rate limits.** Open-Meteo has a fair-use policy. A production app should add a caching layer (TTL-based, e.g. cache hourly data for 1 hour) to avoid hammering the API on repeated Details visits.
- **Obfuscation / minification.** Enable R8 with keep rules for Ktor serialization and Room generated classes.
- **Error UX.** Network errors currently surface as a text error state. A retry button and differentiated messaging (no connection vs. server error) would improve resilience.
- **Accessibility.** `ScoreBadge` communicates score via color; a content description or numeric label should be added for screen readers.
- **Pagination.** Geocoding results are capped at 10. A "load more" mechanism would help for common city names.
- **Analytics / crash reporting.** Neither is wired in v1.

---

## 11. Cross-Platform Delivery Notes

The architecture was designed with KMP migration in mind, even though v1 is Android-only.

**What is already KMP-ready:**
- All `:domain:*` modules are pure Kotlin with no Android dependencies. They can be converted to `kotlin("multiplatform")` modules by switching the Gradle plugin and adding an `androidMain` source set — no source changes required.
- Ktor is a multiplatform library. Only the engine declaration (`ktor-client-okhttp` → `ktor-client-darwin` on iOS) lives in `:data:core`, a single file change.
- Koin supports KMP natively.
- The scoring engine (`ScoreHelper`, resolvers) is pure Kotlin math — zero platform coupling.
- Room is KMP-ready, just needs simple additional database file setup via expect/actual pattern.

**What would require effort:**
- **Compose Multiplatform.** Feature modules use Jetpack Compose, which is Android-only. Compose Multiplatform (JetBrains) covers iOS and desktop with the same API, but migration requires testing UI on each target.

A pragmatic migration path: convert domain and data modules to KMP first (the lowest-risk step), then evaluate Compose Multiplatform for the UI layer in a later phase.

---

## 12. AI Usage Disclosure

Claude (Anthropic) was used throughout this project as a pair-programming assistant:

- **Boilerplate generation.** Repetitive scaffolding (convention plugins, DTO structures, Koin module wiring, test scaffolding) was generated with Claude and verified by the developer before committing.
- **Scoring logic review.** The `ScoreHelper` functions, resolver weights, and hard-penalty thresholds were cross-checked against the specifications in `ACTIVITIES_RANKING.md`. Any discrepancy between the spec and the implementation was identified and resolved manually.
- **README and documentation.** This document was drafted with Claude's assistance and reviewed for accuracy against the live codebase.

**What AI did not replace:** Architecture design, Code review, manual smoke testing, score sanity checks against real weather data (e.g. Berlin in winter), and the judgment calls documented in the Trade-offs section. All generated code was read and understood before being accepted. Poor AI-assisted verification — shipping code that was generated but not understood — was explicitly avoided.
