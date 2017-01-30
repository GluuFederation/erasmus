"use strict";

const express = require('express'),
    router = express.Router();

/**
 * Default route.
 */
router.get('/loggedIn', (req, res) => res.status(200).send({
    "message": "User logged in."
}));

router.use('/', require('./users'));
router.use('/', require('./providers'));
router.use('/', require('./roles'));
router.use('/', require('./organizations'));
router.use('/', require('./federations'));

module.exports = router;
