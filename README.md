[![Release](https://jitpack.io/v/PushDevonics/push-devonics-android.svg)](https://jitpack.io/#PushDevonics/push-devonics-android)

Add it to you settings.gradle in repositories:

    repositories {
            google()
            mavenCentral()
            maven { url 'https://jitpack.io' }
    }
and:

    dependencies {
        implementation platform('com.google.firebase:firebase-bom:28.3.1')
        implementation 'com.google.firebase:firebase-messaging-ktx'
        implementation 'com.github.PushDevonics:push-devonics-android:latest version'
    }
    
Kotlin:
MainActivity in onCreate():

    AppContextKeeper.setContext(applicationContext)
    PushInitialization.run("appId")
    startTime()
    
    // If you want add tag type String
    inputTags("key", "value")
    
MainActivity in onResume():

    if ("transition" == intent.getStringExtra("command")) {
            createTransition()
        }
    
Java:
MainActivity in onCreate():

    AppContextKeeper.Companion.setContext(getApplicationContext);
    PushInitialization.Companion.run("appId");
    DataHelper.Companion.startTime();
    
    // If you want add tag type String
    DataHelper.Companion.inputTags("key", "value");
    
MainActivity in onResume():

    if ("transition" == intent.getStringExtra("command")) {
            DataHelper.Companion.createTransition();
        }
        
