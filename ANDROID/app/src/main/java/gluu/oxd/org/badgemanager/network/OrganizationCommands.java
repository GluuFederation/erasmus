package gluu.oxd.org.badgemanager.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import gluu.oxd.org.badgemanager.Application;
import gluu.oxd.org.badgemanager.Statics.Methods;
import gluu.oxd.org.badgemanager.Statics.Values;

/**
 * Created by lcom76 on 9/11/16.
 */

public class OrganizationCommands {
    RequestQueue queue;

    public OrganizationCommands() {
        Methods.trustEveryone();
        queue = Volley.newRequestQueue(Application.applicationContext);
    }

    public void getOrganizations(ApiCallbacks apiCallbacks) {
        queue.add(createOrgListRquest(apiCallbacks));

    }

    private JsonObjectRequest createOrgListRquest(final ApiCallbacks apiCallbacks) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Values.BaseURL + "/organizations", null, new Response.Listener<JSONObject>() {
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
