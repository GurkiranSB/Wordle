# Wordle Assistant

A small interactive Python tool that narrows a five-letter word list using
Wordle's tile feedback and suggests a next guess. A native Android app is
planned alongside the existing tool.

## Python tool

### Requirements

- Python 3
- pandas

Install pandas if needed:

```bash
python3 -m pip install pandas
```

### Run

From the repository root:

```bash
python3 test.py
```

For each guess, enter the five-letter word and the five tile colors:

- `g` for green
- `y` for yellow
- `b` for black/gray

The script filters `words/words.csv`, displays the remaining candidates, and
prints letter-frequency statistics. Its suggested guess is scored by the
frequency of its distinct letters among the remaining candidates; this is a
simple heuristic, not an optimal-strategy guarantee.

Repeated-letter feedback is considered when filtering: the number of green and
yellow copies sets the minimum count, and a gray copy of that same letter sets
the maximum count. Candidates are limited to words in the bundled dictionary.

The comprehensive guess list is sourced from
[tabatkins/wordle-list](https://github.com/tabatkins/wordle-list). Its MIT
license and copyright notice are included in `words/LICENSE`. The previous
answer-word list is preserved as `words/backup2315words.csv`.

## Android app

The native Kotlin and Jetpack Compose app lives in the [`android/`](android/)
directory. It works offline with the bundled word list, lets you enter a guess
and tap its tiles to set gray/yellow/green feedback, then shows matching
candidates and a frequency-based next-guess suggestion. See
[`android/README.md`](android/README.md) for Android Studio and build instructions.

## Repository layout

```text
.
├── android/          # Native Android app (Kotlin + Jetpack Compose)
├── words/            # Word lists and their license
├── Games Played/     # Saved transcripts from previous attempts
└── test.py           # Python command-line tool
```

The Python tool and Android app are kept in one repository so they can share
the project's history and word-list source while keeping their implementations
separate.

## Game records

`Games Played/` contains saved text transcripts from previous attempts.
Screenshot and other image files are excluded from Git by `.gitignore`.
