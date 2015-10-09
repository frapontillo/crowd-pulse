#!/usr/bin/env bash

USERNAME=frapontillo
PASSWORD=holoyolo
CLIENT_ID=55e72c015e4d83ebb24f3480
CLIENT_SECRET=yolo123
REFRESH_TOKEN=89dadcba42581c4ae455b44a280282ddec9d45e4

curl -H "Content-Type: application/x-www-form-urlencoded" \
    --data-urlencode "grant_type=password" \
    --data-urlencode "client_id=$CLIENT_ID" \
    --data-urlencode "client_secret=$CLIENT_SECRET" \
    --data-urlencode "username=$USERNAME" \
    --data-urlencode "password=$PASSWORD" \
    http://localhost:3000/oauth2/token
