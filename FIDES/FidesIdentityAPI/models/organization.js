"use strict";

const mongoose = require('mongoose'),
  bcrypt = require('bcrypt-nodejs');

const common = require('../helpers/common');

// define the schema for our organization
const organizationSchema = mongoose.Schema({
  name: {
    type: String,
    required: true,
    unique: true
  },
  phoneNo: {
    type: String
  },
  address: {
    type: String
  },
  zipcode: {
    type: String
  },
  state: {
    type: String
  },
  city: {
    type: String
  },
  type: {
    type: String
  },
  description: {
    type: String
  },
  federation: {
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
  },
  trustMarkFile: {
    type: String
  },
  '@context': {
    type: String
  },
  '@id': {
    type: String
  },
  entities: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Provider'
  }],
  federations: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Federation'
  }],
  trustMark: String,
  pendingBadges: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Badge'
  }],
  approvedBadges: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Badge'
  }]
}, {
  timestamps: true
}, {
  strict: false
});

organizationSchema.pre('findOne', populateFederation);
organizationSchema.pre('findById', populateFederation);
organizationSchema.pre('find', populateFederation);
organizationSchema.pre('save', setUrl);

function populateFederation() {
  this.populate('federation');
  return this;
}

function setUrl(next, done) {
  this['@id'] = common.constant.OTTO_BASE_URL + common.constant.OTTO_ORGANIZATION_URL + '/' + this._id;
  this['@context'] = common.constant.CONTEXT_SCHEMA_URL + common.constant.ORGANIZATION_CONTEXT;
  next();
}

// create the model for organizations and expose it to our app
module.exports = mongoose.model('Organization', organizationSchema);
