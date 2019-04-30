package to.mattias.stash.model;

import lombok.Data;

@Data
public class ExpiringItem {

  private int box;
  private StashItem expiringItem;

}
