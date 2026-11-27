# VIDARA Editor — Phase 1 foundation

Open this folder in **Android Studio (Koala or newer)**, let Gradle sync (it generates the Gradle wrapper), then Run.
Unit tests: right-click `TimelineTest` → Run (or `./gradlew test` after sync).

## What exists
- Hilt + Room + Compose Material 3 + Navigation, dark VIDARA theme
- Dashboard, bottom nav (Home / Templates / Create / Projects / Profile), project list
- Photo Picker import (no storage permissions) -> managed copy in app storage
- Non-destructive versioned project model (JSON in Room), autosave, undo/redo, missing-media check
- Media3 preview of the primary track (video + images), seek bar, split / duplicate / delete
- Device capability detection (RAM, codecs, thermal -> LOW / BALANCED / HIGH)
- Template manifest + validator + 3 sample offline templates

## NOT built yet
Multi-track timeline UI, real renderer/export (Media3 Transformer is already a dependency for Phase 4),
text/audio engines, effects, colour grading, keyframes, 97+ more templates, online services,
thumbnails/waveforms. No APK/AAB has been built or tested.
