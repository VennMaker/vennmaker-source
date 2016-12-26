package data;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Objects of this class represent an pending crypto (encrypt or decrypt) operation. Every operation has its own subclasses
 * (e.g ActorCryptoElement for crypting actors). For every subclass only one object of them can exist at one time.
 * The password will be overwritten with '0' after the use of this object.
 *
 */
public abstract class CryptoElement
{
	/**
	 * the salt of encryption or decryption
	 */
	private byte[]	salt;
	
	/**
	 * the password for encryption or decryption
	 */
	private transient char[] password;
	
	/**
	 * Creates a new object of this class with the given password
	 * @param password password for encryption or decryption
	 */
	protected CryptoElement(char[] password)
	{
		this.password = password;
		
		this.salt = new byte[8];
		Random random = new SecureRandom();
		
		for(int i = 0; i <  this.salt.length; i++)
			random.nextBytes(this.salt);
	}
	
	/**
	 * Returns the password for encryption or decryption
	 * @return the password for encryption or decryption
	 */
	public char[] getPassword()
	{
		return this.password;
	}
	
	/**
	 * Sets the password for encryption or decryption
	 * @param password the password for encryption or decryption
	 */
	public void setPassword(char[] password)
	{
		this.password = password;
	}
	
	/**
	 * Returns the Salt for encryption or decryption
	 * @return the Salt for encryption or decryption
	 */
	public byte[] getSalt()
	{
		return this.salt;
	}
	
	/**
	 * Overrides the password with '0'.
	 */
	public void clearPassword()
	{
		for(int i = 0; i < this.password.length; i++)
			this.password[i] = '0';
	}
}
