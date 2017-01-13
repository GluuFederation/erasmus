"use strict";

const Provider = require('../models/provider');

/**
 * Callback function for all the export functions.
 * @callback requestCallback
 * @param {Error} error - Error information from base function.
 * @param {Object} [data] - Data from base function.
 * @param {Object} [info] - Message from base function if object not found.
 */

/**
 * Retrieves all providers.
 * @param {ObjectId} userId - Org admin user id, pass undefined if user is admin.
 * @param {requestCallback} done - Callback function that returns error, object or info.
 */
let getAllProviders = (userId, done) => {
    let queryCondition = {};
    if(userId && userId != 'undefined') {
        queryCondition['createdBy'] = userId;
    }

    let query = Provider.find(queryCondition).populate('createdBy organization');
    query.sort({
        name: 'asc'
    });

    query.exec((err, providers) => {
        if (err)
            done(err);
        else {
            if (providers.length) {
                done(null, providers);
            } else {
                done(null, null, {
                    message: 'No records found'
                });
            }
        }
    });
};

let getProviderById = (providerId, done) => {
    let query = Provider.findOne({
        _id: providerId
    }).populate('createdBy organization');

    query.exec((err, provider) => {
        if (err)
            return done(err);
        else {
            if (!provider) {
                return done(null, null, {
                    message: 'Provider not found'
                });
            } else {
                return done(null, provider);
            }
        }
    });
};

/**
 * Add provider
 * @param {object} req - Request json object
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let addProvider = (req, done) => {
    process.nextTick(() => {
        Provider.findOne({
            'url': req.url
        }, (err, provider) => {
            if (err)
                return done(err);

            // check if already exists or not
            if (!!provider) {
                return done(null, false, {
                    'message': 'Provider is already exists.'
                });
            } else {
                // Register OpenId Connect
                let objProvider = new Provider();

                objProvider.name = req.name;
                objProvider.url = req.url;
                objProvider.clientId = req.clientId;
                objProvider.clientSecret = req.clientSecret;
                // objProvider.keys = req.keys;
                // objProvider.trustMarks = req.trustMarks;
                // objProvider.responseType = req.responseType;
                // objProvider.scope = req.scope;
                // objProvider.state = req.state;
                // objProvider.redirectUri = req.redirectUri;
                // objProvider.error = req.error;
                // objProvider.errorDescription = req.errorDescription;
                // objProvider.errorUri = req.errorUri;
                // objProvider.grantType = req.grantType;
                // objProvider.code = req.code;
                // objProvider.accessToken = req.accessToken;
                // objProvider.tokenType = req.tokenType;
                // objProvider.expiresIn = req.expiresIn;
                // objProvider.username = req.username;
                // objProvider.password = req.password;
                // objProvider.refreshToken = req.refreshToken;
                objProvider.organization = req.organizationId;
                objProvider.createdBy = req.createdBy;
                objProvider.isApproved = false;
                objProvider.isVerified = false;

                objProvider.save(err => {
                    if (err)
                        return done(err);

                    Provider.populate(objProvider, 'organization', function (err, objProvider) {
                        if (err)
                            return done(err);

                        return done(null, objProvider);
                    });
                });
            }
        });
    });
};

/**
 * Update provider
 * @param {object} req - Request json object
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let updateProvider = (req, done) => {
    process.nextTick(() => {
        Provider.findOne({
            '_id': req._id
        }).populate('createdBy organization').exec(function (err, objProvider) {
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
                objProvider.clientId = req.clientId;
                objProvider.clientSecret = req.clientSecret;
                // objProvider.keys = req.keys;
                // objProvider.trustMarks = req.trustMarks;
                // objProvider.responseType = req.responseType;
                // objProvider.scope = req.scope;
                // objProvider.state = req.state;
                // objProvider.redirectUri = req.redirectUri;
                // objProvider.error = req.error;
                // objProvider.errorDescription = req.errorDescription;
                // objProvider.errorUri = req.errorUri;
                // objProvider.grantType = req.grantType;
                // objProvider.code = req.code;
                // objProvider.accessToken = req.accessToken;
                // objProvider.tokenType = req.tokenType;
                // objProvider.expiresIn = req.expiresIn;
                // objProvider.username = req.username;
                // objProvider.password = req.password;
                // objProvider.refreshToken = req.refreshToken;
                objProvider.organization = req.organizationId;

                objProvider.save(err => {
                    if (err)
                        return done(err);

                    Provider.populate(objProvider, 'organization', function (err, objProvider) {
                        if (err)
                            return done(err);

                        return done(null, objProvider);
                    });
                });
            }
        });
    });
};

/**
 * Remove provider
 * @param {ObjectId} providerId - Provider id
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
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

/**
 *
 * @param {ObjectId} providerId - Provider id
 * @param ottoId
 * @param {requestCallback} done - Callback function that returns error, object or info
 * @returns {Object} info - Object with information message.
 */
let approveProvider = (providerId, ottoId, done) => {
    if(!ottoId){
        return done(null, false, {
            'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
        });
    }

    process.nextTick(() => {
        Provider.findOne({
            '_id': providerId
        }).populate('createdBy organization').exec(function (err, objProvider) {
            if (err)
                return done(err);

            // check if already exists or not.
            if (!objProvider) {
                return done(null, false, {
                    'message': 'Provider not found.'
                });
            } else {
                objProvider.ottoId = ottoId;
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
    getAllProviders, getProviderById, addProvider, updateProvider, removeProvider, approveProvider
};
