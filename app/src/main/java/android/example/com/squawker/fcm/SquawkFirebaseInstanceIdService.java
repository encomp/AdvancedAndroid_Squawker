package android.example.com.squawker.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

/** Listens for changes in the InstanceID */
public class SquawkFirebaseInstanceIdService extends FirebaseMessagingService {

  private static String LOG_TAG = SquawkFirebaseInstanceIdService.class.getSimpleName();

  @Override
  public void onNewToken(String token) {
    Log.i(LOG_TAG, "Refreshed token: " + token);
  }

  /**
   * Persist token to third-party servers.
   *
   * <p>Modify this method to associate the user's FCM InstanceID token with any server-side account
   * maintained by your application.
   *
   * @param token The new token.
   */
  private void sendRegistrationToServer(String token) {
    // This method is blank, but if you were to build a server that stores users token
    // information, this is where you'd send the token to the server.
  }
}
