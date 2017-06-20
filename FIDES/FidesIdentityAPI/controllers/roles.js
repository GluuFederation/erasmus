"use strict";

const express = require('express'),
  router = express.Router(),
  httpStatus = require('http-status'),
  common = require('../helpers/common'),
  Roles = require('../helpers/roles');

/**
 * Get all available roles.
 */
router.get('/getAllRoles', (req, res, next) => {
  Roles.getAllRoles()
    .then((roles) => {
      if (!roles) {
        return res.status(httpStatus.OK).send(JSON.stringify({data: [], message: 'Roles ' + common.message.NOT_FOUND}));
      }
      return res.status(httpStatus.OK).send(roles);
    })
    .catch(err => next(err));
});

/**
 * Get role by name string.
 */
router.get('/getRoleByName/:name', (req, res, next) => {
  if (!req.params.name) {
    return res.name(406).send({
      'message': 'Please provide name.'
    });
  }

  Roles.getRoleByName(req.params.name)
    .then((role) => {
      if (!role) {
        return res.status(httpStatus.OK).send(JSON.stringify({data: [], message: 'Role ' + common.message.NOT_FOUND}));
      }
      return res.status(httpStatus.OK).send(role);
    })
    .catch(err => next(err));
});

module.exports = router;