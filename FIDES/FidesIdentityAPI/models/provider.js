"use strict";
// load the things we need
const mongoose = require('mongoose'),
    bcrypt = require('bcrypt-nodejs');

// define the schema for our openid connect provider model
const providerSchema = mongoose.Schema({
    name: {
        type: String,
        required: true,
        unique: true
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
    client_id: {
        type: String,
        required: true
    },
    client_secret: {
        type: String,
        required: true
    },
    response_type: {
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
    redirect_uri: {
        type: String,
        required: true
    },
    error: {
        type: String,
        required: false
    },
    error_description: {
        type: String,
        required: false
    },
    error_uri: {
        type: String,
        required: false
    },
    grant_type: {
        type: String,
        required: true
    },
    code: {
        type: String,
        required: true
    },
    access_token: {
        type: String,
        required: false
    },
    token_type: {
        type: String,
        required: false
    },
    expires_in: {
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
    refresh_token: {
        type: String,
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
