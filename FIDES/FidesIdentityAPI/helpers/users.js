"use strict";
const mongoose = require('mongoose');
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
      $or: [{username: req.username}, {email: req.email}]
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
      return User
        .aggregate([
          {
            $match: {$or: [{username: req.username}, {email: req.email}]}
          }, {
            $lookup: {
              from: "participants",
              localField: "participant",
              foreignField: "_id",
              as: "participant"
            }
          }, {
            $lookup: {
              from: "entities",
              localField: "entity",
              foreignField: "_id",
              as: "entity"
            }
          }, {
            $lookup: {
              from: "roles",
              localField: "role",
              foreignField: "_id",
              as: "role"
            }
          }
        ]);
    })
    .then((user) => {
      user[0].participant = user[0].participant[0];
      user[0].entity = user[0].entity[0];
      user[0].role = user[0].role[0];
      user = user[0];
      return Promise.resolve(user);
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
let getUserByIdentity = (req) => {
  if (req.username)
    req.username = req.username.toLowerCase();
  if (req.email)
    req.email = req.email.toLowerCase();

  return User
    .aggregate([
      {
        $lookup: {
          from: "participants",
          localField: "participant",
          foreignField: "_id",
          as: "participant"
        }
      },
      {
        $unwind: {
          path: "$participant",
          preserveNullAndEmptyArrays: true
        }
      },
      {
        $lookup: {
          from: "federations",
          localField: "participant.memberOf",
          foreignField: "_id",
          as: "participant.memberOf"
        }
      },
      {
        $lookup: {
          from: "entities",
          localField: "entity",
          foreignField: "_id",
          as: "entity"
        }
      },
      {
        $lookup: {
          from: "roles",
          localField: "role",
          foreignField: "_id",
          as: "role"
        }
      },
      {
        $match: {$or: [{'username': req.username}, {'email': req.email}]}
      }
    ])
    .exec()
    .then((user) => {
      // check to see if there is already exists or not.
      if (user.length == 0) {
        return Promise.reject(false);
      }
      user[0].participant = user[0].participant;
      user[0].entity = user[0].entity[0];
      user[0].role = user[0].role[0];
      user = user[0];
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
        .aggregate([
          {
            $match: {
              role: {
                $ne: role._id
              }
            }
          },
          {
            $lookup: {
              from: "participants",
              localField: "participant",
              foreignField: "_id",
              as: "participant"
            }
          },
          {
            $lookup: {
              from: "roles",
              localField: "role",
              foreignField: "_id",
              as: "role"
            }
          }
        ]);
    })
    .then(users => Promise.resolve(users))
    .catch(err => Promise.reject(err));
};

/**
 * Get list of all the participants.
 * @return {users} - return users
 * @return {err} - return error
 */
let getAllParticipants = (params) => {
  return Roles.getRoleByName('admin')
    .then((role) => {
      return User
        .aggregate([
          {
            $match: {role: {$ne: role._id}}
          },
          {
            $lookup: {
              from: "participants",
              localField: "participant",
              foreignField: "_id",
              as: "participant"
            }
          },
          {
            $unwind: {
              path: "$participant",
              preserveNullAndEmptyArrays: true
            }
          },
          {
            $lookup: {
              from: "federations",
              localField: "participant.memberOf",
              foreignField: "_id",
              as: "participant.memberOf"
            }
          },
          {
            $lookup: {
              from: "entities",
              localField: "entity",
              foreignField: "_id",
              as: "entity"
            }
          },
          {
            $lookup: {
              from: "roles",
              localField: "role",
              foreignField: "_id",
              as: "role"
            }
          }
        ]);
    })
    .then((users) => {
      if (users.length) {
        let participants = [];
        users.forEach(function (user) {
          if (!!params.state && !!params.city && params.state != 'all' && params.city != 'all') {
            if (user.participant.state == params.state && user.participant.city == params.city) {
              user.participant.discoveryUrl = user.entity[0].discoveryUrl;
              participants.push(user.participant);
            }
            return;
          }

          user.participant.discoveryUrl = user.entity[0].discoveryUrl;
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
      let query = {role: {$ne: role._id}};
      if (!!userId && userId !== 'undefined') {
        query._id = mongoose.Types.ObjectId(userId);
      }

      return User
        .aggregate([
          {
            $match: query
          }, {
            $lookup: {
              from: "participants",
              localField: "participant",
              foreignField: "_id",
              as: "participant"
            }
          }, {
            $lookup: {
              from: "entities",
              localField: "entity",
              foreignField: "_id",
              as: "entity"
            }
          }, {
            $lookup: {
              from: "roles",
              localField: "role",
              foreignField: "_id",
              as: "role"
            }
          }
        ]);
    })
    .then((users) => {
      if (users.length) {
        let entities = [];
        users.forEach(function (user) {
          let entity = user.entity[0];
          entity.participant = user.participant[0];
          entities.push(entity);
        });
        return Promise.resolve(entities);
      }
      return Promise.reject(null);
    })
    .catch(err => Promise.reject(err));
};

module.exports = {
  createUser,
  authenticateUser,
  getUserByIdentity,
  getAllUsers,
  getAllParticipants,
  getAllEntities,
  updateUser,
  updatePassword,
  updateScimId,
  removeUser
};
