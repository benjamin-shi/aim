package benjaminshi.aim.eci;

/** The class used to represent the ECI Information of each ECI definition
 * @author Benjamin Shi (SHI YU, shiyubnu@icloud.com)
 *
 */
public class ECIInfo {
	protected String ECI = "";
    
	protected int ECIValue = -1;

	protected String Charset = "";
	
	protected String EncodingDisplayName = "";
	
	/** get the ECI escape sequence from 000000 to 999999
	 * @return the ECI escape sequence
	 */
	public String getECI() {
		return ECI;
	}

	/** set the ECI escape sequence 
	 * @param eCI the ECI escape sequence, in [000000, 999999]
	 */
	public void setECI(String eCI) {
		ECI = eCI;
	}

	/** get the value of the ECI, from 0 to 999999
	 * @return the value of the ECI
	 */
	public int getECIValue() {
		return ECIValue;
	}

	/** set the value of ECI
	 * @param eCIValue the value of ECI, in [0, 999999]
	 */
	public void setECIValue(int eCIValue) {
		ECIValue = eCIValue;
	}

	/** get the charset name of the ECI
	 * @return the charset name of the ECI
	 */
	public String getCharset() {
		return Charset;
	}

	/** set the charset name of the ECI
	 * @param charset the charset name
	 */
	public void setCharset(String charset) {
		Charset = charset;
	}
	
	/** get the encoding display name or description of the charset of ECI
	 * @return the encoding display name or description of the charset of ECI
	 */
	String getEncodingDisplayName() {
		return EncodingDisplayName;
	}

	/** set the encoding display name or description of the charset of ECI
	 * @param encodingDisplayName the encoding display name or description of the charset of ECI
	 */
	void setEncodingDisplayName(String encodingDisplayName) {
		EncodingDisplayName = encodingDisplayName;
	}
}
