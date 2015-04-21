TODO
====

- [X] Add `fix-geo-profile` module to attempt a geolocation fix for profiles with null-location
- [X] Add `fix-geo-message` module to attempt a geolocation fix for messages with null-location
- [X] Add `detect-language` module to detect message language
- [X] Add `tag` module to find tags for each message (WikipediaMiner, TagMe, OpenCalais, Babelfy)
- [X] Add `categorize-wikipedia` module to find Wikipedia categories for each tag
- [ ] Edit `social` module to allow searching for messages by a string location (e.g. "Washington DC, USA")
- [ ] Add `social-reply` module to fetch and delay-fetch answers to messages (can only be done for Facebook)
- [ ] Implement a distributed architecture (via [akka.io](www.akka.io))
- [ ] Edit `social` module to enable profiling of users' graph connections