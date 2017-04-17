"use strict";

const User = require('../models/user'),
  Roles = require('../helpers/roles');

/**
 * Authenticate user for login.
 * @param {object} req - Request json object
 * @return {user} - return user
 * @return {err} - return error
 */
let authenticateUser = (req) => {
  if (req.username)
    req.username = req.username.toLowerCase();

  return User
    .findOne({
      $or: [{username: req.username}, {email: req.username}]
    })
    .exec()
    .then((user) => {
      if (!user.validPassword(req.password)) {
        return Promise.reject(false);
      }
      return Promise.resolve(user);
    })
    .catch(err => Promise.reject(err));
};

/**
 * Add user. (TODO: Add detail to SCIM)
 * @param {object} req - Request json object
 * @return {user} - return user
 * @return {err} - return error
 */
let createUser = (req) => {
  if (req.username)
    req.username = req.username.toLowerCase();

  if (req.email)
    req.email = req.email.toLowerCase();

  return User
    .findOne({
      $or: [{username: req.username}, {email: req.email}]
    })
    .exec()
    .then((user) => {
      // check to see if there is already a user with that username/email
      if (!!user) {
        return Promise.reject(false);
      }
      // create the user
      let newUser = new User();

      newUser.username = req.username;
      newUser.email = req.email;
      newUser.firstName = req.firstName;
      newUser.lastName = req.lastName;
      newUser.role = req.roleId;
      newUser.participant = req.participant;
      newUser.entity = req.entity;
      newUser.isActive = req.isActive || false;
      return newUser.save();
    })
    .then((newUser) => {
      return User.populate(newUser, 'role participant');
    })
    .then(newUser => {
      return Promise.resolve(newUser);
    })
    .catch(err => Promise.reject(err));
};

/**
 * Update user detail. (TODO: update detail to SCIM)
 * @param {object} req - Request json object
 * @return {user} - return user
 * @return {err} - return error
 */
let updateUser = (req) => {
  if (req.username)
    req.username = req.username.toLowerCase();
  if (req.email)
    req.email = req.email.toLowerCase();

  return User
    .findOne({
      $or: [{'username': req.username}, {'email': req.email}]
    })
    .exec()
    .then((user) => {
      // check to see if there is already exists or not.
      if (!user) {
        return Promise.reject(false);
      }
      user.firstName = req.firstName;
      user.lastName = req.lastName;
      user.role = req.roleId;
      user.participant = req.participant || user.participant;
      user.isActive = req.isActive;
      user.phoneNo = req.phoneNo || user.phoneNo;
      user.address = req.address || user.address;
      user.zipcode = req.zipcode || user.zipcode;
      user.state = req.state || user.state;
      user.city = req.city || user.city;
      user.description = req.description || user.description;

      return user.save();
    })
    .then((newUser) => {
      return User.populate(newUser, 'role participant entity');
    })
    .then(newUser => {
      return Promise.resolve(newUser);
    })
    .catch(err => Promise.reject(err));
};

/**
 * Update user password. (TODO: update password in SCIM)
 * @param {object} req - Request json object
 * @return {user} - return user
 * @return {err} - return error
 */
let updatePassword = (req) => {
  return User
    .findById(req.id)
    .exec()
    .then(function (user) {
      // check to see if there is already exists or not.
      if (!user) {
        return Promise.resolve(false);
      }
      if (!user.validPassword(req.currentPassword)) {
        return Promise.reject(false);
      }
      user.password = user.generateHash(req.newPassword);
      return user.save();
    })
    .then((user) => Promise.resolve(user))
    .catch(err => Promise.reject(err));
};

/**
 * Update SCIM ID of user after adding user to SCIM.
 * @param {string} userId - Id of user
 * @param {String} scimId - SCIM ID of user, returned after adding user to server using SCIM 2.0.
 * @return {user} - return user
 * @return {err} - return error
 */
let updateScimId = (userId, scimId) => {
  return User
    .findById(userId)
    .exec((user) => {
      // check to see if there is already exists or not.
      if (!user) {
        return Promise.reject(false);
      }
      user.scimId = scimId;
      return user.save();
    })
    .then((user) => Promise.resolve(user))
    .catch(err => Promise.reject(err));
};

/**
 * Remove user. (TODO: update detail to SCIM)
 * @param {string} id - id of user
 * @return {user} - return user
 * @return {err} - return error
 */
let removeUser = (id) => {
  return User
    .findById(id)
    .exec()
    .then((user) => {
      // check to see if there is already exists or not.
      if (!user) {
        return Promise.reject(false);
      }
      return user.remove();
    })
    .then(user => Promise.resolve(user))
    .catch(err => Promise.reject(err));
};

/**
 * Get user detail.
 * @param {object} req - Request json object
 * @return {user} - return user
 * @return {err} - return error
 */
let getUser = (req) => {
  if (req.username)
    req.username = req.username.toLowerCase();
  if (req.email)
    req.email = req.email.toLowerCase();

  return User
    .findOne({
      $or: [{username: req.username}, {email: req.email}, {_id: req.id}]
    })
    .exec()
    .then((user) => {
      // check to see if there is already exists or not.
      if (!user) {
        return Promise.reject(false);
      }
      return Promise.resolve(user);
    })
    .catch(err => err);
};

/**
 * Get list of all the users.
 * @return {users} - return users
 * @return {err} - return error
 */
let getAllUsers = () => {
  return Roles.getRoleByName('admin')
    .then((role) => {
      return User
        .find({role: {$ne: role._id}})
        .exec();
    })
    .then(users => Promise.resolve(users))
    .catch(err => Promise.reject(err));
};

/**
 * Get list of all the participants.
 * @return {users} - return users
 * @return {err} - return error
 */
let getAllParticipants = () => {
  return Roles.getRoleByName('admin')
    .then((role) => {
      return User.find({role: {$ne: role._id}}, 'participant entity')
        .exec();
    })
    .then((users) => {
      if (users.length) {
        let participants = [];
        users.forEach(function (user) {
          user.participant._doc.entity = user.entity.discoveryUrl;
          participants.push(user.participant);
        });
        return Promise.resolve(participants);
      }
      return Promise.reject(false);
    })
    .catch(err => Promise.reject(err));
};

/**
 * Retrieves all entitys.
 * @param {ObjectId} userId - Org admin user id.
 * @return {users} - return users
 * @return {err} - return error
 */
let getAllEntities = (userId) => {
  return Roles.getRoleByName('admin')
    .then((role) => {
      let queryCondition = {};

      queryCondition['role'] = {$ne: role._id};
      if (userId && userId !== 'undefined') {
        queryCondition['_id'] = userId;
      }

      return User.find(queryCondition)
        .exec();
    })
    .then((users) => {
      if (users.length) {
        let entitys = [];
        users.forEach(function (user) {
          let entity = user.entity;
          entity.participant = user.participant;
          entitys.push(entity);
        });
        return Promise.resolve(entitys);
      }
      return Promise.reject(null);
    })
    .catch(err => Promise.reject(err));
};

module.exports = {
  createUser,
  authenticateUser,
  getUser,
  getAllUsers,
  getAllParticipants,
  getAllEntities,
  updateUser,
  updatePassword,
  updateScimId,
  removeUser
};
