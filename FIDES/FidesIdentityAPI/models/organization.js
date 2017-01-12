"use strict";

const mongoose = require('mongoose'),
    bcrypt = require('bcrypt-nodejs');

// define the schema for our role
const organizationSchema = mongoose.Schema({
    name: {
        type: String,
        required: true,
        unique: true
    },
    ottoId: {
        type: mongoose.Schema.Types.ObjectId,
        required: false,
    },
    isActive: {
        type: Boolean,
        default: false
    },
    createdOn: {
        type: Date,
        default: new Date()
    }
});

// create the model for organizations and expose it to our app
module.exports = mongoose.model('Organization', organizationSchema);
