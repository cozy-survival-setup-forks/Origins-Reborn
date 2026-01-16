# TODO
This folder will contain sample .yml files for translations

The plugin will load the file names into memory upon loading, but not save the files in the plugin folder

This means that when the plugin updates, the yml files will update too

The translations.yml file will have a setting at the top named 'default_lang' which is either "none" or a language e.g. "en_gb"

This means when a new translation is registered, it will instead pull from the specified file, if the file is present, otherwise it will use the default value specified in the code

So when using registerTranslation(key, default), the translations.yml file will use the value in the 'default' variable if "default_lang" is unspecified, if it is specified it will use the contents of translations/{lang}.yml (from WITHIN the plugin)

The default translation yml files inside the plugin will have "default_lang" set to their file names at the top

Then, add a /origin translate <lang> command (that will require you to then run /origin translate confirm after warning you it will erase any existing changes they have made, and to back them up if they need to keep them)

This command will overwrite the contents of the translations.yml file with the contents of an internal translation, e.g. "en_gb.yml" will overwrite it
