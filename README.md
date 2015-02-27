crowd-pulse
===========

Crowd Pulse: social extraction and analysis system

## Module descriptions

- `admin-cli`: Command Line Interface to handle the core functions of the platform
- `data-java`: data access layer for Java modules
- `data-nodejs`: data access layer for NodeJS modules
- `oauth-service`: OAuth 2.0 Web Service for authenticating users and apps
- `social`: generic social network module to handle message extraction and user profiling
- `social-facebook`: specific implementation of `social` for Facebook
- `social-twitter`: specific implementation of `social` for Twitter
- `social-cli`: simple CLI to launch social message extraction and user profiling
- `fix-geo-profile`: fixes profile coordinates (lat/long) by forward geocoding via Google APIs
- `fix-geo-message`: fixes message coordinates (lat/long) by assuming they're the same as the user profile
- `detect-language`: detects the language of the extracted languages
- `web-service`: RESTful Web Service to expose stored data

## License

```
   Copyright 2015 Francesco Pontillo

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

```