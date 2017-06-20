"use strict";

const Entity = require('../models/entity');

/**
 * Get all active entities
 * @return {entities} - return all entities
 * @return {err} - return error
 */
let getAllEntities = () => {
  return Entity
    .find({})
    .sort({name: 1})
    .exec()
    .then(entities => entities)
    .catch(err => err);
};

/**
 * Gets entity detail by entity Id.
 * @return {entity} - return all entity
 * @return {err} - return error
 */
let getEntityById = (id) => {
  return Entity.findById(id)
    .exec()
    .then(entities => Promise.resolve(entities))
    .catch(err => Promise.reject(err));
};

/**
 * Gets entity detail by Discovery URL
 * @return {entity} - return all entity
 * @return {err} - return error
 */
let getEntityByUrl = (url, done) => {
  return Entity.findOne({discoveryUrl: url})
    .exec()
    .then(entities => Promise.resolve(entities))
    .catch(err => Promise.reject(err));
};

/**
 * Add entity
 * @param {object} req - Request json object
 * @return {entity} - return entity
 * @return {err} - return error
 */
let addEntity = (req) => {
  let oEntity = new Entity();
  oEntity.name = req.name;
  oEntity.discoveryUrl = req.discoveryUrl;
  oEntity.oxdId = req.oxdId;
  oEntity.redirectUris = req.redirectUris;
  oEntity.participant = req.participant;
  oEntity.responseTypes = req.responseTypes;
  oEntity.createdBy = req.createdBy;
  oEntity.isApproved = false;

  return oEntity.save()
    .then(entity => Promise.resolve(entity))
    .catch(err => Promise.reject(err));
};

/**
 * update entity
 * @param {object} req - Request json object
 * @return {entity} - return entity
 * @return {err} - return error
 */
let updateEntity = (req) => {
  return Entity.findById(req._id)
    .exec()
    .then((oEntity) => {
      oEntity.name = req.name || oEntity.name;
      oEntity.discoveryUrl = req.discoveryUrl || oEntity.discoveryUrl;
      oEntity.participant = req.participantId || oEntity.participant;

      return oEntity.save();
    })
    .then((savedEntity) => Entity.populate(savedEntity, 'participant'))
    .then((savedEntity) => Promise.resolve(savedEntity))
    .catch(err => Promise.reject(err));
};

/**
 * Remove entity by Id
 * @param {ObjectId} id - entity id
 * @return {entity} - return entity
 * @return {err} - return error
 */
let removeEntity = (id) => {
  return Entity
    .findById(id)
    .exec()
    .then((oEntity) => {
      return oEntity
        .remove()
        .then((remEntity) => Promise.resolve(remEntity))
        .catch(err => Promise.reject(err));
    })
    .catch(err => Promise.reject(err));
};

/**
 * Approve Entity by Id
 * @param {ObjectId} orgId - Entity id
 * @param {ObjectId} ottoId - Entity otto id
 * @return {Entity} - return Entity
 * @return {err} - return error
 */
let approveEntity = (id) => {
  return Entity
    .findById(id)
    .exec()
    .then((oEntity) => {
      oEntity.isApproved = true;
      return oEntity.save();
    })
    .then(updatedEntity => Promise.resolve(updatedEntity))
    .catch(err => Promise.reject(err));
};

module.exports = {
  getAllEntities, getEntityById, getEntityByUrl, addEntity, updateEntity, removeEntity, approveEntity
};
