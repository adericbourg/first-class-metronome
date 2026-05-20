# Publishing Setup

This document describes how to set up and run the publishing pipeline for the app.

## Prerequisites

Install Ruby dependencies (first time only, or after `Gemfile.lock` changes):

```bash
bundle install
```

## Local Signing (Manual Builds)

For local development builds, the project reads signing credentials from a `keystore.properties` file (gitignored).

### 1. Generate a Keystore

```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias firstclassmetronome \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

### 2. Configure Signing Properties

```bash
cp keystore.properties.template keystore.properties
```

Edit `keystore.properties`:

```properties
storeFile=release.keystore
storePassword=your_keystore_password
keyAlias=firstclassmetronome
keyPassword=your_key_password
```

### 3. Build Locally

```bash
# APK
./gradlew assembleRelease

# App Bundle (for Play Store)
./gradlew bundleRelease
```

## Screenshots

Screenshots are captured using Fastlane's [screengrab](https://docs.fastlane.tools/actions/screengrab/) tool, which automates the process on a connected emulator. They are regenerated automatically on every release (see "Publishing a Release" below). To iterate on screenshots locally:

1. Start an Android emulator (via Android Studio or `emulator` CLI)
2. Run:

```bash
bundle exec fastlane android screenshots
```

Screenshots are saved to `fastlane/metadata/android/en-US/images/phoneScreenshots/`.

## Automated Publishing (GitHub Actions)

This project uses **Google Play App Signing**: Google manages the app signing key; you only manage an upload key. The upload key and Play credentials are stored as GitHub Actions secrets.

### Required Secrets

Go to **Settings → Secrets and variables → Actions** in the GitHub repository and ensure these secrets exist:

| Secret Name | Value |
|---|---|
| `UPLOAD_KEYSTORE_BASE64` | Base64-encoded upload keystore (`base64 -i upload.keystore`) |
| `UPLOAD_KEYSTORE_PASSWORD` | Password for the keystore file |
| `UPLOAD_KEY_ALIAS` | Key alias (e.g. `upload`) |
| `UPLOAD_KEY_PASSWORD` | Password for the key entry |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Contents of the Play service account JSON file |

For setup instructions (creating the upload keystore, service account, and Play Console permissions), see the Google Play App Signing documentation.

### Publishing a Release

1. Go to **Actions → Release** in the GitHub repository.
2. Click **Run workflow**.
3. Select the version component to bump: `patch`, `minor`, or `major`.
4. Click **Run workflow**.

The workflow then runs the following steps automatically, with no local action required:

1. **Tests + lint** — fails fast before any tag or push if the build is broken.
2. **Version bump** — increments `versionCode` by 1 and bumps `versionName` in `app/build.gradle.kts` according to the selected component.
3. **Commit + tag + push** — commits `Release vX.Y.Z` to `main` and pushes the tag `vX.Y.Z`.
4. **Build + upload** — builds the release AAB and uploads it to the Play Store **internal** track.
5. **Screenshots** — regenerates store screenshots in an emulator and commits the updated PNGs to `main`.

The tag `vX.Y.Z` marks the exact commit that was released.

### Recovery from partial failure

| Failure point | State | Recovery |
|---|---|---|
| Tests/lint fail | `main` unchanged, no tag | Fix the code, merge, re-run the workflow. |
| Version update fails | `main` unchanged | Check `app/build.gradle.kts` formatting; re-run. |
| Push fails | `main` unchanged | Check branch protection and token permissions; re-run. |
| Play upload fails (after push) | Tag pushed, no Play release | Run `bundle exec fastlane android deploy` locally from a checkout of the tag, or re-run only the failed workflow step via **Actions → Re-run failed jobs**. |
| Screenshots fail | Release shipped, screenshots stale | Re-run the screenshots job from **Actions → Re-run failed jobs**. |

## Promoting a Release to Production

Once available in the **internal** track, promote via Google Play Console:

1. Go to **Release → Testing → Internal testing**.
2. Find the release and click **Promote release → Production** (or choose an intermediate track).
3. Add release notes, review, and start the rollout.

A staged rollout (5% → 10% → 50% → 100%) is recommended to catch regressions before full exposure.

## Metadata Updates

To update store descriptions, changelogs, or screenshots without publishing a new build:

```bash
bundle exec fastlane android upload_metadata
```

This runs Fastlane's `upload_metadata` lane: pushes metadata and screenshots to Play Store without building or uploading an AAB.

Metadata files live in `fastlane/metadata/android/en-US/`.

## Privacy Policy

A privacy policy is required for Google Play Store submission. The policy is located at `docs/privacy-policy.md`.

### Hosting on GitHub Pages

1. Go to your repository on GitHub.
2. Navigate to **Settings → Pages**.
3. Under "Source", select **Deploy from a branch**.
4. Select `main` branch and `/docs` folder.
5. Click **Save**.

Your privacy policy will be available at:
```
https://<username>.github.io/<repository>/privacy-policy
```

Use this URL when submitting to the Play Store.
