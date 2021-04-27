package com.wooordhunt;

import com.database.DatabaseController;
import com.excel.ExcelManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleInterface {

    public void startProgram(String[] args) {
        try {
            databaseController.openConnection();
        }
        catch (SQLException | ClassNotFoundException exception) {
            printErrorInfo(exception);
            boolean isCorrectAnswer;
            do {
                isCorrectAnswer = true;
                System.out.println("Do you wont to continue without data base (yes/no)?");
                String choice = new Scanner(System.in).nextLine().toLowerCase();
                if (choice.equals("yes")) {
                    useDataBase = false;
                }
                else if (choice.equals("no")) {
                    closeProgram();
                }
                else {
                    System.out.println("Answer isn't correct. Try again");
                    isCorrectAnswer = false;
                }
            } while (!isCorrectAnswer);
        }



        if (args.length == 0) {
            while (true) {
                System.out.print("Enter the word(s) or command(s): ");
                String enteredWord = new Scanner(System.in).nextLine();
                String[] wordsAndCommands = enteredWord.split("\\s+");
                processWords(wordsAndCommands);
            }
        }

        processWords(args);
    }

    public void printInfo(Word word, int number) {
        System.out.println("WORD: " + number + " " + word.getWord() + " (" + word.getRang() + ")");
        System.out.println("Английская транскрипция: " + word.getUsTranscription());
        System.out.println("Британская транскрипция: " + word.getUkTranscription());
        System.out.println("Возможные переводы слова:");
        for (String translation : word.getTranlations()) {
            System.out.println('\t' + translation);
        }

        System.out.println("Примеры:");
        if (word.examplesIsSet()){
            for (Pair<String, String> example : word.getExamples()) {
                System.out.println('\t' + example.getEngValue() + "\n\t\t" + example.getRuValue());
            }
        }
        System.out.println("Словосочетания:");
        if (word.phrasesIsSet()) {
            for (Pair<String, String> phrase : word.getPhrases()) {
                System.out.println('\t' + phrase.getEngValue() + "\n\t\t" + phrase.getRuValue());
            }
        }
        System.out.println("------------------------------------------------------");
    }
    private Word processWord(String wordStr, WooordhuntParser WHP) throws SQLException, IOException, ParseException {
        Word word = null;

        if (useDataBase) {
            word = databaseController.getWordFromDatabase(wordStr);
        }
        if (word == null) {
            word = WHP.getWord(wordStr);
            if (useDataBase) {
                databaseController.addNewWordToDatabase(word);
            }
        }
        return word;
    }
    private void processWords(String[] wordsAndCommands) {
        Pattern pattern = Pattern.compile("^(.?[a-zA-Z]+)$");

        for (int i = 0; i < wordsAndCommands.length; i++) {
            if (!pattern.matcher(wordsAndCommands[i]).find()) {
                System.out.println("Word or command isn't correct");
                continue;
            }

            switch (wordsAndCommands[i].toLowerCase()) {
                case ".exit":
                    closeProgram();
                    break;
                case ".export":
                    export();
                    break;
                case ".help":
                    showHelp();
                    break;
                default:
                    try {
                        Word word = processWord(wordsAndCommands[i], WHP);
                        printInfo(word, i + 1);
                    } catch (SQLException | IOException | ParseException exception) {
                        System.out.println(wordsAndCommands[i].toUpperCase());
                        printErrorInfo(exception);
                        continue;
                    }
                    break;
            }
        }
    }

    private void showHelp() {
        System.out.println(".help - show all commands");
        System.out.println(".exit - close the programme");
        System.out.println(".export path - export all words from data base in Excel file to path");
    }
    private void export() {
        System.out.print("Enter the file name [export]: ");
        String fileName = new Scanner(System.in).nextLine();
        if (fileName.equals("")) {
            fileName = "export";
        }

        System.out.print("Enter full path to save the file ["+ System.getProperty("user.dir") +"]: ");
        String filePath = new Scanner(System.in).nextLine();
        if (filePath.equals("")) {
            filePath = System.getProperty("user.dir");
        }

        System.out.print("Enter the words: ");
        String enteredWord = new Scanner(System.in).nextLine();
        String[] strWords = enteredWord.split("\\s+");

        Word[] words = new Word[strWords.length];
        for (int i = 0; i < strWords.length; i++) {
            System.out.print("WORD #" + (i + 1) + " " + strWords[i]);
            try {
                words[i] = processWord(strWords[i], WHP);
                System.out.println("successful");
            } catch (SQLException | IOException | ParseException exception) {
                printErrorInfo(exception);
            }
        }

        ExcelManager excelManager = new ExcelManager(filePath, fileName);
        excelManager.exportWords(words);
    }

    private void closeProgram() {
        System.exit(0);
    }
    private void printErrorInfo(Exception exception) {
        System.out.println("Error was occurred:".toUpperCase());
        System.out.println("\tdescription:" + exception.getMessage());
    }

    private boolean useDataBase = true;
    private final DatabaseController databaseController = new DatabaseController();
    private final WooordhuntParser WHP = new WooordhuntParser();
}
