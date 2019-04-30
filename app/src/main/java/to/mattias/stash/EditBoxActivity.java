package to.mattias.stash;

import static java.util.stream.Collectors.toList;
import static to.mattias.stash.rest.RetroFitFactory.getRestClient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import to.mattias.stash.model.Box;
import to.mattias.stash.model.StashItem;
import to.mattias.stash.rest.Client;

public class EditBoxActivity extends AppCompatActivity {

  private Client restClient;
  private ListView boxContentList;
  private List<StashItem> items = new ArrayList<>();
  private List<String> itemsAsStrings = new ArrayList<>();
  private ArrayAdapter contentAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_box);

    Intent intent = getIntent();
    Box box = intent.getParcelableExtra("box");
    items = box.getItems();
    itemsAsStrings = items.stream().map(this::itemToString).collect(toList());

    boxContentList = findViewById(R.id.boxContentList);
    populateContentView();

    FloatingActionButton fab = findViewById(R.id.fab);

    fab.setOnClickListener((view) -> {

      restClient = getRestClient();
      new Thread(() -> {
        try {
          StashItem itemToAdd = new StashItem();
          itemToAdd.setEan("98765432");
          itemToAdd.setDescription("ÖL");
          itemToAdd.setExpiration(new Date());
          Log.d("addItemInBox", itemToAdd.toString());
          restClient.addItemInBox(box.getBoxNumber(), itemToAdd).execute();
          items = restClient.getBox(box.getBoxNumber()).execute().body();
          itemsAsStrings.clear();
          itemsAsStrings.addAll(items.stream().map(this::itemToString).collect(toList()));
          runOnUiThread(() -> contentAdapter.notifyDataSetChanged());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    });
  }

  private void populateContentView() {
    contentAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, itemsAsStrings);
    boxContentList.setAdapter(contentAdapter);
  }

  private List<String> toStrings(List<StashItem> items) {
    List<String> itemsAsStrings = new ArrayList<>();
    items.forEach(item -> {
      StringBuilder sb = new StringBuilder();
      sb.append(item.getDescription())
          .append(" - ");
      SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
      sb.append("bäst före ").append(sdf.format(item.getExpiration()));
      itemsAsStrings.add(sb.toString());
    });

    return itemsAsStrings;
  }

  private String itemToString(StashItem item) {
    StringBuilder sb = new StringBuilder();
    sb.append(item.getDescription()).append(" - ");
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
    sb.append("bäst före ").append(sdf.format(item.getExpiration()));

    return sb.toString();
  }

}
