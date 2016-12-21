"use strict";

// load up the provider model
const Provider = require('../models/provider');

// =============================================================================
// Retrieves all provider ======================================================
// =============================================================================
let getAllProviders = (organizationId, done) => {
    let queryCondition = {};
    if(organizationId && organizationId != 'undefined') {
        console.log('asdf');
        queryCondition['organization'] = organizationId;
    }

    let query = Provider.find(queryCondition).populate('organization');
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

// =============================================================================
// Add Openid connect provider =================================================
// =============================================================================
let addProvider = (req, done) => {
    process.nextTick(() => {
        Provider.findOne({
            'url': req.url
        }, (err, provider) => {
            if (err)
                return done(err);

            // check if already exists or not
            if (provider) {
                return done(null, false, {
                    'message': 'Provider is already exists.'
                });
            } else {
                // Register OpenId Connect
                let objProvider = new Provider();

                objProvider.name = req.name;
                objProvider.url = req.url;
                objProvider.keys = req.keys;
                objProvider.trustMarks = req.trustMarks;
                objProvider.clientId = req.clientId;
                objProvider.clientSecret = req.clientSecret;
                objProvider.responseType = req.responseType;
                objProvider.scope = req.scope;
                objProvider.state = req.state;
                objProvider.redirectUri = req.redirectUri;
                objProvider.error = req.error;
                objProvider.errorDescription = req.errorDescription;
                objProvider.errorUri = req.errorUri;
                objProvider.grantType = req.grantType;
                objProvider.code = req.code;
                objProvider.accessToken = req.accessToken;
                objProvider.tokenType = req.tokenType;
                objProvider.expiresIn = req.expiresIn;
                objProvider.username = req.username;
                objProvider.password = req.password;
                objProvider.refreshToken = req.refreshToken;
                objProvider.organization = req.organizationId;
                objProvider.isApproved = false;
                objProvider.isVerified = false;

                objProvider.save(err => {
                    if (err)
                        return done(err);
                });

                Provider.populate(objProvider, 'organization', function (err, objProvider) {
                    if (err)
                        return done(err);

                    console.log(objProvider);
                    return done(null, objProvider);
                });
            }
        });
    });
};

// =============================================================================
// Update Provider =============================================================
// =============================================================================
let updateProvider = (req, done) => {
    process.nextTick(() => {
        Provider.findOne({
            '_id': req._id
        }).populate('organization').exec(function (err, objProvider) {
            if (err)
                return done(err);

            // check to see if there is already exists or not.
            if (!objProvider) {
                return done(null, false, {
                    'message': 'Provider not found.'
                });
            } else {
                objProvider.name = req.name;
                objProvider.url = req.url;
                objProvider.keys = req.keys;
                objProvider.trustMarks = req.trustMarks;
                objProvider.clientId = req.clientId;
                objProvider.clientSecret = req.clientSecret;
                objProvider.responseType = req.responseType;
                objProvider.scope = req.scope;
                objProvider.state = req.state;
                objProvider.redirectUri = req.redirectUri;
                objProvider.error = req.error;
                objProvider.errorDescription = req.errorDescription;
                objProvider.errorUri = req.errorUri;
                objProvider.grantType = req.grantType;
                objProvider.code = req.code;
                objProvider.accessToken = req.accessToken;
                objProvider.tokenType = req.tokenType;
                objProvider.expiresIn = req.expiresIn;
                objProvider.username = req.username;
                objProvider.password = req.password;
                objProvider.refreshToken = req.refreshToken;
                objProvider.organization = req.organizationId;

                objProvider.save(err => {
                    if (err)
                        return done(err);

                    Provider.populate(objProvider, 'organization', function (err, objProvider) {
                        if (err)
                            return done(err);

                        console.log(objProvider);
                        return done(null, objProvider);
                    });
                });
            }
        });
    });
};

// =============================================================================
// Remove Provider =============================================================
// =============================================================================
let removeProvider = (providerId, done) => {
    process.nextTick(() => {
        Provider.findOne({
            '_id': providerId
        }, (err, objProvider) => {
            if (err)
                return done(err);

            // check if already exists or not.
            if (!objProvider) {
                return done(null, false, {
                    'message': 'Provider not found.'
                });
            } else {
                objProvider.remove(err => {
                    if (err)
                        return done(err);

                    return done(null, objProvider);
                });
            }
        });
    });
};

// =============================================================================
// Approve Provider =============================================================
// =============================================================================
let approveProvider = (providerId, done) => {
    process.nextTick(() => {
        Provider.findOne({
            '_id': providerId
        }).populate('organization').exec(function (err, objProvider) {
            if (err)
                return done(err);

            // check if already exists or not.
            if (!objProvider) {
                return done(null, false, {
                    'message': 'Provider not found.'
                });
            } else {
                objProvider.isApproved = true;
                objProvider.save(err => {
                    if (err)
                        return done(err);

                    return done(null, objProvider);
                });
            }
        });
    });
};

module.exports = {
    getAllProviders, addProvider, updateProvider, removeProvider, approveProvider
};
