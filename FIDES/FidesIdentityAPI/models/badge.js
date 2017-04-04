"use strict";

const mongoose = require('mongoose');
const common = require('../helpers/common');

// define the schema for our role
const badgeSchema = mongoose.Schema({
  name: {
    type: String,
    required: true,
    unique: true
  },
  description: {
    type: String
  },
  type: {
    type: String,
    default: 'BadgeClass'
  },
  category: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'BadgeCategory'
  },
  image: {
    type: String
  },
  narrative: {
    type: String
  },
  isActive: {
    type: Boolean,
    default: false
  }
}, {
  timestamps: true
});

badgeSchema
  .pre('findOne', populateCategory)
  .pre('findById', populateCategory)
  .pre('find', populateCategory);

function populateCategory() {
  this.populate('category');
  return this;
}

// create the model for badge and expose it to our app
module.exports = mongoose.model('Badge', badgeSchema);
