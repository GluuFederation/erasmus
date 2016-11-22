package gluu.oxd.org.badgemanager.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import gluu.oxd.org.badgemanager.Application;
import gluu.oxd.org.badgemanager.Statics.Methods;
import gluu.oxd.org.badgemanager.Statics.Values;

/**
 * Created by lcom76 on 10/11/16.
 */

public class IssuedBadgesCommands {
    RequestQueue queue;

    public IssuedBadgesCommands() {
        Methods.trustEveryone();
        queue = Volley.newRequestQueue(Application.applicationContext);
    }

    public void getIssuedRequestes(ApiCallbacks apiCallbacks) {
        queue.add(createIssuedBadgeListRquest(apiCallbacks));
    }


    private JsonObjectRequest createIssuedBadgeListRquest(final ApiCallbacks apiCallbacks) {
        JSONObject data = new JSONObject();
        JsonObjectRequest request = null;
        try {
            data.put("email", "admin5@admin.com");

            request = new JsonObjectRequest(Request.Method.POST, Values.BaseURL + "/admin/getbadgeinstancesbyemail", data, new Response.Listener<JSONObject>() {
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
        }
        return request;
    }
}
