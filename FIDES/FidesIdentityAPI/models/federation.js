"use strict";

const mongoose = require('mongoose');
const common = require('../helpers/common');

// define the schema for our role
const federationSchema = mongoose.Schema({
  '@context': {
    type: String
  },
  '@id': {
    type: String
  },
  name: {
    type: String,
    required: true,
    unique: true
  },
  description: {
    type: String
  },
  url: {
    type: String
  },
  operates: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Entity'
  },
  registeredBy: {
    type: String
  },
  member: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Participant'
  }],
  federates: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Entity'
  }],
  sponsor: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Participant'
  }],
  technicalContact: [{
    type: mongoose.Schema.Types.Mixed
  }],
  executiveContact: [{
    type: mongoose.Schema.Types.Mixed
  }],
  securityContact: [{
    type: mongoose.Schema.Types.Mixed
  }],
  dataProtectionCodeOfConduct: {
    type: String
  },
  federationAgreement: {
    type: String
  },
  federationPolicy: {
    type: String
  },
  isActive: {
    type: Boolean,
    default: false
  }
}, {
  timestamps: true
}, {
  strict:false
});

federationSchema.pre('save', setUrl);

function setUrl(next, done) {
  this['@id'] = common.constant.OTTO_BASE_URL + common.constant.OTTO_FEDERATION_URL + '/' + this._id;
  this['@context'] = common.constant.CONTEXT_SCHEMA_URL + common.constant.FEDERATION_CONTEXT;
  next();
}

// create the model for federations and expose it to our app
module.exports = mongoose.model('Federation', federationSchema);
