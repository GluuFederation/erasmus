package net.gluu.erasmus.api;

/**
 * Created by Arvind Tomar on 26/5/17.
 */
public interface AccessToken {
    void onAccessTokenSuccess(String accessToken);
    void onAccessTokenFailure();
}
