package it.alessandronatilla.preprocessing.lemmatizer;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class MorphRow {

    private String token;
    private String lemma;
    private String posTag;

    public MorphRow(String lemma, String posTag, String token) {
        this.lemma = lemma;
        this.posTag = posTag;
        this.token = token;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MorphRow{");
        sb.append("lemma='").append(lemma).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append(", posTag='").append(posTag).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MorphRow)) return false;

        MorphRow morphRow = (MorphRow) o;

        if (!lemma.equals(morphRow.lemma)) return false;
        if (!posTag.equals(morphRow.posTag)) return false;
        if (!token.equals(morphRow.token)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = token.hashCode();
        result = 31 * result + lemma.hashCode();
        result = 31 * result + posTag.hashCode();
        return result;
    }
}
