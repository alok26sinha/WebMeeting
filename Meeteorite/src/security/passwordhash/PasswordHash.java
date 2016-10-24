package security.passwordhash;

/**
 *  This is an implementation of the one way hash as described by 
 *  http://crackstation.net/hashing-security.htm
 *  
 *  TODO Hook in security to use this mechanism
 */
public class PasswordHash {
	
	private RandomGenerator saltGenerator = new RandomGenerator();

	public String newSalt() {
		return saltGenerator.getNewRandom(Whirlpool.DIGESTBYTES);  // 512 bits = 64 bytes = 128 characters
	}

	public String hash(String password, String salt) {
		String toBeHashed = password + salt;
		
		Whirlpool w = new Whirlpool();
        byte[] digest = new byte[Whirlpool.DIGESTBYTES];
        w.NESSIEinit();
        w.NESSIEadd(toBeHashed);
        w.NESSIEfinalize(digest);
        
        return HexUtilities.formatAsHexString(digest);
        
	}

}
