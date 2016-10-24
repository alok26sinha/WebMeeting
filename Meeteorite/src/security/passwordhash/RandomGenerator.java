package security.passwordhash;

import java.security.SecureRandom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RandomGenerator {

	public static Log log = LogFactory.getLog(RandomGenerator.class);
	private SecureRandom secureRandom;

	public String getNewRandom(int byteLength) {
		
		
		if (secureRandom == null){
			secureRandom = new SecureRandom();
			long seed = System.currentTimeMillis();
			seed = seed ^ 823748925389l;
			secureRandom.setSeed(seed);
		}
		
		byte[] rnd = new byte[byteLength];

		secureRandom.nextBytes(rnd);

		return HexUtilities.formatAsHexString(rnd);
	}


}
