"use strict";

// load up the role model
const Role = require('../models/role');

// =============================================================================
// Retrieves all provider ======================================================
// =============================================================================
let getAllRoles = (done) => {
    let query = Role.find({
        isActive: true
    });
    query.sort({
        order: 'asc'
    });

    query.exec((err, roles) => {
        if (err)
            done(err);
        else {
            if (roles.length) {
                console.log(roles);
                done(null, roles);
            } else {
                done(null, null, {
                    message: 'No records found'
                });
            }
        }
    });
};

module.exports = {
    getAllRoles
};
