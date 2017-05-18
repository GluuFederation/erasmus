package net.gluu.erasmus.model;

/**
 * Created by Meghna Joshi on 10/5/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScanResponseSuccess {

    @SerializedName("context")
    @Expose
    private String context;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("issuedOn")
    @Expose
    private String issuedOn;
    @SerializedName("expires")
    @Expose
    private String expires;
    @SerializedName("recipient")
    @Expose
    private Recipient recipient;
    @SerializedName("verification")
    @Expose
    private Verification verification;
    @SerializedName("badge")
    @Expose
    private Badge badge;

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

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(String issuedOn) {
        this.issuedOn = issuedOn;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public Verification getVerification() {
        return verification;
    }

    public void setVerification(Verification verification) {
        this.verification = verification;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public class Badge {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("criteria")
        @Expose
        private Criteria criteria;
        @SerializedName("issuer")
        @Expose
        private Issuer issuer;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Criteria getCriteria() {
            return criteria;
        }

        public void setCriteria(Criteria criteria) {
            this.criteria = criteria;
        }

        public Issuer getIssuer() {
            return issuer;
        }

        public void setIssuer(Issuer issuer) {
            this.issuer = issuer;
        }

        public class Criteria {

            @SerializedName("narrative")
            @Expose
            private String narrative;

            public String getNarrative() {
                return narrative;
            }

            public void setNarrative(String narrative) {
                this.narrative = narrative;
            }

        }

        public class Issuer {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("type")
            @Expose
            private String type;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("url")
            @Expose
            private String url;
            @SerializedName("email")
            @Expose
            private String email;
            @SerializedName("verification")
            @Expose
            private Verification_ verification;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public Verification_ getVerification() {
                return verification;
            }

            public void setVerification(Verification_ verification) {
                this.verification = verification;
            }
        }


        public class Verification_ {

            @SerializedName("allowedOrigins")
            @Expose
            private String allowedOrigins;
            @SerializedName("type")
            @Expose
            private String type;

            public String getAllowedOrigins() {
                return allowedOrigins;
            }

            public void setAllowedOrigins(String allowedOrigins) {
                this.allowedOrigins = allowedOrigins;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

        }


    }


}