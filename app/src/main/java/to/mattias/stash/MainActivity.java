package to.mattias.stash;

import static to.mattias.stash.rest.RetroFitFactory.getRestClient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import to.mattias.stash.model.Box;
import to.mattias.stash.model.StashItem;
import to.mattias.stash.rest.Client;

public class MainActivity extends AppCompatActivity {

  private static final int EDIT_BOX_REQUEST = 1;

  private Client restClient;
  private Retrofit retrofit;
  private ListView boxesView;
  private List<Box> boxes = new ArrayList<>();
  private ArrayAdapter boxesAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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
        boxesAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, toStrings(boxes));
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
