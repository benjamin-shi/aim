package benjaminshi.aim.eci;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;

/** The Class used to represent a ECI segment in the data analysis process under ECI protocol 
 * @author Benjamin Shi (SHI YU, shiyubnu@icloud.com)
 *
 */
public class ECISegment
{
	protected int ECIValue = -1;

	/** get the ECI Value in Integer of this segment
	 * @return the ECI Value in Integer of this segment, in [0, 999999]
	 */
	public int getECIValue() {
		return ECIValue;
	}

	/** set the ECI Value in Integer of this segment
	 * @param eCIValue the ECI Value in Integer of this segment, in [0, 999999]
	 */
	public void setECIValue(int eCIValue) {
		if ((eCIValue >= 0) && (eCIValue <= 999999))
        {
            ECIValue = eCIValue;
        }
	}
	
	/** get the ECI in String of this segment
	 * @return the ECI in String of this segment, in [000000, 999999]
	 */
	public String getECI() {
		if ((ECIValue >= 0) && (ECIValue <= 999999))
        {
            return String.format("%06d", ECIValue);
        }
        else
        {
            return "";
        }
	}

	/** set the ECI in String of this segment
	 * @param ECI the ECI in String of this segment, in [000000, 999999]
	 */
	public void setECI(String ECI) {
		int val = -1;

		try
		{
			val = Integer.parseInt(ECI);
		}
		catch (NumberFormatException ex)
		{
			val = -1;
		}
		if ((val >= 0) && (val <= 999999))
        {
            ECIValue = val;
        }
	}
	
	/** get the ECI escape sequence of this segment
	 * @return the ECI escape sequence of this segment, like \000003
	 */
	public byte[] getEscapeECISequence()
    {
		String eci = getECI();
		
		byte[] seq = null;
		
		if (eci.length() > 0)
		{
			eci = "\\" + eci;
			try
			{
				seq = eci.getBytes("ISO-8859-1");
			}
			catch (Exception ex)
			{
				seq = null;
			}
		}
		return seq;
	}

	/** get the charset of this segment
	 * @return the charset of this segment
	 */
	public String getCharset() {
		int eci = this.getECIValue();
		
		if (ECI_TO_CHARSET.containsKey(eci))
		{
			return ECI_TO_CHARSET.get(eci);
		}
		
		return "";
	}

	/** set the charset of this segment, and will auto set the ECI if success
	 * @param charset the charset of this segment, like "UTF-8", "ISO-8859-1"
	 */
	public void setCharset(String charset) {
		if (CHARSET_TO_ECI.containsKey(charset))
		{
			this.setECIValue(CHARSET_TO_ECI.get(charset));
		}
	}
	
	/** get the encoding display name or description of this segment
	 * @return the encoding display name or description of this segment
	 */
	public String getEncodingDisplayName() {
		String charset = this.getCharset();
		
		if ((charset.length() > 0) && (CHARSET_DISPLAYNAME.containsKey(charset))) 
			charset = CHARSET_DISPLAYNAME.get(charset);
		else
			charset = "";
		return charset;
	}

	protected byte[] SegmentData = null;

	/** get the segment data hex byte array of this segment, which is un-escaped under ECI protocol.
	 * @return the segment data hex byte array of this segment
	 */
	public byte[] getSegmentData() {
		return SegmentData;
	}

	/** set the segment data hex byte array of this segment, which is escaped under ECI protocol.
	 * @param data the segment data hex byte array of this segment
	 */
	public void setSegmentData(byte[] data) {
		byte[] segmentData = null;
		
		if (null != data)
		{
			byte iiByte;
			int ii;
			int size = 0;
			
			for (ii = 0;ii < data.length; ++ii)
			{
				iiByte = data[ii];
				++size;
				if ((0x5C == iiByte) && (ii + 1 < data.length) && (0x5C == data[ii + 1]))
				{
					++ii;
				}
			}
			
			segmentData = new byte[size];
			int pos = 0;
			for (ii = 0;ii < data.length; ++ii)
			{
				iiByte = data[ii];
				segmentData[pos] = iiByte;
				++pos;
				if ((0x5C == iiByte) && (ii + 1 < data.length) && (0x5C == data[ii + 1]))
				{
					++ii;
				}
			}
		}
		
		this.SegmentData = segmentData;
	}
	
