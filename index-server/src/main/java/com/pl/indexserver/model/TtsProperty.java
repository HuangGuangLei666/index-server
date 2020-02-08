package com.pl.indexserver.model;

public class TtsProperty {

    private String pronunciationPeople;

    public TtsProperty(String pronunciationPeople) {
        this.pronunciationPeople = pronunciationPeople;
    }

    public String getPronunciationPeople() {
        return pronunciationPeople;
    }

    public void setPronunciationPeople(String pronunciationPeople) {
        this.pronunciationPeople = pronunciationPeople;
    }
}
