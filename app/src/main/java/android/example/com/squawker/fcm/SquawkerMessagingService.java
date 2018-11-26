package android.example.com.squawker.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

public final class SquawkerMessagingService extends FirebaseMessagingService {
  private static final String TAG = "SquawkerMessaging";

  @Override
  public void onNewToken(String token) {
    Log.i(TAG, "Refreshed token: " + token);
  }
}
