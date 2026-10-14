# Wordle Coach for Android

A native Android solver and coach built with Kotlin and Jetpack Compose. It
works offline and bundles the same five-letter word list as the Python tool.

## Features

- Enter a guess and tap each tile to cycle through gray, yellow, and green.
- Filter possible answers using all submitted guesses, including Wordle's
  repeated-letter count rules.
- Get a suggested next guess based on letter frequency among remaining answers.
- Review the clue trail and possible answers, or start a new game.
- Animated tiles and a responsive, touch-friendly interface.

## Open and run

Open this `android/` directory in Android Studio, allow Gradle to sync, and run
the `app` configuration on an emulator or Android device. The project uses
Android Gradle Plugin 8.7.3, Kotlin 2.0.21, compile/target SDK 35, and supports
Android 7.0 (API 24) and newer. A JDK 17 installation and Android SDK 35 are
required.

From a terminal with Gradle and the Android SDK configured, run app unit tests
from this directory with:

```sh
gradle :app:testDebugUnitTest
```

The dictionary source and its MIT license are bundled in
`app/src/main/assets/words.txt` and `app/src/main/assets/WORD-LIST-LICENSE.txt`.
The source list is maintained at the repository root in `words/words.csv`.
