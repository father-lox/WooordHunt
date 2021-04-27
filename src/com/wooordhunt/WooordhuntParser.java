package com.wooordhunt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WooordhuntParser {
    public Word getWord(String word) throws IOException, ParseException {
        Document page = getPage(word);

        return new Word(
                getWord(page),
                getRank(page),
                getUsTranscription(page),
                getUkTranscription(page),
                getTranslations(page),
                getPhrases(page),
                getExamples(page));
    }

    private Document getPage(String word) throws IOException, ParseException {
        //TODO: Реализовать обработку неправильно ввделных слов
        //Document page = Jsoup.parse(new File("C:\\Projects\\Java\\WooordHunt\\files\\Increase перевод, произношение, транскрипция, примеры использования.htm"), "utf-8");

        Document page = Jsoup
                .connect(_BASE_URL +  word)
                .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13G34 MicroMessenger/6.5.7 NetType/4G Language/zh_CNNULL")
                .referrer("http://www.temp.com")
                .get();

        if (itemIsHas(page, _SELECTOR_ERROR)) {
            throw  new ParseException(page.selectFirst(_SELECTOR_ERROR).text(), 0);
        }

        if (itemIsHas(page, _SELECTOR_RANK + " a[href]")) {
            page = getPage(page.selectFirst(_SELECTOR_RANK + " a[href]").attr("href").split("/")[2]);
        }

        return page;
    }
    private String getWord(Document page) {
        Element wordElement = page.selectFirst("h1");
        return wordElement.ownText();
    }
    private int getRank(Document page) {
        Element rankElement = page.selectFirst(_SELECTOR_RANK);

        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(rankElement.ownText());
        StringBuilder numberStr = new StringBuilder();

        while (matcher.find()) {
            numberStr.append(matcher.group());
        }
        return Integer.parseInt(numberStr.toString());
    }
    private String getUsTranscription(Document page) {
        return page.selectFirst(_SELECTOR_US_TRANSCRIPTION).ownText().replaceAll("[ |]", "");
    }
    private String getUkTranscription(Document page) {
        return page.selectFirst(_SELECTOR_UK_TRANSCRIPTION).ownText().replaceAll("[ |]", "");
    }
    private String[] getTranslations(Document page) {
         return page.selectFirst(_SELECTOR_TRANSLATIONS).ownText().split(", ");
    }
    private ArrayList<Pair<String, String>> getPhrases(Document page) {
        String[] phrases = page.selectFirst(_SELECTOR_PHRASES).wholeText().trim().split("    ");

        if (phrases.length == 0) {
            return null;
        }

        ArrayList<Pair<String, String>> PairPhrases = new ArrayList<>();
        for (int i = 0; i < phrases.length; i++) {
            String[] temp = phrases[i].split(" — ");
            if  (temp.length == 2) {
                PairPhrases.add(new Pair<>(temp[0], temp[1]));
            }
        }
        return PairPhrases;
    }
    private ArrayList<Pair<String, String>> getExamples(Document page) {
        Elements examples = page.select(_SELECTOR_EXAMPLE);

        if (examples.size() == 0) {
            return null;
        }

        ArrayList<Pair<String, String>> PairExamples = new ArrayList<>();
        for (int i = 0; i < examples.size(); i++) {
            PairExamples.add(new Pair<>(examples.get(i).previousElementSibling().ownText().trim(), examples.get(i).ownText().trim()));
        }
        return PairExamples;
    }
    private boolean itemIsHas(Document page, String cssQuery) {
        return page.select(cssQuery).size() > 0;
    }

    //Service Variables
    private final String _BASE_URL = "https://wooordhunt.ru/word/";
    private final String _SELECTOR_RANK = "#wd_title #word_rank_box";
    private final String _SELECTOR_US_TRANSCRIPTION = "#wd_title #us_tr_sound .transcription";
    private final String _SELECTOR_UK_TRANSCRIPTION = "#wd_title #uk_tr_sound .transcription";
    private final String _SELECTOR_TRANSLATIONS = "#wd_content .t_inline_en";
    private final String _SELECTOR_PHRASES = "#wd_content .phrases";
    private final String _SELECTOR_EXAMPLE = "#wd_content .ex_t";
    private final String _SELECTOR_ERROR = "#word_not_found";
}