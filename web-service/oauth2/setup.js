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

'use strict';

var Q = require('q');
var oAuthServer = require('oauth2-server');

var modelGenerator = require('./model');

module.exports = function(crowdPulse, app) {
  app.oAuth = oAuthServer({
    model: modelGenerator(crowdPulse),
    grants: ['authorization_code', 'password', 'refresh_token', 'client_credentials'],
    debug: true,
    refreshTokenLifetime: 1209600
  });

  app.all('/oauth2/token', app.oAuth.grant());

  // Show them the "do you authorise xyz app to access your content?" page
  app.get('/oauth2/authorize', function(req, res, next) {
    // If the user isn't logged in at this point, send it to your own login implementation
    if (!req.session.user) {
      return res.redirect('/login?client_id=' +
                          req.query.client_id + '&redirect_uri=' + req.query.redirect_uri);
    }

    // if the user is logged in, render the authorization page
    res.render('pages/authorize', {
      client_id: req.query.client_id,
      redirect_uri: req.query.redirect_uri
    });
  });

  // Handle authorization submission
  app.post('/oauth2/authorize', function(req, res, next) {
    // If the user is not logged in, redirect it to the login implementation
    if (!req.session.user) {
      return res.redirect('/login?client_id=' + req.query.client_id +
                          '&redirect_uri=' + req.query.redirect_uri);
    }
    // otherwise, go on
    next();
  }, app.oAuth.authCodeGrant(function(req, next) {
    var hasUserAuthorizedApp = req.body.allow === 'yes';
    // The first param should to indicate an error
    // The second param should a bool to indicate if the user did authorize the app
    // The third param should for the user/uid (only used for passing to saveAuthCode)
    next(null, hasUserAuthorizedApp, req.session.user.id, req.session.user);
  }));

  // Show login implementation
  app.get('/login', function(req, res, next) {
    res.render('pages/login', {
      client_id: req.query.client_id,
      redirect_uri: req.query.redirect_uri
    });
  });

  // Handle the login submission
  app.post('/login', function(req, res, next) {
    crowdPulse.User.findOneIdByNameSecret(req.body.username, req.body.password)
      .then(function(user) {
        if (!user) {
          // wrong login, re-render the login page
          res.render('pages/login', {
            client_id: req.body.client_id,
            redirect_uri: req.body.redirect_uri
          });
        } else {
          // in case of a successful login
          // save the user into the session
          req.session.user = {id: user.toString()};
          // redirect to /oauth2/authorize with the client_id and redirect_uri
          return res.redirect('/oauth2/authorize?client_id=' +
                              req.body.client_id + '&redirect_uri=' + req.body.redirect_uri);
        }
      });
  });

  app.use(app.oAuth.errorHandler());

  return Q.resolve(app).promise;
};