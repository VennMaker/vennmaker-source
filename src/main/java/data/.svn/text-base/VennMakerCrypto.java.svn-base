/**
 * 
 */
package data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;

/**
 * 
 * 
 */
public class VennMakerCrypto
{

	protected SecretKeySpec				skeySpec;

	protected String						keyfile;

	
	private List<CryptoElement>		cryptoElements;

	/**
	 * Singleton: Referenz.
	 */
	private static VennMakerCrypto	instance;
	
	/**
	 * Generates a new Object of this class with an initalized 
	 * salt
	 */
	public VennMakerCrypto()
	{
		this.cryptoElements = new ArrayList<CryptoElement>();
	}
	
	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige Instanz in diesem Prozess.
	 */
	public static VennMakerCrypto getInstance()
	{
		if (instance == null)
		{
			instance = new VennMakerCrypto();

		}
		return instance;
	}

	private void error(String t)
	{
		System.out.println("ERROR: VennMakerCrypto:\t" + t);
	}

	/**
	 * Generate a AES key, store the key in memory and write the key to a file
	 * 
	 * @param k
	 *           name of the key file
	 */
	public boolean generateKeyAES(String k)
	{
		KeyGenerator kgen;
		SecretKey skey;

		try
		{
			this.keyfile = k;
			kgen = KeyGenerator.getInstance("AES");

			kgen.init(128);

			skey = kgen.generateKey();

			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
					this.keyfile));
			out.writeObject(skey);
			out.close();
			out = new ObjectOutputStream(new FileOutputStream(this.keyfile));
			out.writeObject(skey);
			out.close();

		} catch (NoSuchAlgorithmException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
			return false;
		} catch (FileNotFoundException exn)
		{
			error("writing key-file");
			return false;
		} catch (IOException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
			return false;
		}

		skeySpec = (SecretKeySpec) skey;

		return true;

	}

	/**
	 * Load a AES key from a file
	 * 
	 * @param k
	 *           name of the key file
	 */
	public boolean getKeyAESfromFile(String k)
	{

		ObjectInputStream keyIn;
		byte[] raw;

		try
		{
			this.keyfile = k;

			keyIn = new ObjectInputStream(new FileInputStream(keyfile));
			SecretKey key = (SecretKey) keyIn.readObject();
			keyIn.close();

			raw = key.getEncoded();

		} catch (FileNotFoundException exn)
		{
			error("key-file not found");
			return false;
		} catch (IOException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
			return false;

		} catch (ClassNotFoundException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
			return false;

		}

		skeySpec = new SecretKeySpec(raw, "AES");
		return true;
	}

	/**
	 * Encode a string
	 * 
	 * @param input
	 *           string string to encode
	 */
	public String encodeAES(String input)
	{
		byte[] bytes = null;
		byte[] out = null;
		try
		{
			bytes = input.getBytes("ISO8859_1");
		} catch (UnsupportedEncodingException exn1)
		{
			// TODO Auto-generated catch block
			exn1.printStackTrace();
		}

		Cipher c;

		try
		{
			c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, skeySpec);

			out = c.doFinal(bytes);

		} catch (NoSuchAlgorithmException exn1)
		{
			error("no AES key found");
		} catch (NoSuchPaddingException exn1)
		{
			error("no AES key found");
		} catch (InvalidKeyException exn)
		{
			error("no AES key found");
		} catch (IllegalBlockSizeException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (BadPaddingException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		}

		String result = null;
		try
		{
			if (out != null)
				result = new String(out, "ISO8859_1");
		} catch (UnsupportedEncodingException exn)
		{
			error("encode AES failure");
		}
		return result;

	}

	/**
	 * Decode a string
	 * 
	 * @param input
	 *           encoded string encoded string to decode
	 */
	public String decodeAES(String input)
	{

		if (skeySpec == null)
			return null;

		InputStream is = null;
		try
		{
			is = new ByteArrayInputStream(input.getBytes("ISO8859_1"));
		} catch (UnsupportedEncodingException exn1)
		{
			// TODO Auto-generated catch block
			exn1.printStackTrace();
		}
		Cipher c;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, skeySpec);

			CipherInputStream cis = new CipherInputStream(is, c);

			for (int b; (b = cis.read()) != -1;)
				bos.write(b);

			cis.close();
		} catch (NoSuchAlgorithmException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (NoSuchPaddingException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (InvalidKeyException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (IOException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		}

		String result = null;
		try
		{
			result = new String(((ByteArrayOutputStream) bos).toByteArray(),
					"ISO8859_1");
		} catch (UnsupportedEncodingException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		}

		return result;
	}

	/**
	 * Check the data protection status
	 * 
	 * @return false no key is loaded true a key is loaded
	 */

	public boolean getStatus()
	{
		if (skeySpec == null)
			return false;
		else
			return true;
	}

	/**
	 * Delete the crypto key
	 */

	public void delProtection()
	{

		skeySpec = null;
		this.keyfile = null;

	}

	/**
	 * Returns the key file name
	 * 
	 * @return key file name
	 */
	public String getKeyFileName()
	{

		return this.keyfile;

	}

	/**
	 * Encodes an input string with an user password
	 * 
	 * @param password
	 *           : User password
	 * @param in
	 *           : Input string
	 * @return encoded string
	 */

	public String encodeWithPasswordString(char[] password, byte[] salt, String in)
	{
		
		Cipher encryptCipher;
		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();

		final PBEParameterSpec ps = new PBEParameterSpec(salt, 100);
		SecretKeyFactory kf;
		try
		{
			kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");

			final SecretKey k = kf.generateSecret(new PBEKeySpec(password));
			encryptCipher = Cipher
					.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
			encryptCipher.init(Cipher.ENCRYPT_MODE, k, ps);

			byte[] b = in.getBytes("UTF-8");
			byte[] enc = encryptCipher.doFinal(b);
			return encoder.encode(enc);
		} catch (NoSuchAlgorithmException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (InvalidKeySpecException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (NoSuchPaddingException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (InvalidKeyException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (InvalidAlgorithmParameterException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (UnsupportedEncodingException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (IllegalBlockSizeException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (BadPaddingException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		}

		return null;

	}
	
	/**
	 * Encodes all Strings in the given <code>List</code> with the password and salt stored in the <code>CryptoElement</code>
	 * @param element <code>CryptoElement</code> with stored password an salt
	 * @param stringsToEncrypt a <code>List</code> with the Strings to encrypt
	 * @return a <code>List</code> with the encrypted Strings
	 */
	public List<String> encodeAllInList(CryptoElement element, List<String> stringsToEncrypt)
	{
		addCryptoElement(element);
		
		List<String> encryptedStrings = new ArrayList<String>();
		
		for(String str : stringsToEncrypt)
			encryptedStrings.add(encodeWithPasswordString(element.getPassword(), element.getSalt(),str));
		
		
		element.clearPassword();
		
		return encryptedStrings;
	}
	
	/**
	 * Decodes all Strings in the given <code>List</code> with the password and salt stored in the <code>CryptoElement</code>
	 * @param element <code>CryptoElement</code> with stored password an salt
	 * @param stringsToEncrypt a <code>List</code> with the Strings to decrypt
	 * @return a <code>List</code> with the decrypted Strings
	 */
	public List<String> decodeAllInList(CryptoElement element, List<String> stringsToDecrypt)
	{
		List<String> decryptedStrings = new ArrayList<String>();
		
		CryptoElement decodeElement = getCryptoElementForClass(element.getClass());
		
		if(decodeElement == null)
			return new ArrayList<String>();
		
		decodeElement.setPassword(element.getPassword());
		
		for(String str : stringsToDecrypt)
		{
			String decode = decodeWithPasswordString(decodeElement.getPassword(), decodeElement.getSalt(), str);
			
			if(decode == null)
				return new ArrayList<String>();
			
			decryptedStrings.add(decode);
		}
		
		element.clearPassword();
		decodeElement.clearPassword();
		this.removeCryptoElement(decodeElement);
		
		return decryptedStrings;
	}
	
	/**
	 * Decodes the names of the actors stored in the given <code>Vector</code>
	 * @param element <code>CryptoElement</code> with stored password an salt
	 * @param actors <code>Vector</code> with the actors whose name should be decoded
	 * @return a <code>List</code> with the decoded actor names
	 */
	public List<String> decodeActorNames(CryptoElement element, Vector<Akteur> actors)
	{
		
		List<String> actorNames = new ArrayList<String>();
		
		for(Akteur akt : actors)
			actorNames.add(akt.getName());
		
		return decodeAllInList(element, actorNames);
	}
	
	/**
	 * Encodes the names of the actors stored in the given <code>Vector</code>
	 * @param element <code>CryptoElement</code> with stored password an salt
	 * @param actors <code>Vector</code> with the actors whose name should be encoded
	 * @return a <code>List</code> with the encoded actor names
	 */
	public List<String> encodeActorNames(CryptoElement element, Vector<Akteur> actors)
	{
		List<String> actorNames = new ArrayList<String>();
		
		for(Akteur akt : actors)
			actorNames.add(akt.getName());
		
		return encodeAllInList(element, actorNames);
	}
	
	/**
	 * Returns a <code>List</code> with all currently stored cryptoElements
	 * @return a <code>List</code> with all currently stored cryptoElements
	 */
	public List<CryptoElement> getCryptoElements()
	{
		return this.cryptoElements;
	}
	
	/**
	 * Sets a <code>List</code> with all currently stored cryptoElements. If the given <code>List</code> is <code>null</code>, 
	 * a new <code>List</code> will be created
	 * @param cryptoElements a <code>List</code> with all currently stored cryptoElements
	 */
	public void setCryptoElements(List<CryptoElement> cryptoElements)
	{
		if(cryptoElements == null)
			this.cryptoElements = new ArrayList<CryptoElement>();
		else
			this.cryptoElements = cryptoElements;
	}
	
	/**
	 * Removes a given <code>CryptoElement</code> from the list. This only happens if a 
	 * decryption operation was sucessfull
	 * @param element <code>CryptoElement</code> to remove
	 * @return <code>true</code> if the element has been removed, <code>false</code> if there was no such element
	 */
	public boolean removeCryptoElement(CryptoElement element)
	{
		return this.cryptoElements.remove(element);
	}
	

	/**
	 * Decode input string with user password
	 * 
	 * @param password
	 *           : user password
	 * @param salt : the salt for decryption
	 * 
	 * @param in
	 *           : input string
	 * @return decoded string
	 */
	public String decodeWithPasswordString(char[] password, byte[] salt, String in)
	{

		Cipher decryptCipher;
		BASE64Decoder decoder = new BASE64Decoder();

		final PBEParameterSpec ps = new PBEParameterSpec(salt, 100);
		SecretKeyFactory kf;
		try
		{
			kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");

			final SecretKey k = kf.generateSecret(new PBEKeySpec(password));

			decryptCipher = Cipher
					.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
			decryptCipher.init(Cipher.DECRYPT_MODE, k, ps);

			byte[] dec = decoder.decodeBuffer(in);
			byte[] b = decryptCipher.doFinal(dec);
			return new String(b, "UTF-8");

		} catch (NoSuchAlgorithmException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (InvalidKeySpecException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (NoSuchPaddingException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (InvalidKeyException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (InvalidAlgorithmParameterException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (IOException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (IllegalBlockSizeException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (BadPaddingException exn)
		{

			// TODO Auto-generated catch block
			// exn.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Adds a new <code>CryptoElement</code> to the internal list. Only one <code>CryptoElemt</code> for a special encryption method (e.g Actor encryption)
	 * can exist at one time
	 * @param elem a new <code>CryptoElement</code>
	 */
	public void addCryptoElement(CryptoElement elem)
	{
		boolean found = false;
		
		for(CryptoElement element : cryptoElements)
		{
			if(element.getClass().getCanonicalName().equals(elem.getClass().getCanonicalName()))
			{
				found = true;
				break;
			}
		}
		
		if(!found)
			this.cryptoElements.add(elem);
	}
	
	/**
	 * Returns a <code>CryptoElement</code> by its classname. Only one <code>CryptoElemt</code> for a special encryption method (e.g Actor encryption)
	 * can exist at one time
	 * @param clazz <code>Class</code> of the <code>CryptoElement</code> to get
	 * @return the matching <code>CryptoElemnt</code>
	 */
	private CryptoElement getCryptoElementForClass(Class<? extends CryptoElement> clazz)
	{
		for(CryptoElement elem  : cryptoElements)
			if(elem.getClass().getCanonicalName().equals(clazz.getCanonicalName()))
				return elem;
		
		return null;
	}
}
