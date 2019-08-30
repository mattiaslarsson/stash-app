package to.mattias.stash.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitFactory {
  private static Retrofit retrofit;

  static {
    retrofit = new Retrofit.Builder()
        .baseUrl("https://stash-application.herokuapp.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }

  public static Client getRestClient() {
    return retrofit.create(Client.class);
  }

}
