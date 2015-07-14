sentiment-sentiwordnet
======================

The `sentiment-sentiwordnet` plugin uses both MultiWordNet and SentiWordNet
to provide a sentiment value for messages. Therefore, you need a few file in
the class loader accessible resources directory:

- `LANGUAGE_index` [MultiWordNet](http://multiwordnet.fbk.eu/english/home.php)
files, where `LANGUAGE` is the two-characters code for the language you want 
to support (you can fetch English and Italian indexes
[here](https://github.com/frapontillo/multiwordnet-simple)).
- `sentiwordnet` is the [SentiWordNet](http://sentiwordnet.isti.cnr.it/) file,
containing mappings from WordNet synsets to sentiment values.
