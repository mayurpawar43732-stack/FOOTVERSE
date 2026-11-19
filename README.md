# FOOTVERSE ⚽
### The World Is Your Pitch

FOOTVERSE is a native Android football game prototype written in Kotlin.

## Current features
- **14 national teams:** India, Portugal, Argentina, France, Brazil, Germany, USA, Spain, England, Japan, Netherlands, Italy, Croatia and Morocco.
- **Player rosters:** includes notable international players such as Cristiano Ronaldo, Lionel Messi, Sunil Chhetri, Kylian Mbappé, Neymar Jr, Jude Bellingham, Lamine Yamal, Virgil van Dijk, Luka Modrić and Achraf Hakimi.
- **Squad builder:** select up to 11 players or use Auto Pick Best XI.
- **Formations:** 4-3-3, 4-4-2, 3-5-2 and 4-2-3-1; formation changes the support-player layout.
- **Quick matches:** player movement, pass and shoot controls, score, match timer and opponent AI that tracks the ball and attempts shots.
- **Tournament mode:** win successive matches to progress through the FOOTVERSE Cup.
- **Animated match presentation:** pitch stripes, player bobbing, ball shadow, ball movement and bounce.
- **GitHub Actions:** builds a debug APK automatically on pushes to main and manual workflow dispatch.

## Build and download the APK
1. Open the [Actions tab](../../actions).
2. Select **Build FOOTVERSE APK** and open the latest run.
3. Download the **FOOTVERSE-debug-apk** artifact.
4. Extract the ZIP and install `app-debug.apk` on your Android device. You may need to allow installation from your file manager.

## Controls
- Home screen: tap the arrows to change national team.
- Squad Builder: tap player cards to add/remove players; the starting XI is limited to 11.
- Formations: tap a formation to select it.
- Match: tap the pitch to move; use **PASS** and **SHOOT** on the right.

## Development status
This is an evolving 2D prototype, not a finished 3D simulation. Team and player names are included as text roster data; official logos, player portraits, real-world likenesses, licensed kits, stadiums, commentary and online multiplayer are not included. Tournament progression and AI are lightweight gameplay systems and can be expanded in later versions.
