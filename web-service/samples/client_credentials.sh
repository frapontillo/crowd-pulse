#!/usr/bin/env bash

CLIENT_ID=55e72c015e4d83ebb24f3480
CLIENT_SECRET=yolo123
AUTH=$(echo -n "$CLIENT_ID:$CLIENT_SECRET" | base64)

curl -H "Content-Type: application/x-www-form-urlencoded" \
    -H "Authorization: Basic $AUTH=" \
    --data-urlencode "grant_type=client_credentials" \
    http://localhost:3000/oauth2/token
