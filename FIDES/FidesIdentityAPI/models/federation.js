"use strict";

const mongoose = require('mongoose');
const common = require('../helpers/common');

// define the schema for our role
const federationSchema = mongoose.Schema({
  name: {
    type: String,
    required: true,
    unique: true
  },
  isActive: {
    type: Boolean,
    default: false
  },
  keys: [{
    privatekey: String,
    publickey: String,
    keyguid: String,
    alg: String
  }],
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
  organization: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Organization'
  },
  participants: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Organization'
  }]
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
