package to.mattias.stash.model;

import java.util.Comparator;

public class DescriptionSorter implements Comparator<StashItem> {

  @Override
  public int compare(StashItem o1, StashItem o2) {
    return o1.getDescription().compareToIgnoreCase(o2.getDescription());
  }
}
