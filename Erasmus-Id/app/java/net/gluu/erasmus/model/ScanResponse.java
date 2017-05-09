package net.gluu.erasmus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScanResponse {

@SerializedName("error")
@Expose
private Boolean error;
@SerializedName("errorMsg")
@Expose
private String errorMsg;

public Boolean getError() {
return error;
}

public void setError(Boolean error) {
this.error = error;
}

public String getErrorMsg() {
return errorMsg;
}

public void setErrorMsg(String errorMsg) {
this.errorMsg = errorMsg;
}

}
