"use strict";

// load up the user model
const providerModel = require('../models/provider');

// =============================================================================
// Register Openid connect provider ============================================
// =============================================================================
let registerProvider = (body,done) => {
                 // Register OpenId Connect
                let objProviderModel = new providerModel();

                objProviderModel.name = body.name;
                objProviderModel.opUrls = body.opUrls;
                objProviderModel.trustMarks = body.trustMarks;
                objProviderModel.client_id=body.client_id;
                objProviderModel.client_secret=body.client_secret;
                objProviderModel.response_type=body.response_type;
                objProviderModel.code=body.code;
                objProviderModel.state=body.state;
                objProviderModel.error=body.error;
                objProviderModel.error_description=body.error_description;
                objProviderModel.nonce=body.nonce;
                objProviderModel.error_uri=body.error_uri;
                objProviderModel.expires_in=body.expires_in;
                objProviderModel.redirect_uri=body.redirect_uri;
                objProviderModel.scope=body.scope;
                objProviderModel.username=body.username;
                objProviderModel.password=body.password;
                objProviderModel.refresh_token=body.refresh_token;
                objProviderModel.grant_type=body.grant_type;
                objProviderModel.access_token=body.access_token;
                objProviderModel.keys = body.keys;
                objProviderModel.token_type=body.token_type;
                objProviderModel.display=body.display;
                objProviderModel.prompt=body.prompt;
                objProviderModel.acr_values=body.acr_values;

                objProviderModel.save(err => {
                    if (err)
                        return done(err);
                });

    return done(null, objProviderModel);
};

// =============================================================================
// Retrieves all provider ===============================================
// =============================================================================
let getAllProviders = (done) => {

    let query = providerModel.find(true);
    query.sort({
       name: 'asc'
    });

   query.exec((err, providers) => {
            if (err)
                done(err);
            else {
                if (providers.length) {
                    console.log(providers);
                    done(null, providers);
                } else {
                    done(null, null, {
                        message: 'No records found'
                    });
                }
            }
        });
};

module.exports = {
    registerProvider,getAllProviders
};
