# Google Play submission preparation

## Store listing draft

- **App name:** Word Puzzle Coach
- **Short description:** Narrow down five-letter puzzles with color clues and smart suggestions.
- **Full description:**

  Stuck on a five-letter word puzzle? Word Puzzle Coach helps you reason
  through your clues.

  Enter a guess, tap each tile to match its gray, yellow, or green feedback,
  and see which answers remain possible. Get a frequency-based suggestion for
  your next guess, review your clue trail, and start fresh whenever you like.

  Everything runs on your device. No account or internet connection is
  required. This is an independent puzzle helper and is not affiliated with or
  endorsed by The New York Times or the Wordle game.

- **Suggested category:** Tools
- **Support contact:** Add a monitored email address in Play Console before submission.
- **Privacy policy:** Publish [`PRIVACY-POLICY.md`](PRIVACY-POLICY.md) at a stable public HTTPS URL and add that URL to Play Console. Replace the contact placeholder before publishing it.

## Before production

1. Create or use a Google Play developer account and complete Google's identity and developer verification steps.
2. Create the app in Play Console with the permanent package name `com.gurkiransb.wordlecoach`. This ID cannot be changed after the first Play upload.
3. Set up Play App Signing. Create an upload key and keep a secure offline backup; never commit it or send it in chat.
4. Add the four signing secrets listed below to the repository's **Settings → Secrets and variables → Actions**.
5. Prepare and review the store listing: a 512×512 PNG high-resolution icon, a 1024×500 feature graphic, at least two real phone screenshots, a support email, and the hosted privacy policy.
6. Complete Play Console's app access, ads, target audience, content rating, Data safety, and other applicable declarations truthfully. The app currently requests no runtime permissions and has no analytics or ads SDK.
7. For a new personal developer account created after November 13, 2023, Google currently requires a closed test with at least 12 opted-in testers for 14 continuous days before applying for production access.
8. Once the four signing secrets are set, create and push a tag such as `play-v1.0.0` from the commit you intend to release. This starts the signed release workflow; the version code is assigned from the workflow run number and the version name comes from the tag. Alternatively, after the workflow is on the default branch, use **Actions → Android release bundle → Run workflow** and provide an unused, increasing version code and a version name. Download `word-puzzle-coach-play-bundle` from a successful run and upload its `.aab` to the Play Console testing track.
9. Test the closed-track build, collect feedback, complete any production-access application required by Google, and submit the reviewed release in Play Console.

Google Play's current target API requirement for new apps and updates takes
effect August 31, 2026: target Android 16 (API 36) or higher. This project
targets API 36. Recheck Play Console requirements at submission time.

## Release signing secrets

Create a dedicated upload key on a trusted computer. Example (choose and store
your own strong passwords; keep a secure backup of the keystore):

```sh
keytool -genkeypair -v \
  -keystore word-puzzle-coach-upload.jks \
  -storetype JKS \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias word-puzzle-coach-upload
```

Add these Actions repository secrets. The keystore is base64-encoded for
transport through GitHub Secrets; it is decoded only into the temporary hosted
runner and removed after the bundle build.

- `ANDROID_KEYSTORE_BASE64`: on macOS, copy the single-line value with
  `base64 -i word-puzzle-coach-upload.jks | tr -d '\n' | pbcopy`.
- `ANDROID_KEYSTORE_PASSWORD`: the keystore password.
- `ANDROID_KEY_ALIAS`: `word-puzzle-coach-upload` (or the alias you chose).
- `ANDROID_KEY_PASSWORD`: the key password.

Never put the keystore, its passwords, or base64 contents in the repository,
workflow logs, an issue, or chat. Use the same upload key for future updates.

## Art still needed

The app includes an Android launcher icon, but Play Console requires separate
store-listing graphics and genuine screenshots from the running app. Capture
screenshots from a device or emulator after reviewing the final release build;
do not use mockups that misrepresent the app.
