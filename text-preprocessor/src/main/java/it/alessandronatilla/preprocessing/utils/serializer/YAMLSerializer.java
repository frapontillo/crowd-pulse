package it.alessandronatilla.preprocessing.utils.serializer;

import it.alessandronatilla.preprocessing.lemmatizer.MorphITDict;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class YAMLSerializer {

    public static String serialize(MorphITDict dict) {

        Yaml yaml = new Yaml();
        return yaml.dump(dict);
    }

    public static MorphITDict deserialize(String yaml) {
        Constructor constructor = new Constructor(MorphITDict.class);
        TypeDescription typeDescription = new TypeDescription(
                MorphITDict.class);
        constructor.addTypeDescription(typeDescription);
        Yaml yml = new Yaml(constructor);
        MorphITDict dict = (MorphITDict) yml.load(yaml);

        return dict;

    }
}
