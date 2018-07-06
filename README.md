# JustWatchForTV

This application is an Android TV wrapper on top of the AJAX APIs exposed by https://www.justwatch.com.

The code currently is hardcoded to use the en_IN locale. The home screen shows New and Popular content, and does not paginate. 
Also, selecting a service to open a Movie or a TV Show doesn't open the content in that app.

This is just a hobby project, and was written because JustWatch doesn't have an Android TV app yet.

The application was written in Android Studio.

To deploy this to your TV (or Android TV Box), import it into Android Studio. Then open a command prompt and run the following
command:
    adb connect <IP Address of your TV>

For example:
    adb connect 192.168.1.140
  
Your TV will now show up in ADB as a connected device. Then "Run" the app from Android Studio on your TV. 
