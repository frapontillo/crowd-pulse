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

(function() {
  'use strict';

  angular
    .module('webUi')
    .controller('ViewIndexController', ViewIndexController);

  /* global Highcharts:false */
  /** @ngInject */
  function ViewIndexController($scope, $timeout, $window, Stat) {
    var vm = this;

    vm.params = {};

    // CHART BUILDERS

    var buildBaseHighcharts = function(type) {
      return {
        chart: {
          plotBackgroundColor: null,
          plotBorderWidth: null,
          plotShadow: false,
          type: type
        },
        title: {
          text: null
        },
        exporting: {
          buttons: {
            contextButton: {
              enabled: false
            }
          }
        },
        credits: {
          enabled: false
        },
        legend: {
          enabled: false
        }
      };
    };

    var buildPieChart = function(title, values) {
      var chart = buildBaseHighcharts('pie');
      chart.tooltip = {
        pointFormat: '{series.name}: <b>{point.y} ({point.percentage:.1f}%)</b>'
      };
      chart.plotOptions = {
        pie: {
          allowPointSelect: true,
          cursor: 'pointer',
          dataLabels: {
            enabled: true,
            format: '<b>{point.name}</b>: {point.y} ({point.percentage:.1f}%)',
            style: {
              color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
            }
          }
        }
      };
      chart.series = [{
        name: title,
        colorByPoint: true,
        data: values
      }];
      return chart;
    };

    var buildBarChart = function(xTitle, xValues, yTitle, yValues) {
      var chart = buildBaseHighcharts('bar');
      chart.tooltip = {
        pointFormat: '{series.name}: <b>{point.y}</b>'
      };
      chart.xAxis = {
        categories: xValues,
        title: {
          text: xTitle
        }
      };
      chart.yAxis = {
        title: {
          text: yTitle
        },
        labels: {
          overflow: 'justify'
        }
      };
      chart.series = [{
        name: yTitle,
        colorByPoint: true,
        data: yValues
      }];
      return chart;
    };

    var buildTimelineChart = function(title, series) {
      var chart = buildBaseHighcharts('spline');
      chart.xAxis = {
        type: 'datetime',
        dateTimeLabelFormats: {
          month: '%e. %b',
          year: '%b'
        },
        title: {
          text: 'Date'
        }
      };
      chart.yAxis = {
        title: {
          text: title
        }
      };
      chart.tooltip = {
        headerFormat: '<b>{series.name}</b><br>',
        pointFormat: '{point.x:%e. %b}: {point.y:.2f}'
      };
      chart.plotOptions = {
        spline: {
          marker: {
            enabled: true
          }
        }
      };
      chart.series = series;
      return chart;
    };

    // REST FETCHERS

    var buildStatParams = function() {
      return {
        db: vm.params.database,
        type: vm.params.filterOn,
        terms: vm.params.query,
        from: vm.params.fromDate,
        to: vm.params.toDate
      };
    };

    var getStatWords = function() {
      return Stat.Terms.getList(buildStatParams());
    };

    var getStatSentiment = function() {
      return Stat.Sentiment.getList(buildStatParams());
    };

    var getTimelineSentiment = function() {
      return Stat.SentimentTimeline.getList(buildStatParams());
    };

    var getTimelineMessage = function() {
      return Stat.MessageTimeline.getList(buildStatParams());
    };

    // REST TO CHART MAPPERS

    var matStatToPie = function(stats) {
      return stats.map(function(stat) {
        return {
          name: stat.name,
          y: stat.value
        };
      });
    };

    var mapStatToBar = function(stats) {
      var cats = stats.map(function(stat) {
        return stat.name;
      });
      var series = stats.map(function(stat) {
        return stat.value;
      });
      return [cats, series];
    };

    var mapStatToTimeline = function(stats) {
      return stats.map(function(stat) {
        var color = '#000000';
        if (stat.name === 'positive') {
          color = '#73E639';
        } else if (stat.name === 'negative') {
          color = '#E63939';
        }
        return {
          name: stat.name,
          data: stat.values.map(function(elem) {
            return [(new Date(elem.date)).getTime(), elem.value];
          }),
          color: color
        };
      });
    };

    // CHART GENERATION HANDLERS

    var statWordCloud = function() {
      return getStatWords()
        .then(function(stats) {
          return stats.map(function(stat) {
            return {
              text: stat.name,
              weight: stat.value
            };
          });
        })
        .then(function(stats) {
          vm.stat = stats;
        });
    };

    var statWordPie = function() {
      return getStatWords()
        .then(matStatToPie)
        .then(function(stats) {
          vm.stat = buildPieChart('Occurrences', stats);
        });
    };

    var statWordBar = function() {
      return getStatWords()
        .then(mapStatToBar)
        .then(function(categoriesSeries) {
          vm.stat = buildBarChart(vm.params.filterOn, categoriesSeries[0], 'Occurrences',
            categoriesSeries[1]);
        });
    };

    var statSentimentPie = function() {
      return getStatSentiment()
        .then(matStatToPie)
        .then(function(stats) {
          vm.stat = buildPieChart('Sentiments', stats);
        });
    };

    var statSentimentBar = function() {
      return getStatSentiment()
        .then(mapStatToBar)
        .then(function(categoriesSeries) {
          vm.stat = buildBarChart(vm.params.filterOn, categoriesSeries[0], 'Sentiments',
            categoriesSeries[1]);
        });
    };

    var statSentimentTimeline = function() {
      return getTimelineSentiment()
        .then(mapStatToTimeline)
        .then(function(timeline) {
          vm.stat = buildTimelineChart('Sentiment Distribution', timeline);
        });
    };

    var statMessageTimeline = function() {
      return getTimelineMessage()
        .then(mapStatToTimeline)
        .then(function(timeline) {
          vm.stat = buildTimelineChart('Message Distribution', timeline);
        });
    };

    var handlers = {
      'word-cloud': statWordCloud,
      'word-pie': statWordPie,
      'word-bar': statWordBar,
      'sentiment-pie': statSentimentPie,
      'sentiment-bar': statSentimentBar,
      'sentiment-timeline': statSentimentTimeline,
      'message-timeline': statMessageTimeline,
    };

    $scope.$watch('vm.params', function(newValue, oldValue) {
      if (newValue === oldValue) {
        return;
      }
      vm.stat = null;
      handlers[newValue.dataViz]()
        .then(function() {
          // forcefully dispatch a resize event to make the word cloud recalculate its dimensions
          $timeout(function() {
            /* global Event:false */
            $window.dispatchEvent(new Event('resize'));
          });
        });
    }, true);
  }

})();
