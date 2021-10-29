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
        
MainActivity in onDestroy():

    sendTimeStatistic()

If you want use custom icon add it to AndroidManifest in application tag:

    <meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@drawable/your_icon" />
    <meta-data 
            android:name="com.google.firebase.messaging.default_notification_color"
            android:resource="@color/your_color" />
    <meta-data
            android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="default_notification_channel_id" />
            
In application tag add service:

    <service
            android:name=".MyFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
