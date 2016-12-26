package interview.elements.information;

import interview.InterviewElementInformation;

/**
 * Objects of this class contains information for the CryptActorElement
 * 
 *
 */
public class CryptActorElementInformation extends InterviewElementInformation
{
	/**
	 * Indicates if the element is for encryption
	 */
	private boolean encrypt;
	
	/**
	 * The info text shown to the user
	 */
	private String infoText;
	
	/**
	 * Constructs a new Object of this Element
	 * @param encrypt <code>true</code> if the element is for encryption, <code>false</code> if the element is for decryption
	 */
	public CryptActorElementInformation(boolean encrypt)
	{
		this.encrypt = encrypt;
	}
	
	/**
	 * Constructs a new Object of this Element
	 */
	public CryptActorElementInformation()
	{
		
	}
	
	/**
	 * Returns<code>true</code> if the element is for encryption, <code>false</code> if the element is for decryption
	 * @return <code>true</code> if the element is for encryption, <code>false</code> if the element is for decryption
	 */
	public boolean isEncrypt()
	{
		return encrypt;
	}
	
	/**
	 * Sets <code>true</code> if the element is for encryption, <code>false</code> if the element is for decryption
	 * @param encrypt <code>true</code> if the element is for encryption, <code>false</code> if the element is for decryption
	 */
	public void setEncrypt(boolean encrypt)
	{
		this.encrypt = encrypt;
	}
	
	/**
	 * Returns the info text shown to the user
	 * @return the info text shown to the user
	 */
	public String getInfoText()
	{
		return infoText;
	}
	
	/**
	 * Sets the info text shown to the user
	 * @param infoText the info text shown to the user
	 */
	public void setInfoText(String infoText)
	{
		this.infoText = infoText;
	}
}
