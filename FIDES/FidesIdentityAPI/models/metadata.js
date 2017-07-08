"use strict";

const mongoose = require('mongoose');
const common = require('../helpers/common');

// define the schema for meta data
const metadataSchema = mongoose.Schema({
  '@id': {
    type: String
  },
  '@context': {
    type: String
  },
  metadataFormat: {
    type: String
  },
  expiration: {
    type: Date
  }
}, {
  timestamps: true
}, {
  strict: false
});

metadataSchema.pre('save', setUrl);

function setUrl(next, done) {
  this['@id'] = common.constant.OTTO_BASE_URL + common.constant.OTTO_METADATA_URL + '/' + this._id;
  this['@context'] = common.constant.CONTEXT_SCHEMA_URL + common.constant.METADATA_CONTEXT;
  next();
}

//module.exports = mongoose.model('Metadata', metadataSchema);