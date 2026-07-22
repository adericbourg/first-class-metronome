# Feature 0009: About

## Goal

Let users see app identity, version, author/source, licence, and basic
device debug info, and reach the project's GitHub page or email the
author directly.

## Design

### Navigation

- Add an "About" entry in the existing top-right menu, below "Settings"
  (order: Work log, Settings, About)
- The About screen has a back arrow (←) at the top-left to return to the main screen
- Fully static / read-only screen — no editable state, no persistence

### Screen Layout

```
┌─────────────────────────────────────────┐
│ ← About                                 │
├─────────────────────────────────────────┤
│                                         │
│  First-class Metronome                  │
│  v1.3.0 (13)                            │
│                                         │
│  Author: Alban Dericbourg               │
│  alban@dericbourg.dev              [↗]  │  (tappable, opens mail client)
│                                         │
│  ─────────────────────────────────────  │
│                                         │
│  Source code                            │
│  github.com/adericbourg/                │
│  FirstClassMetronome               [↗]  │  (tappable, opens browser)
│                                         │
│  Licensed under the GNU GPL v3 — you're │
│  free to use, study, share, and modify  │
│  this app's source code.                │
│                                         │
│  ─────────────────────────────────────  │
│                                         │
│  Android 14 (API 34) · Pixel 7          │
│                                         │
└─────────────────────────────────────────┘
```

### Content

| Item             | Source                                                        |
|------------------|-----------------------------------------------------------------|
| App name         | Hardcoded string: "First-class Metronome"                       |
| Author           | Hardcoded string: "Alban Dericbourg"                             |
| Author email     | Hardcoded, tappable → `ACTION_SENDTO` (`mailto:`)                |
| Version          | Runtime `PackageManager.getPackageInfo(...)`, not `BuildConfig`  |
| Source link      | Hardcoded URL, tappable → `ACTION_VIEW`                          |
| Licence sentence | Hardcoded, plain-language GPLv3 explanation                      |
| Debug info       | `Build.VERSION.RELEASE` / `Build.VERSION.SDK_INT` / `Build.MODEL`|

## Accessibility (WCAG 2.2 Level AA)

| Element         | contentDescription                          |
|------------------|----------------------------------------------|
| Back button      | "Go back"                                     |
| About menu item  | "About"                                       |
| Email row        | "Send email to alban@dericbourg.dev"          |
| Source link row  | "Open source code on GitHub"                  |

- Tappable rows: minimum 48dp x 48dp touch target
- Links are distinguished by underline + icon, not colour alone

## Edge Cases

| Scenario                                     | Behavior                                     |
|-----------------------------------------------|-----------------------------------------------|
| PackageManager lookup throws                  | Fall back to "unknown" version display        |
| No browser installed to handle `ACTION_VIEW`  | Tap silently no-ops (caught exception)        |
| No mail client installed                      | Tap silently no-ops (caught exception)        |

## Implementation Notes

1. Add `AppInfoProvider` interface + `DefaultAppInfoProvider` in `device/`
2. Bind it in `di/SettingsModule.kt`'s `SettingsBindingsModule`
3. Add `AboutState`, `AboutViewModel`
4. Add `AboutScreen.kt` (stateful `AboutScreen` + stateless `AboutContent`)
5. Add `AppScreen.About`
6. Wire `onNavigateToAbout` through `BeatSelectionScreen`/`BeatSelectionContent`
7. Wire `AppScreen.About` branch in `MainActivity`
8. Add `<queries>` to `AndroidManifest.xml`

## Out of Scope

- In-app changelog / release notes
- Licences of third-party dependencies (OSS attribution screen)
- Rendering the full GPLv3 legal text in-app
