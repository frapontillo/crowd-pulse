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

ProfileSchema.statics.listGraphNodes = function(users) {
  var inNodesQuery = [
    {
      $match: {
        username: {$in: users}
      }
    }, {
      $project: {
        _id: false,
        'id': '$connections'
      }
    }, {
      $unwind: '$id'
    }, {
      $group: {
        _id: '$id'
      }
    }, {
      $match: {
        _id: {
          $not: {
            $in: users
          }
        }
      }
    }, {
      $project: {
        _id: false,
        id: '$_id'
      }
    }, {
      $sort: {
        id: 1
      }
    }
  ];
  var outNodesQuery = [
    {
      $match: {
        username: {$in: users}
      }
    }, {
      $group: {
        _id: '$username'
      }
    }, {
      $project: {
        _id: false,
        'id': '$_id'
      }
    }, {
      $sort: {
        id: 1
      }
    }
  ];
  return Q.all([this.aggregate(inNodesQuery).exec(), this.aggregate(outNodesQuery).exec()])
    .spread(function(inNodes, outNodes) {
      return [].concat(inNodes).concat(outNodes);
    });
};

ProfileSchema.statics.listGraphEdges = function(users) {
  var query = [
    {
      $match: {
        username: {$in: users},
        $or: [{connections: {$ne: null}}, {connections: {$gt: 0}}]
      }
    }, {
      $project: {
        _id: false,
        source: '$username',
        target: '$connections'
      }
    }, {
      $unwind: '$target'
    }
  ];
  return Q(this.aggregate(query).exec());
};

ProfileSchema.statics.search = function(username) {
  var model = this;
  var regex = new RegExp('^' + (username || ''), 'i');
  var aggregations = [
    {
      $match: {
        username: {$regex: regex, $options: 'i'}
      }
    }, {
      $project: {
        _id: false,
        username: true
      }
    }, {
      $sort: {
        username: 1
      }
    }
  ];
  return Q(model.aggregate(aggregations).exec());
};

module.exports = ProfileSchema;
