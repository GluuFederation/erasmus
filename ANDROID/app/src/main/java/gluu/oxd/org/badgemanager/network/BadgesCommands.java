package gluu.oxd.org.badgemanager.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import gluu.oxd.org.badgemanager.Application;
import gluu.oxd.org.badgemanager.Statics.Methods;
import gluu.oxd.org.badgemanager.Statics.Values;
import gluu.oxd.org.badgemanager.models.Badges;

/**
 * Created by lcom76 on 9/11/16.
 */

public class BadgesCommands {

    RequestQueue queue;

    public BadgesCommands() {
        Methods.trustEveryone();
        queue = Volley.newRequestQueue(Application.applicationContext);
    }

    public void getBadges(ApiCallbacks apiCallbacks) {
        queue.add(createBadgeListRquest(apiCallbacks));

    }


    public void upDate(Badges badges, ApiCallbacks apiCallbacks) {
        queue.add(upDateBadge(badges, apiCallbacks));

    }

    private JsonObjectRequest upDateBadge(Badges badges, final ApiCallbacks apiCallbacks) {
        String s = new Gson().toJson(badges, Badges.class);

        JsonObjectRequest request = null;
        try {
            request = new JsonObjectRequest(Request.Method.POST, Values.BaseURL + "/badges/update/" + badges.getInum().trim(), new JSONObject(s), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    apiCallbacks.success(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    apiCallbacks.error(error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            apiCallbacks.error(new VolleyError("in  valid request"));

        }
        return request;
    }

    private JsonObjectRequest createBadgeListRquest(final ApiCallbacks apiCallbacks) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Values.BaseURL + "/badges", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                apiCallbacks.success(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallbacks.error(error);
            }
        });

        return request;
    }
}
