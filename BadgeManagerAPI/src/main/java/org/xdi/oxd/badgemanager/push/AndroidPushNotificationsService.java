package org.xdi.oxd.badgemanager.push;

import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Arvind Tomar on 31/5/17.
 */
@Component
public class AndroidPushNotificationsService {
    private static final String FIREBASE_SERVER_KEY = "AAAA5W1Stk4:APA91bFP4tJDHn49ulwWf1qsEcpclMlrV97Ue06niOxKNSMbNr_Nzqis0tW7O5NwHkqU77l3xQWAzU5oTjVxuQEnHrrnO6DG2HmrbGaNLfFRgYBAiMR6XfY4vx1KNZmdn3ViTo14XcgM";

    @Async
    public CompletableFuture<FirebaseResponse> send(HttpEntity<String> entity) {

        RestTemplate restTemplate = new RestTemplate();

        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
        restTemplate.setInterceptors(interceptors);

        FirebaseResponse firebaseResponse = restTemplate.postForObject("https://fcm.googleapis.com/fcm/send", entity, FirebaseResponse.class);

        return CompletableFuture.completedFuture(firebaseResponse);
    }
}
