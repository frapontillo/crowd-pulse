package it.alessandronatilla.preprocessing.utils.serializer;

import it.alessandronatilla.preprocessing.lemmatizer.mongo.LemmaKey;
import it.alessandronatilla.preprocessing.lemmatizer.mongo.MorphITDict;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class YAMLSerializer {

    public static String serialize(MorphITDict dict) {
        Constructor constructor = new Constructor(MorphITDict.class);
        TypeDescription typeDescription = new TypeDescription(
                MorphITDict.class);
        typeDescription.putMapPropertyType("dict", LemmaKey.class, String.class);
        constructor.addTypeDescription(typeDescription);

        Yaml yaml = new Yaml(constructor);
        return yaml.dump(dict);
    }

    public static MorphITDict deserialize(String yaml) {
        Constructor constructor = new Constructor(MorphITDict.class);
        TypeDescription typeDescription = new TypeDescription(
                MorphITDict.class);
        typeDescription.putMapPropertyType("dict", LemmaKey.class, String.class);
        constructor.addTypeDescription(typeDescription);

        Yaml yml = new Yaml(constructor);
        MorphITDict dict = (MorphITDict) yml.load(yaml);

        return dict;

    }
}
