import java.sql.*;

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
        try {
            Statement statement = dbConnection.createStatement();
            statement.execute(query);

            ResultSet result = statement.getResultSet();
            result.next();
            int totalFound = result.getInt("COUNT(IdToken)");
            if(totalFound == 0) {
                returnValue = false;
            }
            else {
                returnValue = true;
            }

            result.close();
            statement.close();
        }
        catch (SQLException e) {
            e.getSQLState();
        }
        return returnValue;
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
