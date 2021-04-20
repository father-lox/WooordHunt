package com.database;

import com.wooordhunt.Pair;
import com.wooordhunt.Word;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseController {
    //TODO: Реализовать метод инициализации
    //TODO: Реализовать метод получения всех данных с ДБ
    public void addNewWordToDatabase(Word word) throws SQLException {
        if (!connection.isClosed()) {
            int wordId = 0;
            String insertSqlQuery = "INSERT INTO words " +
                    "(word, rank, us_transcription, uk_transcription, translations) "+
                    "values (LOWER(?), ?, ?, ?, ?);";
            String insertExampleSqlQuery = "INSERT INTO examples" +
                    "(wordId, eng_example, ru_example)" +
                    " values(?, ?, ?);";
            String insertPhraseSqlQuery = "INSERT INTO phrases " +
                    "(wordId, eng_phrase, ru_phrase) " +
                    "values(?, ?, ?);";

            //Добавление записи
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, word.getWord());
                preparedStatement.setInt(2, word.getRang());
                preparedStatement.setString(3, word.getUsTranscription());
                preparedStatement.setString(4, word.getUkTranscription());
                preparedStatement.setString(5, preparedTranslation(word.getTranlations()));
                preparedStatement.executeUpdate();
                wordId = preparedStatement.getGeneratedKeys().getInt(1);
            }
            catch (SQLException exception) {
                printErrorMessage(exception.getErrorCode(), exception.getSQLState(), exception.getMessage());
            }


            //Inserting phrases
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertPhraseSqlQuery)) {
                for (Pair<String, String> phrase: word.getPhrases()) {
                    preparedStatement.setInt(1, wordId);
                    preparedStatement.setString(2, phrase.getEngValue());
                    preparedStatement.setString(3, phrase.getRuValue());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
            catch (SQLException exception) {
                printErrorMessage(exception.getErrorCode(), exception.getSQLState(), exception.getMessage());
            }

            //Inserting examples
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertExampleSqlQuery)) {
                for (Pair<String, String> example: word.getExamples()) {
                    preparedStatement.setInt(1, wordId);
                    preparedStatement.setString(2, example.getEngValue());
                    preparedStatement.setString(3, example.getRuValue());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
            catch (SQLException exception) {
                printErrorMessage(exception.getErrorCode(), exception.getSQLState(), exception.getMessage());
            }
        }
    }

    public Word getWordFromDatabase(String searchingWord) throws SQLException {
            int wordId = 0, rank = -1;
            String word = "", usTranscription = "", ukTranscription = "";
            String[] translations = null;

            String sqlSelectWord = "SELECT * FROM words WHERE word = LOWER('" + searchingWord +"')";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlSelectWord);
            while (resultSet.next()) {
                wordId = resultSet.getInt("id");
                word = resultSet.getString("word");
                rank = resultSet.getInt("rank");
                usTranscription = resultSet.getString("us_transcription");
                ukTranscription = resultSet.getString("uk_transcription");
                translations = resultSet.getString("translations").split(", ");
            }

            ArrayList<Pair<String, String>> phrases = new ArrayList<>();
            String sqlSelectPhrases = "SELECT eng_phrase, ru_phrase FROM phrases WHERE wordId = " + wordId;
            resultSet = statement.executeQuery(sqlSelectPhrases);
            while (resultSet.next()) {
                phrases.add(new Pair<>(resultSet.getString("eng_phrase"), resultSet.getString("ru_phrase")));
            }

            ArrayList<Pair<String, String>> examples = new ArrayList<>();
            String sqlSelectExamples = "SELECT eng_example, ru_example FROM examples WHERE wordId = " + wordId;
            resultSet = statement.executeQuery(sqlSelectExamples);
            while (resultSet.next()) {
                examples.add(new Pair<>(resultSet.getString("eng_example"), resultSet.getString("ru_example")));
            }

            if (!(!word.equals("") && rank != -1 && !usTranscription.equals("") && !ukTranscription.equals("") && translations.length != 0)) {
                return null;
            }
            if (phrases.size() > 0 && examples.size() > 0) {
                return new Word(word, rank, usTranscription, ukTranscription, translations, phrases, examples);
            }

            Word result = new Word(word, rank, usTranscription, ukTranscription, translations);
            if (examples.size() > 0) {
                result.setExamples(examples);
            }
            else {
                result.setPhrases(phrases);
            }
            return result;
    }

    public void openConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:db\\dictionary v2.0.db");
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }


    private String preparedTranslation(String[] strings) {
        StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i < strings.length - 1) {
                resultString.append(strings[i]).append(", ");
            }
            else {
                resultString.append(strings[i]);
            }
        }
        return resultString.toString();
    }

    private void printErrorMessage(int errorCode, String state, String message) {
        System.out.println("ERROR ("+ errorCode +"): \n\t" +
                "SQLState" + state + "\n\t" +
                "Message" + message + "\n\t");
    }

    /*private int getCountRows(Connection connection, String columnName, String tableName, String where) {
        int countRows = -1;
        String sqlRequest = "SELECT COUNT("+ columnName +") FROM " + tableName + " WHERE " + where;
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(sqlRequest);
            resultSet.next();
            countRows = resultSet.getInt(1);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }

        return countRows;
    }*/

    private Connection connection;
}
