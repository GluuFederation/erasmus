package net.gluu.erasmus.api;

import net.gluu.erasmus.model.BadgeRequests;
import net.gluu.erasmus.model.BadgeTemplates;
import net.gluu.erasmus.model.ParticipantsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("participants/{state}/{city}")
    Call<ParticipantsResponse> getParticipants(@Path("state") String state, @Path("city") String city);

    @GET("badges/request/list/{status}")
    Call<BadgeRequests> getBadgeRequests(@Header("AccessToken") String accessToken, @Path("status") String status, @Query("opHost") String opHost);

    @POST("/badges/templates/")
    Call<BadgeTemplates> getBadgeTemplates(@Header("AccessToken") String accessToken, @Query("opHost") String opHost, @Query("type") String type);
}
