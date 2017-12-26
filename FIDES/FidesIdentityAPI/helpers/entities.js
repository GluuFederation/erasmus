"use strict";
const common = require('./common');
common.ottoConfig.isServerStart = false;
const Entity = require('otto-node-package')(common.ottoConfig).model.entityModel;

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
  let oEntity = new Entity({
    name: req.name,
    discoveryUrl: req.discoveryUrl,
    oxdId: req.oxdId,
    redirectUris: req.redirectUris,
    operatedBy: req.operatedBy,
    responseTypes: req.responseTypes,
    createdBy: req.createdBy,
    isApproved: false,
    federatedBy: req.federatedBy
  });

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
      const obj = {
        name: req.name || oEntity.name,
        signedJwksUri: req.signedJwksUri || oEntity.signedJwksUri || '',
        signingKeys: req.signingKeys || oEntity.signingKeys || '',
        type: req.type || oEntity.type || ''
      };
      return Entity.findOneAndUpdate({_id: oEntity._id}, obj);
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
      return Entity.findOneAndUpdate({_id: oEntity._id}, {isApproved: true});
    })
    .then(updatedEntity => Promise.resolve(updatedEntity))
    .catch(err => Promise.reject(err));
};

let getEntityWithParticipant = (id) => {
  return Entity
    .aggregate([
      {
        $lookup:{
          from:"participants",
          localField:"operatedBy.id",
          foreignField:"_id",
          as:"participant"
        }
      },
      {
        $match:{
          _id:id
        }
      }
    ])
    .then((oEntity) => {
      oEntity[0].participant = oEntity[0].participant[0];
      oEntity = oEntity[0];
      return Promise.resolve(oEntity);
    })
    .catch(err => Promise.reject(err));
};

module.exports = {
  getAllEntities,
  getEntityById,
  getEntityByUrl,
  addEntity,
  updateEntity,
  removeEntity,
  approveEntity,
  getEntityWithParticipant
};
