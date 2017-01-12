"use strict";

const mongoose = require('mongoose'),
    bcrypt = require('bcrypt-nodejs');

// define the schema for openid connect provider
const providerSchema = mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    url: {
        type: String,
        required: true,
        unique: true
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
        required: false //true
    },
    scope: {
        type: String,
        required: false
    },
    state: {
        type: String,
        required: false //true
    },
    redirectUri: {
        type: String,
        required: false //true
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
        required: false //true
    },
    code: {
        type: String,
        required: false //true
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
    ottoId: {
        type: mongoose.Schema.Types.ObjectId,
        required: false,
    },
    createdOn: {
        type: Date,
        default: new Date()
    },
    createdBy: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    }
});

// create the model for openid connect provider and expose it to our app
module.exports = mongoose.model('Provider', providerSchema);
