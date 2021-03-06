package net.gluu.erasmus.api;

import net.gluu.erasmus.model.APIBadgeDetail;
import net.gluu.erasmus.model.APIBadgeRequest;
import net.gluu.erasmus.model.BadgeRequest;
import net.gluu.erasmus.model.BadgeRequests;
import net.gluu.erasmus.model.BadgeTemplates;
import net.gluu.erasmus.model.DisplayBadge;
import net.gluu.erasmus.model.NotificationRequest;
import net.gluu.erasmus.model.ParticipantsResponse;
import net.gluu.erasmus.model.PrivacyRequest;
import net.gluu.erasmus.model.ScanResponseSuccess;
import net.gluu.erasmus.model.SetPermissionRequest;
import net.gluu.erasmus.model.TemplateBadgeRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface APIInterface {

    @GET("participants/{state}/{city}")
    Call<ParticipantsResponse> getParticipants(@Path("state") String state, @Path("city") String city);

    @POST("badges/request/list")
    Call<BadgeRequest> getBadgeRequests(@Header("AccessToken") String accessToken, @Body APIBadgeRequest badgeRequest);

    @POST("badges/templates")
    Call<BadgeTemplates> getBadgeTemplates(@Header("AccessToken") String accessToken, @Body TemplateBadgeRequest templateBadgeRequest);

    @POST("badges/request")
    Call<BadgeRequest> makeBadgeRequest(@Header("AccessToken") String accessToken, @Body APIBadgeRequest badgeRequest);

    @HTTP(method = "DELETE", path = "badges/request/delete", hasBody = true)
    Call<BadgeRequest> deleteBadge(@Header("AccessToken") String accessToken, @Body APIBadgeDetail badgeDetail);

    @POST("badges/details")
    Call<DisplayBadge> getBadge(@Header("AccessToken") String accessToken, @Body APIBadgeDetail badgeDetail);

    @POST("badges/setPrivacy")
    Call<BadgeRequest> setPrivacy(@Header("AccessToken") String accessToken, @Body PrivacyRequest privacy);

    @GET
    Call<ScanResponseSuccess> getScanAllResult(@Url String url);

    @POST("notification/send")
    Call<BadgeRequest> sendNotification(@Header("AccessToken") String accessToken, @Body NotificationRequest notificationRequest);

    @POST("badges/setPermission")
    Call<BadgeRequest> setBadgePermission(@Header("AccessToken") String accessToken, @Body SetPermissionRequest permissionRequest);
}
