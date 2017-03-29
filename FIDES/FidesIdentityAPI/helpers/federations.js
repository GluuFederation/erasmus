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
    .exec()
    .then((federations) => Promise.resolve(federations))
    .catch((err) => Promise.reject(err));
};

/**
 * Get federation by Id
 * @param {ObjectId} id - Federation id
 * @return {federation} - return federation
 * @return {err} - return error
 */
let getFederationById = (id) => {
  return Federation
    .findById(id)
    .exec()
    .then((federation) => Promise.resolve(federation))
    .catch((err) => Promise.reject(err));
};

/**
 * Get federation by name
 * @param {String} name - Federation name
 * @return {federations} - return federation
 * @return {err} - return error
 */
let getFederationByName = (name) => {
  return Federation
    .findOne({
      name: new RegExp('^' + name + '$', "i")
    })
    .exec()
    .then((federation) => Promise.resolve(federation))
    .catch((err) => Promise.reject(err));
};

/**
 * Add federation
 * @param {object} req - Request json object
 * @return {federation} - return federation
 * @return {err} - return error
 */
let addFederation = (req) => {
  let oFederation = new Federation();
  oFederation.name = req.name;
  oFederation.isActive = req.isActive || false;

  return oFederation.save()
    .then(federation => Promise.resolve(federation))
    .catch(err => Promise.reject(err));
};

/**
 * Update federation
 * @param {object} req - Request json object
 * @return {federation} - return federation
 * @return {err} - return error
 */
let updateFederation = (req) => {
  const id = req._id;
  return Federation
    .findById(id)
    .exec()
    .then((oFederation) => {
      oFederation.name = req.name || oFederation.name;
      oFederation.isActive = req.isActive || oFederation.isActive;
      return oFederation.save()
        .then(updatedFederation => Promise.resolve(updatedFederation))
        .catch(err => Promise.reject(err));
    })
    .catch(err => Promise.reject(err));
};

/**
 * Remove federation by Id
 * @param {ObjectId} id - Federation id
 * @return {federation} - return federation
 * @return {err} - return error
 */
let removeFederation = (id) => {
  return Federation
    .findById(id)
    .exec()
    .then((oFederation) => {
      return oFederation
        .remove()
        .then((remFed) => Promise.resolve(remFed))
        .catch(err => Promise.reject(err));
    })
    .catch(err => Promise.reject(err));
};

/**
 * Add Organization in Federation as Participant
 * @param {ObjectId} id - Federation id
 * @return {ObjectId} id - Organization id
 * @return {err} - return error
 */
let addParticipant = (fid, oid) => {
  return Federation
    .findById(fid)
    .exec()
    .then((oFederation) => {
      if (oFederation.participants.indexOf(oid) > -1) {
        return Promise.reject({error: 'Organization already exist'});
      }
      oFederation.participants.push(oid);
      return oFederation.save();
    })
    .then((oFederation) => Promise.resolve(oFederation))
    .catch(err => Promise.reject(err));
};

module.exports = {
  getAllFederations,
  getFederationById,
  getFederationByName,
  addFederation,
  updateFederation,
  removeFederation,
  addParticipant
};
