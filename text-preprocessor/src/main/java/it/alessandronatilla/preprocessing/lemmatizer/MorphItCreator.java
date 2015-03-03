package it.alessandronatilla.preprocessing.lemmatizer;

import it.alessandronatilla.preprocessing.utils.serializer.YAMLSerializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
class MorphItCreator {

    private String fname = "/morph-it_048.txt";

    public MorphItCreator() {
        initialize();
    }

    private void initialize() {
        URL resourceURL = getClass().getResource(fname);
        Path resourcePath = null;
        BufferedReader reader = null;
        MorphITDict dict = new MorphITDict();

        try {
            resourcePath = Paths.get(resourceURL.toURI());
            reader = new BufferedReader(new FileReader(resourcePath.toString()));
            String input;

//            int i = 1;
            while ((input = reader.readLine()) != null) {
                if (input == "") break;
//                System.out.println(input);
//                System.out.println(i);
                String res[] = input.split("\t");
                String postag = res[2].split(":")[0];
                dict.addLemma(new LemmaKey(postag, res[0].toLowerCase()), res[1].toLowerCase());
//                i++;
            }

            String yaml = YAMLSerializer.serialize(dict);
            FileWriter fileWriter = new FileWriter("lemma_dict.yaml");
            fileWriter.write(yaml);
            fileWriter.flush();
            fileWriter.close();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
