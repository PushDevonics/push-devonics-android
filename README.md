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

MainActivity:

    private lateinit var pushDevonics: PushDevonics
    
MainActivity in onCreate():

    pushDevonics = PushDevonics(applicationContext, "appId")
    lifecycle.addObserver(pushDevonics)
    
    // If you need internalId
    val internalId = pushDevonics.getInternalId()
    
    // If you want add tag type String
    pushDevonics.setTags("key", "value")
    
    // If you need deeplink
    val deepLink = intent.getStringExtra("deeplink")
    
    // If you need open URL in browser
    pushDevonics.openUrl(intent.getStringExtra("open_url"))
    
Java:

MainActivity:

    private PushDevonics pushDevonics;
    
MainActivity in onCreate():

    pushDevonics = new PushDevonics(getApplicationContext(), "appId");
    getLifecycle().addObserver(pushDevonics);
        
    // If you need internalId
    String internalId = pushDevonics.getInternalId();
    
    // If you want add tag type String
    pushDevonics.setTags("key", "value");
    
    // If you need deeplink
    String deeplink = getIntent().getStringExtra("deeplink");
    
    // If you need open URL in browser
    pushDevonics.openUrl(getIntent().getStringExtra("open_url"));
        
