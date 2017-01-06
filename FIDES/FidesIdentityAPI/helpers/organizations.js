"use strict";

// load up the organization model
const Organization = require('../models/organization');

/**
 * Callback function for all the export functions.
 * @callback requestCallback
 * @param {Error} error - Error information from base function.
 * @param {Object} [data] - Data from base function.
 * @param {string} [info] - Message from base function if object not found.
 */

/**
 * Get all active organizations
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let getAllOrganizations = (done) => {
    let query = Organization.find({
        //isActive: true
    });
    query.sort({
        name: 'asc'
    });

    query.exec((err, organizations) => {
        if (err)
            return done(err);
        else {
            if (organizations.length) {
                return done(null, organizations);
            } else {
                return done(null, null, {
                    message: 'No records found'
                });
            }
        }
    });
};

/**
 * Get organization by Id
 * @param {uuid} orgId - Organization id
 * @param done - Callback function that returns error, object or info
 */
let getOrganizationById = (orgId, done) => {
    let query = Organization.findOne({
        _id: orgId
    });

    query.exec((err, organization) => {
        if (err)
            return done(err);
        else {
            if (!organization) {
                return done(null, null, {
                    message: 'Organization not found'
                });
            } else {
                return done(null, organization);
            }
        }
    });
};

/**
 * Add organization
 * @param {object} req - Request json object
 * @param done - Callback function that returns error, object or info
 */
let addOrganization = (req, done) => {
    process.nextTick(() => {
        Organization.findOne({
            name: req.name
        }, (err, organization) => {
            if (err)
                return done(err);

            // check if already exists or not
            if (!!organization) {
                return done(null, false, {
                    'message': 'Organization is already exists and may not be approved yet.'
                });
            } else {
                let objOrganization = new Organization();

                objOrganization.name = req.name;
                objOrganization.isApproved = req.isApproved || false;

                objOrganization.save(err => {
                    if (err)
                        return done(err);

                    return done(null, objOrganization);
                });
            }
        });
    });
};

/**
 * Remove organization by Id
 * @param {uuid} orgId - Organization id
 * @param [done] - Callback function that returns error, object or info
 */
let removeOrganization = (orgId, done) => {
    let query = Organization.findOne({
        _id: orgId
    });

    query.exec((err, objOrganization) => {
        if (err)
            return done(err);
        else {
            if (!objOrganization) {
                return done(null, null, {
                    message: 'Organization not found'
                });
            } else {
                objOrganization.remove(err => {
                    if (err)
                        return done(err);

                    return done(null, objOrganization);
                });
            }
        }
    });
};

module.exports = {
    getAllOrganizations,
    getOrganizationById,
    addOrganization,
    removeOrganization
};
