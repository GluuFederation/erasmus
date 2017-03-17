"use strict";

const mongoose = require('mongoose');

// define the schema for our role
const federationSchema = mongoose.Schema({
  name: {
    type: String,
    required: true,
    unique: true
  },
  ottoId: {
    type: mongoose.Schema.Types.ObjectId
  },
  ownerOrganizationOttoId: {
    type: mongoose.Schema.Types.ObjectId
  },
  isActive: {
    type: Boolean,
    default: false
  }
}, {
  timestamps: true
});

// create the model for federations and expose it to our app
module.exports = mongoose.model('Federation', federationSchema);
