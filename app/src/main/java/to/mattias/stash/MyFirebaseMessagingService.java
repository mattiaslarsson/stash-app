package to.mattias.stash;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import java.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

  private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

  @Override
  public void onNewToken(String token) {
    Toast.makeText(this, "Token: " + token, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onMessageReceived(RemoteMessage message) {
    if (!message.getData().isEmpty()) {
      JSONObject data = new JSONObject(message.getData());
        try {
          int box = data.getInt("box");
          String artikel = data.getString("artikel");
          if (VERSION.SDK_INT >= VERSION_CODES.O) {
            LocalDate date = LocalDate.parse(data.getString("date"));
            Log.d(TAG, "date: " + date);
            Log.d(TAG, "box: " + box);
            Log.d(TAG, "artikel: " + artikel);
            Log.d(TAG, "data: " + message.getData());
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
    }
    if (message.getNotification() != null) {
      Log.d(TAG, "notification: " + message.getNotification().getBody());
    }

    vibrateAndNotify();
  }

  private void vibrateAndNotify() {
    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
    r.play();
    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
    }
  }
}
