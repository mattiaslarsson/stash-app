package to.mattias.stash;

import static java.util.stream.Collectors.toList;
import static to.mattias.stash.rest.RetroFitFactory.getRestClient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import retrofit2.Response;
import to.mattias.stash.model.Box;
import to.mattias.stash.model.DescriptionSorter;
import to.mattias.stash.model.ExpirationSorter;
import to.mattias.stash.model.StashItem;
import to.mattias.stash.rest.Client;

public class EditBoxActivity extends AppCompatActivity {

  private ListView boxContentList;
  private List<StashItem> items = new ArrayList<>();
  private List<String> itemsAsStrings = new ArrayList<>();
  private Button sortByDescription, sortByExpiration, addItemButton;
  private ArrayAdapter contentAdapter;
  private int boxNumber;
  private static final int ADD_ITEM_REQUEST = 1;
  private Client restClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_box);

    sortByDescription = findViewById(R.id.sortByDescriptionButton);
    sortByExpiration = findViewById(R.id.sortByExpirationButton);

    restClient = getRestClient();

    Intent intent = getIntent();
    Box box = intent.getParcelableExtra("box");
    boxNumber = box.getBoxNumber();

    items = box.getItems();

    for (StashItem item : items) {
      itemsAsStrings.add(itemToString(item));
    }

    boxContentList = findViewById(R.id.boxContentList);
    populateContentView();

    addItemButton = findViewById(R.id.addItemButton);

    addItemButton.setOnClickListener((view) -> {
      Intent addItemIntent = new Intent(this, AddItemActivity.class);
      startActivityForResult(addItemIntent, ADD_ITEM_REQUEST);
    });

    setSortListeners();
  }

  private void setSortListeners() {
    sortByDescription.setOnClickListener((view) -> {
      doSortByDescription();
    });

    sortByExpiration.setOnClickListener((view) -> {
      doSortByExpiration();
    });
  }

  private void doSortByDescription() {
    Collections.sort(items, new DescriptionSorter());
    itemsAsStrings.clear();
    itemsAsStrings.addAll(items.stream().map(this::itemToString).collect(toList()));
    contentAdapter.notifyDataSetChanged();
  }

  private void doSortByExpiration() {
    Collections.sort(items, new ExpirationSorter());
    itemsAsStrings.clear();
    itemsAsStrings.addAll(items.stream().map(this::itemToString).collect(toList()));
    contentAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == ADD_ITEM_REQUEST && resultCode == RESULT_OK) {
      StashItem addedItem = data.getParcelableExtra("item");
      new Thread(() -> {
        try {
          Response response = restClient.addItemInBox(boxNumber, addedItem).execute();
          if (response.isSuccessful()) {
            items = restClient.getBox(boxNumber).execute().body();
            itemsAsStrings.add(itemToString(addedItem));
            runOnUiThread(() -> contentAdapter.notifyDataSetChanged());
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    }
  }

  @Override
  public void onBackPressed() {
    setResult(RESULT_OK);
    super.onBackPressed();
  }

  private void populateContentView() {
    contentAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, itemsAsStrings);
    boxContentList.setAdapter(contentAdapter);
  }

  private String itemToString(StashItem item) {
    StringBuilder sb = new StringBuilder();
    sb.append(item.getDescription()).append(" - ");
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
    sb.append("bäst före ").append(sdf.format(item.getExpiration()));

    return sb.toString();
  }

}
