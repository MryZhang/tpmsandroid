package com.ethan.morephone.fcm;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class NotifyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "NotifyFbIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        /*
         * Use the service to refresh the registration binding. The token is not passed
         * because the token is requested by the Service.
         */

//        Call<TokenResponse> call = TwilioSDKStarterAPI.registerToken(new TokenRequest(token));
//        call.enqueue(new Callback<TokenResponse>() {
//            @Override
//            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
//                if (response.isSuccess()) {
//                    Log.d("TAG", "TOKEN SUCCESS" + response.body().token);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TokenResponse> call, Throwable t) {
//
//            }
//        });

        Intent intent = new Intent(this, BindingIntentService.class);
        startService(intent);
    }

}