	/** get the text representation of the segment, which is un-escaped under ECI protocol.
	 * @return the text representation of the segment
	 */
	public String getSegmentText() {
		String text = "";
		String charset = this.getCharset();
		
		if (charset.length() > 0)
		{
			try
			{
				text = new String(this.SegmentData, charset);
			}
			catch (Exception ex)
			{
				text = "";
			}
		}
		
		return text;
	}

	/** set the text representation of the segment, which is escaped under ECI protocol.
	 * @param segmentText the text representation of the segment
	 */
	public void setSegmentText(String segmentText) {
		String charset = this.getCharset();
		
		if (charset.length() <= 0) charset = "ISO-8859-1";
		try
		{
			this.setSegmentData(segmentText.getBytes(charset));
		}
		catch (Exception ex)
		{
			
		}
	}
	
	/** get the segment data hex byte array of this segment, which is escaped under ECI protocol.
	 * @return the segment data hex byte array of this segment
	 */
	public byte[] getEscapedSegmentData()
	{
		byte[] escapedSegmentData = null;
		
		if (null != this.SegmentData)
		{
			int ii = 0;
			int pos = 0;
			int size = 0;
			
			for (ii = 0;ii < this.SegmentData.length;++ii)
			{
				++size;
				if (0x5C == this.SegmentData[ii])
				{
					++size;
				}
			}
			
			escapedSegmentData = new byte[size];
			
			for (ii = 0;ii < this.SegmentData.length;++ii)
			{
				escapedSegmentData[pos] = this.SegmentData[ii];
				++pos;
				if (0x5C == this.SegmentData[ii])
				{
					escapedSegmentData[pos] = this.SegmentData[ii];
					++pos;
				}
			}
		}
		
		return escapedSegmentData;
	}
	
	/** get the text representation of segment data of this segment, which is escaped under ECI protocol.
	 * @return the text representation of segment data of this segment
	 */
	public String getEscapedSegmentText()
	{
		String text = "";
		
		String charset = this.getCharset();
		
		if (charset.length() <= 0) charset = "ISO-8859-1";
		
		try
		{
			text = new String(this.getEscapedSegmentData(), charset);
		}
		catch (Exception ex)
		{
			text = "";
		}
		
		return text;
	}
	
	static Map<Integer, String> ECI_TO_CHARSET = null;
    static Map<String, Integer> CHARSET_TO_ECI = null;

    static Map<String, String> CHARSET_DISPLAYNAME = null;

    static void matchECItoCharset(int eci, String charset)
    {
    	if (!ECI_TO_CHARSET.containsKey(eci))
    		ECI_TO_CHARSET.put(eci, charset);
    	CHARSET_TO_ECI.put(charset, eci);
    }

    static void matchECItoCharset(int eci, String[] charsets)
    {
        if ((null != charsets) && (charsets.length > 0))
        	ECI_TO_CHARSET.put(eci, charsets[0]);
        for (String charset : charsets)
        {
        	CHARSET_TO_ECI.put(charset, eci);
        }
    }
    
    static void matchCharsetDisplayName(String charset, String name)
    {
    	CHARSET_DISPLAYNAME.put(charset, name);
    }
    
