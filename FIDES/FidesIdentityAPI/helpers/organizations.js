"use strict";

const Organization = require('../models/organization');

/**
 * Get all active organizations
 * @return {organizations} - return all organizations
 * @return {err} - return error
 */
let getAllOrganizations = () => {
    return Organization
        .find({})
        .sort({ name: 1 })
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
    let oOrganization = new Organization();
    oOrganization.name = req.name;
    oOrganization.isApproved = req.isApproved || false;

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
            return oOrganization.save()
                .then(updatedOrganization => Promise.resolve(updatedOrganization))
                .catch(err => Promise.reject(err));
        })
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
let approveOrganization = (orgId, ottoId, fedId) => {
    return Organization
        .findById(orgId)
        .exec()
        .then((oOrganization) => {
            oOrganization.ottoId = ottoId;
            oOrganization.federationId = fedId;
            oOrganization.isApproved = true;
            return oOrganization.save()
                .then(updatedOrganization => Promise.resolve(updatedOrganization))
                .catch(err => Promise.reject(err));
        })
        .catch(err => Promise.reject(err));
};

module.exports = {
    getAllOrganizations,
    getOrganizationById,
    getOrganizationByName,
    addOrganization,
    updateOrganization,
    removeOrganization,
    approveOrganization
};
