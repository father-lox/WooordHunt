package com.wooordhunt;

import java.util.ArrayList;

public class Word {
    public Word(String word, int rank, String usTranscription, String ukTranscription, String[] translations, ArrayList<Pair<String, String>> phrases, ArrayList<Pair<String, String>> examples) {
        _word = word;
        _rank = rank;
        _usTranscription = usTranscription;
        _ukTranscription = ukTranscription;
        _translations = translations;
        _phrases = phrases;
        _phrasesIsSet = true;
        _examples = examples;
        _examplesIsSet = true;
    }
    public Word(String word, int rank, String usTranscription, String ukTranscription, String[] translations) {
        _word = word;
        _rank = rank;
        _usTranscription = usTranscription;
        _ukTranscription = ukTranscription;
        _translations = translations;
        _phrases = null;
        _examples = null;
    }

    public boolean setPhrases(ArrayList<Pair<String, String>> phrases) {
        if (_phrases != null)
            return false;
        _phrases = phrases;
        _phrasesIsSet = true;
        return true;
    }
    public boolean setExamples(ArrayList<Pair<String, String>> examples) {
        if (_examples != null)
            return false;
        _examples = examples;
        _examplesIsSet = true;
        return true;
    }
    public String getWord() {
        return _word;
    }
    public int getRang() {
        return _rank;
    }
    public String getUsTranscription() {
        return _usTranscription;
    }
    public String getUkTranscription() {
        return _ukTranscription;
    }
    public String[] getTranlations() {
        return _translations;
    }
    public String getTranlations(String separator) {
        StringBuilder concatenationString = new StringBuilder();
        for (String translate : _translations) {
            concatenationString.append(translate).append(separator);
        }
        return concatenationString.toString();
    }
    public ArrayList<Pair<String, String>> getPhrases() {
        return _phrases;
    }
    public ArrayList<Pair<String, String>> getExamples() {
        return _examples;
    }
    public String getExamples(String concatenation, String separator) {
        StringBuilder examples = new StringBuilder();
        for (Pair<String, String> example :
                _examples) {
            examples.append(example.getEngValue()).append(concatenation).append(example.getRuValue()).append(separator);
        }
        return examples.toString();
    }
    public boolean phrasesIsSet() {return _phrasesIsSet;}
    public boolean examplesIsSet() {return _examplesIsSet;}

    private final String _word;
    private final int _rank;
    private final String _usTranscription;
    private final String _ukTranscription;
    private final String[] _translations;
    private boolean _phrasesIsSet = false;
    private boolean _examplesIsSet = false;
    private ArrayList<Pair<String, String>> _phrases;
    private ArrayList<Pair<String, String>> _examples;
}