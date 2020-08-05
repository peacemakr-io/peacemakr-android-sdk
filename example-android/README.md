# Peacemakr Encryption + Decryption Sample Android App:

Quick example on how to use Peacemakr in an Android App.

## How to run this:

 * Open this folder in Android Studio
 * Download latest version of PeaemakrCoreCrypto.aar from releases in this repository
 * Open SecondFragment.java and update your API key
 * And provision an Android device, and on that Android device, run "app"

### Production Recommendations

 * Not recommended to use main thread (responsible for GUI rendering) for actual Encryption or Decryption operations, as the first call may result in network activity to fetch required keys.
