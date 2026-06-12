# Weather-Based Activity Ranking System

## Overview

This document defines the weather parameters, weights, ideal conditions, and scoring rationale used to rank cities for the following activities:

- Skiing
- Surfing
- Outdoor Sightseeing
- Indoor Sightseeing

The goal is to calculate an activity-specific score (0–100) that reflects how suitable current or forecasted weather conditions are for a given activity.

In addition to ideal ranges, this document defines **hard penalty thresholds** where conditions become dangerous, impractical, or fundamentally incompatible with the activity. When a hard penalty threshold is reached, the corresponding parameter should receive a score of **0**.

## Sources

The parameter selection, weighting rationale, and threshold recommendations are derived from a combination of:

### Skiing

- https://www.mdpi.com/2073-4433/13/10/1569
- https://www.snowtrex.co.uk/magazine/safe-skiing/skiing-in-all-weathers/

### Surfing

- https://surflearner.com/wind-for-surfing-explained/

### Outdoor Human Comfort & Sightseeing

- https://en.wikipedia.org/wiki/Apparent_temperature
- https://www.weather.gov/arx/why_dewpoint_vs_humidity

### General Weather Interpretation

- https://open-meteo.com/en/docs
- https://www.wmo.int/

---

# Skiing

## Objective

Rank cities based on snow quality, snow availability, visibility, safety, and overall skier comfort.

## Parameters

| Parameter         | Weight | Ideal Range | Hard Penalty (Score = 0)                                        | Description                                    | Reasoning                                                        |
| ----------------- | ------ | ----------- | --------------------------------------------------------------- | ---------------------------------------------- | ---------------------------------------------------------------- |
| Snow Depth        | 25%    | > 50 cm     | < 10 cm                                                         | Total snow accumulated on the ground           | Primary indicator of skiable terrain and snow coverage           |
| Snowfall          | 20%    | 2–15 cm/day | 0 cm snowfall for extended periods combined with low snow depth | New snow received during the evaluation period | Fresh snowfall improves skiing conditions                        |
| Visibility        | 15%    | > 10 km     | < 500 m                                                         | Maximum visible distance                       | Essential for navigation and safety                              |
| Wind Speed (10m)  | 15%    | < 5 m/s     | > 20 m/s                                                        | Average surface wind speed                     | Strong winds reduce comfort and may close lifts                  |
| Temperature (2m)  | 10%    | -6°C to 0°C | > 10°C                                                          | Air temperature near the surface               | Influences snow quality (powder vs slush)                        |
| Rain              | 10%    | 0 mm        | > 5 mm/day                                                      | Liquid precipitation                           | Rain degrades snow quality and creates unsafe conditions         |
| Cloud Cover Total | 5%     | 20–70%      | No hard penalty                                                 | Percentage of sky covered by clouds            | Excessive cloud cover may reduce visibility and terrain contrast |

## Scoring Logic

### Notes

- Rain above 5 mm/day should reduce the Rain score to 0.
- Wind speeds above 20 m/s should reduce the Wind score to 0 due to likely lift closures and safety concerns.
- Visibility below 500 m should reduce the Visibility score to 0.
- Snow depth below 10 cm should reduce the Snow Depth score to 0 because skiing becomes impractical.

---

# Surfing

## Objective

Rank cities based on weather conditions that contribute to comfortable and clean surfing conditions.

> Note: A production-grade surf ranking should additionally use wave height, swell height, swell period, swell direction, and tides. These are not available in the current dataset.

## Parameters

| Parameter          | Weight | Ideal Range   | Hard Penalty (Score = 0) | Description                                | Reasoning                                         |
| ------------------ | ------ | ------------- | ------------------------ | ------------------------------------------ | ------------------------------------------------- |
| Wind Speed (10m)   | 50%    | 0–2.5 m/s     | > 15 m/s                 | Average surface wind speed                 | Wind strongly influences wave quality             |
| Sea Level Pressure | 15%    | 1005–1025 hPa | No hard penalty          | Atmospheric pressure adjusted to sea level | Indicates weather systems that may generate swell |
| Temperature (2m)   | 15%    | 20–30°C       | < 0°C or > 40°C          | Air temperature                            | Affects user comfort                              |
| Visibility         | 10%    | > 10 km       | < 500 m                  | Maximum visible distance                   | Impacts safety and overall experience             |
| Precipitation      | 10%    | 0 mm          | > 10 mm/day              | Total precipitation                        | Heavy rain reduces comfort and visibility         |

