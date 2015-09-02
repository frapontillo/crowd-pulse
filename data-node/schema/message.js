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

var mongoose = require('mongoose');
var TagSchema = require('./tag');
var TokenSchema = require('./token');

var MessageSchema = new mongoose.Schema({
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
  tags: [TagSchema],
  tokens: [TokenSchema],
  sentiment: Number
});

var SCHEMA_NAME = 'Message';

MessageSchema.statics.getSchemaName = function() {
  return SCHEMA_NAME;
};

MessageSchema.set('collection', SCHEMA_NAME);

module.exports = MessageSchema;
