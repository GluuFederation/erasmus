package gluu.oxd.org.badgemanager;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gluu.oxd.org.badgemanager.Statics.Methods;
import gluu.oxd.org.badgemanager.Statics.Values;
import gluu.oxd.org.badgemanager.Views.DialogWbviewFragment;
import gluu.oxd.org.badgemanager.models.Person;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tvSigin)
    TextView tvSigin;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    private static final String TAG = "MainActivity";
    RequestQueue queue;
    String authUrl;
    private boolean success = false;
    private boolean isCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        queue = Volley.newRequestQueue(this);

        Methods.trustEveryone();
    }

    @OnClick({R.id.tvSigin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSigin:
                try {
                    if (!isCalled) {
                        view.setEnabled(false);
                        AuthotizeURL();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public void AuthotizeURL() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("arcValue", "basic");
        JsonObjectRequest reqAuthorizeURL = new JsonObjectRequest(Request.Method.POST, Values.BaseURL + "/oxd/getAuthorizationUrl", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                try {
                    authUrl = response.getString("url");
                    final DialogWbviewFragment dialogWbviewFragment = DialogWbviewFragment.newInstance(authUrl);
                    dialogWbviewFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.full_screen_dialog);
                    dialogWbviewFragment.setAuthDialogListner(new DialogWbviewFragment.AuthDialogListner() {
                        @Override
                        public void onSuccess(final String url) {
                            if (!success) {
                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        if (dialogWbviewFragment.isVisible() || dialogWbviewFragment.isRemoving()) {
                                            dialogWbviewFragment.dismissAllowingStateLoss();
                                        }
                                    }
                                };
                                runOnUiThread(runnable);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        userData(url);
                                    }
                                }, 1500);
                            } else {

                            }
                        }

                        @Override
                        public void onError(String error) {
                            tvSigin.setEnabled(true);

                        }
                    });
                    dialogWbviewFragment.show(MainActivity.this.getFragmentManager(), dialogWbviewFragment.getClass().getName().toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
            }
        });

        queue.add(reqAuthorizeURL);
    }


    public void userData(String url) {
        success = true;
        Uri uri = Uri.parse(url);
        String URL = Values.BaseURL + "/oxd/userdata?" + uri.getQuery();
        JsonObjectRequest userData = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                isCalled = false;
                success = false;
                Person person = new Person();

                try {
                    JSONObject claims = response.getJSONObject("claims");
                    if (claims != null) {

                        if (!claims.isNull("preferred_username")) {
                            person.setPreferredUsername(claims.getJSONArray("preferred_username").getString(0));
                        }
                        if (!claims.isNull("picture")) {
                            person.setPicture(claims.getJSONArray("picture").getString(0));
                        }
                        if (!claims.isNull("zoneinfo")) {
                            person.setZoneinfo(claims.getJSONArray("zoneinfo").getString(0));
                        }

                        if (!claims.isNull("birthdate")) {
                            person.setBirthdate(claims.getJSONArray("birthdate").getString(0));
                        }

                        if (!claims.isNull("gender")) {
                            person.setGender(claims.getJSONArray("gender").getString(0));
                        }

                        if (!claims.isNull("given_name")) {
                            person.setGivenName(claims.getJSONArray("given_name").getString(0));
                        }

                        if (!claims.isNull("nickname")) {
                            person.setNickname(claims.getJSONArray("nickname").getString(0));
                        }

                        if (!claims.isNull("email")) {
                            person.setEmail(claims.getJSONArray("email").getString(0));
                            person.setEmail_verified(claims.getJSONArray("email").getString(0));
                        }

                        if (!claims.isNull("name")) {
                            person.setName(claims.getJSONArray("name").getString(0));
                        }

                        if (!claims.isNull("locale")) {
                            person.setLocale(claims.getJSONArray("locale").getString(0));
                        }

                        if (!claims.isNull("role")) {
                            person.setRole(claims.getJSONArray("role").getString(0));
                        }

                        if (!claims.isNull("sub")) {
                            person.setSub(claims.getJSONArray("sub").getString(0));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Values.person = person;
                startActivity(new Intent(MainActivity.this, HomeScreen.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
                success = false;
                isCalled = false;
                tvSigin.setEnabled(true);
            }
        });

        queue.add(userData);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (queue != null) {
            queue.stop();
        }
    }
}
