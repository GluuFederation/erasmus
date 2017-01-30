"use strict";

const Federation = require('../models/federation');

/**
 * Callback function for all the export functions.
 * @callback requestCallback
 * @param {Error} error - Error information from base function.
 * @param {Object} [data] - Data from base function.
 * @param {Object} [info] - Message from base function if object not found.
 */

/**
 * Get all active federations
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let getAllFederations = (done) => {
    return Federation
        .find({})
        .sort({name: 1})
        .exec((err, federations) => {
            if (err)
                return done(err);
            else {
                if (federations.length) {
                    return done(null, federations);
                }
                return done(null, null, {message: 'No records found'});
            }
        });
};

/**
 * Get federation by Id
 * @param {ObjectId} id - Federation id
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let getFederationById = (id, done) => {
    return Federation
        .findById(id)
        .exec((err, federation) => {
            if (err)
                return done(err);
            else {
                if (!federation) {
                    return done(null, null, {message: 'Federation not found'});
                }
                return done(null, federation);
            }
        });
};

let getFederationByName = (orgName, done) => {
    return Federation
        .findOne({
            name: new RegExp('^' + orgName + '$', "i")
        })
        .exec((err, federation) => {
            if (err)
                return done(err);
            else {
                if (!federation) {
                    return done(null, null, {
                        message: 'Federation not found'
                    });
                } else {
                    return done(null, federation);
                }
            }
        });
};

/**
 * Add federation
 * @param {object} req - Request json object
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let addFederation = (req, done) => {
    return Federation
        .findOne({name: req.name})
        .exec((err, federation) => {
            if (err)
                return done(err);

            // check if already exists or not
            if (federation != null) {
                return done(null, false, {
                    'message': 'Federation is already exists and may not be approved yet.'
                });
            } else {
                let objFederation = new Federation();

                objFederation.name = req.name;
                objFederation.isActive = req.isActive || false;

                objFederation.save(err => {
                    if (err)
                        return done(err);

                    return done(null, objFederation);
                });
            }
        });
};

/**
 * Update federation
 * @param {object} req - Request json object
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let updateFederation = (req, done) => {
    const id = req._id;
    return Federation
        .findById(id)
        .exec((err, oFederation) => {
            if (err)
                return done(err);

            // check to see if there is already exists or not.
            if (!oFederation) {
                return done(null, false, {
                    'message': 'Federation not found.'
                });
            }

            if (req.name) {
                oFederation.name = req.name;
            }
            if (req.isActive) {
                oFederation.isActive = req.isActive;
            }

            oFederation.save(err => {
                if (err) {
                    return done(err);
                }

                return done(null, oFederation);
            });
        });
};

/**
 * Remove federation by Id
 * @param {ObjectId} id - Federation id
 * @param {requestCallback} [done] - Callback function that returns error, object or info
 */
let removeFederation = (id, done) => {
    return Federation
        .findById(id)
        .exec((err, objFederation) => {
            if (err)
                return done(err);
            else {
                if (!objFederation) {
                    return done(null, null, {
                        message: 'Federation not found'
                    });
                } else {
                    objFederation.remove(err => {
                        if (err)
                            return done(err);

                        return done(null, objFederation);
                    });
                }
            }
        });
};

module.exports = {
    getAllFederations,
    getFederationById,
    getFederationByName,
    addFederation,
    updateFederation,
    removeFederation
};
