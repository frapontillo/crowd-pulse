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

var qSend = require('../lib/expressQ').send;
var qErr = require('../lib/expressQ').error;
var router = require('express').Router();

module.exports = function(crowdPulse) {

  router.route('/databases')
    .get(function(req, res) {
      return crowdPulse.getDatabases().then(qSend(res)).catch(qErr(res));
    });

  return router;
};