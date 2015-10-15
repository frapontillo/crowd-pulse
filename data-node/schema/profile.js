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
var builder = require('./schemaBuilder');
var schemas = require('./schemaName');

var ProfileSchema = builder(schemas.profile, {
  id: mongoose.Schema.ObjectId,
  source: String,
  username: String,
  customTags: [String],
  activationDate: Date,
  followers: Number,
  followings: Number,
  language: String,
  location: String,
  latitude: Number,
  longitude: Number,
  connections: [String]
});

ProfileSchema.statics.listGraphNodes = function() {
  var inNodesQuery = [
    {
      $match: {
        connections: {$ne: null}
      }
    }, {
      $unwind: '$connections'
    }, {
      $group: {
        _id: '$connections'
      }
    }, {
      $project: {
        _id: false,
        id: '$_id'
      }
    }
  ];
  var outNodesQuery = [
    {
      $match: {
        connections: {$ne: null}
      }
    }, {
      $project: {
        _id: false,
        username: true,
        connections: {$size: '$connections'}
      }
    },
    {
      $match: {
        connections: {$gt: 0}
      }
    }, {
      $group: {
        _id: '$username'
      }
    }, {
      $project: {
        _id: false,
        id: '$_id'
      }
    }
  ];
  return Q.all([this.aggregate(inNodesQuery).exec(), this.aggregate(outNodesQuery).exec()])
    .spread(function(inNodes, outNodes) {
      return [].concat(inNodes).concat(outNodes);
    });
};

ProfileSchema.statics.listGraphEdges = function() {
  var query = [{
    $match: {
      connections: {$ne: null}
    }
  }, {
    $project: {
      _id: false,
      source: '$username',
      target: '$connections'
    }
  }, {
    $unwind: '$target'
  }];
  return Q(this.aggregate(query).exec());
};

module.exports = ProfileSchema;
