import Message.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataBaseConnector {
    private Connection dbConnection = null;

    public DataBaseConnector() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            dbConnection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/location_aware_database?user=root");
        } catch (SQLException e) {
            System.out.println(e.getSQLState());
            System.out.println(e.getErrorCode());
        }
        catch (ClassNotFoundException e) {
            e.getCause();
        }
    }

    public boolean checkIdToken(String idToken) {
        boolean returnValue = false;
        String query = "SELECT COUNT(IdToken) from authentication WHERE IdToken = " + idToken;

        ResultSet result = sendQuery(query);
        try {
            result.next();
            int totalFound = result.getInt("COUNT(IdToken)");
            if(totalFound == 0) {
                returnValue = false;
            }
            else {
                returnValue = true;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getSQLState());
        }

        return returnValue;
    }

    public List<Message> getAllUnreadMessages(String idToken) {
        List<Message> allMessages = new ArrayList<>();

        String query = "SELECT * from chatmessage WHERE ToPerson = " + idToken + " AND ReadMessage = 0";

        ResultSet result = sendQuery(query);
        try {
            while(result.next()) {
                String chatID = result.getString("ChatID");
                String to = result.getString("To");
                String from = result.getString("From");
                String content = result.getString("Content");
                LocalDateTime time = result.getTimestamp("MessageTime").toLocalDateTime();

                allMessages.add(new Message(chatID, to, from, content, time));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getSQLState());
        }
        return allMessages;
    }

    public void updateGPS(String idToken, Location location) {

    }

    private ResultSet sendQuery(String query) {
        try {
            Statement statement = dbConnection.createStatement();
            statement.execute(query);
            return statement.getResultSet();
        }
        catch (SQLException e) {
            System.out.println(e.getSQLState());
        }
        return null;
    }

    public void closeConnection() {
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                System.out.println(e.getErrorCode());
            }
        }
    }
}
