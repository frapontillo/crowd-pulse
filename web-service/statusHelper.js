var buildError = function(message, error) {
  return {
    error: error.message,
    stack: error.stack,
    message: message
  };
};

var StatusHelper = {
  notFound: function(res, element, error) {
    res.status(404);
    res.send(buildError('Can\'t find resource: ' + element, error));
  },
  forbidden: function(res, description, error) {
    res.status(403);
    res.send(buildError('Can\'t perform the desired operation:\n' + description, error));
  }
};

module.exports = StatusHelper;