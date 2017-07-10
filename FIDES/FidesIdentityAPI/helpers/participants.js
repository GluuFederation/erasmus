"use strict";
const mongoose = require('mongoose');
const common = require('./common');
common.ottoConfig.isServerStart = false;
const Participant = require('otto-node-package')(common.ottoConfig).model.participantModel;
const Federation = require('otto-node-package')(common.ottoConfig).model.federationModel;

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
      var obj = {
        name: req.name || oParticipant.name,
        isApproved: req.isApproved || oParticipant.isApproved,
        phoneNo: req.phoneNo || oParticipant.phoneNo,
        address: req.address || oParticipant.address,
        zipcode: req.zipcode || oParticipant.zipcode,
        state: req.state || oParticipant.state,
        city: req.city || oParticipant.city,
        type: req.type || oParticipant.type,
        description: req.description || oParticipant.description,
        trustMarkFile: req.trustMarkFile || oParticipant.trustMarkFile
      };
      return Participant.findOneAndUpdate({_id: oParticipant._id}, obj);
    })
    .then((updateParticipant) => Participant.findById(updateParticipant._id).populate('memberOf'))
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
      var obj = {
        isApproved: true,
        memberOf: oParticipant.memberOf
      };
      return Participant.findOneAndUpdate({_id: oParticipant._id}, obj);
      //return oParticipant.save();
    })
    .then((updateParticipant) => {
      return Participant.findById(updateParticipant._id).populate('memberOf');
    })
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
        return Promise.reject({error: 'Federation already exist'});
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
    .aggregate([
      {
        $lookup: {
          from: "badges",
          localField: "pendingBadges",
          foreignField: "_id",
          as: "pendingBadges"
        }
      },
      {
        $lookup: {
          from: "badges",
          localField: "approvedBadges",
          foreignField: "_id",
          as: "approvedBadges"
        }
      },
      {
        $match: {
          _id: mongoose.Types.ObjectId(oid)
        }
      }
    ])
    .then(oParticipant => {
      oParticipant = oParticipant[0];
      return Promise.resolve(oParticipant)
    })
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
