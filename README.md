crowd-pulse
===========

Reactive and Extensible Social Extraction and Analysis System.

-----------

This package contains just a `build.gradle` file that, when built, fetches all the available 
`pulse` and `crowd-pulse` modules and puts them in the classpath in order to make their plugins 
available for use.

## Install

Download and build the gradle script file that references all Crowd Pulse plugins.

```shell
$ wget https://github.com/frapontillo/crowd-pulse/archive/0.0.1.tar.gz -O crowd-pulse.tar.gz
$ mkdir crowd-pulse
$ tar -xfvz crowd-pulse.tar.gz -C crowd-pulse --strip-components 1
$ ./crowd-pulse/gradlew installDist
```

## Configure

Copy all `.properties` files and external resources (dictionaries, lists, etc.) in the 
`/crowd-pulse/build/install/crowd-pulse/lib` directory.

## Run

Call the main executable:

```shell
$ ./crowd-pulse/build/install/crowd-pulse/bin/crowd-pulse
```

## Available plugins

All plugins in the following modules are included (links to repositories):

* [`frapontillo/pulse-email-notify`](https://github.com/frapontillo/pulse-email-notify)
* [`frapontillo/crowd-pulse-social-twitter`](https://github.com/frapontillo/crowd-pulse-social-twitter)
* [`frapontillo/crowd-pulse-social-facebook`](https://github.com/frapontillo/crowd-pulse-social-facebook)
* [`frapontillo/crowd-pulse-data-java`](https://github.com/frapontillo/crowd-pulse-data-java)
* [`frapontillo/crowd-pulse-detect-language-optimaize`](https://github.com/frapontillo/crowd-pulse-detect-language-optimaize)
* [`frapontillo/crowd-pulse-fix-geo-profile-google-maps`](https://github.com/frapontillo/crowd-pulse-fix-geo-profile-google-maps)
* [`frapontillo/crowd-pulse-fix-geo-message-from-profile`](https://github.com/frapontillo/crowd-pulse-fix-geo-message-from-profile)
* [`frapontillo/crowd-pulse-index-uniba`](https://github.com/frapontillo/crowd-pulse-index-uniba)
* [`frapontillo/crowd-pulse-tag-babelfy`](https://github.com/frapontillo/crowd-pulse-tag-babelfy)
* [`frapontillo/crowd-pulse-tag-me`](https://github.com/frapontillo/crowd-pulse-tag-me)
* [`frapontillo/crowd-pulse-tag-open-calais`](https://github.com/frapontillo/crowd-pulse-tag-open-calais)
* [`frapontillo/crowd-pulse-tag-wikipedia-miner`](https://github.com/frapontillo/crowd-pulse-tag-wikipedia-miner)
* [`frapontillo/crowd-pulse-categorize-wikipedia`](https://github.com/frapontillo/crowd-pulse-categorize-wikipedia)
* [`frapontillo/crowd-pulse-tokenize-open-nlp`](https://github.com/frapontillo/crowd-pulse-tokenize-open-nlp)
* [`frapontillo/crowd-pulse-lemmatize-morphit`](https://github.com/frapontillo/crowd-pulse-lemmatize-morphit)
* [`frapontillo/crowd-pulse-lemmatize-stanford-corenlp`](https://github.com/frapontillo/crowd-pulse-lemmatize-stanford-corenlp)
* [`frapontillo/crowd-pulse-lemmatize-multi`](https://github.com/frapontillo/crowd-pulse-lemmatize-multi)
* [`frapontillo/crowd-pulse-pos-tag-open-nlp`](https://github.com/frapontillo/crowd-pulse-pos-tag-open-nlp)
* [`frapontillo/crowd-pulse-pos-tag-simple-it`](https://github.com/frapontillo/crowd-pulse-pos-tag-simple-it)
* [`frapontillo/crowd-pulse-pos-tag-simple-en`](https://github.com/frapontillo/crowd-pulse-pos-tag-simple-en)
* [`frapontillo/crowd-pulse-pos-tag-simple-multi`](https://github.com/frapontillo/crowd-pulse-pos-tag-simple-multi)
* [`frapontillo/crowd-pulse-rem-stop-word-simple`](https://github.com/frapontillo/crowd-pulse-rem-stop-word-simple)
* [`frapontillo/crowd-pulse-infogram`](https://github.com/frapontillo/crowd-pulse-infogram)
* [`frapontillo/crowd-pulse-sentiment-sentit`](https://github.com/frapontillo/crowd-pulse-sentiment-sentit)
* [`frapontillo/crowd-pulse-sentiment-sentiwordnet`](https://github.com/frapontillo/crowd-pulse-sentiment-sentiwordnet)

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