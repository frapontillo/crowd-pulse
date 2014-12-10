/*
 * Copyright 2014 Francesco Pontillo
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

var express = require('express');
var logger = require('morgan');
var bodyParser = require('body-parser');
var oAuthServer = require('oauth2-server');
var DataLayer = require('crowd-pulse-data-nodejs');
var modelGenerator = require('./model');

// set
var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(logger('dev'));

function setupOAuth(dataLayer) {
  app.oAuth = oAuthServer({
    model: modelGenerator(dataLayer),
    grants: ['authorization_code', 'password', 'refresh_token', 'client_credentials'],
    debug: true
  });

  app.all('/oauth/token', app.oAuth.grant());

  app.get('/', app.oAuth.authorise(), function (req, res) {
    res.send('Secret area');
  });

  app.use(app.oAuth.errorHandler());
}

function gracefulExit() {
  dataLayer.disconnect();
}

var dataLayer = new DataLayer();
dataLayer.connect('mongodb://localhost/test')
  .then(function() {
    setupOAuth(dataLayer.model);
    return true;
  })
  .then(function() {
    app.listen(3000);
  });

process
  .on('SIGTERM', gracefulExit);