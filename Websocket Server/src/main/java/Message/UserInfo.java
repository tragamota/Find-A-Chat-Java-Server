package Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class UserInfo {
    private String idToken, nickName, firstName, lastName;
    private String profileImage;
    private Location location;

    public UserInfo(String idToken, String nickName, String firstName, String lastName, String imagePath, double longitude, double langitude) {
        this.idToken = idToken;
        this.nickName = nickName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = new Location(longitude, langitude);
        this.profileImage = loadImage(imagePath);
    }

    public UserInfo(String idToken, String nickName, String firstName, String lastName) {
        this.idToken = idToken;
        this.nickName = nickName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private String loadImage(String imagePath) {
        String loadedImage = null;
        if(imagePath != null) {
            try {
                byte[] imageInBytes = Files.readAllBytes(Paths.get(Paths.get("").toAbsolutePath() + "\\profileImage\\" + imagePath));
                loadedImage = Base64.getEncoder().encodeToString(imageInBytes);
                System.out.println(loadedImage);
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return loadedImage;
    }

    public static BufferedImage decodeImage(String base64Image) {
        BufferedImage decodedImage = null;
        byte[] imageInBytes;

        imageInBytes = Base64.getDecoder().decode(base64Image);
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageInBytes);
            decodedImage = ImageIO.read(bis);
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decodedImage;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getNickName() {
        return nickName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public Location getLocation() {
        return location;
    }
}
