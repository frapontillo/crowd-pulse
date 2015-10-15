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

  var asArray = function(value) {
    var terms = [];
    if (!_.isUndefined(value)) {
      terms = _.isArray(value) ? value : [value];
    }
    return terms;
  };

  router.route('/stats/terms')
    // /api/stats/terms?db=sexism&from=2015-10-11&to=2015-10-13&type=tag&terms=aword&terms=anotherword
    .get(function(req, res) {
      var dbConn = new CrowdPulse();
      return dbConn.connect(config.database.url, req.query.db)
        .then(function(conn) {
          var stats = [];
          var queryTypes = ['tag', 'category', 'token'];
          // if the query type is not known, assume all
          if (_.isUndefined(req.query.type) || queryTypes.indexOf(req.query.type) < 0) {
            queryTypes.forEach(function(queryType) {
              stats.push(conn.Message.statTerms(queryType, [], req.query.from, req.query.to));
            });
          } else {
            var terms = asArray(req.query.terms);
            stats.push(conn.Message.statTerms(req.query.type, terms, req.query.from, req.query.to));
          }
          return Q.all(stats);
        })
        .then(function(results) {
          var result = [];
          results.forEach(function(r) {
            result = result.concat(r);
          });
          var all = _.sortByOrder(result, ['value'], ['desc']);
          return all.slice(0, 200);
        })
        .then(qSend(res))
        .catch(qErr(res))
        .finally(function() {
          dbConn.disconnect();
        });
    });

  var handleGenericStat = function(req, res, handler) {
    var dbConn = new CrowdPulse();
    return dbConn.connect(config.database.url, req.query.db)
      .then(function(conn) {
        var terms = asArray(req.query.terms);
        return handler(conn, req.query.type, terms, req.query.from, req.query.to);
      })
      .then(qSend(res))
      .catch(qErr(res))
      .finally(function() {
        dbConn.disconnect();
      });
  };

  router.route('/stats/sentiment')
    // /api/stats/sentiment?db=sexism&from=2015-10-11&to=2015-10-13&type=tag&terms=aword&terms=anotherword
    .get(function(req, res) {
      return handleGenericStat(req, res, function(conn, type, terms, from, to) {
        return conn.Message.statSentiment(type, terms, from, to);
      });
    });

  router.route('/stats/sentiment/timeline')
    // /api/stats/sentiment?db=sexism&from=2015-10-11&to=2015-10-13&type=tag&terms=aword&terms=anotherword
    .get(function(req, res) {
      return handleGenericStat(req, res, function(conn, type, terms, from, to) {
        return conn.Message.statSentimentTimeline(type, terms, from, to);
      });
    });

  router.route('/stats/message/timeline')
    // /api/stats/message/timeline?db=sexism&from=2015-10-11&to=2015-10-13&type=tag&terms=aword&terms=anotherword
    .get(function(req, res) {
      return handleGenericStat(req, res, function(conn, type, terms, from, to) {
        return conn.Message.statMessageTimeline(type, terms, from, to);
      });
    });

  return router;
};