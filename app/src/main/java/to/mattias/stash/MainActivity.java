package to.mattias.stash;

import static to.mattias.stash.rest.RetroFitFactory.getRestClient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Retrofit;
import to.mattias.stash.model.Box;
import to.mattias.stash.rest.Client;

public class MainActivity extends AppCompatActivity {

  private static final int EDIT_BOX_REQUEST = 1;
  private static final String TAG = MainActivity.class.getSimpleName();

  private Client restClient;
  private Retrofit retrofit;
  private ListView boxesView;
  private List<Box> boxes = new ArrayList<>();
  private ArrayAdapter boxesAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initFirebase();


    restClient = getRestClient();

    boxesView = findViewById(R.id.boxList);
    getAllBoxesFromBackend();

    boxesView.setOnItemClickListener((parent, view, position, id) -> {
      Box box = boxes.get(position);
      Intent editBoxIntent = new Intent(this, EditBoxActivity.class);
      editBoxIntent.putExtra("box", box);
      startActivityForResult(editBoxIntent, EDIT_BOX_REQUEST);
    });

  }

  private void initFirebase() {
    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener((task) -> {
      if (!task.isSuccessful()) {
        Log.w(TAG, "getInstanceId failed", task.getException());
        return;
      }

      String token = task.getResult().getToken();
      Log.d(TAG, "Token: " + token);
      new Thread(() -> {
        try {
          restClient.setNotificationTarget(token).execute();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == EDIT_BOX_REQUEST && resultCode == RESULT_OK) {
      getAllBoxesFromBackend();
    }
  }

  private void getAllBoxesFromBackend() {
    new Thread(() -> {
      Call<List<Box>> boxesCall = restClient.getBoxes();
      try {
        boxes = boxesCall.execute().body();
        boxesAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
            toStrings(boxes));
        runOnUiThread(() -> boxesView.setAdapter(boxesAdapter));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  private List<String> toStrings(List<Box> boxes) {
    List<String> boxesAsStrings = new ArrayList<>();
    boxes.stream().forEach(box -> {
      StringBuilder sb = new StringBuilder();
      sb.append("Box #").append(Integer.toString(box.getBoxNumber()));
      sb.append(" innehåller ").append(Integer.toString(box.getItems().size())).append(" artiklar");
      boxesAsStrings.add(sb.toString());
    });

    return boxesAsStrings;
  }
}
