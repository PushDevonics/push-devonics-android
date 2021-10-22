package pro.devonics.push

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "MyFirebaseMessagingService"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    override fun handleIntent(intent: Intent) {
        Log.d(TAG, "handleIntent = $intent")
        try {
            if (intent.extras != null) {
                val builder = RemoteMessage.Builder("MyFirebaseMessagingService")
                for (key in intent.extras!!.keySet()) {
                    builder.addData(key!!, intent.extras!![key].toString())
                }
                onMessageReceived(builder.build())
                Log.d(TAG, "handleIntent 1")
            } else {
                super.handleIntent(intent)
                Log.d(TAG, "handleIntent 2")
            }
        } catch (e: Exception) {
            super.handleIntent(intent)//if app close
            Log.d(TAG, "handleIntent e: $e")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag", "UnspecifiedImmutableFlag", "ServiceCast")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived")

        //val intent = Intent(this, MainActivity::class.java)
        //val intent = Intent()
        val packageName = applicationContext.packageName

        Log.d(TAG, "onMessageReceived packageName: $packageName")
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        //intent.component = ComponentName(this, MainActivity::class.java)
        //intent.setClassName(this, "$packageName.MainActivity")
        intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent?.putExtra("command", "transition")
        /*intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }*/

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, 0)//PendingIntent.FLAG_ONE_SHOT)
        val channelId = "Default"

        if (remoteMessage.data["image"] != null) {
            //get image
            val largeIcon = remoteMessage
                .data["image"]?.let { getBitmapFromUrl(it) }

            //get icon
            val smallIcon = remoteMessage
                .notification?.imageUrl?.let { getBitmapFromUrl(it.toString()) }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle(remoteMessage.notification?.title)
                .setContentText(remoteMessage.notification?.body)
                //.setContentText("https://www.google.com.ua/")
                .setLargeIcon(smallIcon)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setChannelId(channelId)
                .setStyle(NotificationCompat.BigPictureStyle()
                    .bigPicture(largeIcon)
                    .bigLargeIcon(smallIcon))
                .setContentIntent(pendingIntent)

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Default channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(0, builder.build())
        } else {
            val builder = NotificationCompat.Builder(this, channelId)
                //.setSmallIcon(R.mipmap.ic_launcher)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle(remoteMessage.notification?.title)
                .setContentText(remoteMessage.notification?.body)
                //.setContentText("http://developer.alexanderklimov.ru/android/")
                //.setLargeIcon(smallIcon)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setChannelId(channelId)
                //.setStyle(NotificationCompat.BigPictureStyle().bigPicture(largeIcon).bigLargeIcon(smallIcon))
                .setContentIntent(pendingIntent)

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Default channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(0, builder.build())
        }
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput
        connection.connect()
        val input = connection.inputStream
        return BitmapFactory.decodeStream(input)
    }

    @SuppressLint("LongLogTag")
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "################ onNewToken##################: $p0")
    }
}