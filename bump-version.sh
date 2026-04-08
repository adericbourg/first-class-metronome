#!/usr/bin/env bash
set -euo pipefail

GRADLE_FILE="app/build.gradle.kts"
BUMP_TYPE="${1:-}"

usage() {
    echo "Usage: $0 <major|minor|patch>"
    echo ""
    echo "  major  Bump major version (e.g. 1.2.3 → 2.0.0)"
    echo "  minor  Bump minor version (e.g. 1.2.3 → 1.3.0)"
    echo "  patch  Bump patch version (e.g. 1.2.3 → 1.2.4)"
}

die() {
    echo "Error: $1" >&2
    exit 1
}

# Validate argument
if [[ -z "$BUMP_TYPE" ]]; then
    usage
    exit 1
fi

case "$BUMP_TYPE" in
    major|minor|patch) ;;
    *) die "Unknown bump type '$BUMP_TYPE'. Must be one of: major, minor, patch" ;;
esac

# Ensure we're at the project root
[[ -f "$GRADLE_FILE" ]] || die "Must be run from project root (cannot find $GRADLE_FILE)"

# Ensure working tree is clean
if ! git diff --quiet || ! git diff --cached --quiet; then
    die "Working tree is not clean. Commit or stash your changes first."
fi

# Read current version
VERSION_CODE=$(grep -E '^\s+versionCode\s*=' "$GRADLE_FILE" | grep -oE '[0-9]+')
VERSION_NAME=$(grep -E '^\s+versionName\s*=' "$GRADLE_FILE" | grep -oE '"[^"]+"' | tr -d '"')

[[ -n "$VERSION_CODE" ]] || die "Could not parse versionCode from $GRADLE_FILE"
[[ -n "$VERSION_NAME" ]] || die "Could not parse versionName from $GRADLE_FILE"

# Parse version name: major.minor.patch (with optional legacy -N suffix, which is dropped)
if [[ "$VERSION_NAME" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)(-[0-9]+)?$ ]]; then
    MAJOR="${BASH_REMATCH[1]}"
    MINOR="${BASH_REMATCH[2]}"
    PATCH="${BASH_REMATCH[3]}"
else
    die "Cannot parse versionName '$VERSION_NAME'. Expected format: major.minor.patch"
fi

# Compute new version
case "$BUMP_TYPE" in
    major) NEW_VERSION_NAME="$((MAJOR + 1)).0.0" ;;
    minor) NEW_VERSION_NAME="${MAJOR}.$((MINOR + 1)).0" ;;
    patch) NEW_VERSION_NAME="${MAJOR}.${MINOR}.$((PATCH + 1))" ;;
esac

NEW_VERSION_CODE=$((VERSION_CODE + 1))
NEW_TAG="v${NEW_VERSION_NAME}"

# Show what will change and ask for confirmation
echo "Current: versionCode=$VERSION_CODE, versionName=\"$VERSION_NAME\""
echo "New:     versionCode=$NEW_VERSION_CODE, versionName=\"$NEW_VERSION_NAME\""
echo "Tag:     $NEW_TAG"
echo ""
read -r -p "Proceed? [y/N] " CONFIRM || true
[[ "$CONFIRM" =~ ^[Yy]$ ]] || { echo "Aborted."; exit 0; }

# Update build.gradle.kts (perl -i works on both macOS and Linux)
perl -i -pe "s/versionCode = ${VERSION_CODE}/versionCode = ${NEW_VERSION_CODE}/" "$GRADLE_FILE"
perl -i -pe "s/versionName = \"${VERSION_NAME}\"/versionName = \"${NEW_VERSION_NAME}\"/" "$GRADLE_FILE"

# Verify the substitutions took effect
UPDATED_CODE=$(grep -E '^\s+versionCode\s*=' "$GRADLE_FILE" | grep -oE '[0-9]+')
UPDATED_NAME=$(grep -E '^\s+versionName\s*=' "$GRADLE_FILE" | grep -oE '"[^"]+"' | tr -d '"')

[[ "$UPDATED_CODE" == "$NEW_VERSION_CODE" ]] || die "versionCode update failed (got '$UPDATED_CODE')"
[[ "$UPDATED_NAME" == "$NEW_VERSION_NAME" ]] || die "versionName update failed (got '$UPDATED_NAME')"

# Commit and tag
git add "$GRADLE_FILE"
git commit -m "Release $NEW_TAG"
git tag "$NEW_TAG"

echo ""
echo "Done! To publish, run:"
echo "  git push origin main $NEW_TAG"
