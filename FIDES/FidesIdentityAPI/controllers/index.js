"use strict";

const express = require('express'),
    router = express.Router();

// normal routes ===============================================================
router.get('/loggedIn', (req, res) => res.status(200).send({
    "message": "User logged in."
}));

router.use('/', require('./users'));
router.use('/', require('./providers'));
router.use('/', require('./roles'));

module.exports = router;
