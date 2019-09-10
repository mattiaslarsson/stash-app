package to.mattias.stash;

import static to.mattias.stash.rest.RetroFitFactory.getRestClient;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.RemoteMessage.Notification;
import java.io.IOException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import to.mattias.stash.model.Box;
import to.mattias.stash.model.StashItem;
import to.mattias.stash.rest.Client;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

  private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
  private static final String CHANNEL_ID = "channelId";
  private static final int NOTIFICATION_ID = 1;
  private Client restClient = getRestClient();


  @Override
  public void onNewToken(String token) {
    Looper.prepare();
    Toast.makeText(this, "Token: " + token, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onMessageReceived(RemoteMessage message) {
    createNotificationChannel();
    if (!message.getData().isEmpty()) {
      JSONObject data = new JSONObject(message.getData());
      try {
        JSONArray items = new JSONArray(data.getString("items"));
        for (int i = 0; i < items.length(); i++) {
          JSONObject item = (JSONObject) items.get(i);
          int box = Integer.parseInt(item.getString("box"));
          Log.d(TAG, "Item: " + item);
          Log.d(TAG, "Box: " + box);
          notify(box);
        }

      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  private void createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = "name";
      String description = "description";
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  public void notify(int boxNumber) {
    Intent notifyIntent = new Intent(this, EditBoxActivity.class);
    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

    try {
      List<StashItem> items = restClient.getBox(boxNumber).execute().body();
      Box box = new Box(items.get(0).getBox(), items);
      notifyIntent.putExtra("box", box);
      PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);

      NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.common_full_open_on_phone)
        .setContentIntent(notifyPendingIntent)
        .setContentTitle("Expiring item")
        .setStyle(new BigTextStyle().bigText("You have items in stash that are soon to be expired"))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true);

      NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
      notificationManager.notify(NOTIFICATION_ID, builder.build());

    } catch (IOException e) {
      e.printStackTrace();
    }

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
