package net.frakbot.crowdpulse.playground.graph;

import com.google.gson.Gson;

import java.io.*;

/**
 * Graph configuration utilities.
 *
 * @author Francesco Pontillo
 */
public class GraphUtil {

    /**
     * Read a {@link Graph} from a file.
     *
     * @param filePath The path of the file.
     * @return The {@link Graph} read from the file.
     * @throws FileNotFoundException if the input path doesn't match any file on the file system.
     */
    public static Graph readGraph(String filePath) throws FileNotFoundException {
        Gson gson = new Gson();
        File file = new File(filePath);
        FileReader fileReader = new FileReader(file);
        return gson.fromJson(fileReader, Graph.class).buildGraph();
    }

    /**
     * Read a {@link Graph} from a file among the resources in the classpath.
     *
     * @param resourceName       The name of the resource file.
     * @param resourceClassOwner The {@link Class} in the same classpath as the resource.
     * @return The {@link Graph} read from the resource file.
     */
    public static Graph readGraph(String resourceName, Class resourceClassOwner) {
        Gson gson = new Gson();
        InputStream configInput = resourceClassOwner.getClassLoader().getResourceAsStream(resourceName);
        InputStreamReader inputStreamReader = new InputStreamReader(configInput);
        return gson.fromJson(inputStreamReader, Graph.class).buildGraph();
    }
}
