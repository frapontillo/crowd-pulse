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
var mongoose = require('mongoose');

module.exports = function(name, schema) {

  var TheSchema = new mongoose.Schema(schema);
  TheSchema.statics.getSchemaName = function() {
    return name;
  };
  TheSchema.set('collection', name);

  TheSchema.statics.getById = function(id) {
    return Q(this.findById(id).exec());
  };

  return TheSchema;
};
