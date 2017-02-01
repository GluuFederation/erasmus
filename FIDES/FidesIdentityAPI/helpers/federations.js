"use strict";

const Federation = require('../models/federation');

/**
 * Get all active federations
 * @return {federations} - return all federations
 * @return {err} - return error
 */
let getAllFederations = () => {
    return Federation
        .find({})
        .sort({name: 1})
        .exec((federations) => federations)
        .catch((err) => err);
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
 * @return {}
 */
let addFederation = (req) => {
    let oFederation = new Federation();
    oFederation.name = req.name;
    oFederation.isActive = req.isActive || false;

    return oFederation.save()
        .then(federation => federation)
        .catch(err => err);
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
