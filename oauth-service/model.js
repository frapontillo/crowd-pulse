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

var modelGenerator = function(dataLayer) {
  var model = {};
  /*
   * oauth2-server callbacks
   */
  model.getAccessToken = function (bearerToken, callback) {
    console.log('in getAccessToken (bearerToken: ' + bearerToken + ')');
    dataLayer.AccessTokenModel.findOne({ accessToken: bearerToken }, callback);
  };

  model.getClient = function (clientId, clientSecret, callback) {
    console.log('in getClient (clientId: ' + clientId + ', clientSecret: ' + clientSecret + ')');
    if (clientSecret === null) {
      return dataLayer.AppModel.findOne({ id: clientId }, callback);
    }
    dataLayer.AppModel.findOne({ id: clientId, secret: clientSecret }, callback);
  };

  model.grantTypeAllowed = function (clientId, grantType, callback) {
    console.log('in grantTypeAllowed (clientId: ' + clientId + ', grantType: ' + grantType + ')');

    dataLayer.AppModel
      .findOneQ({ id: clientId, allowedGrants: grantType })
      .then(function(value) {
        callback(false, (!!value));
      })
      .catch(function() {
        callback(true, false);
      });
  };

  model.saveAccessToken = function (token, clientId, expires, userId, callback) {
    console.log('in saveAccessToken (token: ' + token + ', clientId: ' + clientId + ', userId: ' + userId + ', expires: ' + expires + ')');

    var accessToken = new dataLayer.AccessTokenModel({
      accessToken: token,
      userId: userId,
      appId: clientId,
      expires: expires
    });

    accessToken.save(callback);
  };

  /*
   * Required to support password grant type
   */
  model.getUser = function (username, password, callback) {
    console.log('in getUser (username: ' + username + ', password: ' + password + ')');

    dataLayer.UserModel.findOne({
      username: username,
      secret: password
    }, function(err, user) {
      if(err) {
        return callback(err);
      }
      callback(null, user.id);
    });
  };

  /*
   * Required to support refreshToken grant type
   */
  model.saveRefreshToken = function (token, clientId, expires, userId, callback) {
    console.log('in saveRefreshToken (token: ' + token + ', clientId: ' + clientId +', userId: ' + userId + ', expires: ' + expires + ')');

    var refreshToken = new dataLayer.RefreshTokenModel({
      refreshToken: token,
      userId: userId,
      appId: clientId,
      expires: expires
    });

    refreshToken.save(callback);
  };

  model.getRefreshToken = function (refreshToken, callback) {
    console.log('in getRefreshToken (refreshToken: ' + refreshToken + ')');

    dataLayer.RefreshTokenModel.findOne({ refreshToken: refreshToken }, callback);
  };

  return model;
};

module.exports = modelGenerator;
