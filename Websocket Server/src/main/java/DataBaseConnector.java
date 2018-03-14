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
        if(location != null) {
            if (checkIfLocationIsAlreadySet(idToken)) {
                String query = "UPDATE location SET longitude = " + location.getLongitude() + " , langitude = " + location.getLangitude() + " WHERE IdToken = " + idToken;
                sendQuery(query);
            } else {
                String query = "Insert into location Value (" + idToken + ", " + location.getLongitude() + ", " + location.getLangitude() + ")";
                sendQuery(query);
            }
        }
    }

    public List<UserInfo> getAllLocations() {
        List<UserInfo> locations = new ArrayList<>();

        String query = "Select * from location join userinfo";

        ResultSet s = sendQuery(query);
        try {
            while(s.next()) {
                String idToken = s.getString("IdToken");
                String nickname = s.getString("NickName");
                String firstName = s.getString("FirstName");
                String lastName = s.getString("LastName");
                String imagePath = s.getString("ImagePath");
                double langitude = s.getDouble("Langitude");
                double longitude = s.getDouble("longitude");

                locations.add(new UserInfo(idToken, nickname, firstName, lastName, imagePath, longitude, langitude));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }

    public void deleteLocation(String idToken) {
        String query = "DELETE from location WHERE idToken = " + idToken;

        sendQuery(query);
    }

    private boolean checkIfLocationIsAlreadySet(String idToken) {
        String query = "SELECT count(*) from location Where IdToken = " + idToken;

        ResultSet resultSet = sendQuery(query);
        try {
            resultSet.next();
            int exist = resultSet.getInt("count(*)");
            if(exist > 0) {
                return true;
            }
            resultSet.close();
        }
        catch (SQLException e) {
            e.getSQLState();
        }
        return false;
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
