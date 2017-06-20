"use strict";

const mongoose = require('mongoose'),
  bcrypt = require('bcrypt-nodejs');

const common = require('../helpers/common');

// define the schema for our participant
const participantSchema = mongoose.Schema({
  '@id': {
    type: String
  },
  '@context': {
    type: String
  },
  name: {
    type: String,
    required: true,
    unique: true
  },
  url: {
    type: String
  },
  description: {
    type: String
  },
  memberOf: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Federation'
  }],
  operates: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Entity'
  },
  registeredBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'RegistrationAuthority'
  },
  technicalContact: [{
    type: mongoose.Schema.Types.Mixed
  }],
  executiveContact: [{
    type: mongoose.Schema.Types.Mixed
  }],
  securityContact: [{
    type: mongoose.Schema.Types.Mixed
  }],
  isApproved: {
    type: Boolean,
    default: false
  },
  trustMarkFile: {
    type: String
  },
  pendingBadges: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Badge'
  }],
  approvedBadges: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Badge'
  }],
  type: {
    type: String
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
  }
}, {
  timestamps: true
}, {
  strict: false
});

participantSchema.pre('findOne', populateFederation);
participantSchema.pre('findById', populateFederation);
participantSchema.pre('find', populateFederation);
participantSchema.pre('save', setUrl);

function populateFederation() {
  this.populate('memberOf');
  return this;
}

function setUrl(next, done) {
  this['@id'] = common.constant.OTTO_BASE_URL + common.constant.OTTO_PARTICIPANT_URL + '/' + this._id;
  this['@context'] = common.constant.CONTEXT_SCHEMA_URL + common.constant.PARTICIPANT_CONTEXT;
  this.registeredBy = common.constant.RA_ID;
  next();
}

// create the model for participants and expose it to our app
module.exports = mongoose.model('Participant', participantSchema);
