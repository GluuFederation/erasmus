"use strict";

const mongoose = require('mongoose');
const common = require('../helpers/common');

// define the schema for registration authority
const registrationAuthoritySchema = mongoose.Schema({
  '@id': {
    type: String
  },
  '@context': {
    type: String
  },
  name: {
    type: String,
    required: true
  },
  url: {
    type: String
  },
  description: {
    type: String
  },
  federation_endpoint: {
    type: String
  },
  participant_endpoint: {
    type: String
  },
  entity_endpoint: {
    type: String
  },
  registers: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Federation'
  }]
}, {
  timestamps: true
}, {
  strict: false
});

registrationAuthoritySchema.pre('save', setUrl);

function setUrl(next, done) {
  this['@id'] = common.constant.OTTO_BASE_URL + common.constant.OTTO_RA_URL + '/' + this._id;
  this['@context'] = common.constant.CONTEXT_SCHEMA_URL + common.constant.RA_CONTEXT;
  next();
}

//module.exports = mongoose.model('RegistrationAuthority', registrationAuthoritySchema);