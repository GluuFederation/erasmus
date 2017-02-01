"use strict";

const Organization = require('../models/organization');

/**
 * Callback function for all the export functions.
 * @callback requestCallback
 * @param {Error} error - Error information from base function.
 * @param {Object} [data] - Data from base function.
 * @param {Object} [info] - Message from base function if object not found.
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
 * @param {ObjectId} orgId - Organization id
 * @param {requestCallback} done - Callback function that returns error, object or info
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

let getOrganizationByName = (orgName, done) => {
    let query = Organization.findOne({
        name: new RegExp('^' + orgName + '$', "i")
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
 * @param {requestCallback} done - Callback function that returns error, object or info
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
 * Update organization
 * @param {object} req - Request json object
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let updateOrganization = (req, done) => {
    process.nextTick(() => {
        Organization.findOne({
            '_id': req._id
        }).exec(function (err, objOrg) {
            if (err)
                return done(err);

            // check to see if there is already exists or not.
            if (!objOrg) {
                return done(null, false, {
                    'message': 'Organization not found.'
                });
            } else {
                if (req.name) {
                    objOrg.name = req.name;
                }
                if (req.ottoId) {
                    objOrg.ottoId = req.ottoId;
                }
                if (req.isApproved) {
                    objOrg.isApproved = req.isApproved;
                }

                objOrg.save(err => {
                    if (err) {
                        return done(err);
                    }

                    return done(null, objOrg);
                });
            }
        });
    });
};

/**
 * Remove organization by Id
 * @param {ObjectId} orgId - Organization id
 * @param {requestCallback} [done] - Callback function that returns error, object or info
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

/**
 * Approve organization
 * @param {ObjectId} orgId - Organization id
 * @param {ObjectId} ottoId - ObjectId of OTTO system returned after adding it to OTTO
 * @param {requestCallback} done - Callback function that returns error, object or info
 * @returns {Object} info - Object with information message.
 */
let approveOrganization = (orgId, ottoId, fedId, done) => {
    if(!ottoId){
        return done(null, false, {
            'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
        });
    }

    process.nextTick(() => {
        Organization.findOne({
            '_id': orgId
        }).exec(function (err, objOrganization) {
            if (err)
                return done(err);

            // check if already exists or not.
            if (!objOrganization) {
                return done(null, false, {
                    'message': 'Organization not found.'
                });
            } else {
                objOrganization.ottoId = ottoId;
                objOrganization.federationId = fedId;
                objOrganization.isApproved = true;
                objOrganization.save(err => {
                    if (err)
                        return done(err);

                    return done(null, objOrganization);
                });
            }
        });
    });
};

module.exports = {
    getAllOrganizations,
    getOrganizationById,
    getOrganizationByName,
    addOrganization,
    updateOrganization,
    removeOrganization,
    approveOrganization
};
