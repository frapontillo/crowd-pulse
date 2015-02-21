TODO
====

- [ ] Add `geo-profile-consolidation` module to attempt a geolocation fix for profiles with null-location
- [ ] Add `tag` module to find tags for each message (4 pre-existing tagging systems + babelfy)
- [ ] Add `category` module to find Wikipedia categories for each tag (include stop-word handling)
- [ ] Edit `social` module to allow searching for messages by a string location (e.g. "Washington DC, USA")
- [ ] Add `social-reply` module to fetch and delay-fetch answers to messages (can only be done for Facebook)
- [ ] Implement a distributed architecture (via [akka.io](www.akka.io))
- [ ] Edit `social` module to enable profiling of users' graph connections