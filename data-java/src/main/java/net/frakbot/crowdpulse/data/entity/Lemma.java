package net.frakbot.crowdpulse.data.entity;


/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class Lemma extends Entity {
    private String token;
    private String lemma;
    private String posTag;


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
}
