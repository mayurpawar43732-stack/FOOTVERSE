# FOOTVERSE ⚽
### The World Is Your Pitch

FOOTVERSE is an Android football game prototype built with native Kotlin and Android Canvas. The project is designed to grow into a national-team football game with custom squads and famous international players.

## Current playable build
- Landscape Android game screen
- National-team selection: India, Portugal, Argentina, France, Brazil, Germany, USA, Spain, England and Japan
- Quick match with a pitch, player movement, shooting, goals, score and match timer
- GitHub Actions workflow that builds and uploads a debug APK

## Build the APK
1. Open the **Actions** tab in this repository.
2. Select **Build FOOTVERSE APK**.
3. Open the latest successful workflow run.
4. Under **Artifacts**, download **FOOTVERSE-debug-apk**.
5. Extract the ZIP and install `app-debug.apk` on your Android device. You may need to allow installation from your file manager.

You can also run `gradle assembleDebug` with JDK 17 and Android SDK installed.

## Planned next features
- Expanded national-team and player database
- Custom squad builder and formations
- Better opponent AI and match controls
- Player ratings, tournaments, career mode and local multiplayer
- Improved animations, stadiums, sound and game settings

## Important
This is the first playable foundation, not yet a complete professional 3D football simulator. Player likenesses, official team badges, league branding and licensed assets will only be added when properly licensed or replaced with original artwork.
