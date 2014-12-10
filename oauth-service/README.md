crowd-pulse-oauth-service
=========================

Crowd Pulse OAuth 2.0 Web Service

##Authorization Code

Call with a `GET` the following **authorization** endpoint:

```
GET http://oauth-service:3000/oauth/authorize?
    response_type=code&
    client_id=theclientid123&
    redirect_uri=http://yourapp.com&
    scope=some,scopes
```

If the process is correct, the user will be redirected to the `redirect_uri`, that will hold the generated
authorization code for the user.

Then, make a `GET` request to the following **token** endpoint:

```
GET http://oauth-service:3000/oauth/token?
    response_type=code&
    client_id=theclientid123&
    redirect_uri=http://yourapp.com&
    scope=some,scopes
```