package it.alessandronatilla.preprocessing.lemmatizer;

import com.mongodb.MongoClient;
import it.alessandronatilla.preprocessing.lemmatizer.exceptions.EmptyEntryException;
import net.frakbot.crowdpulse.data.entity.Lemma;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
class MorphItCreator {

    private String fname = "/morph-it_048.txt";
    MorphITDict dict = new MorphITDict();

    public MorphItCreator() {
        initialize();
    }

    private void initialize() {
        URL resourceURL = getClass().getResource(fname);
        Path resourcePath = null;
        BufferedReader reader = null;
        Charset charset = Charset.forName("ISO-8859-1");
        int i = 1;

        try {
            resourcePath = Paths.get(resourceURL.toURI());
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(resourcePath.toString()), charset));
            String input;

            Morphia morphia = new Morphia();
            Datastore datastore = morphia.createDatastore(new MongoClient(), "test");
            morphia.map(Lemma.class);
            datastore.ensureIndexes();



            while ((input = reader.readLine()) != null) {
                if (input == "") break;
//                System.out.println(input);
//                System.out.println(i);
                String res[] = input.split("\t");
                String postag = res[2].split(":")[0];
                String token = res[0].toLowerCase();
//                if(token.startsWith("abbarbicher")) {
//                    System.out.println("KTM");
//                }
                String lemma_s = res[1].toLowerCase();

                Lemma lemma = new Lemma();
                lemma.setPosTag(postag);
                lemma.setToken(token);
                lemma.setLemma(lemma_s);

                datastore.save(lemma);

                i++;
            }

//            try {
//                FileOutputStream fos = new FileOutputStream("dict_lemma.ser");
//                ObjectOutputStream oos = new ObjectOutputStream(fos);
//                oos.writeObject(dict);
//                oos.close();
//            } catch (IOException ioe) {
//                System.out.println("Errore: " + ioe.toString());
//            }

//            String yaml = YAMLSerializer.serialize(dict);
//            Writer out = new BufferedWriter(new OutputStreamWriter(
//                    new FileOutputStream("lemma_dict.yaml"), "UTF-8"));
//            try {
//                out.write(yaml);
//            } finally {
//                out.close();
//            }

        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Line err:" + i);
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

    private void addLemma(LemmaKey lemmaKey, String lemma) {
        if (lemmaKey == null || lemmaKey.getPosTag() == null || lemmaKey.getPosTag().length() == 0
                || lemmaKey.getToken() == null || lemmaKey.getToken().length() == 0)
            throw new EmptyEntryException(lemmaKey.toString());

        dict.getDict().put(lemmaKey, lemma);

    }
}
