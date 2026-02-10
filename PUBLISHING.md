# Publishing Setup

This document describes how to set up publishing for the app.

## Local Signing (Manual Builds)

For local development and manual releases, the project reads signing credentials from a `keystore.properties` file (gitignored).

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

## Automated Publishing (GitHub Actions)

The project includes a GitHub Action that automatically publishes to Google Play Store when you push a tag starting with `v` (e.g., `v1.0.0`).

This setup uses **Google Play App Signing**, where Google manages the app signing key and you only manage an upload key. Benefits:
- If your upload key is compromised, you can reset it
- Google optimizes APKs for different devices
- Safer than managing the app signing key yourself

### Setup Steps

#### 1. Create Your App on Google Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Create a new app
3. Complete the app setup (content rating, store listing, etc.)
4. **Important**: Upload your first AAB manually to create the app. Google Play App Signing is enabled by default for new apps.

#### 2. Generate an Upload Keystore

This key is **only for uploading** — Google will re-sign with their own key.

When you run the command below, you'll be prompted to enter:
- **Keystore password**: Password to protect the keystore file (use this for `UPLOAD_KEYSTORE_PASSWORD`)
- **Key password**: Password to protect the specific key entry (use this for `UPLOAD_KEY_PASSWORD`)
- Additional information (name, organization, etc.)

Run this from the `app` folder.
```bash
keytool -genkeypair -v \
  -keystore upload.keystore \
  -alias upload \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

**Important**: Save the passwords you enter — you'll need them for GitHub secrets in step 5.

The `-alias upload` parameter defines the key alias (use `upload` for `UPLOAD_KEY_ALIAS` unless you change it).

#### 3. Set Up Google Cloud Service Account

1. **Create a Google Cloud Project** (or use an existing one):
   - Go to [Google Cloud Console](https://console.cloud.google.com)
   - Create a new project if needed

2. **Enable the Google Play Developer API**:
   - Go to the [Google Play Developer API page](https://console.developers.google.com/apis/api/androidpublisher.googleapis.com/)
   - Click **Enable**

3. **Create a Service Account**:
   - Go to [Service Accounts](https://console.cloud.google.com/iam-admin/serviceaccounts) in Google Cloud Console
   - Click **Create service account**
   - Name it (e.g., `github-play-publisher`)
   - Click **Create and Continue**
   - **Skip the optional IAM role assignment** (permissions are managed in Play Console, not Cloud Console)
   - Click **Done**

4. **Create a JSON key for the service account**:
   - Click on the service account you just created
   - Go to the **Keys** tab
   - Click **Add Key** → **Create new key** → **JSON**
   - Save the downloaded JSON file securely

#### 4. Grant Access in Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Navigate to **Users and permissions**
3. Click **Invite new users**
4. Enter the service account email (from the JSON file, looks like `name@project.iam.gserviceaccount.com`)
5. Under **App permissions**, select your app
6. Grant these permissions:
   - **Release to production, exclude devices, and use Play App Signing**
   - **Release apps to testing tracks**
   - **Manage testing tracks and edit tester lists**
7. Click **Invite user** → **Send invite**

#### 5. Configure GitHub Secrets

Go to your GitHub repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**.

Add these secrets:

| Secret Name | Value | Source |
|-------------|-------|--------|
| `UPLOAD_KEYSTORE_BASE64` | Base64-encoded upload keystore (see below) | Your `upload.keystore` file |
| `UPLOAD_KEYSTORE_PASSWORD` | Password for the keystore file | Keystore password from step 2 |
| `UPLOAD_KEY_ALIAS` | Key alias | The `-alias` parameter from step 2 (e.g., `upload`) |
| `UPLOAD_KEY_PASSWORD` | Password for the key entry | Key password from step 2 |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Contents of the service account JSON file | JSON file from step 3 |

To base64-encode your keystore:

```bash
base64 -i upload.keystore | pbcopy  # macOS (copies to clipboard)
# or
base64 upload.keystore              # Linux (prints to stdout)
```

#### 6. Publish a Release

1. Update `versionCode` and `versionName` in `app/build.gradle.kts`
2. Commit your changes
3. Create and push a tag:

```bash
git tag v1.0.0
git push origin v1.0.0
```

The GitHub Action will automatically build and upload to the **internal** track.

### Changing the Release Track

Edit `.github/workflows/publish-play-store.yml` and change the `track` value:

- `internal` — Internal testing (default)
- `alpha` — Closed testing
- `beta` — Open testing
- `production` — Production release

### Troubleshooting

**"The current user has insufficient permissions"**
- Verify the service account has the correct permissions in Play Console
- Wait a few minutes after granting permissions — they can take time to propagate

**"Package not found"**
- You must upload the first AAB manually through Play Console before automated uploads work

**"APK specifies a version code that has already been used"**
- Increment `versionCode` in `app/build.gradle.kts`

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
