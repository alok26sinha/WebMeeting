package security.passwordhash;

public class HexUtilities {

	public static String formatAsHexString(byte[] digest) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			String plainText = Integer.toHexString(0xFF & digest[i]);

			if (plainText.length() < 2) {
				plainText = "0" + plainText;
			}

			hexString.append(plainText);
		}
		return hexString.toString();
	}

}
