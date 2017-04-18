"use strict";

const mongoose = require('mongoose'),
  bcrypt = require('bcrypt-nodejs');

var Role = require('./role');

// define the schema for user
const userSchema = mongoose.Schema({
  username: {
    type: String,
    required: true,
    unique: true,
    lowercase: true
  },
  email: {
    type: String,
    required: true,
    unique: true,
    lowercase: true
  },
  password: {
    type: String,
    required: false
  },
  firstName: {
    type: String,
    required: false
  },
  lastName: {
    type: String,
    required: false
  },
  isActive: {
    type: Boolean,
    default: false
  },
  scimId: {
    type: String,
    required: false
  },
  participant: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Participant'
  },
  role: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Role'
  },
  entity: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Entity'
  },
  phoneNo: {
    type: String,
    required: false
  },
  address: {
    type: String,
    required: false
  },
  zipcode: {
    type: String,
    required: false
  },
  state: {
    type: String,
    required: false
  },
  city: {
    type: String,
    required: false
  },
  description: {
    type: String,
    required: false
  }
}, {
  timestamps: true
});

// generating a hash
userSchema.methods.generateHash = function (password) {
  return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};

// checking if password is valid
userSchema.methods.validPassword = function (password) {
  return bcrypt.compareSync(password, this.password);
};

// making the model safe
userSchema.methods.safeModel = function () {
  return {
    _id: this._id,
    username: this.username,
    email: this.email,
    firstName: this.firstName,
    lastName: this.lastName,
    isActive: this.isActive,
    createdOn: this.createdOn,
    scimId: this.scimId,
    participant: this.participant,
    role: this.role,
    entity: this.entity,
    phoneNo: this.phoneNo,
    address: this.address,
    zipcode: this.zipcode,
    state: this.state,
    city: this.city,
    description: this.description
  }
};

//populate
userSchema.pre('findOne', populateMasters);
userSchema.pre('findById', populateMasters);
userSchema.pre('find', populateMasters);

function populateMasters() {
  this.populate('role participant entity');
  return this;
}
// create the model for users and expose it to our app
module.exports = mongoose.model('User', userSchema);
