//index.js
"user strict";

require('dotenv').config({path: './.env-dev'}); //for development
//require('dotenv').config({path: './.env-prod'}); // for production

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

// configuration ===============================================================
mongoose.Promise = global.Promise;
mongoose.connect(process.env.DB_URL, (err, res) => {
    if(err)
        console.log(`err connecting to db on ${process.env.DB_URL}, err: ${err}`);
    else
        console.log(`database connected on ${process.env.DB_URL}`);
}); // connect to our database

app.set('port', process.env.PORT || 8000);
//app.set('views', __dirname + '/views');
//app.set('view engine', 'ejs');

// app.get('/', function (req, res) {
//     console.log('test');
//     res.status(200);
// });

// app.get('/', function (req, res) {
//    res.render('pages/index');
// });
// app.get('/index', function (req, res) {
//     res.render('pages/index');
// });
// app.get('/registerServer', function (req, res) {
//     res.render('pages/registerServer');
// });

//Allow cross origin
app.use(cors());

//Swagger Settings
app.use(swagger.init(app, {
    apiVersion: '1.0',
    swaggerVersion: '1.0.5',
    basePath: process.env.BASE_URL,
    swaggerURL: '/swagger',
    swaggerJSON: '/api-docs.json',
    swaggerUI: './public/swagger/',
       apis: ['./swagger/users.yml','./swagger/providers.yml']
}));

//logger
app.use(morgan('dev'));

//JWT token
/*app.use('/', expressJwt({ secret: process.env.APP_SECRET }));
app.use('/', function(req, res, next) {
	var authorization = req.header("authorization");
	var session = JSON.parse( new Buffer((authorization.split(' ')[1]).split('.')[1], 'base64').toString());
    res.locals.session = session;
    next();
});*/

app.use(cookieParser());

app.use(express.static(path.join(__dirname, 'public')));

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));

// required for passport
app.use(session({
    secret: process.env.APP_SECRET,
    saveUninitialized: true
})); // session secret

/*// Error handlers
app.use(function (err, req, res, next) {
    res.redirect('/login');
});*/

// routes ======================================================================
app.use(require('./controllers/index')); // load our routes and pass in our app
//require('./controllers/users');

server.listen(process.env.PORT, () => {
    console.log(`-------------------------------------------------------------------\nServer started successfully!, Open this URL ${process.env.BASE_URL}\n-------------------------------------------------------------------`);
});