> **Trade-off**: Wind Direction (originally 30%) is excluded because scoring it requires a coastline-relative bearing for each city, which is not available in the current dataset. Its weight has been redistributed — 10% to Wind Speed and 5% to each remaining parameter.

## Scoring Logic

### Notes

- Wind speeds above 15 m/s should receive a score of 0.
- Visibility below 500 m should receive a score of 0.
- Heavy rainfall above 10 mm/day should receive a score of 0.

---

# Outdoor Sightseeing

## Objective

Rank cities based on overall human comfort and conditions suitable for walking, exploring, and viewing attractions.

## Parameters

| Parameter                 | Weight | Ideal Range | Hard Penalty (Score = 0) | Description                                         | Reasoning                                    |
| ------------------------- | ------ | ----------- | ------------------------ | --------------------------------------------------- | -------------------------------------------- |
| Apparent Temperature      | 30%    | 18–25°C     | < -10°C or > 40°C        | Perceived temperature considering humidity and wind | Best representation of human thermal comfort |
| Precipitation Probability | 25%    | 0–20%       | > 90%                    | Chance of precipitation occurring                   | Visitors generally avoid rainy conditions    |
| Precipitation             | 20%    | 0 mm        | > 10 mm/day              | Expected precipitation amount                       | Direct impact on outdoor activities          |
| Visibility                | 10%    | > 10 km     | < 500 m                  | Maximum visible distance                            | Important for scenic views and landmarks     |
| Relative Humidity         | 5%     | 40–70%      | > 95%                    | Moisture content of the air                         | Affects comfort levels                       |
| Wind Speed (10m)          | 5%     | < 4 m/s     | > 20 m/s                 | Average surface wind speed                          | Strong winds reduce comfort                  |
| Cloud Cover Total         | 5%     | 20–50%      | No hard penalty          | Percentage of cloud cover                           | Moderate cloud cover often improves comfort  |

## Scoring Logic

### Notes

- Apparent temperature should be preferred over raw temperature.
- Precipitation above 10 mm/day should receive a score of 0.
- Visibility below 500 m should receive a score of 0.
- Wind speeds above 20 m/s should receive a score of 0.
- Apparent temperatures below -10°C or above 40°C should receive a score of 0.

---

# Indoor Sightseeing

## Objective

Rank cities based on how attractive indoor activities become under current weather conditions.

Examples include:

- Museums
- Art galleries
- Aquariums
- Shopping centers
- Historic indoor attractions

Unlike other activities, poor outdoor weather generally increases the attractiveness of indoor sightseeing.

## Parameters

| Parameter              | Weight | Ideal Range     | Maximum Score Conditions            | Description                             | Reasoning                                      |
| ---------------------- | ------ | --------------- | ----------------------------------- | --------------------------------------- | ---------------------------------------------- |
| Precipitation          | 35%    | > 10 mm/day     | ≥ 20 mm/day                         | Total precipitation                     | Rain encourages indoor activities              |
| Temperature Discomfort | 25%    | < 0°C or > 35°C | ≤ -10°C or ≥ 40°C                   | Deviation from comfortable temperatures | Extreme heat or cold drives visitors indoors   |
| Wind Speed (10m)       | 20%    | > 10 m/s        | ≥ 20 m/s                            | Average surface wind speed              | Strong winds discourage outdoor exploration    |
| Visibility             | 10%    | < 2 km          | ≤ 500 m                             | Maximum visible distance                | Poor visibility reduces outdoor appeal         |
| Weather Code           | 10%    | Severe weather  | Thunderstorms, blizzards, dense fog, freezing precipitation | Encoded weather condition               | Captures qualitatively unique hazards not already covered by other parameters |

## Scoring Logic

### Conditions That Increase Indoor Sightseeing Score

