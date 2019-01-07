package Connection;

import Message.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBaseConnector {
    private Connection dbConnection = null;
    private Statement statement = null;

    public DataBaseConnector() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            dbConnection = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/location_aware_database?user=root");
        } catch (SQLException e) {
            System.out.println(e.getSQLState());
            System.out.println(e.getErrorCode());
        }
        catch (ClassNotFoundException e) {
            System.out.println(e.getCause());
        }
    }

    public String newUser(LoginInfo login, UserInfo user) {
        String userInfoQuery, loginInfoQuery, returnValue;
        if(!checkUsername(login.getUserName())) {
            String idToken;
            do {
                idToken = LoginInfo.generateIdToken(login.getUserName());
            } while(checkIdToken(idToken));
            userInfoQuery   = "INSERT INTO userinfo VALUES(\"" + idToken + "\", \"" + user.getNickName() + "\", \"" + user.getFirstName() + "\", \"" + user.getLastName() + "\", null)";
            loginInfoQuery  = "INSERT INTO authentication VALUES(\"" + login.getUserName() + "\", \"" + login.getPassWord() + "\", \"" + idToken +  "\")";

            try {
                sendQuery(userInfoQuery);
                statement.close();
                sendQuery(loginInfoQuery);
                statement.close();
            }
            catch (SQLException e) {
                e.getSQLState();
            }
            returnValue = idToken;
        }
        else {
            returnValue = null;
        }
        return returnValue;
    }

    public boolean checkIdToken(String idToken) {
        String query = "SELECT COUNT(IdToken) from authentication WHERE IdToken = \"" + idToken + "\"";
        boolean returnValue = false;

        ResultSet result = sendQuery(query);
        if(result != null) {
            try {
                result.next();
                boolean keyFound = result.getBoolean("COUNT(IdToken)");
                returnValue = keyFound;
                result.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getSQLState());
            }
        }
        return returnValue;
    }

    public List<Message> getAllUnreadMessages(String idToken) {
        String query = "SELECT * from chatmessage WHERE ToPerson = \"" + idToken + "\" AND ReadMessage = false";
        List<Message> allMessages = new ArrayList<>();

        ResultSet result = sendQuery(query);
        if(result != null) {
            try {
                while (result.next()) {
                    String chatID = result.getString("ChatID");
                    String to = result.getString("ToPerson");
                    String from = result.getString("FromPerson");
                    String content = result.getString("Content");
                    LocalDateTime time = result.getTimestamp("MessageDateTime").toLocalDateTime();

                    allMessages.add(new Message(chatID, from, to, content, time));
                }
                result.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getSQLState());
            }
        }

        for(Message unreadMessage : allMessages) {
            setMessageAsRead(unreadMessage);
        }

        return allMessages;
    }

    public void updateGPS(String idToken, Location location) {
        String query;
        if(location != null) {
            if (checkIfLocationIsAlreadySet(idToken)) {
                query = "UPDATE location SET latitude = " + location.getLatitude() + " , longitude = " + location.getLongitude() + " WHERE IdToken = \"" + idToken + "\"";
            } else {
                query = "INSERT INTO location VALUE (\""+ idToken + "\", " + location.getLatitude() + ", " + location.getLongitude() + ")";
            }

            try {
                sendQuery(query);
                statement.close();
            }
            catch (SQLException e) {
                System.out.println(e.getSQLState());
            }
        }
    }

    public List<UserInfo> getAllLocations() {
        String query = "SELECT * FROM location INNER JOIN Userinfo ON userinfo.IdToken = location.IdToken";
        List<UserInfo> locations = new ArrayList<>();

        ResultSet result = sendQuery(query);
        if(result != null) {
            try {
                while (result.next()) {
                    String idToken      = result.getString("IdToken");
                    String nickname     = result.getString("NickName");
                    String firstName    = result.getString("FirstName");
                    String lastName     = result.getString("LastName");
                    String imagePath    = result.getString("ImagePath");
                    double latitude     = result.getDouble("latitude");
                    double longitude    = result.getDouble("longitude");

                    locations.add(new UserInfo(idToken, nickname, firstName, lastName, imagePath, latitude, longitude));
                }
                result.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return locations;
    }

    public String startChat(String idToken, HashMap<String, Object> json) {
        String dateWithTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"));
        String chatIdHash = generateChatHash(idToken, (String) json.get("toId"));
        String query = "INSERT INTO chat VALUE(\"" + chatIdHash + "\", \"" + idToken + "\", \"" + json.get("toId") + "\", \"" + dateWithTime + "\")";

        try {
            sendQuery(query);
            statement.close();
        }
        catch(SQLException e) {
            e.getSQLState();
        }

        return chatIdHash;
    }

    public String findIdToken(LoginInfo login) {
        String userToken = null;
        String query = "SELECT IdToken FROM authentication WHERE (Username = \"" + login.getUserName() + "\" AND Password = \"" + login.getPassWord() + "\")";

        ResultSet result = sendQuery(query);
        if(result != null) {
            try {
                if (result.next()) {
                    userToken = result.getString("IdToken");
                }
            }
            catch (SQLException e) {
                e.getSQLState();
            }
        }

        return userToken;
    }

    public void saveImagePath(String idToken) {
        String query;
        if(checkProfilePicture(idToken)) {
            query = "UPDATE userinfo SET ImagePath = \"" + idToken + ".jpg\" WHERE idToken = \"" + idToken + "\"";

            try {
                sendQuery(query);
                statement.close();
            }
            catch (SQLException e) {
                e.getSQLState();
            }
        }
    }

    public void addMessageToChat(Message message) {
        String dateWithTime = message.getTime().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSSSSS"));
        String query = "INSERT INTO chatmessage VALUE(\"" + message.getChatId() + "\", \"" + message.getFrom() + "\", \""  + message.getTo() + "\", \"" + message.getContent()
                        + "\", \"" + dateWithTime + "\", false )";

        try {
            sendQuery(query);
            statement.close();
        }
        catch (SQLException e) {
            e.getSQLState();
        }
    }

    public void setMessageAsRead(Message receivedMessage) {
        String dateWithTime = receivedMessage.getTime().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSSSSS"));
        String query = "UPDATE chatmessage SET ReadMessage = 1 WHERE ( ChatID = \"" + receivedMessage.getChatId() + "\" AND FromPerson = \"" + receivedMessage.getFrom()
                        + "\" AND MessageDateTime = \"" + dateWithTime + "\")";

        try {
            sendQuery(query);
            statement.close();
        }
        catch (SQLException e) {
            e.getSQLState();
        }
    }

    public void deleteLocation(String idToken) {
        String query;
        if(checkIfLocationIsAlreadySet(idToken)) {
            query = "DELETE from location WHERE IdToken = \"" + idToken + "\"";
            try {
                sendQuery(query);
                statement.close();
            } catch (SQLException e) {
                e.getSQLState();
            }
        }
    }

    public void closeConnection() {
        if(dbConnection != null) {
            try {
                dbConnection.close();
            }
            catch (SQLException e) {
                e.getSQLState();
            }
        }
    }

    private boolean checkUsername(String username) {
        String query = "SELECT count(1) from authentication Where Username = \"" + username + "\"";
        boolean usernameTaken = false;

        ResultSet result = sendQuery(query);
        if(result != null) {
            try {
                result.next();
                usernameTaken = result.getBoolean("COUNT(1)");
                result.close();
                statement.close();
            }
            catch (SQLException e) {
                e.getSQLState();
            }
        }
        return usernameTaken;
    }

    private boolean checkProfilePicture(String idToken) {
        String query = "SELECT count(1) from userinfo WHERE IdToken = \"" + idToken + "\" AND ImagePath IS NULL";
        boolean profilePictureSet = false;

        ResultSet result = sendQuery(query);
        if(result != null) {
            try {
                result.next();
                profilePictureSet = result.getBoolean("count(1)");
                result.close();
                statement.close();
            }
            catch (SQLException e) {
                e.getSQLState();
            }
        }
        return profilePictureSet;
    }

    private boolean checkIfLocationIsAlreadySet(String idToken) {
        boolean returnValue = false;
        String query = "SELECT count(1) from location Where IdToken = \"" + idToken + "\"";

        ResultSet resultSet = sendQuery(query);
        try {
            resultSet.next();
            boolean exist = resultSet.getBoolean("count(1)");
            returnValue = exist;
            resultSet.close();
        }
        catch (SQLException e) {
            System.out.println(e.getSQLState());
        }
        return returnValue;
    }

    private ResultSet sendQuery(String query) {
        ResultSet result = null;
        try {
            statement = dbConnection.createStatement();
            statement.execute(query);
            result = statement.getResultSet();
        }
        catch (SQLException e) {
            System.out.println(e.getSQLState());
        }
        return result;
    }

    private String generateChatHash(String fromIdToken, String toIdToken) {
        String chatIdHash = null;
        try {
            MessageDigest shaGenerator = MessageDigest.getInstance("SHA-256");
            byte[] chatHash = shaGenerator.digest((fromIdToken + toIdToken).getBytes());

            StringBuilder buffer = new StringBuilder();
            for(byte b : chatHash) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            chatIdHash = buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return  chatIdHash;
    }
}
