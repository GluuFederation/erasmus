"use strict";

const mongoose = require('mongoose'),
    bcrypt = require('bcrypt-nodejs');

var Role = require('./role');

// define the schema for user
const userSchema = mongoose.Schema({
    username: {
        type: String,
        required: true,
        unique: true
    },
    email: {
        type: String,
        required: true,
        unique: true
    },
    password: {
        type: String,
        required: false
    },
    firstName: {
        type: String,
        required: false
    },
    lastName: {
        type: String,
        required: false
    },
    isActive: {
        type: Boolean,
        default: false
    },
    scimId: {
        type: String,
        required: false
    },
    organization: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Organization'
    },
    role: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Role'
    },
    provider: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Provider'
    }
}, {
    timestamps: true
});

// generating a hash
userSchema.methods.generateHash = function(password) {
    return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};

// checking if password is valid
userSchema.methods.validPassword = function(password) {
    return bcrypt.compareSync(password, this.password);
};

// making the model safe
userSchema.methods.safeModel = function() {
    return {
        _id: this._id,
        username: this.username,
        email: this.email,
        firstName: this.firstName,
        lastName: this.lastName,
        isActive: this.isActive,
        createdOn: this.createdOn,
        scimId: this.scimId,
        organization: this.organization,
        role: this.role,
        provider: this.provider
    }
};

// create the model for users and expose it to our app
module.exports = mongoose.model('User', userSchema);
