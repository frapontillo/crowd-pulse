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

var spawn = require('child_process').spawn;
var Rx = require('rx');

module.exports = function(io, crowdPulse) {

  var tailMap = {};

  var rxTail = function(fileName) {
    var tailProc = spawn('tail', ['-n', '100', '-f', fileName]);
    var observable = Rx.Observable
      .create(function(observer) {
        observer.onNext(['tail', '-n', '100', '-f', fileName].join(' '));
        tailProc.stdout.on('data', function(data) {
          observer.onNext(data.toString('utf-8'));
        });
        tailProc.on('exit', function() {
          observer.onCompleted();
        });
      })
      .flatMap(function(string) {
        var lines = string.split('\n');
        return Rx.Observable.from(lines);
      })
      .bufferWithTimeOrCount(1000, 20);
    return [tailProc, observable];
  };

  var tail = function(fileName, socket) {
    var tailProc = spawn('tail', ['-n', '+0', '-f', fileName]);
    tailProc.stdout.on('data', function(data) {
      socket.emit('logs:tail', data.toString('utf-8'));
    });
    return tailProc;
  };

  var addTail = function(socket, runId, pid) {
    if (!tailMap[socket.id]) {
      tailMap[socket.id] = {};
    }
    tailMap[socket.id][runId] = pid;
  };

  var remTail = function(socket, runId) {
    // if there's a single ID to be removed, kill one process
    if (runId) {
      return killSingleTail(socket, runId);
    }
    if (!tailMap[socket.id]) {
      return;
    }
    // if no ID is specified, kill'em all
    Object.keys(tailMap[socket.id]).forEach(function(run) {
      killSingleTail(socket, run);
    })
  };

  var killSingleTail = function(socket, runId) {
    try {
      var pid = tailMap[socket.id][runId];
      process.kill(pid);
      delete tailMap[socket.id][runId];
    } catch (ignored) {}
  };

  return io.of('/logs').on('connection', function(socket) {

    socket.on('openLog', function(runId) {
      socket.emit('logs:clear');

      crowdPulse.ProjectRun.getById(runId)
        .then(function(run) {
          return run.log;
        })
        .then(rxTail)
        .spread(function(tailProc, observable) {
          addTail(socket, runId, tailProc.pid);
          observable.subscribe(function(lines) {
            if (lines.length > 0) {
              socket.emit('logs:tail', lines);
            }
          });
        });
    });

    // when a log needs to be closed, kill it
    socket.on('closeLog', function(runId) {
      return remTail(socket, runId);
    });

    // when a client disconnects, kill all pending tails
    socket.on('disconnect', function() {
      remTail(socket);
    });
  });
};