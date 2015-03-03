package it.alessandronatilla.preprocessing.lemmatizer;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class LemmaKey {
    private String token;
    private String posTag;

    public LemmaKey() {
    }

    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LemmaKey(String posTag, String token) {
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
