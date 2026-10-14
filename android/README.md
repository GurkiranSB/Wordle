# Word Puzzle Coach for Android

A native Android solver and coach built with Kotlin and Jetpack Compose. It
works offline and bundles the same five-letter word list as the Python tool.

## Features

- Enter a guess and tap each tile to cycle through gray, yellow, and green.
- Filter possible answers using all submitted guesses, including Wordle's
  repeated-letter count rules.
- Get a suggested next guess based on letter frequency among remaining answers.
- Review the clue trail and possible answers, or start a new game.
- Animated tiles and a responsive, touch-friendly interface.

## Build on GitHub

The `Android` GitHub Actions workflow builds and tests this app when changes
are pushed to `feature/android-app`. Open the repository's **Actions** tab,
select the latest successful **Android** run, and download the
`wordle-coach-debug-apk` artifact to get an installable debug build. The
artifact is retained for 14 days. The workflow can also be started manually
from the Actions tab with **Run workflow**.

## Build locally

Open this `android/` directory in Android Studio, allow Gradle to sync, and run
the `app` configuration on an emulator or Android device. The project uses
Android Gradle Plugin 8.10.1, Kotlin 2.0.21, compile/target SDK 36, and supports
Android 7.0 (API 24) and newer. A JDK 17 installation and Android SDK 36 are
required.

## Google Play preparation

See [`PLAY-STORE.md`](PLAY-STORE.md) for the store-listing draft, current
submission checklist, and the manual signed Android App Bundle workflow. The
Play listing name is **Word Puzzle Coach**; the app is an independent word
puzzle helper and is not affiliated with or endorsed by The New York Times.

The dictionary source and its MIT license are bundled in
`app/src/main/assets/words.txt` and `app/src/main/assets/WORD-LIST-LICENSE.txt`.
The source list is maintained at the repository root in `words/words.csv`.
