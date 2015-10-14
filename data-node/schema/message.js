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

var MessageSchema = builder(schemas.message, {
  id: mongoose.Schema.ObjectId,
  oId: String,
  text: String,
  source: String,
  fromUser: String,
  toUsers: [String],
  refUsers: [String],
  date: Date,
  parent: String,
  customTags: [String],
  language: String,
  latitude: Number,
  longitude: Number,
  favs: Number,
  shares: Number,
  tags: [schemas.tag],
  tokens: [schemas.token],
  sentiment: Number
});

var buildSearchQuery = function(type, search) {
  var queryOn;
  var unwinds = 1;
  if (type === 'tag') {
    queryOn = 'tags._id';
  } else if (type === 'token') {
    queryOn = 'tags._id';
  } else if (type === 'category') {
    // categories are two levels nested
    queryOn = 'tags.categories.text';
    unwinds = 2;
  } else {
    throw new Error('"' + type + '" is an unknown search type.');
  }
  var regex = new RegExp(search, 'i');
  // match all messages with at least one element matching
  var matchFirstStep = {
    '$match': {}
  };
  matchFirstStep['$match'][queryOn] = {$regex: regex, $options: 'i'};
  // project only elements of interest
  var projectSecondStep = {
    $project: {_id: false, 'item': '$' + queryOn}
  };
  // unwind all elements from arrays to single values (n levels of nesting = n unwinds)
  var unwindStep = {$unwind: '$item'};
  var steps = [
    matchFirstStep,
    projectSecondStep
  ];
  for (var i = 0; i < unwinds; i++) {
    steps.push(unwindStep);
  }
  // remove duplicate values, re-search on the search parameter, finally sort elements
  steps.push(
    {$group: {_id: "$item"}},
    {$match: {_id: {$regex: regex, $options: 'i'}}},
    {$sort: {_id: 1}}
  );
  return steps;
};

MessageSchema.statics.searchTerm = function(type, term) {
  var model = this;
  return Q(model.aggregate(buildSearchQuery(type, term)).exec());
};

var buildStatQuery = function(type, terms, from, to) {
  var tagsSel = { $literal: [] };
  var tokensSel = { $literal: [] };
  var categoriesSel = { $literal: [[]] };

  var hasTags = (type === 'tag');
  var hasTokens = (type === 'token');
  var hasCategories = (type === 'category');
  var hasFrom = _.isDate(from);
  var hasTo = _.isDate(to);
  var hasTerms = (terms && terms.length > 0);

  var filter = undefined;

  // if there is at least one filter, create the object and apply it
  if (hasTerms || hasFrom || hasTo) {
    filter = { $match: {} };
    if (hasTags) {
      filter.$match['tags._id'] = { $all: terms };
    } else if (hasTokens) {
      filter.$match['tokens.text'] = { $all: terms };
    } else if (hasCategories) {
      filter.$match['tags.categories.text'] = { $all: terms };
    }
    if (hasFrom || hasTo) {
      filter.$match['date'] = {};
      if (hasFrom) {
        filter.$match['date']['$gte'] = from;
      }
      if (hasTo) {
        filter.$match['date']['lte'] = to;
      }
    }
  }

  var unionSet = [];
  if (hasTags || hasCategories) {
    tagsSel = { $ifNull: ['$tags', []] };
    if (hasTags) {
      unionSet.push('$tags');
    }
    if (hasCategories) {
      unionSet.push('$categories');
      categoriesSel = {
        $cond: {
          if: { $or: [ {$eq: [ '$tags.categories', undefined ]}, {$eq: [ '$tags.categories', [] ]}] },
          then: [[]],
          else: '$tags.categories'
        }
      };
    }
  }

  if (hasTokens) {
    unionSet.push('$tokens');
    tokensSel = { $ifNull: ['$tokens', []] };
  }

  var aggregations = [{
    $match: {
      $or: [
        {'tags._id': { $ne: null }},
        {'tokens.text': { $ne: null }},
        {'tags.categories.text': { $ne: null }}
      ]
    }
  }];

  if (filter) {
    aggregations.push(filter);
  }

  aggregations.push({
    $project: {
      _id: false,
      'tags': tagsSel,
      'tokens': tokensSel
    }
  }, {
    $project: {
      'tags._id': true,
      'tags.stopWord': true,
      'tokens.text': true,
      'tokens.stopWord': true,
      'categories': categoriesSel
    }
  }, {
    $unwind: '$categories'
  }, {
    $project: {
      'words': { $setUnion: unionSet }
    }
  }, {
    $unwind: '$words'
  }, {
    $match: {
      'words.stopWord': false
    }
  }, {
    $project: {
      'text': { $ifNull: ['$words.text', '$words._id'] }
    }
  }, {
    $group: {_id: '$text', value: { $sum: 1 } }
  }, {
    $project: { _id: false, name: '$_id', value: true } }, {
    $sort: { value: -1 }
  }, {
    $limit: 200
  });

  return aggregations;
};

MessageSchema.statics.statTerms = function(type, terms, from, to) {
  return Q(this.aggregate(buildStatQuery(type, terms, from, to)).exec());
};

module.exports = MessageSchema;
