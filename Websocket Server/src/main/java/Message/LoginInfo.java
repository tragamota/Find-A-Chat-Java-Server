package Message;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

public class LoginInfo {
    private String userName, passWord, idToken;

    public LoginInfo(String userName, String passWord, String idToken) {
        this.userName = userName;
        this.passWord = passWord;
        this.idToken = idToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public static String generateIdToken(String userName) {
        String returnValue = null;
        try {
            MessageDigest sha256Generator = MessageDigest.getInstance("SHA-256");
            byte[] hashData = sha256Generator.digest((userName + LocalDateTime.now().toString()).getBytes("UTF-8"));

           StringBuffer buffer = new StringBuffer();
           for(byte b : hashData) {
               buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
           }
           returnValue = buffer.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnValue;
    }
}
