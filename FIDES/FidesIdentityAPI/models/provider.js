"use strict";
// load the things we need
const mongoose = require('mongoose'),
    bcrypt = require('bcrypt-nodejs');

// define the schema for our openid connect provider model
const providerSchema = mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    url: {
        type: String,
        required: true
    },
    keys: {
        type: String,
        required: false
    },
    trustMarks: {
        type: String,
        required: false
    },
    clientId: {
        type: String,
        required: true
    },
    clientSecret: {
        type: String,
        required: true
    },
    responseType: {
        type: String,
        required: true
    },
    scope: {
        type: String,
        required: false
    },
    state: {
        type: String,
        required: true
    },
    redirectUri: {
        type: String,
        required: true
    },
    error: {
        type: String,
        required: false
    },
    errorDescription: {
        type: String,
        required: false
    },
    errorUri: {
        type: String,
        required: false
    },
    grantType: {
        type: String,
        required: true
    },
    code: {
        type: String,
        required: true
    },
    accessToken: {
        type: String,
        required: false
    },
    tokenType: {
        type: String,
        required: false
    },
    expiresIn: {
        type: String,
        required: false
    },
    username: {
        type: String,
        required: false
    },
    password: {
        type: String,
        required: false
    },
    refreshToken: {
        type: String,
        required: false
    },
    organization: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Organization'
    },
    isVerified: {
        type: Boolean,
        required: false
    },
    isApproved: {
        type: Boolean,
        required: false
    },
    createdOn: {
        type: Date,
        default: new Date()
    }
});

// create the model for openid connect provider and expose it to our app
module.exports = mongoose.model('Provider', providerSchema);
