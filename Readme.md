# Steps to setup Android project on local

1. Setup Android Studio on Local
2. Checkout the code from this repository
3. Open the project using Android Studio
4. using SDK Manager - add SDKs
5. Add debug.keystore for google authorization service in ~/.android/ folder(keep a backup of your old debug.keystore)
6. Add google-services.json file to the app folder of the project that you can generate after registering your app on Google firebase
7. Add a new file secure.properties in the root folder of the project, that will contain the google map key, a template is given in secure_template.properties
8. Build the app using Android Studio and test it on your device. The app should launch
9. In FunduApplication.java, you can add your firebase related keys
10. File V1API.java has all the configuration and endpoint related config (BASE_URL is the backend URL that should be updated)