| Condition    | High Score Threshold                        |
| ------------ | ------------------------------------------- |
| Heavy Rain   | ≥ 20 mm/day                                 |
| Snowstorm    | Weather code indicating heavy snow/blizzard |
| Dense Fog    | Visibility ≤ 500 m                          |
| Extreme Cold | ≤ -10°C                                     |
| Extreme Heat | ≥ 40°C                                      |
| Strong Wind  | ≥ 20 m/s                                    |

### Example Comfort Threshold

Comfortable outdoor temperature:

- 18–25°C

As temperature moves away from this range:

- Indoor sightseeing score increases.

Unlike the other activities, there are no weather conditions that automatically invalidate indoor sightseeing. Severe weather generally increases its attractiveness.

---

# Recommended Core Weather Dataset

The following variables provide the best balance between accuracy, interpretability, and implementation complexity:

- Apparent Temperature
- Relative Humidity
- Precipitation Probability
- Precipitation
- Snowfall
- Snow Depth
- Wind Speed (10m)
- Wind Direction (10m)
- Wind Gusts (10m)
- Visibility
- Cloud Cover Total
- Weather Code
- Sea Level Pressure

These variables should be sufficient for generating robust rankings across all four supported activities.

---

# Scoring Implementation Notes

## Daytime-Only Aggregation

All **averaged** metrics (temperature, apparent temperature, wind speed, humidity, visibility, pressure, cloud cover) are computed from daytime hours only — **06:00–20:00 local time**.

Rationale:
- All four activities happen during the day. Night readings drag averaged values toward colder, calmer conditions that do not reflect the actual experience.
- Averaging a full 24-hour window would produce misleadingly pessimistic scores, especially for temperature and wind.

Precipitation totals (rain, snowfall, showers, snow depth) are **not** filtered — they accumulate across the full 24-hour period so overnight events are not under-counted.

If a given day has no points in the 06:00–20:00 window (e.g. near-polar latitudes in winter), all hourly points are used as a fallback.

---

## Cloud Cover Scoring Floor

Cloud cover uses a **soft minimum of 30** rather than a hard 0 at extremes. Unlike temperature or wind speed, no cloud cover extreme is a hard deal-breaker for any activity — both fully clear and fully overcast skies are suboptimal but rarely prohibitive.

The scoring function:
- Returns **100** within the ideal range.
- Linearly interpolates between **30** (worst extreme) and **100** (ideal edge) outside the range.
- Never returns below **30**, regardless of how far the value drifts from ideal.

This prevents cloud cover from over-penalising a day relative to parameters that carry genuinely dangerous hard thresholds (e.g. wind > 20 m/s → 0).

---

## Weather Code Coverage

Not all Open-Meteo WMO codes contribute a non-zero severity score. Only codes that represent a **qualitatively unique hazard** not already captured by another parameter are scored:

| Code(s) | Description | Severity Score | Rationale |
| ------- | ----------- | -------------- | --------- |
| 45, 48 | Fog / depositing rime fog | 70 | Unique hazard — not captured by any other parameter |
| 56, 57 | Freezing drizzle, light and heavy | 60 | Icing hazard — precipitation volume alone does not convey the danger |
| 66, 67 | Freezing rain, light and heavy | 70 | Same as above, more intense |
| 71–77 | Snowfall (slight–heavy), snow grains | 80 | Unique hazard — partially overlaps snowfall parameter but captures ongoing conditions |
| 85, 86 | Snow showers, slight and heavy | 80 | Same as above |
| 95–99 | Thunderstorms (with or without hail) | 100 | Most severe category |

**Intentionally skipped codes (score = 0):**

| Code(s) | Description | Why skipped |
| ------- | ----------- | ----------- |
| 0–3 | Clear sky to overcast | Covered by the cloud cover parameter |
| 51–55 | Drizzle, light to dense | Covered by the precipitation parameter |
| 61–65 | Rain, slight to heavy | Covered by the precipitation parameter |
| 80–82 | Rain showers, slight to violent | Covered by the precipitation parameter |

Scoring these skipped codes would double-count the effect of precipitation and cloud cover, inflating the Weather Code component's contribution.
