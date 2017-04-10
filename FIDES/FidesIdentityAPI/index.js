"user strict";

require('dotenv').config({path: './.env-dev'}); //for development
// require('dotenv').config({path: './.env-prod'}); // for production

const express = require('express'),
    app = express(),
    cookieParser = require('cookie-parser'),
    mongoose = require('mongoose'),
    morgan = require('morgan'),
    bodyParser = require('body-parser'),
    session = require('express-session'),
    expressJwt = require('express-jwt'),
    path = require('path'),
    swagger = require('swagger-express'),
    router = express.Router(),
    cors = require('cors'),
    server = require('http').Server(app);

// MongoDB connection configuration
mongoose.Promise = global.Promise;
mongoose.connect(process.env.DB_URL, (err, res) => {
    if (err)
        console.log(`err connecting to db on ${process.env.DB_URL}, err: ${err}`);
    else
        console.log(`database connected on ${process.env.DB_URL}`);
}); // connect to our database

// Set port
app.set('port', process.env.PORT || 8000);

//Set view engine and view directory.
//app.set('views', __dirname + '/views');
//app.set('view engine', 'ejs');

// Allow cross origin
app.use(cors());

// Swagger Settings
app.use(swagger.init(app, {
    apiVersion: '1.0',
    swaggerVersion: '1.0.5',
    basePath: process.env.BASE_URL,
    swaggerURL: '/swagger',
    swaggerJSON: '/api-docs.json',
    swaggerUI: './public/swagger/',
    apis: ['./swagger/users.yml', './swagger/federations.yml', './swagger/organizations.yml', './swagger/providers.yml']
}));

// Logger
app.use(morgan('dev'));

// JWT token {path: ['/login', '/registerDetail', '/isUserAlreadyExist/**/', '/getAllOrganizations']}
let filter = function(req) {
    if(['/validateEmail', '/login', '/validateRegistrationDetail', '/registerDetail', '/getAllOrganizations'].indexOf(req.path) >= 0) {
        return true;
    } else if(req.path.startsWith('/isUserAlreadyExist') || req.path.startsWith('/images/trustmark/') || req.path.startsWith('/images/badges/') || req.path.startsWith('/getBadgeByOrganization') || req.path.startsWith('/getBadgeByIssuer')) {
        return true;
    }
};
app.use(expressJwt({secret: process.env.APP_SECRET}).unless(filter));

// Validate each call before route
app.use('/', function (err, req, res, next) {
    if (err.name === 'UnauthorizedError') {
        res.status(401).send({
            'message': 'Please login again. Session expired.'
        });
        return;
    } else if (req.originalUrl !== '/login') {
        var authorization = req.header("authorization");
        if (authorization) {
            var session = JSON.parse(new Buffer((authorization.split(' ')[1]).split('.')[1], 'base64').toString());
            res.locals.session = session;
        }
    }
    next();
});

// Load cookie parser
app.use(cookieParser());

// Set directory for express
app.use(express.static(path.join(__dirname, 'public')));

// Load body parser
app.use(bodyParser.json());
app.use(bodyParser.json({limit:'50mb'}));
app.use(bodyParser.urlencoded({limit:'50mb',extended:true}));

// required for passport
app.use(session({
    secret: process.env.APP_SECRET,
    saveUninitialized: true
})); // session secret

// For self-signed certificate.
process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';

// Register routes. Loaded main route. Index route loads other routes.
app.use(require('./controllers/index'));

// Start listening server
server.listen(process.env.PORT, () => {
    console.log(`-----------------------\nServer started successfully!, Open this URL ${process.env.BASE_URL}\n-----------------------`);
});
