"use strict";

const Provider = require('../models/provider');

/**
 * Get all active providers
 * @return {providers} - return all providers
 * @return {err} - return error
 */
let getAllProviders = () => {
    return Provider
        .find({})
        .sort({name: 1})
        .exec()
        .then(providers => providers)
        .catch(err => err);
};

/**
 * Gets provider detail by provider Id.
 * @return {provider} - return all provider
 * @return {err} - return error
 */
let getProviderById = (id) => {
    return Provider.findById(id)
        .exec()
        .then(providers => Promise.resolve(providers))
        .catch(err => Promise.reject(err));
};

/**
 * Gets provider detail by Discovery URL
 * @return {provider} - return all provider
 * @return {err} - return error
 */
let getProviderByUrl = (url, done) => {
    return Provider.findOne({ discoveryUrl: url })
        .exec()
        .then(providers => Promise.resolve(providers))
        .catch(err => Promise.reject(err));
};

/**
 * Add provider
 * @param {object} req - Request json object
 * @return {provider} - return provider
 * @return {err} - return error
 */
let addProvider = (req) => {
    let oProvider = new Provider();
    oProvider.name = req.name;
    oProvider.discoveryUrl = req.discoveryUrl;
    oProvider.clientId = req.clientId;
    oProvider.clientSecret = req.clientSecret;
    // oProvider.keys = req.keys;
    // oProvider.trustMarks = req.trustMarks;
    // oProvider.responseType = req.responseType;
    // oProvider.scope = req.scope;
    // oProvider.state = req.state;
    // oProvider.redirectUri = req.redirectUri;
    // oProvider.error = req.error;
    // oProvider.errorDescription = req.errorDescription;
    // oProvider.errorUri = req.errorUri;
    // oProvider.grantType = req.grantType;
    // oProvider.code = req.code;
    // oProvider.accessToken = req.accessToken;
    // oProvider.tokenType = req.tokenType;
    // oProvider.expiresIn = req.expiresIn;
    // oProvider.username = req.username;
    // oProvider.password = req.password;
    // oProvider.refreshToken = req.refreshToken;
    oProvider.authorizationEndpoint = req.authorizationEndpoint;
    oProvider.redirectUris = req.redirectUris;
    oProvider.responseTypes = req.responseTypes;
    oProvider.organization = req.organizationId;
    oProvider.createdBy = req.createdBy;
    oProvider.isApproved = false;
    oProvider.isVerified = false;

    return oProvider.save()
        .then(provider => Promise.resolve(provider))
        .catch(err => Promise.reject(err));
};

/**
 * update provider
 * @param {object} req - Request json object
 * @return {provider} - return provider
 * @return {err} - return error
 */
let updateProvider = (req) => {
    return Provider.findById(req._id)
        .exec()
        .then((oProvider) => {
            oProvider.name = req.name;
            oProvider.discoveryUrl = req.discoveryUrl;
            oProvider.clientId = req.clientId;
            oProvider.clientSecret = req.clientSecret;
            // oProvider.keys = req.keys;
            // oProvider.trustMarks = req.trustMarks;
            // oProvider.responseType = req.responseType;
            // oProvider.scope = req.scope;
            // oProvider.state = req.state;
            // oProvider.redirectUri = req.redirectUri;
            // oProvider.error = req.error;
            // oProvider.errorDescription = req.errorDescription;
            // oProvider.errorUri = req.errorUri;
            // oProvider.grantType = req.grantType;
            // oProvider.code = req.code;
            // oProvider.accessToken = req.accessToken;
            // oProvider.tokenType = req.tokenType;
            // oProvider.expiresIn = req.expiresIn;
            // oProvider.username = req.username;
            // oProvider.password = req.password;
            // oProvider.refreshToken = req.refreshToken;
            oProvider.organization = req.organizationId;
            return oProvider.save();
        })
        .then((savedProvider) => {
            console.log(savedProvider);
            return Provider.populate(savedProvider, 'organization', function (savedProvider) {
                console.log(savedProvider);
                return Promise.resolve(savedProvider);
            });
        })
        .catch(err => Promise.reject(err));
};

/**
 * Remove provider by Id
 * @param {ObjectId} id - provider id
 * @return {provider} - return provider
 * @return {err} - return error
 */
let removeProvider = (id) => {
    return Provider
        .findById(id)
        .exec()
        .then((oProvider) => {
            return oProvider
                .remove()
                .then((remProvider) => Promise.resolve(remProvider))
                .catch(err => Promise.reject(err));
        })
        .catch(err => Promise.reject(err));
};

/**
 * Approve Provider by Id
 * @param {ObjectId} orgId - Provider id
 * @param {ObjectId} ottoId - Provider otto id
 * @return {Provider} - return Provider
 * @return {err} - return error
 */
let approveProvider = (proId, ottoId) => {
    return Provider
        .findById(proId)
        .exec()
        .then((oProvider) => {
            oProvider.ottoId = ottoId;
            oProvider.isApproved = true;
            return oProvider.save()
                .then(updatedProvider => Promise.resolve(updatedProvider))
                .catch(err => Promise.reject(err));
        })
        .catch(err => Promise.reject(err));
};

module.exports = {
    getAllProviders, getProviderById, getProviderByUrl, addProvider, updateProvider, removeProvider, approveProvider
};
