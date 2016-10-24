package security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class CertificateSubsystem {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	    keyGen.initialize(1024);
	    KeyPair keypair = keyGen.genKeyPair();
	    PrivateKey privateKey = keypair.getPrivate();
	    PublicKey publicKey = keypair.getPublic();

	}

}
