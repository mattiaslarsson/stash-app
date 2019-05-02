package to.mattias.stash;

import static java.util.Calendar.YEAR;
import static to.mattias.stash.rest.RetroFitFactory.getRestClient;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import retrofit2.Response;
import to.mattias.stash.model.Article;
import to.mattias.stash.model.StashItem;
import to.mattias.stash.rest.Client;

public class AddItemActivity extends AppCompatActivity {

  private static final int SCAN_REQUEST_CODE = 1;
  private final Calendar calendar = Calendar.getInstance();
  private EditText ean;
  private EditText description;
  private EditText expiryDate;
  private Button scanButton;
  private FloatingActionButton fab;
  private Client restClient;
  private boolean hasFoundItem = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_item);

    ean = findViewById(R.id.ean);
    description = findViewById(R.id.description);
    expiryDate = findViewById(R.id.expiryDate);
    scanButton = findViewById(R.id.scanEanButton);
    fab = findViewById(R.id.addItemFab);
    restClient = getRestClient();

    setupListeners();
  }

  private void setupListeners() {
    setEanListener();
    setScanListener();
    setExpiryDateListener();
    setFabListener();
  }

  private void setEanListener() {
    ean.setOnFocusChangeListener((view, hasFocus) -> {
      if (!hasFocus) {
        getArticle();
      }
    });

  }

  private void setScanListener() {
    scanButton.setOnClickListener((view) -> {
      try {
        Intent scanIntent = new Intent("com.google.zxing.client.android.SCAN");
        scanIntent.putExtra("SCAN_MODE", "PRODUCT_MODE");

        startActivityForResult(scanIntent, SCAN_REQUEST_CODE);
      } catch (ActivityNotFoundException e) {
        Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        startActivity(marketIntent);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
      String content = data.getStringExtra("SCAN_RESULT");
      ean.setText(content);
      getArticle();
    }
  }

  private void setExpiryDateListener() {
    expiryDate.setFocusable(false);
    final DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
      calendar.set(year, month, dayOfMonth);
      SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY", Locale.getDefault());
      expiryDate.setText(sdf.format(calendar.getTime()));
    };

    expiryDate.setOnClickListener((view) -> {
      getArticle();
      new DatePickerDialog(AddItemActivity.this, dateSetListener, calendar.get(YEAR),
          calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    });
  }

  private void setFabListener() {
    fab.setOnClickListener((view) -> {
      if (!ean.getText().toString().isEmpty() && !description.getText().toString().isEmpty()
          && !expiryDate.getText().toString().isEmpty()) {
        StashItem addedItem = new StashItem();
        addedItem.setEan(ean.getText().toString());
        addedItem.setDescription(description.getText().toString());
        addedItem.setExpiration(calendar.getTime());

        if (!hasFoundItem) {
          addArticle(addedItem);
        }

        Intent data = new Intent();
        data.putExtra("item", addedItem);
        setResult(RESULT_OK, data);
        finish();
      }
    });

  }

  private void getArticle() {
    String eanCode = ean.getText().toString();
    if (!eanCode.trim().isEmpty()) {
      new Thread(() -> {
        try {
          Response<Article> response = restClient.getArticleByEan(eanCode).execute();
          if (response.isSuccessful()) {
            hasFoundItem = true;
            runOnUiThread(() -> description.setText(response.body().getDescription()));
          }
          hasFoundItem = false;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    }

  }

  private void addArticle(StashItem item) {
    String eanCode = item.getEan();
    String descriptionText = item.getDescription();
    if (!eanCode.trim().isEmpty() && !descriptionText.trim().isEmpty()) {
      new Thread(() -> {
        try {
          Article article = new Article();
          article.setEan(eanCode);
          article.setDescription(descriptionText);
          restClient.addArticle(article).execute();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    }
  }

}
