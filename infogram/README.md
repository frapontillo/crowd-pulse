infogram
========

The `infogram` plugin needs a `infogram.properties` file in the class loader accessible resources directory.
The file must contain the following properties:

- `infogram.apikey` is the API key for your Infogram account
- `infogram.secret` is your secret key for your Infogram account

The configuration for the plugin can specify a `path` where the generated infographs must be saved as PNGs. If no `path`
is set, the files will be saved into the system temporary directory.