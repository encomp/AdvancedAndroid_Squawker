package android.example.com.squawker.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public final class SquawkMessageService extends FirebaseMessagingService {

  private static String LOG_TAG = SquawkMessageService.class.getSimpleName();
  private static final int REMINDER_NOTIFICATION_ID = 1138;
  private static final int REMINDER_PENDING_INTENT_ID = 3417;
  private static final String REMINDER_NOTIFICATION_CHANNEL = "reminder_notification_channel";
  private static final int NOTIFICATION_MAX_CHARACTERS = 30;
  private static final String JSON_KEY_AUTHOR = SquawkContract.COLUMN_AUTHOR;
  private static final String JSON_KEY_AUTHOR_KEY = SquawkContract.COLUMN_AUTHOR_KEY;
  private static final String JSON_KEY_MESSAGE = SquawkContract.COLUMN_MESSAGE;
  private static final String JSON_KEY_DATE = SquawkContract.COLUMN_DATE;
  private static final List<String> KEYS =
      ImmutableList.of(JSON_KEY_AUTHOR, JSON_KEY_AUTHOR_KEY, JSON_KEY_MESSAGE, JSON_KEY_DATE);

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    final ImmutableMap<String, String> immutableMap = ImmutableMap.copyOf(remoteMessage.getData());
    String format = "\t%s: \t%s\n";
    StringBuilder entireMessage = new StringBuilder("{\n");
    KEYS.stream()
        .forEach(
            key -> {
              entireMessage.append(String.format(format, key, immutableMap.get(key)));
            });
    entireMessage.append("}");
    Log.d(LOG_TAG, entireMessage.toString());
    insertSquawk(immutableMap);
    sendNotification(immutableMap);
  }

  /**
   * Inserts a single squawk into the database;
   *
   * @param data Map which has the message data in it
   */
  private void insertSquawk(final ImmutableMap<String, String> data) {

    AsyncTask<Void, Void, Void> insertSquawkTask =
        new AsyncTask<Void, Void, Void>() {

          @Override
          protected Void doInBackground(Void... voids) {
            ContentValues newMessage = new ContentValues();
            newMessage.put(SquawkContract.COLUMN_AUTHOR, data.get(JSON_KEY_AUTHOR));
            newMessage.put(SquawkContract.COLUMN_MESSAGE, data.get(JSON_KEY_MESSAGE).trim());
            newMessage.put(SquawkContract.COLUMN_DATE, data.get(JSON_KEY_DATE));
            newMessage.put(SquawkContract.COLUMN_AUTHOR_KEY, data.get(JSON_KEY_AUTHOR_KEY));
            getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, newMessage);
            return null;
          }
        };

    insertSquawkTask.execute();
  }

  private void sendNotification(ImmutableMap<String, String> data) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    // Create the pending intent to launch the activity
    PendingIntent pendingIntent =
        PendingIntent.getActivity(
            this, REMINDER_PENDING_INTENT_ID, intent, PendingIntent.FLAG_ONE_SHOT);

    String author = data.get(JSON_KEY_AUTHOR);
    String message = data.get(JSON_KEY_MESSAGE);
    if (message.length() > NOTIFICATION_MAX_CHARACTERS) {
      message = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
    }

    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(this, REMINDER_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_duck)
            .setContentTitle(String.format(getString(R.string.notification_message), author))
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel mChannel =
          new NotificationChannel(
              REMINDER_NOTIFICATION_CHANNEL,
              getString(R.string.main_notification_channel_name),
              NotificationManager.IMPORTANCE_HIGH);
      notificationManager.createNotificationChannel(mChannel);
    }

    notificationManager.notify(REMINDER_NOTIFICATION_ID, notificationBuilder.build());
  }
}
