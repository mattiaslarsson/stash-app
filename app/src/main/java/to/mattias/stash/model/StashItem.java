package to.mattias.stash.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.JsonAdapter;
import java.util.Date;
import lombok.Data;

@Data
public class StashItem implements Parcelable {

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    public StashItem createFromParcel(Parcel in) {
      return new StashItem(in);
    }

    @Override
    public Object[] newArray(int size) {
      return new StashItem[size];
    }
  };
  private String ean;
  private String description;
  @JsonAdapter(DateAdapter.class)
  private Date expiration;
  private int box;

  public StashItem() {
  }

  public StashItem(Parcel in) {
    this.ean = in.readString();
    this.description = in.readString();
    this.expiration = (Date) in.readSerializable();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.ean);
    dest.writeString(this.description);
    dest.writeSerializable(this.expiration);
  }

}
