package to.mattias.stash.model;

import java.util.Comparator;

public class ExpirationSorter implements Comparator<StashItem> {

  @Override
  public int compare(StashItem o1, StashItem o2) {
    if (o1.getExpiration().before(o2.getExpiration())) return -1;
    else if (o2.getExpiration().before(o1.getExpiration())) return 1;

    return 0;
  }
}
