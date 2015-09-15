crowd-pulse
===========

Crowd Pulse: social extraction and analysis system

## Flow

Crowd Pulse uses an event/flow architecture based on RxJava and several extraction and processing tasks.

Every task **must** inherit from `IPlugin<Input, Output, IPluginConfig>` where:

- `Input` is the class of the input `Observable` the task has to process
- `Output` is the class of the output `Observable` that the task returns
- `Config` is the (optional) class for the configuration object that will be passed to the plugin

Here is a complete Crowd Pulse flow:

1. `IPlugin<Void, Message, ExtractionParameters>` takes care of extracting messages according to some configuration
1. `IPlugin<Message, Message, VoidConfig>` extracts all the replies for the input messages
1. `IPlugin<Message, Profile, VoidConfig>` extracts all the profiles for the input messages
1. `IPlugin<Profile, Profile, VoidConfig>` extracts all connections for the input profiles
1. `IPlugin<Profile, Profile, VoidConfig>` fixes the geolocation for every extracted profile
1. `IPlugin<Object, Message, VoidConfig>` waits for some given streams to complete and then emits another stream at once
1. `IPlugin<Message, Message, VoidConfig>` fixes the geolocation for messages according to the info contained in authors' profiles
1. `IPlugin<Message, Message, VoidConfig>` detects and sets the language for every message
1. `IPlugin<Message, Message, VoidConfig>` tags every message in the stream
1. `IPlugin<Message, Message, VoidConfig>` categorizes all tags for each message in the stream
1. `IPlugin<Message, Message, VoidConfig>` tokenizes every message in the stream
1. `IPlugin<Message, Message, VoidConfig>` sets some tokens as stop words for every message in the stream
1. `IPlugin<Message, Message, VoidConfig>` lemmatizes every token for each message in the stream
1. `IPlugin<Message, Message, VoidConfig>` tags every message token with a Part of Speech (POS) tag
1. `IPlugin<Message, Message, VoidConfig>` simplifies every POS tag into simpler ones
1. `IPlugin<Message, Message, VoidConfig>` performs sentiment analysis on every message in the stream

## Module descriptions

- `common-util`: shared Java/RxJava utility classes
- `data-java`: data access layer for Java modules
- `data-node`: data access layer for NodeJS modules
- `playground-cli`: Command Line Interface containing a CLI to test features
- `social`: generic social network module to handle message extraction and user profiling
  - `social-facebook`: specific implementation of `social` for Facebook
  - `social-twitter`: specific implementation of `social` for Twitter
- `fix-geo-profile`: fixes profile coordinates (lat/long) by forward geocoding
  - `fix-geo-profile-google-maps`, concrete implementation based on Google APIs
- `fix-geo-message`: fixes message coordinates (lat/long)
  - `fix-geo-message-from-profile`: fixes message coordinates by assuming they're the same as the user profile
- `detect-language`: detects the language of the extracted languages
  - `detect-language-optimaize`: uses [optimaize/language-detector](https://github.com/optimaize/language-detector)
- `tag`: finds tags for a message
  - `tag-babelfy`: uses the Babelfy API
  - `tag-me`: uses the TagMe API
  - `tag-open-calais`: uses the OpenCalais API
  - `tag-wikipedia-miner`: uses WikipediaMiner
- `categorize`: finds multiple categories for each tag
  - `categorize-wikipedia`: uses the Wikipedia Web Service to find categories
- `tokenize`: splits messages into tokens
  - `tokenize-open-nlp`: uses Apache OpenNLP to achieve multi-language tokenization
- `pos-tag`: assigns to every token in a message a Part-Of-Speech tag
  - `pos-tag-open-nlp`: uses Apache OpenNLP to achieve multi-language POS-tagging
- `pos-tag-simple`: simplifies pre-existing POS tags from language-specific tagsets to a generic one
  - `pos-tag-simple-multi`: selects the most appropriate language-specific implementation of simple POS tagging
  - `pos-tag-simple-en`: simple POS tagger for English, uses the Penn Treebank POS tags
  - `pos-tag-simple-it`: simple POS tagger for Italian, uses TANL POS tags
- `rem-stop-word`: marks some tokens as stop words 
  - `rem-stop-word-simple`: uses stop-word files containing lists of stop words for each language
- `lemmatize`: lemmatizes every token in a message
  - `lemmatize-multi`: uses a default/language based strategy to select the most proper lemmatizer between 
  those available
  - `lemmatize-stanford-corenlp`: uses Stanford CoreNLP to lemmatize tokens (English only, for now)
  - `lemmatize-morphit`: uses the MorphIT dictionary to match a word and its POS with a certain lemma (Italian only)
- `sentiment`: runs a sentiment analysis algorithm for messages
  - `sentiment-sentit`: uses the SentIt Web Service to perform sentiment analysis
  - `sentiment-sentiwordnet`: uses a combination of MultiWordNet and SentiWordNet to perform sentiment analysis on 
  Tokens and Messages
- `infogram`: generates and fetches Infogram word clouds for tags, categories, lemmas
- `index-uniba`: sends extracted messages to the Uniba indexing service by Natilla and Impedovo
  
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