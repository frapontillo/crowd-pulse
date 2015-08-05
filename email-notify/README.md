email-notify
============

The `email-notify` plugins reads the configuration from a `email.properties` file containing the following parameters:

  * `email.host`, the SMTP server (e.g. "smtp.gmail.com")
  * `email.port`, the port to connect to
  * `email.username`, the username to connect to the server
  * `email.password`, the password associated to the username
  * `email.use_ssl`, whether to use SSL for connection
  * `email.from`, the email address to send messages from
  * `email.subject` (optional, defaults to "CrowdPulse Notification"), the subject of the email
  * `email.body_success` (optional, defaults to "The pipeline "{{NAME}}" has completed successfully."), 
    the body of the email when the plugin completes successfully (occurrences of "{{NAME}}" will be replaced with the 
    name of the process set in the plugin)
  * `email.body_error` (optional, defaults to "The pipeline "{{NAME}}" has ERRORED!"), 
    the body of the email when the plugin errors (occurrences of "{{NAME}}" will be replaced with the name of the 
    process set in the plugin)
  * `email.notify_success` (optional, defaults to `true`), whether to notify a successful completion
  * `email.notify_error` (optional, defaults to `true`), whether to notify an errored completion
    
The generic configuration parameters can be overridden by the plugin configuration in the pipeline: parameters have the
same name without the `email.` prefix (e.g. `host`, `port`, etc.).

To specify the recipients of the email, simply set the `addresses` attribute with an array of recipient emails
(recipients are **never** read from the `email.properties` file.

The following example configuration overrides `host` and `notify_error` and specifies a list of addresses.

```json
    "notify": {
      "plugin": "email-notifier",
      "config": {
        "host": "another.smtp.provider.com",
        "notify_error": false,
        "addresses": ["francescopontillo@gmail.com", "another@email.com"]
      }
    }
```
