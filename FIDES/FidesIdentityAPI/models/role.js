"use strict";

const mongoose = require('mongoose'),
    bcrypt = require('bcrypt-nodejs');

// define the schema for user role
const roleSchema = mongoose.Schema({
    name: {
        type: String,
        required: true,
        unique: true
    },
    nameSlug: {
        type: String,
        required: true,
        unique: true
    },
    order: {
        type: Number,
        required: true,
    },
    isActive: {
        type: Boolean,
        default: true
    },
    createdOn: {
        type: Date,
        default: new Date()
    }
});

// create the model for roles and expose it to our app
module.exports = mongoose.model('Role', roleSchema);
