"use strict";

const Organization = require('../models/organization');
const Provider = require('../models/provider');
const Federation = require('../models/federation');

/**
 * Get all active organizations
 * @return {organizations} - return all organizations
 * @return {err} - return error
 */
let getAllOrganizations = () => {
  return Organization
    .find({})
    .sort({name: 1})
    .exec()
    .then((organizations) => Promise.resolve(organizations))
    .catch((err) => Promise.reject(err));
};

/**
 * Get organization by Id
 * @param {ObjectId} id - Organization id
 * @return {organization} - return organization
 * @return {err} - return error
 */
let getOrganizationById = (id) => {
  return Organization
    .findById(id)
    .exec()
    .then((organization) => Promise.resolve(organization))
    .catch((err) => Promise.reject(err));
};

/**
 * Get organization by name
 * @param {String} name - Organization name
 * @return {organizations} - return organization
 * @return {err} - return error
 */
let getOrganizationByName = (name) => {
  return Organization
    .findOne({
      name: new RegExp('^' + name + '$', "i")
    })
    .exec()
    .then((organization) => Promise.resolve(organization))
    .catch((err) => Promise.reject(err));
};

/**
 * Add organization
 * @param {object} req - Request json object
 * @return {organization} - return organization
 * @return {err} - return error
 */
let addOrganization = (req) => {
  let oOrganization = new Organization({
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

  return oOrganization.save()
    .then(organization => Promise.resolve(organization))
    .catch(err => Promise.reject(err));
};

/**
 * Update organization
 * @param {object} req - Request json object
 * @return {organization} - return organization
 * @return {err} - return error
 */
let updateOrganization = (req) => {
  const id = req._id;
  return Organization
    .findById(id)
    .exec()
    .then((oOrganization) => {
      oOrganization.name = req.name || oOrganization.name;
      oOrganization.isApproved = req.isApproved || oOrganization.isApproved;
      oOrganization.phoneNo = req.phoneNo || oOrganization.phoneNo;
      oOrganization.address = req.address || oOrganization.address;
      oOrganization.zipcode = req.zipcode || oOrganization.zipcode;
      oOrganization.state = req.state || oOrganization.state;
      oOrganization.city = req.city || oOrganization.city;
      oOrganization.type = req.type || oOrganization.type;
      oOrganization.description = req.description || oOrganization.description;
      oOrganization.trustMarkFile = req.trustMarkFile || oOrganization.trustMarkFile;

      return oOrganization.save();
    })
    .then((updateOrganization) => Organization.populate(updateOrganization, 'federationId'))
    .then((oOrganization) => Promise.resolve(oOrganization))
    .catch(err => Promise.reject(err));
};

/**
 * Remove organization by Id
 * @param {ObjectId} id - Organization id
 * @return {organization} - return organization
 * @return {err} - return error
 */
let removeOrganization = (id) => {
  return Organization
    .findById(id)
    .exec()
    .then((oOrganization) => {
      return oOrganization
        .remove()
        .then((remOrg) => Promise.resolve(remOrg))
        .catch(err => Promise.reject(err));
    })
    .catch(err => Promise.reject(err));
};

/**
 * Approve organization by Id
 * @param {ObjectId} orgId - Organization id
 * @param {ObjectId} ottoId - Organization otto id
 * @param {ObjectId} fedId - Federation id
 * @return {organization} - return organization
 * @return {err} - return error
 */
let approveOrganization = (orgId, fedId) => {
  return Organization
    .findById(orgId)
    .exec()
    .then((oOrganization) => {
      oOrganization.federation = fedId;
      oOrganization.isApproved = true;
      return oOrganization.save();
    })
    .then((updateOrganization) => Organization.populate(updateOrganization, 'federation'))
    .then((oOrganization) => Promise.resolve(oOrganization))
    .catch(err => Promise.reject(err));
};

/**
 * Join organization and Entity
 * @param {ObjectId} oid - Organization id
 * @param {ObjectId} pid - Provider id
 * @return {Object} - return Provider
 * @return {err} - return error
 */
let linkOrganizationAndEntity = (oid, pid) => {
  return Organization
    .findById(oid)
    .exec()
    .then((oOrganization) => {
      if (oOrganization.entities.indexOf(pid) > -1) {
        return Promise.reject({ error: 'Federation Entity already exist'});
      }
      oOrganization.entities.push(pid);
      return oOrganization.save();
    })
    .then((oOrganization) => {
      return Provider.findById(pid).exec()
    })
    .then((oProvider) => {
      oProvider.organization = oid;
      return oProvider.save();
    })
    .then((oProvider) => Promise.resolve(oProvider))
    .catch(err => Promise.reject(err));
};

/**
 * Set owner organization for federation
 * @param {ObjectId} oid - Organization id
 * @param {ObjectId} fid - Federation id
 * @return {Object} - return Federation
 * @return {err} - return error
 */
let setOwnerOrganization = (oid, fid) => {
  return Organization
    .findById(oid)
    .exec()
    .then((oOrganization) => {
      if (oOrganization.federations.indexOf(fid) > -1) {
        return Promise.reject({ error: 'Federation already exist'});
      }
      oOrganization.federations.push(fid);
      return oOrganization.save();
    })
    .then((oOrganization) => {
      return Federation.findById(fid).exec()
    })
    .then((oFederation) => {
      oFederation.organization = oid;
      return oFederation.save();
    })
    .then((oFederation) => Promise.resolve(oFederation))
    .catch(err => Promise.reject(err));
};

module.exports = {
  getAllOrganizations,
  getOrganizationById,
  getOrganizationByName,
  addOrganization,
  updateOrganization,
  removeOrganization,
  approveOrganization,
  linkOrganizationAndEntity,
  setOwnerOrganization
};