	static {
		ECI_TO_CHARSET = new Hashtable<Integer, String>();
		CHARSET_TO_ECI = new Hashtable<String, Integer>();
		CHARSET_DISPLAYNAME = new Hashtable<String, String>();
		
		//add known charsetECI
		matchECItoCharset(-1, ""); //Default
		matchECItoCharset(3, "ISO-8859-1");
		{
			matchECItoCharset(3, "819");
			matchECItoCharset(3, "8859_1");
			matchECItoCharset(3, "cp819");
			matchECItoCharset(3, "csISOLatin1");
			matchECItoCharset(3, "ibm-819");
			matchECItoCharset(3, "IBM819");
			matchECItoCharset(3, "IBM-819");
			matchECItoCharset(3, "ISO_8859_1");
			matchECItoCharset(3, "ISO_8859-1");
			matchECItoCharset(3, "ISO_8859-1:1987");
			matchECItoCharset(3, "ISO8859_1");
			matchECItoCharset(3, "ISO8859-1");
			matchECItoCharset(3, "ISO-8859-1");
			matchECItoCharset(3, "iso-ir-100");
			matchECItoCharset(3, "l1");
			matchECItoCharset(3, "latin1");
		}
		matchECItoCharset(4, "ISO-8859-2");
		{
			matchECItoCharset(4, "8859_2");
			matchECItoCharset(4, "912");
			matchECItoCharset(4, "cp912");
			matchECItoCharset(4, "csISOLatin2");
			matchECItoCharset(4, "ibm912");
			matchECItoCharset(4, "ibm-912");
			matchECItoCharset(4, "ISO_8859-2");
			matchECItoCharset(4, "ISO_8859-2:1987");
			matchECItoCharset(4, "iso8859_2");
			matchECItoCharset(4, "ISO8859-2");
			matchECItoCharset(4, "ISO-8859-2");
			matchECItoCharset(4, "iso-ir-101");
			matchECItoCharset(4, "l2");
			matchECItoCharset(4, "latin2");
			matchECItoCharset(4, "windows-28592");
		}
		matchECItoCharset(5, "ISO-8859-3");
		{
			matchECItoCharset(5, "8859_3");
			matchECItoCharset(5, "913");
			matchECItoCharset(5, "cp913");
			matchECItoCharset(5, "csISOLatin3");
			matchECItoCharset(5, "ibm913");
			matchECItoCharset(5, "ibm-913");
			matchECItoCharset(5, "ISO_8859-3");
			matchECItoCharset(5, "ISO_8859-3:1988");
			matchECItoCharset(5, "iso8859_3");
			matchECItoCharset(5, "ISO8859-3");
			matchECItoCharset(5, "ISO-8859-3");
			matchECItoCharset(5, "iso-ir-109");
			matchECItoCharset(5, "l3");
			matchECItoCharset(5, "latin3");
			matchECItoCharset(5, "windows-28593");
		}
		matchECItoCharset(6, "ISO-8859-4");
		{
			matchECItoCharset(6, "8859_4");
			matchECItoCharset(6, "914");
			matchECItoCharset(6, "cp914");
			matchECItoCharset(6, "csISOLatin4");
			matchECItoCharset(6, "ibm914");
			matchECItoCharset(6, "ibm-914");
			matchECItoCharset(6, "ISO_8859-4");
			matchECItoCharset(6, "ISO_8859-4:1988");
			matchECItoCharset(6, "iso8859_4");
			matchECItoCharset(6, "iso8859-4");
			matchECItoCharset(6, "ISO-8859-4");
			matchECItoCharset(6, "iso-ir-110");
			matchECItoCharset(6, "l4");
			matchECItoCharset(6, "latin4");
			matchECItoCharset(6, "windows-28594");
		}
		matchECItoCharset(7, "ISO-8859-5");
		{
			matchECItoCharset(7, "8859_5");
			matchECItoCharset(7, "915");
			matchECItoCharset(7, "cp915");
			matchECItoCharset(7, "csISOLatinCyrillic");
			matchECItoCharset(7, "cyrillic");
			matchECItoCharset(7, "ibm915");
			matchECItoCharset(7, "ibm-915");
			matchECItoCharset(7, "ISO_8859-5");
			matchECItoCharset(7, "ISO_8859-5:1988");
			matchECItoCharset(7, "iso8859_5");
			matchECItoCharset(7, "ISO8859-5");
			matchECItoCharset(7, "ISO-8859-5");
			matchECItoCharset(7, "iso-ir-144");
			matchECItoCharset(7, "windows-28595");
		}
		matchECItoCharset(8, "ISO-8859-6");
		{
			matchECItoCharset(8, "1089");
			matchECItoCharset(8, "8859_6");
			matchECItoCharset(8, "arabic");
			matchECItoCharset(8, "ASMO-708");
			matchECItoCharset(8, "cp1089");
			matchECItoCharset(8, "csISOLatinArabic");
			matchECItoCharset(8, "ECMA-114");
			matchECItoCharset(8, "ibm1089");
			matchECItoCharset(8, "ibm-1089");
			matchECItoCharset(8, "ISO_8859-6");
			matchECItoCharset(8, "ISO_8859-6:1987");
			matchECItoCharset(8, "iso8859_6");
			matchECItoCharset(8, "ISO8859-6");
			matchECItoCharset(8, "ISO-8859-6");
			matchECItoCharset(8, "ISO-8859-6-E");
			matchECItoCharset(8, "ISO-8859-6-I");
			matchECItoCharset(8, "iso-ir-127");
			matchECItoCharset(8, "windows-28596");
			matchECItoCharset(8, "x-ISO-8859-6S");
		}
		matchECItoCharset(9, "ISO-8859-7");
		{
			matchECItoCharset(9, "813");
			matchECItoCharset(9, "8859_7");
			matchECItoCharset(9, "cp813");
			matchECItoCharset(9, "csISOLatinGreek");
			matchECItoCharset(9, "ECMA-118");
			matchECItoCharset(9, "ELOT_928");
			matchECItoCharset(9, "greek");
			matchECItoCharset(9, "greek8");
			matchECItoCharset(9, "ibm813");
			matchECItoCharset(9, "ibm-813");
			matchECItoCharset(9, "ISO_8859-7");
			matchECItoCharset(9, "ISO_8859-7:1987");
			matchECItoCharset(9, "iso8859_7");
			matchECItoCharset(9, "iso8859-7");
			matchECItoCharset(9, "ISO-8859-7");
			matchECItoCharset(9, "iso-ir-126");
			matchECItoCharset(9, "sun_eu_greek");
			matchECItoCharset(9, "windows-28597");
		}
		matchECItoCharset(10, "ISO-8859-8");
		{
			matchECItoCharset(10, "8859_8");
			matchECItoCharset(10, "916");
			matchECItoCharset(10, "cp916");
			matchECItoCharset(10, "csISOLatinHebrew");
			matchECItoCharset(10, "hebrew");
			matchECItoCharset(10, "ibm916");
			matchECItoCharset(10, "ibm-916");
			matchECItoCharset(10, "ISO_8859-8");
			matchECItoCharset(10, "ISO_8859-8:1988");
			matchECItoCharset(10, "iso8859_8");
			matchECItoCharset(10, "ISO8859-8");
			matchECItoCharset(10, "ISO-8859-8");
			matchECItoCharset(10, "ISO-8859-8-E");
			matchECItoCharset(10, "ISO-8859-8-I");
			matchECItoCharset(10, "iso-ir-138");
			matchECItoCharset(10, "windows-28598");
		}
		matchECItoCharset(11, "ISO-8859-9");
		{
			matchECItoCharset(11, "8859_9");
			matchECItoCharset(11, "920");
			matchECItoCharset(11, "cp920");
			matchECItoCharset(11, "csISOLatin5");
			matchECItoCharset(11, "ibm920");
			matchECItoCharset(11, "ibm-920");
			matchECItoCharset(11, "ISO_8859-9");
			matchECItoCharset(11, "ISO_8859-9:1989");
			matchECItoCharset(11, "iso8859_9");
			matchECItoCharset(11, "ISO8859-9");
			matchECItoCharset(11, "ISO-8859-9");
			matchECItoCharset(11, "iso-ir-148");
			matchECItoCharset(11, "l5");
			matchECItoCharset(11, "latin5");
			matchECItoCharset(11, "windows-28599");
		}
		matchECItoCharset(12, "ISO-8859-10");
		{
			matchECItoCharset(12, "csISOLatin6");
			matchECItoCharset(12, "ISO_8859-10:1992");
			matchECItoCharset(12, "ISO-8859-10");
			matchECItoCharset(12, "iso-ir-157");
			matchECItoCharset(12, "l6");
			matchECItoCharset(12, "latin6");
		}
		matchECItoCharset(13, "x-iso-8859-11");
		{
			matchECItoCharset(13, "iso8859_11");
			matchECItoCharset(13, "iso-8859-11");
			matchECItoCharset(13, "x-iso-8859-11");
		}
		matchECItoCharset(15, "ISO-8859-13");
		{
			matchECItoCharset(15, "8859_13");
			matchECItoCharset(15, "iso_8859-13");
			matchECItoCharset(15, "iso8859_13");
			matchECItoCharset(15, "ISO8859-13");
			matchECItoCharset(15, "ISO-8859-13");
			matchECItoCharset(15, "windows-28603");
			matchECItoCharset(15, "x-IBM921");
		}
		matchECItoCharset(16, "ISO-8859-14");
		{
			matchECItoCharset(16, "ISO_8859-14:1998");
			matchECItoCharset(16, "ISO-8859-14");
			matchECItoCharset(16, "iso-celtic");
			matchECItoCharset(16, "iso-ir-199");
			matchECItoCharset(16, "l8");
			matchECItoCharset(16, "latin8");
		}
		matchECItoCharset(17, "ISO-8859-15");
		{
			matchECItoCharset(17, "8859_15");
			matchECItoCharset(17, "923");
			matchECItoCharset(17, "cp923");
			matchECItoCharset(17, "csISO885915");
			matchECItoCharset(17, "csisolatin0");
			matchECItoCharset(17, "csISOlatin0");
			matchECItoCharset(17, "csisolatin9");
			matchECItoCharset(17, "csISOlatin9");
			matchECItoCharset(17, "ibm-923");
			matchECItoCharset(17, "IBM923");
			matchECItoCharset(17, "IBM-923");
			matchECItoCharset(17, "ISO_8859-15");
			matchECItoCharset(17, "ISO8859_15");
			matchECItoCharset(17, "iso8859_15_fdis");
			matchECItoCharset(17, "ISO8859_15_FDIS");
			matchECItoCharset(17, "ISO8859-15");
			matchECItoCharset(17, "ISO-8859-15");
			matchECItoCharset(17, "l9");
			matchECItoCharset(17, "L9");
			matchECItoCharset(17, "latin0");
			matchECItoCharset(17, "LATIN0");
			matchECItoCharset(17, "Latin-9");
			matchECItoCharset(17, "LATIN9");
			matchECItoCharset(17, "windows-28605");
		}
		matchECItoCharset(18, "ISO-8859-16");
		{
			matchECItoCharset(18, "csISO885916");
			matchECItoCharset(18, "ISO_8859-16");
			matchECItoCharset(18, "ISO_8859-16:2001");
			matchECItoCharset(18, "iso-ir-226");
			matchECItoCharset(18, "l10");
			matchECItoCharset(18, "latin10");
		}
		matchECItoCharset(20, "Shift_JIS");
		{
			matchECItoCharset(20, "cp932");
			matchECItoCharset(20, "cp943c");
			matchECItoCharset(20, "csShiftJIS");
			matchECItoCharset(20, "csWindows31J");
			matchECItoCharset(20, "ms_kanji");
			matchECItoCharset(20, "MS_Kanji");
			matchECItoCharset(20, "shift_jis");
			matchECItoCharset(20, "Shift_JIS");
			matchECItoCharset(20, "shift-jis");
			matchECItoCharset(20, "sjis");
			matchECItoCharset(20, "windows-31j");
			matchECItoCharset(20, "windows-932");
			matchECItoCharset(20, "x-JISAutoDetect");
			matchECItoCharset(20, "x-MS932_0213");
			matchECItoCharset(20, "x-ms-cp932");
			matchECItoCharset(20, "x-sjis");
		}
		matchECItoCharset(21, "windows-1250");
		{
			matchECItoCharset(21, "cp1250");
			matchECItoCharset(21, "cp5346");
			matchECItoCharset(21, "windows-1250");
		}
		matchECItoCharset(22, "windows-1251");
		{
			matchECItoCharset(22, "ansi-1251");
			matchECItoCharset(22, "cp1251");
			matchECItoCharset(22, "cp5347");
			matchECItoCharset(22, "windows-1251");
		}
		matchECItoCharset(23, "windows-1252");
		{
			matchECItoCharset(23, "cp1252");
			matchECItoCharset(23, "cp5348");
			matchECItoCharset(23, "ibm1252");
			matchECItoCharset(23, "ibm-1252");
			matchECItoCharset(23, "windows-1252");
		}
		matchECItoCharset(24, "windows-1256");
		{
			matchECItoCharset(24, "cp1256");
			matchECItoCharset(24, "windows-1256");
			matchECItoCharset(24, "x-windows-1256S");
		}
		matchECItoCharset(25, "UTF-16BE");
		{
			matchECItoCharset(25, "ISO-10646-UCS-2");
			matchECItoCharset(25, "UnicodeBigUnmarked");
			matchECItoCharset(25, "UTF_16BE");
			matchECItoCharset(25, "UTF-16BE");
			matchECItoCharset(25, "windows-1201");
			matchECItoCharset(25, "x-utf-16be");
			matchECItoCharset(25, "X-UTF-16BE");
		}
		matchECItoCharset(26, "UTF-8");
		{
			matchECItoCharset(26, "unicode-1-1-utf-8");
			matchECItoCharset(26, "UTF8");
			matchECItoCharset(26, "UTF-8");
			matchECItoCharset(26, "windows-65001");
		}
		matchECItoCharset(27, "US-ASCII");
		{
			matchECItoCharset(27, "646");
			matchECItoCharset(27, "ANSI_X3.4-1968");
			matchECItoCharset(27, "ANSI_X3.4-1986");
			matchECItoCharset(27, "ASCII");
			matchECItoCharset(27, "ascii7");
			matchECItoCharset(27, "cp367");
			matchECItoCharset(27, "csASCII");
			matchECItoCharset(27, "default");
			matchECItoCharset(27, "IBM367");
			matchECItoCharset(27, "iso_646.irv:1983");
			matchECItoCharset(27, "ISO_646.irv:1991");
			matchECItoCharset(27, "ISO646-US");
			matchECItoCharset(27, "iso-ir-6");
			matchECItoCharset(27, "us");
			matchECItoCharset(27, "US-ASCII");
			matchECItoCharset(27, "windows-20127");
		}
		matchECItoCharset(28, "Big5");
		{
			matchECItoCharset(28, "Big5");
			matchECItoCharset(28, "csBig5");
			matchECItoCharset(28, "windows-950");
			matchECItoCharset(28, "x-windows-950");
		}
		matchECItoCharset(29, "GB2312");
		{
			matchECItoCharset(29, "csGB2312");
			matchECItoCharset(29, "csISO58GB231280");
			matchECItoCharset(29, "EUC_CN");
			matchECItoCharset(29, "euccn");
			matchECItoCharset(29, "euc-cn");
			matchECItoCharset(29, "GB_2312-80");
			matchECItoCharset(29, "gb2312");
			matchECItoCharset(29, "GB2312");
			matchECItoCharset(29, "gb2312-1980");
			matchECItoCharset(29, "gb2312-80");
			matchECItoCharset(29, "x-EUC-CN");
		}
		matchECItoCharset(30, "EUC-KR");
		{
			matchECItoCharset(30, "5601");
			matchECItoCharset(30, "csEUCKR");
			matchECItoCharset(30, "csKSC56011987");
			matchECItoCharset(30, "euc_kr");
			matchECItoCharset(30, "euckr");
			matchECItoCharset(30, "EUC-KR");
			matchECItoCharset(30, "iso-ir-149");
			matchECItoCharset(30, "korean");
			matchECItoCharset(30, "ks_c_5601-1987");
			matchECItoCharset(30, "KS_C_5601-1987");
			matchECItoCharset(30, "KS_C_5601-1989");
			matchECItoCharset(30, "ksc_5601");
			matchECItoCharset(30, "KSC_5601");
			matchECItoCharset(30, "ksc5601");
			matchECItoCharset(30, "ksc5601_1987");
			matchECItoCharset(30, "ksc5601-1987");
			matchECItoCharset(30, "ms949");
			matchECItoCharset(30, "windows-949");
			matchECItoCharset(30, "x-KSC5601");
		}
		matchECItoCharset(31, "GBK");
		{
			matchECItoCharset(31, "chinese");
			matchECItoCharset(31, "CP936");
			matchECItoCharset(31, "GBK");
			matchECItoCharset(31, "iso-ir-58");
			matchECItoCharset(31, "MS936");
			matchECItoCharset(31, "windows-936");
		}
		matchECItoCharset(32, "GB18030");
		{
			matchECItoCharset(32, "gb18030");
			matchECItoCharset(32, "GB18030");
			matchECItoCharset(32, "gb18030-2000");
			matchECItoCharset(32, "windows-54936");
		}
		matchECItoCharset(33, "UTF-16LE");
		{
			matchECItoCharset(33, "UnicodeLittleUnmarked");
			matchECItoCharset(33, "UTF_16LE");
			matchECItoCharset(33, "UTF-16LE");
			matchECItoCharset(33, "windows-1200");
			matchECItoCharset(33, "x-utf-16le");
			matchECItoCharset(33, "X-UTF-16LE");
		}
		matchECItoCharset(34, "UTF-32BE");
		{
			matchECItoCharset(34, "UTF_32BE");
			matchECItoCharset(34, "UTF-32BE");
			matchECItoCharset(34, "X-UTF-32BE");
		}
		matchECItoCharset(35, "UTF-32LE");
		{
			matchECItoCharset(35, "UTF_32LE");
			matchECItoCharset(35, "UTF-32LE");
			matchECItoCharset(35, "X-UTF-32LE");
		}
		
		//display name of charset
		matchCharsetDisplayName("ISO-8859-1", "ISO/IEC 8859-1 Latin alphabet No. 1 (Western European)");
		matchCharsetDisplayName("ISO-8859-2", "ISO/IEC 8859-2 Latin alphabet No. 2 (Central European)");
		matchCharsetDisplayName("ISO-8859-3", "ISO/IEC 8859-3 Latin alphabet No. 3 (South European)");
		matchCharsetDisplayName("ISO-8859-4", "ISO/IEC 8859-4 Latin alphabet No. 4 (North European)");
		matchCharsetDisplayName("ISO-8859-5", "ISO/IEC 8859-5 Latin/Cyrillic alphabet");
		matchCharsetDisplayName("ISO-8859-6", "ISO/IEC 8859-6 Latin/Arabic alphabet");
		matchCharsetDisplayName("ISO-8859-7", "ISO/IEC 8859-7 Latin/Greek alphabet");
		matchCharsetDisplayName("ISO-8859-8", "ISO/IEC 8859-8 Latin/Hebrew alphabet");
		matchCharsetDisplayName("ISO-8859-9", "ISO/IEC 8859-9 Latin alphabet No. 5 (Turkish)");
		matchCharsetDisplayName("ISO-8859-10", "ISO/IEC 8859-10 Latin alphabet No. 6 (Nordic)");
		matchCharsetDisplayName("x-iso-8859-11", "ISO/IEC 8859-11 Latin/Thai alphabet");
		matchCharsetDisplayName("ISO-8859-13", "ISO/IEC 8859-13 Latin alphabet No. 7 (Baltic Rim)");
		matchCharsetDisplayName("ISO-8859-14", "ISO/IEC 8859-14 Latin alphabet No. 8 (Celtic)");
		matchCharsetDisplayName("ISO-8859-15", "ISO/IEC 8859-15 Latin alphabet No. 9 ");
		matchCharsetDisplayName("ISO-8859-16", "ISO/IEC 8859-16 Latin alphabet No. 10 (South-Eastern European)");
		matchCharsetDisplayName("Shift_JIS", "Shift JIS (JIS X 0208 Annex 1 + JIS X 0201)");
		matchCharsetDisplayName("windows-1250", "Windows 1250 Latin 2 (Central Europe)");
		matchCharsetDisplayName("windows-1251", "Windows 1251 Cyrillic");
		matchCharsetDisplayName("windows-1252", "Windows 1252 Latin 1");
		matchCharsetDisplayName("windows-1256", "Windows 1256 Arabic");
		matchCharsetDisplayName("UTF-16BE", "ISO/IEC 10646 Universal Coded Character Set (UCS), encoding scheme: UTF-16BE");
		matchCharsetDisplayName("UTF-8", "ISO/IEC 10646 Universal Coded Character Set (UCS), encoding scheme: UTF-8");
		matchCharsetDisplayName("US-ASCII", "ISO/IEC 646:1991 International Reference Version of ISO 7-bit coded character set ");
		matchCharsetDisplayName("Big5", "Big5 Chinese Character Set");
		matchCharsetDisplayName("GB2312", "GB2312 Chinese Character Set");
		matchCharsetDisplayName("EUC-KR", "KS X 1001 (formerly KS C 5601) Korean Character Set");
		matchCharsetDisplayName("GBK", "GBK (extension of GB2312 for Simplified Chinese)");
		matchCharsetDisplayName("GB18030", "GB18030 Chinese coded character set");
		matchCharsetDisplayName("UTF-16LE", "ISO/IEC 10646 Universal Coded Character Set (UCS), encoding scheme: UTF-16LE");
		matchCharsetDisplayName("UTF-32BE", "ISO/IEC 10646 Universal Coded Character Set (UCS), encoding scheme: UTF-32BE");
		matchCharsetDisplayName("UTF-32LE", "ISO/IEC 10646 Universal Coded Character Set (UCS), encoding scheme: UTF-32LE");
    }
	
	/** All supported ECIs and their information in the type of {@link ECIInfo}
	 * @return A array of all supported ECI information in the type of  {@link ECIInfo}
	 */
	public static ECIInfo[] AllCharasetECIInfo()
    {
        ECIInfo[] allinfo = null;
        if (ECI_TO_CHARSET.containsKey(0))
        {
            allinfo = new ECIInfo[ECI_TO_CHARSET.size() - 1];
        }
        else
        {
            allinfo = new ECIInfo[ECI_TO_CHARSET.size()];
        }

        int pos = 0;

        for (int eci : ECI_TO_CHARSET.keySet())
        {
            if (0 != eci)
            {
                ECIInfo info = new ECIInfo();

                info.ECIValue = eci;
                info.ECI = String.format("%06d", eci);
                info.Charset = ECI_TO_CHARSET.get(eci);
                info.EncodingDisplayName = CHARSET_DISPLAYNAME.get(info.Charset);

                allinfo[pos] = info;
                ++pos;
            }
        }

        return allinfo;
    }
}