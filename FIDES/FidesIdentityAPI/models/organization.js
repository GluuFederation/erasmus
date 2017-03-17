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
  phoneNo: {
    type: String,
    required: true
  },
  address: {
    type: String,
    required: true
  },
  zipcode: {
    type: String,
    required: true
  },
  state: {
    type: String,
    required: true
  },
  city: {
    type: String,
    required: true
  },
  type: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  ottoId: {
    type: mongoose.Schema.Types.ObjectId,
    required: false,
  },
  federationId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Federation'
  },
  isApproved: {
    type: Boolean,
    default: false
  },
  isActive: {
    type: Boolean,
    default: false
  }
}, {
  timestamps: true
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
