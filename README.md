# Wordle Assistant

A small interactive Python script that narrows a five-letter word list using
the tile colors returned by a Wordle game.

## Requirements

- Python 3
- pandas

Install pandas if needed:

```bash
python3 -m pip install pandas
```

## Run

From the project root, run:

```bash
python3 test.py
```

For each guess, enter the five-letter word and its five tile colors:

- `g` for green
- `y` for yellow
- `b` for black/gray

The script filters `words/words.csv`, displays the remaining candidates, and
prints letter-frequency statistics to help choose another guess. This dictionary
contains the comprehensive list of potentially valid five-letter Wordle guesses.
The previous answer-word list is preserved as
`words/backup2315words.csv`.

The comprehensive list is sourced from
[tabatkins/wordle-list](https://github.com/tabatkins/wordle-list), which
provides the potentially valid guesses from Wordle's source. Its MIT license
and copyright notice are included in `words/LICENSE`.

## Current limitations

- The candidates are limited to the words present in `words/words.csv`. If the
  answer is missing from that file, the script cannot find it.
- Repeated-letter feedback is not handled with Wordle's exact letter-count
  rules, so candidate filtering can be inaccurate for guesses with repeated
  letters.
- The script reports candidate words and letter frequencies; automatic guess
  suggestions are not implemented yet.

## Game records

`Games Played/` contains saved text transcripts from previous attempts.
Screenshot and other image files are excluded from Git by `.gitignore`.
