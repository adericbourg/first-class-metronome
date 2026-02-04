# Publishing Setup

This document describes how to set up your local environment for publishing the app.

## Release Signing

Android requires APKs to be signed before distribution. The project is configured to read signing credentials from a `keystore.properties` file (gitignored).

### 1. Generate a Keystore

Run this command once and store the keystore file securely:

```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias firstclassmetronome \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

You'll be prompted for:
- Keystore password
- Key password
- Your name, organization, and location (used in the certificate)

### 2. Configure Signing Properties

Copy the template and fill in your values:

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

### 3. Build a Signed Release APK

```bash
./gradlew assembleRelease
```

The signed APK will be at `app/build/outputs/apk/release/app-release.apk`.

### 4. Build an App Bundle (for Play Store)

```bash
./gradlew bundleRelease
```

The bundle will be at `app/build/outputs/bundle/release/app-release.aab`.

## Important Notes

- **Back up your keystore securely.** If you lose it, you cannot update your app on the Play Store.
- **Never commit `keystore.properties` or `*.keystore` files.** They are gitignored by default.
- Consider using [Google Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756) to let Google manage your app signing key.

## Privacy Policy

A privacy policy is required for Google Play Store submission. The policy is located at `docs/privacy-policy.md`.

### Hosting on GitHub Pages

1. Go to your repository on GitHub
2. Navigate to **Settings** → **Pages**
3. Under "Source", select **Deploy from a branch**
4. Select `main` branch and `/docs` folder
5. Click **Save**

Your privacy policy will be available at:
```
https://<username>.github.io/<repository>/privacy-policy
```

Use this URL when submitting to the Play Store.
