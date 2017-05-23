"use strict";

const Badge = require('../models/badge');
const Participant = require('../models/participant');
const Entity = require('../models/entity');
const User = require('../models/user');

/**
 * Get all active badges
 * @return {badges} - return all badges
 * @return {err} - return error
 */
let getAllBadges = () => {
  return Badge
    .find({})
    .sort({name: 1})
    .exec()
    .then((badges) => Promise.resolve(badges))
    .catch((err) => Promise.reject(err));
};

/**
 * Get badge by Id
 * @param {ObjectId} id - Badge id
 * @return {badge} - return badge
 * @return {err} - return error
 */
let getBadgeById = (id) => {
  return Badge
    .findById(id)
    .exec()
    .then((badge) => Promise.resolve(badge))
    .catch((err) => Promise.reject(err));
};

/**
 * Get badge by name
 * @param {String} name - Badge name
 * @return {badges} - return badge
 * @return {err} - return error
 */
let getBadgeByName = (name) => {
  return Badge
    .findOne({
      name: new RegExp('^' + name + '$', "i")
    })
    .exec()
    .then((badge) => Promise.resolve(badge))
    .catch((err) => Promise.reject(err));
};

/**
 * Add badge
 * @param {object} req - Request json object
 * @return {badge} - return badge
 * @return {err} - return error
 */
let addBadge = (req) => {
  let oBadge = new Badge({
    name: req.name,
    description: req.description,
    type: req.type,
    image: req.image,
    narrative: req.narrative,
    isActive: req.isActive,
    category: req.category
  });

  return oBadge.save()
    .then(badge => Promise.resolve(badge))
    .then(badge => Badge.populate(badge, 'category'))
    .then(badge => Promise.resolve(badge))
    .catch(err => Promise.reject(err));
};

/**
 * Update badge
 * @param {object} req - Request json object
 * @return {badge} - return badge
 * @return {err} - return error
 */
let updateBadge = (req, id) => {
  return Badge
    .findById(id)
    .exec()
    .then((oBadge) => {
      oBadge.name = req.name || oBadge.name;
      oBadge.description = req.description || oBadge.description;
      oBadge.type = req.type || oBadge.type;
      oBadge.image = req.image || oBadge.image;
      oBadge.narrative = req.narrative || oBadge.narrative;
      oBadge.isActive = req.isActive || oBadge.isActive;
      oBadge.category = req.category || oBadge.category;

      return oBadge.save();
    })
    .then(badge => Promise.resolve(badge))
    .then(badge => Badge.populate(badge, 'category'))
    .then(badge => Promise.resolve(badge))
    .catch(err => Promise.reject(err));
};

/**
 * Remove badge by Id
 * @param {ObjectId} id - Badge id
 * @return {badge} - return badge
 * @return {err} - return error
 */
let removeBadge = (id) => {
  return Badge
    .findById(id)
    .exec()
    .then((oBadge) => {
      return oBadge
        .remove()
        .then((remFed) => Promise.resolve(remFed))
        .catch(err => Promise.reject(err));
    })
    .catch(err => Promise.reject(err));
};

/**
 * Remove badge by Id
 * @param {ObjectId} id - Participant id
 * @param {[ObjectId]} id - Badges id
 * @return {participant} - return participant
 * @return {err} - return error
 */
let badgeRequest = (oid, bids) => {
  return Participant
    .findOne({_id: oid, pendingBadges: { $nin: bids }})
    .exec()
    .then((oOrg) => {
      bids = bids.filter(function(item, i, ar){ return ar.indexOf(item) === i; });
      bids.forEach((item) => {
        oOrg.pendingBadges.push(item);
      });
      return oOrg.save();
    })
    .then((participant) => Promise.resolve(participant))
    .catch(err => Promise.reject(err));
};

/**
 * Remove badge by Id
 * @param {ObjectId} id - Participant id
 * @param {[ObjectId]} id - Badges id
 * @return {participant} - return participant
 * @return {err} - return error
 */
let badgeApprove = (oid, bids) => {
  return Participant
    .findById(oid)
    .exec()
    .then((oOrg) => {
      bids.forEach((item) => {
        oOrg.approvedBadges.push(item);
      });
      oOrg.pendingBadges = oOrg.pendingBadges.filter(function(item){ return oOrg.approvedBadges.indexOf(String(item)) == -1; });
      return oOrg.save();
    })
    .then((participant) => Promise.resolve(participant))
    .catch(err => Promise.reject(err));
};

/**
 * Get federation by name
 * @param {String} name - Federation name
 * @return {federations} - return federation
 * @return {err} - return error
 */
let getBadgeByIssuer = (discoveryUrl) => {
  return Entity
    .findOne({
      discoveryUrl: new RegExp('^' + discoveryUrl + '$', 'i')
    })
    .then((oEntity) => {
      if (!oEntity) {
        return Promise.resolve(null);
      }
      return Participant.findById(oEntity.participant._id).populate('approvedBadges');
    })
    .catch((err) => Promise.reject(err));
};

module.exports = {
  getAllBadges,
  getBadgeById,
  getBadgeByName,
  addBadge,
  updateBadge,
  removeBadge,
  badgeRequest,
  badgeApprove,
  getBadgeByIssuer
};
