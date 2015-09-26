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

var fs = require('fs');
var spawn = require('child_process').spawn;

/**
 * Execute Crowd Pulse Core using the provided parameters.
 *
 * @param {String} exec - The Crowd Pulse Core executable path.
 * @param {String=} projectRunId - The project run ID to update in the database.
 * @param {String=} log - The exec to redirect the standard output to.
 * @param {String=} databaseConnection - The connection string for the Mongo DB where the project
 * run information is contained.
 * @param {String} configuration - A configuration JSON for the run.
 */
var execute = function(exec, projectRunId, log, databaseConnection, configuration) {
  var input = 'pipe';
  var output = 'pipe';
  var error = 'pipe';

  // prepare the crowd pulse arguments
  var args = [];
  if (projectRunId) {
    args.push('--run', projectRunId);
  }
  if (log) {
    args.push('--log', log);
    // write output and error to the same file
    var logFile = fs.openSync(log, 'w');
    output = logFile;
    error = logFile;
  }
  if (databaseConnection) {
    args.push('--db', databaseConnection);
  }

  console.log('Crowd Pulse launching:', exec, args.join(' '));

  // spawn the process as detached so it doesn't depend on the NodeJS application
  var child = spawn(exec, args, {
    detached: true,
    stdio: [ input, output, error ]
  });

  // write the configuration JSON and end the stream
  child.stdin.write(configuration);
  child.stdin.end();

  // let the NodeJS application lose all references to the child process so not to wait for it
  child.unref();

  console.log('Crowd Pulse launched');
};

module.exports = {
  execute: execute
};