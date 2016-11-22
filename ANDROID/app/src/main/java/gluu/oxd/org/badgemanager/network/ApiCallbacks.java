package gluu.oxd.org.badgemanager.network;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by lcom76 on 9/11/16.
 */

public interface ApiCallbacks {
    public void success(JSONObject jsonObject);

    public void error(VolleyError error);
}
