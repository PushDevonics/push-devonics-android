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
        implementation 'com.github.Voice77777:PushLibrary:1.0.11'
    }
MainActivity in onCreate():

    AppContextKeeper.setContext(applicationContext)
    PushInitialization.run()
    startTime()
    
    // If you want add tag type String
    inputTags(key, value)
MainActivity in onDestroy():

    sendTimeStatistic()
