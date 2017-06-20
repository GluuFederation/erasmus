"use strict";

const mongoose = require('mongoose');
const common = require('../helpers/common');

// define the schema for our role
const badgeCategorySchema = mongoose.Schema({
  name: {
    type: String,
    required: true,
    unique: true
  },
  description: {
    type: String
  }
}, {
  timestamps: true
});

// create the model for badgeCategory and expose it to our app
module.exports = mongoose.model('BadgeCategory', badgeCategorySchema);
