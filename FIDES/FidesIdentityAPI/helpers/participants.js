"use strict";

const Participant = require('../models/participant');
const Entity = require('../models/entity');
const Federation = require('../models/federation');

/**
 * Get all active participants
 * @return {participants} - return all participants
 * @return {err} - return error
 */
let getAllParticipants = () => {
  return Participant
    .find({})
    .sort({name: 1})
    .exec()
    .then((participants) => Promise.resolve(participants))
    .catch((err) => Promise.reject(err));
};

/**
 * Get participant by Id
 * @param {ObjectId} id - Participant id
 * @return {participant} - return participant
 * @return {err} - return error
 */
let getParticipantById = (id) => {
  return Participant
    .findById(id)
    .exec()
    .then((participant) => Promise.resolve(participant))
    .catch((err) => Promise.reject(err));
};

/**
 * Get participant by name
 * @param {String} name - Participant name
 * @return {participants} - return participant
 * @return {err} - return error
 */
let getParticipantByName = (name) => {
  return Participant
    .findOne({
      name: new RegExp('^' + name + '$', "i")
    })
    .exec()
    .then((participant) => Promise.resolve(participant))
    .catch((err) => Promise.reject(err));
};

/**
 * Add participant
 * @param {object} req - Request json object
 * @return {participant} - return participant
 * @return {err} - return error
 */
let addParticipant = (req) => {
  let oParticipant = new Participant({
    isApproved: req.isApproved || false,
    name: req.name,
    phoneNo: req.phoneNo,
    address: req.address,
    zipcode: req.zipcode,
    state: req.state,
    city: req.city,
    type: req.type,
    description: req.description
  });

  return oParticipant.save()
    .then(participant => Promise.resolve(participant))
    .catch(err => Promise.reject(err));
};

/**
 * Update participant
 * @param {object} req - Request json object
 * @return {participant} - return participant
 * @return {err} - return error
 */
let updateParticipant = (req) => {
  const id = req._id;
  return Participant
    .findById(id)
    .exec()
    .then((oParticipant) => {
      oParticipant.name = req.name || oParticipant.name;
      oParticipant.isApproved = req.isApproved || oParticipant.isApproved;
      oParticipant.phoneNo = req.phoneNo || oParticipant.phoneNo;
      oParticipant.address = req.address || oParticipant.address;
      oParticipant.zipcode = req.zipcode || oParticipant.zipcode;
      oParticipant.state = req.state || oParticipant.state;
      oParticipant.city = req.city || oParticipant.city;
      oParticipant.type = req.type || oParticipant.type;
      oParticipant.description = req.description || oParticipant.description;
      oParticipant.trustMarkFile = req.trustMarkFile || oParticipant.trustMarkFile;

      return oParticipant.save();
    })
    .then((updateParticipant) => Participant.populate(updateParticipant, 'federationId'))
    .then((oParticipant) => Promise.resolve(oParticipant))
    .catch(err => Promise.reject(err));
};

/**
 * Remove participant by Id
 * @param {ObjectId} id - Participant id
 * @return {participant} - return participant
 * @return {err} - return error
 */
let removeParticipant = (id) => {
  return Participant
    .findById(id)
    .exec()
    .then((oParticipant) => {
      return oParticipant
        .remove()
        .then((remOrg) => Promise.resolve(remOrg))
        .catch(err => Promise.reject(err));
    })
    .catch(err => Promise.reject(err));
};

/**
 * Approve participant by Id
 * @param {ObjectId} orgId - Participant id
 * @param {ObjectId} ottoId - Participant otto id
 * @param {ObjectId} fedId - Federation id
 * @return {participant} - return participant
 * @return {err} - return error
 */
let approveParticipant = (orgId, fedId) => {
  return Participant
    .findById(orgId)
    .exec()
    .then((oParticipant) => {
      oParticipant.memberOf.push(fedId);
      oParticipant.isApproved = true;
      return oParticipant.save();
    })
    .then((updateParticipant) => Participant.populate(updateParticipant, 'memberOf'))
    .then((oParticipant) => Promise.resolve(oParticipant))
    .catch(err => Promise.reject(err));
};

/**
 * Join participant and Entity
 * @param {ObjectId} oid - Participant id
 * @param {ObjectId} pid - Entity id
 * @return {Object} - return Entity
 * @return {err} - return error
 */
let addEntityInPartcipant = (pid, eid) => {
  return Participant
    .findById(pid)
    .exec()
    .then((oParticipant) => {
      oParticipant.operates = eid;
      return oParticipant.save();
    })
    .then((oParticipant) => Promise.resolve(oParticipant))
    .catch(err => Promise.reject(err));
};

/**
 * Set owner participant for federation
 * @param {ObjectId} oid - Participant id
 * @param {ObjectId} fid - Federation id
 * @return {Object} - return Federation
 * @return {err} - return error
 */
let setOwnerParticipant = (oid, fid) => {
  return Participant
    .findById(oid)
    .exec()
    .then((oParticipant) => {
      if (oParticipant.federations.indexOf(fid) > -1) {
        return Promise.reject({ error: 'Federation already exist'});
      }
      oParticipant.federations.push(fid);
      return oParticipant.save();
    })
    .then((oParticipant) => {
      return Federation.findById(fid).exec()
    })
    .then((oFederation) => {
      oFederation.participant = oid;
      return oFederation.save();
    })
    .then((oFederation) => Promise.resolve(oFederation))
    .catch(err => Promise.reject(err));
};

/**
 * Set owner participant for federation
 * @param {ObjectId} oid - Participant id
 * @param {ObjectId} fid - Federation id
 * @return {Object} - return Federation
 * @return {err} - return error
 */
let getBadgeByParticipant = (oid) => {
  return Participant
    .findById(oid)
    .exec()
    .then(oParticipant => Participant.populate(oParticipant, 'approvedBadges pendingBadges'))
    .then(oParticipant => Promise.resolve(oParticipant))
    .catch(err => Promise.reject(err));
};

module.exports = {
  getAllParticipants,
  getParticipantById,
  getParticipantByName,
  addParticipant,
  updateParticipant,
  removeParticipant,
  approveParticipant,
  addEntityInPartcipant,
  setOwnerParticipant,
  getBadgeByParticipant
};
