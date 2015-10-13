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
var _ = require('lodash');
var qSend = require('../lib/expressQ').send;
var qErr = require('../lib/expressQ').error;
var router = require('express').Router();
var CrowdPulse = require('crowd-pulse-data-node');
var config = require('./../config.json');

module.exports = function() {

  router.route('/stats/terms')
    // /api/stats/terms?db=sexism&from=2015-10-11&to=2015-10-13&type=tag&terms=aword&terms=anotherword
    .get(function(req, res) {
      var dbConn = new CrowdPulse();
      return dbConn.connect(config.database.url, req.query.db)
        .then(function(conn) {
          var terms = [];
          if (!_.isUndefined(req.query.terms)) {
            terms = _.isArray(req.query.terms) ? req.query.terms : [req.query.terms];
          }
          var query = {};
          query[req.query.type] = terms;
          return conn.Message.statTerms(query, req.query.from, req.query.to);
        })
        .then(function(result) {
          return result;
        })
        .then(qSend(res))
        .catch(qErr(res))
        .finally(function() {
          dbConn.disconnect();
        });
    });

  return router;
};