package pellekrogholt.labexercise8;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class SymmetricCrypt {

	private static final String password = "bdsp08-06";
	private static final String plaintext = "The quick brown fox jumps over the lazy dog.";

	public static void main(String args[]) throws Exception {
		// Create Key
		byte key[] = password.getBytes();
		DESKeySpec desKeySpec = new DESKeySpec(key);
		
// possible input for SecretKeyFactory.getInstance(<key type>)
//		Algorithm	Maximum Keysize
//		DES	64
//		DESede	*
//		RC2	128
//		RC4	128
//		RC5	128
//		RSA	*
//		all others	128
		
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

		// Create Cipher
		Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		desCipher.init(Cipher.ENCRYPT_MODE, secretKey);

		// Create outputstream 
		// note: nb when running in eclipse files goes to <path to eclipse workspace>/distributedsystems/ 
		FileOutputStream fos = new FileOutputStream("out.txt");
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		CipherOutputStream cos = new CipherOutputStream(bos, desCipher);
		ObjectOutputStream oos = new ObjectOutputStream(cos);

		// Write
		oos.writeUTF(plaintext);
		oos.flush();
		oos.close();

		// note: extract encrypted file - key is needed there but should not be send along with the file right ?
		
		// Change cipher mode
		desCipher.init(Cipher.DECRYPT_MODE, secretKey);

		// Create stream
		FileInputStream fis = new FileInputStream("out.txt");
		BufferedInputStream bis = new BufferedInputStream(fis);
		CipherInputStream cis = new CipherInputStream(bis, desCipher);
		ObjectInputStream ois = new ObjectInputStream(cis);

		// Read 
		String plaintext2 = ois.readUTF();
		ois.close();
		
		
		// Compare
		System.out.println(plaintext);
		System.out.println(plaintext2);
		System.out.println("Identical? " + (plaintext.equals(plaintext2)));
	}
}
