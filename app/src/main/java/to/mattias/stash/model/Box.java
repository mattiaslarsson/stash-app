package to.mattias.stash.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;
import lombok.Data;

@Data
public class Box implements Parcelable {

  private int boxNumber;
  private List<StashItem> items;

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    public Box createFromParcel(Parcel in) {
      return new Box(in);
    }

    @Override
    public Object[] newArray(int size) {
      return new Box[size];
    }
  };

  public Box(Parcel in) {
    this.boxNumber = in.readInt();
    this.items = in.readArrayList(this.getClass().getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.boxNumber);
    dest.writeList(this.items);
  }
}
