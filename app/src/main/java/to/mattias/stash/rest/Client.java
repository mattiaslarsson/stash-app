package to.mattias.stash.rest;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import to.mattias.stash.model.Article;
import to.mattias.stash.model.Box;
import to.mattias.stash.model.StashItem;

public interface Client {

  @GET("/stash")
  @Headers("accept: application/json")
  Call<List<Box>> getBoxes();

  @GET("/stash/{boxNumber}")
  @Headers("accept: application/json")
  Call<List<StashItem>> getBox(@Path("boxNumber") int boxNumber);

  @POST("/stash/{boxNumber}")
  @Headers("content-type: application/json")
  Call<Void> addItemInBox(@Path("boxNumber") int boxNumber, @Body StashItem item);

  @DELETE("/stash/{boxNumber}")
  Call<Void> deleteBox(@Path("boxNumber") int boxNumber);

  @DELETE("/stash/{boxNumber}/ean/{ean}")
  Call<Void> deleteItemInBox(@Path("boxNumber") int boxNumber, @Path("ean") String ean);

  @GET("/stash/ean/{ean}")
  @Headers("accept: application/json")
  Call<List<Box>> getBoxesByEan(@Path("ean") String ean);

  @GET("/stash/description/{despcription}")
  @Headers("accept: application/json")
  Call<List<Box>> getBoxesByDescription(@Path("description") String description);

  @GET("/article/{ean}")
  @Headers("accept: application/json")
  Call<Article> getArticleByEan(@Path("ean") String ean);

  @POST("/article")
  @Headers("content-type: application/json")
  Call<Void> addArticle(@Body Article article);

}
