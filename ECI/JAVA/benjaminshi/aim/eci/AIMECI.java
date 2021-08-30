package benjaminshi.aim.eci;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import java.lang.System;

/** The Class used for ECI protocol
 * @author Benjamin Shi (SHI YU, shiyubnu@icloud.com)
 * 
 */
public class AIMECI {

	/** Parse ECI escaped text into List of {@link ECISegment}
	 * @param escapedText the ECI escaped text
	 * @return List of {@link ECISegment} parsed by using ECI protocol
	 */
	public static List<ECISegment> parseECI(String escapedText)
	{
		List<ECISegment> segments = new ArrayList<ECISegment>();
		
		ECISegment seg = null;
        String seg_text = "";

        int posSegment = -1;
		
        Pattern p = Pattern.compile("^\\\\\\d{6}");
        
        Matcher m = p.matcher(escapedText);
        
        if (!m.find())
        {
        	seg = new ECISegment();
            segments.add(seg);
            seg.ECIValue = 0;
            seg_text = "";
            ++posSegment;
        }
        for (int ii = 0;ii < escapedText.length();++ii)
        {
        	m = p.matcher(escapedText.substring(ii));
        	
            if (m.find())
            {
                if (posSegment >= 0)
                {
                    seg.setSegmentText(seg_text);
                }
                
                seg = new ECISegment();
                segments.add(seg);
                seg_text = "";
                ++posSegment;

                seg.setECI(escapedText.substring(ii + 1, ii + 7));
                
                ii += 6;
                continue;
            }

            seg_text += escapedText.substring(ii, ii + 1);
        }

        if (posSegment >= 0)
        {
            seg.setSegmentText(seg_text);
        }

        return segments;
	}
	
	/** Parse ECI escaped byte array into List of {@link ECISegment}
	 * @param escapedData the ECI escaped byte array
	 * @return List of {@link ECISegment} parsed by using ECI protocol
	 */
	public static List<ECISegment> parseECI(byte[] escapedData)
	{
		List<ECISegment> segments = new ArrayList<ECISegment>();

        if (null != escapedData)
        {
            ECISegment seg = null;
            List<Byte> segDataList = new ArrayList<Byte>();
            byte[] segData = null;
            
            int posSegment = -1;

            int ii = 0, jj;

            boolean isECI = false;

            if ((escapedData.length >= 7) && (0x5C == escapedData[0]))
            {
                isECI = true;
                for (ii = 1;ii < 7;++ii)
                {
                    if ((escapedData[ii] < 0x30) || (escapedData[ii] > 0x39))
                    {
                        isECI = false;
                        break;
                    }
                }
            }

            if (!isECI)
            {
                seg = new ECISegment();
                segments.add(seg);
                seg.ECIValue = 0;
                segDataList.clear();
                ++posSegment;
            }

            for (ii = 0; ii < escapedData.length; ++ii)
            {
                isECI = false;

                if ((0x5C == escapedData[ii]) && (ii + 6 < escapedData.length))
                {
                    isECI = true;

                    for (jj = 1;jj < 7;++jj)
                    {
                        if ((escapedData[ii + jj] < 0x30) || (escapedData[ii + jj] > 0x39))
                        {
                            isECI = false;
                            break;
                        }
                    }


                    if (isECI)
                    {
                        if (posSegment >= 0)
                        {
                        	segData = new byte[segDataList.size()];
                        	for (jj = 0;jj < segDataList.size();++jj)
                        	{
                        		segData[jj] = segDataList.get(jj);
                        	}
                            seg.setSegmentData(segData);
                        }

                        seg = new ECISegment();
                        segments.add(seg);
                        segDataList.clear();
                        ++posSegment;

                        try {
							seg.setECI(new String(escapedData, ii + 1, 6, "ISO-8859-1"));
						} catch (UnsupportedEncodingException e) {
							;
						}

                        ii += 6;
                        continue;
                    }
                }

                segDataList.add(escapedData[ii]);
            }

            if (posSegment >= 0)
            {
            	segData = new byte[segDataList.size()];
            	for (jj = 0;jj < segDataList.size();++jj)
            	{
            		segData[jj] = segDataList.get(jj);
            	}
                seg.setSegmentData(segData);
            }
        }

        return segments;
	}
	
	/** Convert List of {@link ECISegment} into ECI escaped transmit data.
	 * @param segments List of {@link ECISegment}
	 * @return ECI escaped transmit data.
	 * @throws ECIException when ECI escaped transmit data is not correct.
	 */
	public static byte[] ToECITransmitData(List<ECISegment> segments) throws ECIException
    {
		byte[] transmitData = null;
		
		int size = 0;
		
        boolean isStart = true;

        int pos = 0;
        
        for (ECISegment seg : segments)
        {
            byte[] eciSequence = seg.getEscapeECISequence();
            byte[] dataSequence = seg.getEscapedSegmentData();

            if ((!isStart) && (null == eciSequence))
            {
                throw new ECIException("ECI error: no set ECI in one eci segment");
            }

            if (null != eciSequence)
            {
            	size += eciSequence.length;
            }

            if (null != dataSequence)
            {
            	size += dataSequence.length;
            }

            if (isStart)
            {
                isStart = false;
            }
        }
        
        if (size > 0)
        {
        	transmitData = new byte[size];
        	isStart = true;
        	for (ECISegment seg : segments)
            {
                byte[] eciSequence = seg.getEscapeECISequence();
                byte[] dataSequence = seg.getEscapedSegmentData();

                if ((!isStart) && (null == eciSequence))
                {
                    throw new ECIException("ECI error: no set ECI in one eci segment");
                }

                if (null != eciSequence)
                {
                	System.arraycopy(eciSequence, 0, transmitData, pos, eciSequence.length);
                	pos += eciSequence.length;
                }

                if (null != dataSequence)
                {
                	System.arraycopy(dataSequence, 0, transmitData, pos, dataSequence.length);
                	pos += dataSequence.length;
                }

                if (isStart)
                {
                    isStart = false;
                }
            }
        }
        
        return transmitData;
    }
	
	/** Convert List of {@link ECISegment} into ECI escaped transmit text.
	 * @param segments List of {@link ECISegment}
	 * @return ECI escaped transmit text.
	 * @throws ECIException when ECI escaped transmit data is not correct.
	 */
	public static String ToECIEscapedTransmitText(List<ECISegment> segments) throws ECIException
    {
		String text = "";
		
		boolean isStart = true;
		
		for (ECISegment seg : segments)
        {
			String eciSequence = seg.getECI();
			String dataSequence = seg.getEscapedSegmentText();

            if ((!isStart) && (eciSequence.length() <= 0))
            {
                throw new ECIException("ECI error: no set ECI in one eci segment");
            }

            if (eciSequence.length() > 0)
            {
            	text += "\\" + eciSequence;
            }
            text += dataSequence;
            
            if (isStart)
            {
                isStart = false;
            }
        }
		
		return text;
    }
	
	/** All supported ECIs and their information in the type of {@link ECIInfo}
	 * @return A array of all supported ECI information in the type of  {@link ECIInfo}
	 */
	public static ECIInfo[] AllCharasetECIInfo()
    {
        return ECISegment.AllCharasetECIInfo();
    }
}

