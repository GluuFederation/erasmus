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
    federationId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Federation"
    },
    isApproved: {
        type: Boolean,
        default: false
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

organizationSchema.pre('findOne', populateFederation);
organizationSchema.pre('findById', populateFederation);
organizationSchema.pre('find', populateFederation);

function populateFederation() {
    this.populate('federationId');
    return this;
}

// create the model for organizations and expose it to our app
module.exports = mongoose.model('Organization', organizationSchema);
