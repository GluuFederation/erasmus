"use strict";

const common = require('./common');
common.ottoConfig.isServerStart = false;
const Federation = require('otto-node-package')(common.ottoConfig).model.federationModel;

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
  let oFederation = new Federation({
    name: req.name,
    isActive: req.isActive || false,
  });

  oFederation.sponsor.push(req.sponsor || null);

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
      var obj = {
        name: req.name || oFederation.name,
        isActive: req.isActive || oFederation.isActive
      };

      return Federation.findOneAndUpdate({_id: oFederation._id}, obj)
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
 * Add Participant in Federation as Participant
 * @param {ObjectId} id - Federation id
 * @return {ObjectId} id - Participant id
 * @return {err} - return error
 */
let addParticipant = (fid, oid) => {
  return Federation
    .findById(fid)
    .exec()
    .then((oFederation) => {
      if (oFederation.member.indexOf(oid) > -1) {
        return Promise.reject({error: 'Participant already exist'});
      }
      oFederation.member.push(oid);
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
