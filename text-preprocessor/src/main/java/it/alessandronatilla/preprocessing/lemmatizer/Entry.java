package it.alessandronatilla.preprocessing.lemmatizer;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
class Entry {
    public String token;
    public String posTag;

    public Entry(String posTag, String token) {
        this.posTag = posTag;
        this.token = token;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Entry{");
        sb.append("posTag='").append(posTag).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
