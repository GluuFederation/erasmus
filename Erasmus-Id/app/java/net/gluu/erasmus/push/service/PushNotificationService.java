package net.gluu.erasmus.push.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import net.gluu.erasmus.Application;
import net.gluu.erasmus.BadgeAccessDialog;
import net.gluu.erasmus.BadgeStatusActivity;
import net.gluu.erasmus.R;
import net.gluu.erasmus.ScanFailureActivity;
import net.gluu.erasmus.ScanSuccessActivity;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.model.ScanResponseSuccess;
import net.gluu.erasmus.push.Config;
import net.gluu.erasmus.utils.NotificationUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushNotificationService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "TAG";

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.v(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.v(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            Log.v(TAG, "Notification Title: " + remoteMessage.getNotification().getTitle());

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.v(TAG, "Data Payload: " + remoteMessage.getData().toString());
                Log.v(TAG, "Badge Id: " + remoteMessage.getData().get("badge"));
                Log.v(TAG, "Type: " + remoteMessage.getData().get("notifyType"));

                try {
                    handleDataMessage(remoteMessage.getData(), remoteMessage.getNotification());
                } catch (Exception e) {
                    Log.v(TAG, "Exception: " + e.getMessage());
                }
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(Map<String, String> data, RemoteMessage.Notification notification) {

        try {

            if (data.get("notifyType").equals("1")) {
                Log.v(TAG, "from email: " + data.get("fromEmail"));
                Log.v(TAG, "badge: " + data.get("badge"));
                Log.v(TAG, "badge title: " + data.get("badgeTitle"));
                Intent intent = new Intent(getApplicationContext(), BadgeAccessDialog.class);
                intent.putExtra(getString(R.string.key_message), notification.getBody());
                intent.putExtra(getString(R.string.key_badge), data.get("badge"));
                intent.putExtra(getString(R.string.key_badge_title), data.get("badgeTitle"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (data.get("notifyType").equals("2")) {
                Log.v(TAG, "from asserter: " + data.get("fromAsserter"));
                Log.v(TAG, "temp url: " + data.get("tempUrl"));
                if (data.get("tempUrl").length() > 0) {
                    verifyBadge(data.get("tempUrl"));
                } else {
                    Intent i = new Intent(getApplicationContext(), BadgeAccessDialog.class);
                    i.putExtra(getString(R.string.key_message), notification.getBody());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            } else if (data.get("notifyType").equals("3")) {
                Intent intent = new Intent(getApplicationContext(), BadgeAccessDialog.class);
                intent.putExtra(getString(R.string.key_message), notification.getBody());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (data.get("notifyType").equals("4")) {
                Intent intent = new Intent(getApplicationContext(), BadgeStatusActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.v(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    private void verifyBadge(String url) {
        if (Application.checkInternetConnection(getApplicationContext())) {
            Call<ScanResponseSuccess> call = APIService.createService(APIInterface.class).getScanAllResult(url);
            call.enqueue(new Callback<ScanResponseSuccess>() {
                @Override
                public void onResponse(Call<ScanResponseSuccess> call, Response<ScanResponseSuccess> response) {

                    if (response.isSuccessful()) {
                        if (response.errorBody() == null && response.body() != null) {
                            ScanResponseSuccess scanResponseObj = response.body();

                            if (scanResponseObj != null) {
                                Application.scanResponseSuccess = scanResponseObj;
                                if (scanResponseObj.getError() == null || !scanResponseObj.getError()) {
                                    Intent i = new Intent(getApplicationContext(), ScanSuccessActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(getApplicationContext(), ScanFailureActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ScanResponseSuccess> call, Throwable t) {
                    Log.e(TAG, "onFailure: ", t.getCause());
                }
            });
        } else {
            Log.v("TAG", getString(R.string.no_internet));
        }
    }
}
