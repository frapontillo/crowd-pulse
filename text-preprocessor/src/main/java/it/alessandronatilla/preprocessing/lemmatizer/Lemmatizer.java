package it.alessandronatilla.preprocessing.lemmatizer;

import it.alessandronatilla.preprocessing.utils.serializer.YAMLSerializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class Lemmatizer {

    private String fname = "/lemma_dict.yaml";
    private MorphITDict dict;

    public Lemmatizer() {
        URL resourceURL = getClass().getResource(fname);
        Path resourcePath = null;
        BufferedReader reader = null;
        String input;

        try {
            resourcePath = Paths.get(resourceURL.toURI());
            reader = new BufferedReader(new FileReader(resourcePath.toString()));
            StringBuffer yaml = new StringBuffer();
            while ((input = reader.readLine()) != null) {
                yaml.append(input);
            }
            dict = YAMLSerializer.deserialize(yaml.toString());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String lemmatize(String token, String postag) {

        return dict.getLemma(new Entry(postag, token));
    }

}
