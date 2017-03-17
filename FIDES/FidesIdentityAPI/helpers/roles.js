"use strict";

const Role = require('../models/role');

/**
 * Get all available roles
 * @return {Roles} - return all Roles
 * @return {err} - return error
 */
let getAllRoles = () => {
  return Role
    .find({isActive: true})
    .sort({order: 1})
    .exec()
    .then((roles) => {
      return Promise.resolve(roles);
    })
    .catch(err => Promise.reject(err));
};

/**
 * Get role by name string
 * @param {string} name - name or role
 * @return {Role} - return all Role
 * @return {err} - return error
 */
let getRoleByName = (name) => {
  return Role
    .findOne({name: name, isActive: true})
    .sort({order: 1})
    .exec()
    .then(role => Promise.resolve(role))
    .catch(err => Promise.reject(err));
};

module.exports = {
  getAllRoles,
  getRoleByName
};
