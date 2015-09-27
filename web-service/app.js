/*
 * Copyright 2015 Francesco Pontillo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var path = require('path');
var express = require('express');
var session = require('express-session');
var cookieParser = require('cookie-parser');
var cors = require('cors');
var logger = require('morgan');
var bodyParser = require('body-parser');
var CrowdPulse = require('crowd-pulse-data-node');

var bootstrap = require('./bootstrap/bootstrap');
var oAuthSetup = require('./oauth2/setup');

var projects = require('./endpoint/projects');

var config = require('./config.json');

var app = express();
app.use(cookieParser());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(logger('dev'));
app.use(session(config.session));
app.use(cors());

var crowdPulse = new CrowdPulse();

var connect = function() {
  return crowdPulse.connect(config.database.url);
};

var webServiceSetup = function(crowdPulse, app) {
  var API = '/api';
  app.use(API, projects(crowdPulse));
  // TODO: add more endpoints here
};

connect()
  /*.then(function() {
    return bootstrap(crowdPulse, config);
  })*/
  .then(function() {
    return webServiceSetup(crowdPulse);
  })
  .then(function() {
    return oAuthSetup(crowdPulse, app);
  })
  .then(function() {
    app.set('views', path.join(__dirname, 'views'));
    app.set('view engine', 'ejs');
    app.use(express.static(path.join(__dirname, 'public')));

    app.get('/', app.oAuth.authorise(), function (req, res) {
      res.send('Secret area');
    });
    console.log('Listening...');
    app.listen(5000);
  })
  .catch(function(err) {
    console.error(err.stack);
  });

var gracefulExit = function() {
  crowdPulse.disconnect();
};

process.on('SIGTERM', gracefulExit);

module.exports = app;