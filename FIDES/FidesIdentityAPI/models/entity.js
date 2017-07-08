"use strict";

const mongoose = require('mongoose'),
  bcrypt = require('bcrypt-nodejs');

const common = require('../helpers/common');

// define the schema for openid connect entity
const entitySchema = mongoose.Schema({
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
  registeredBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'RegistrationAuthority'
  },
  federatedBy: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Federation'
  }],
  metadata: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Metadata'
  },
  discoveryUrl: {
    type: String
    //unique: true
  },
  oxdId: {
    type: String,
    required: false
  },
  redirectUris: {
    type: String,
    required: false
  },
  responseTypes: {
    type: String,
    required: false
  },
  participant: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Participant'
  },
  isApproved: {
    type: Boolean,
    required: false
  },
  type : {
    type: String
  },
  signedJwksUri: {
    type: String
  },
  signingKeys: {
    type: String
  }
}, {
  timestamps: true
}, {
  strict:false
});

entitySchema.pre('findOne', populateParticipant);
entitySchema.pre('findById', populateParticipant);
entitySchema.pre('find', populateParticipant);
entitySchema.pre('save', setUrl);

function populateParticipant() {
  return this.populate('participant');
}

function setUrl(next, done) {
  this['@id'] = common.constant.OTTO_BASE_URL + common.constant.OTTO_ENTITY_URL + '/' + this._id;
  this['@context'] = common.constant.CONTEXT_SCHEMA_URL + common.constant.ENTITY_CONTEXT;
  this.registeredBy = common.constant.RA_ID;
  this.metadata = common.constant.METADATA_ID;
  next();
}

// create the model for openid connect entity and expose it to our app
//module.exports = mongoose.model('Entity', entitySchema);
