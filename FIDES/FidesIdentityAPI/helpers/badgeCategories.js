"use strict";

const BadgeCategory = require('../models/badgeCategory');

/**
 * Get all active badgeCategories
 * @return {badgeCategories} - return all badgeCategories
 * @return {err} - return error
 */
let getAllBadgeCategories = () => {
  return BadgeCategory
    .find({})
    .sort({name: 1})
    .exec()
    .then((badgeCategories) => Promise.resolve(badgeCategories))
    .catch((err) => Promise.reject(err));
};

/**
 * Get badgeCategory by Id
 * @param {ObjectId} id - BadgeCategory id
 * @return {badgeCategory} - return badgeCategory
 * @return {err} - return error
 */
let getBadgeCategoryById = (id) => {
  return BadgeCategory
    .findById(id)
    .exec()
    .then((badgeCategory) => Promise.resolve(badgeCategory))
    .catch((err) => Promise.reject(err));
};

/**
 * Get badgeCategory by name
 * @param {String} name - BadgeCategory name
 * @return {badgeCategories} - return badgeCategory
 * @return {err} - return error
 */
let getBadgeCategoryByName = (name) => {
  return BadgeCategory
    .findOne({
      name: new RegExp('^' + name + '$', "i")
    })
    .exec()
    .then((badgeCategory) => Promise.resolve(badgeCategory))
    .catch((err) => Promise.reject(err));
};

/**
 * Add badgeCategory
 * @param {object} req - Request json object
 * @return {badgeCategory} - return badgeCategory
 * @return {err} - return error
 */
let addBadgeCategory = (req) => {
  let oBadgeCategory = new BadgeCategory();
  oBadgeCategory.name = req.name;
  oBadgeCategory.description = req.description;

  return oBadgeCategory.save()
    .then(badgeCategory => Promise.resolve(badgeCategory))
    .catch(err => Promise.reject(err));
};

/**
 * Update badgeCategory
 * @param {object} req - Request json object
 * @return {badgeCategory} - return badgeCategory
 * @return {err} - return error
 */
let updateBadgeCategory = (req, id) => {
  return BadgeCategory
    .findById(id)
    .exec()
    .then((oBadgeCategory) => {
      oBadgeCategory.name = req.name || oBadgeCategory.name;
      oBadgeCategory.description = req.description || oBadgeCategory.description;
      return oBadgeCategory.save()
        .then(updatedBadgeCategory => Promise.resolve(updatedBadgeCategory))
        .catch(err => Promise.reject(err));
    })
    .catch(err => Promise.reject(err));
};

/**
 * Remove badgeCategory by Id
 * @param {ObjectId} id - BadgeCategory id
 * @return {badgeCategory} - return badgeCategory
 * @return {err} - return error
 */
let removeBadgeCategory = (id) => {
  return BadgeCategory
    .findById(id)
    .exec()
    .then((oBadgeCategory) => {
      return oBadgeCategory
        .remove()
        .then((remFed) => Promise.resolve(remFed))
        .catch(err => Promise.reject(err));
    })
    .catch(err => Promise.reject(err));
};

module.exports = {
  getAllBadgeCategories,
  getBadgeCategoryById,
  getBadgeCategoryByName,
  addBadgeCategory,
  updateBadgeCategory,
  removeBadgeCategory
};
