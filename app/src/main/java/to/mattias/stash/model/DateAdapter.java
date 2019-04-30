package to.mattias.stash.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAdapter extends TypeAdapter<Date> {

  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  @Override
  public void write(JsonWriter out, Date value) throws IOException {
    out.value(sdf.format(value));
  }

  @Override
  public Date read(JsonReader in) throws IOException {
    try {
      return sdf.parse(in.nextString());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
}
