"use strict";

const Role = require('../models/role');

/**
 * Callback function for all the export functions.
 * @callback requestCallback
 * @param {Error} error - Error information from base function.
 * @param {Object} [data] - Data from base function.
 * @param {Object} [info] - Message from base function if object not found.
 */

/**
 * Get all available roles
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let getAllRoles = (done) => {
    let query = Role.find({
        isActive: true
    });
    query.sort({
        order: 'asc'
    });

    query.exec((err, roles) => {
        if (err)
            done(err);
        else {
            if (roles.length) {
                done(null, roles);
            } else {
                done(null, null, {
                    message: 'No records found'
                });
            }
        }
    });
};

/**
 * Get role by name string
 * @param {string} name - name or role
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let getRoleByName = (name, done) => {
    let query = Role.findOne({
        name: name,
        isActive: true
    });

    query.exec((err, role) => {
        if (err)
            done(err);
        else {
            if (!role) {
                done(null, null, {
                    message: 'Role not found or not active'
                });
            } else {
                done(null, role);
            }
        }
    });
};

module.exports = {
    getAllRoles,
    getRoleByName
};
