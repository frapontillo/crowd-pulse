package it.alessandronatilla.preprocessing.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class TanlMorphTagsetConvertion {

    private static Map<String, String> tagset = new HashMap<String, String>();

    static {
        tagset.put("A", "ADJ");
        tagset.put("B", "B");
        tagset.put("C", "C");
        tagset.put("D", "D");
        tagset.put("E", "E");
        tagset.put("N", "DET-NUM-CARD");
        tagset.put("P", "PRO");
        tagset.put("R", "ART");
        tagset.put("S", "NOUN");
        tagset.put("V", "VER");
        tagset.put("I", "INT");
//        tagset.put("MOD", "VER");
    }

    public static String tanl_to_morph_tagset(String tag) {
        if (tag.startsWith("A")) return tagset.get("A");
        if (tag.startsWith("B")) return tagset.get("B");
        if (tag.startsWith("C")) return tagset.get("C");
        if (tag.startsWith("D")) return tagset.get("D");
        if (tag.startsWith("E")) return tagset.get("E");
        if (tag.startsWith("N")) return tagset.get("N");
        if (tag.startsWith("P")) return tagset.get("P");
        if (tag.startsWith("R")) return tagset.get("R");
        if (tag.startsWith("S")) return tagset.get("S");
        if (tag.startsWith("V")) return tagset.get("V");
        if (tag.startsWith("I")) return tagset.get("I");
//        if (tag.startsWith("MOD")) return tagset.get("I");


        return null;
    }
}
