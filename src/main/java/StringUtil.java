import java.security.MessageDigest;

public class StringUtil {
    //Applies Sha256 to a string and return the result
    public static String applySha256(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // applies sha256 to our input
            byte[] hash = messageDigest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer(); // this will contain hash as a hexadecimal
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append(0);
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
