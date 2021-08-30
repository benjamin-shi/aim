using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;

namespace benjaminshi.barcode.encode
{
    /// <summary>
    /// Class used for AIM ECI algorithms
    /// </summary>
    public class AIMECI
    {
        /// <summary>
        /// Class for ECI information
        /// </summary>
        public class ECIInfo
        {
            public string ECI { get; set; } = "";
            
            public int ECIValue { get; set; } = -1;

            public int CodePage { get; set; } = 0;

            public string EncodingDisplayName { get; set; } = "";
        }

        /// <summary>
        /// Class for ECI segment
        /// </summary>
        public class ECISegment
        {
            private int _ECIValue = -1;

            public string ECI
            {
                get
                {
                    if ((_ECIValue >= 0) && (_ECIValue <= 999999))
                    {
                        return _ECIValue.ToString("000000");
                    }
                    else
                    {
                        return "";
                    }
                }
                set
                {
                    int val = 0;

                    if (int.TryParse(value, out val))
                    {
                        if ((val >= 0) && (val <= 999999))
                        {
                            ECIValue = val;
                        }
                    }
                }
            }

            public int ECIValue
            {
                get
                {
                    int val = 0;

                    if (int.TryParse(ECI, out val))
                    {
                        if ((val >= 0) && (val <= 999999))
                        {
                            return val;
                        }
                    }
                    return -1;
                }
                set
                {
                    if ((value >= 0) && (value <= 999999))
                    {
                        _ECIValue = value;
                    }
                }
            }

            public byte[] EscapeECISequence
            {
                get
                {
                    string eci = ECI;
                    if ((!String.IsNullOrEmpty(eci)) && (!String.Equals("000000", eci)))
                    {
                        return Encoding.ASCII.GetBytes("\\" + eci);
                    }
                    return null;
                }
            }

            internal static IDictionary<int, int> ECI_TO_CODEPAGE = null;
            internal static IDictionary<int, int> CODEPAGE_TO_ECI = null;

            internal static IDictionary<int, string> CODEPAGE_DISPLAYNAME = null;

            internal static void matchECItoCodePage(int eci, int codePage)
            {
                ECI_TO_CODEPAGE[eci] = codePage;
                CODEPAGE_TO_ECI[codePage] = eci;
            }

            internal static void matchECItoCodePage(int eci, int[] codePages)
            {
                if ((null != codePages) && (codePages.Length > 0))
                    ECI_TO_CODEPAGE[eci] = codePages[0];
                foreach (int codePage in codePages)
                {
                    CODEPAGE_TO_ECI[codePage] = eci;
                }
            }

            
            internal static void matchCodePageDisplayName(int codePage, string name)
            {
                CODEPAGE_DISPLAYNAME[codePage] = name;
            }

            /// <summary>
            /// static Init
            /// </summary>
            static ECISegment()
            {
                ECI_TO_CODEPAGE = new Dictionary<int, int>();
                CODEPAGE_TO_ECI = new Dictionary<int, int>();
                CODEPAGE_DISPLAYNAME = new Dictionary<int, string>();

                //add known charsetECI
                matchECItoCodePage(0, 28591); //Default
                matchECItoCodePage(3, 28591);
                matchECItoCodePage(4, 28592);
                matchECItoCodePage(5, 28593);
                matchECItoCodePage(6, 28594);
                matchECItoCodePage(7, 28595);
                matchECItoCodePage(8, 28596);
                matchECItoCodePage(9, 28597);
                matchECItoCodePage(10, 28598);
                matchECItoCodePage(11, 28599);
                matchECItoCodePage(13, 874);
                matchECItoCodePage(15, 28603);
                //matchECItoCodePage(16, 28604);
                matchECItoCodePage(17, 28605);
                //matchECItoCodePage(18, 28606);
                matchECItoCodePage(20, 932);
                matchECItoCodePage(21, 1250);
                matchECItoCodePage(22, 1251);
                matchECItoCodePage(23, 1252);
                matchECItoCodePage(24, 1256);
                matchECItoCodePage(25, 1201);
                matchECItoCodePage(26, 65001);
                matchECItoCodePage(27, 20127);
                matchECItoCodePage(28, 950);
                matchECItoCodePage(29, 20936);
                matchECItoCodePage(30, 949);
                matchECItoCodePage(31, 936);
                matchECItoCodePage(32, 54936);
                matchECItoCodePage(33, 1200);
                matchECItoCodePage(34, 12001);
                matchECItoCodePage(35, 12000);

                //display name of codepages
                matchCodePageDisplayName(37, "IBM EBCDIC US-Canada");
                matchCodePageDisplayName(437, "OEM United States");
                matchCodePageDisplayName(500, "IBM EBCDIC International");
                matchCodePageDisplayName(708, "Arabic (ASMO 708)");
                matchCodePageDisplayName(720, "Arabic (Transparent ASMO); Arabic (DOS)");
                matchCodePageDisplayName(737, "OEM Greek (formerly 437G); Greek (DOS)");
                matchCodePageDisplayName(775, "OEM Baltic; Baltic (DOS)");
                matchCodePageDisplayName(850, "OEM Multilingual Latin 1; Western European (DOS)");
                matchCodePageDisplayName(852, "OEM Latin 2; Central European (DOS)");
                matchCodePageDisplayName(855, "OEM Cyrillic (primarily Russian)");
                matchCodePageDisplayName(857, "OEM Turkish; Turkish (DOS)");
                matchCodePageDisplayName(858, "OEM Multilingual Latin 1 + Euro symbol");
                matchCodePageDisplayName(860, "OEM Portuguese; Portuguese (DOS)");
                matchCodePageDisplayName(861, "OEM Icelandic; Icelandic (DOS)");
                matchCodePageDisplayName(862, "OEM Hebrew; Hebrew (DOS)");
                matchCodePageDisplayName(863, "OEM French Canadian; French Canadian (DOS)");
                matchCodePageDisplayName(864, "OEM Arabic; Arabic (864)");
                matchCodePageDisplayName(865, "OEM Nordic; Nordic (DOS)");
                matchCodePageDisplayName(866, "OEM Russian; Cyrillic (DOS)");
                matchCodePageDisplayName(869, "OEM Modern Greek; Greek, Modern (DOS)");
                matchCodePageDisplayName(870, "IBM EBCDIC Multilingual/ROECE (Latin 2); IBM EBCDIC Multilingual Latin 2");
                matchCodePageDisplayName(874, "ISO 8859-15 Thai");
                matchCodePageDisplayName(875, "IBM EBCDIC Greek Modern");
                matchCodePageDisplayName(932, "Japanese (Shift-JIS)");
                matchCodePageDisplayName(936, "GBK");
                matchCodePageDisplayName(949, "Korean (KS X 1001)");
                matchCodePageDisplayName(950, "Chinese Traditional (Big5)");
                matchCodePageDisplayName(1026, "IBM EBCDIC Turkish (Latin 5)");
                matchCodePageDisplayName(1047, "IBM EBCDIC Latin 1/Open System");
                matchCodePageDisplayName(1140, "IBM EBCDIC US-Canada (037 + Euro symbol); IBM EBCDIC (US-Canada-Euro)");
                matchCodePageDisplayName(1141, "IBM EBCDIC Germany (20273 + Euro symbol); IBM EBCDIC (Germany-Euro)");
                matchCodePageDisplayName(1142, "IBM EBCDIC Denmark-Norway (20277 + Euro symbol); IBM EBCDIC (Denmark-Norway-Euro)");
                matchCodePageDisplayName(1143, "IBM EBCDIC Finland-Sweden (20278 + Euro symbol); IBM EBCDIC (Finland-Sweden-Euro)");
                matchCodePageDisplayName(1144, "IBM EBCDIC Italy (20280 + Euro symbol); IBM EBCDIC (Italy-Euro)");
                matchCodePageDisplayName(1145, "IBM EBCDIC Latin America-Spain (20284 + Euro symbol); IBM EBCDIC (Spain-Euro)");
                matchCodePageDisplayName(1146, "IBM EBCDIC United Kingdom (20285 + Euro symbol); IBM EBCDIC (UK-Euro)");
                matchCodePageDisplayName(1147, "IBM EBCDIC France (20297 + Euro symbol); IBM EBCDIC (France-Euro)");
                matchCodePageDisplayName(1148, "IBM EBCDIC International (500 + Euro symbol); IBM EBCDIC (International-Euro)");
                matchCodePageDisplayName(1149, "IBM EBCDIC Icelandic (20871 + Euro symbol); IBM EBCDIC (Icelandic-Euro)");
                matchCodePageDisplayName(1200, "UTF-16LE");
                matchCodePageDisplayName(1201, "UTF-16BE");
                matchCodePageDisplayName(1250, "Windows 1250: Latin 2 (Central Europe)");
                matchCodePageDisplayName(1251, "Windows 1251: Cyrillic (Slavic)");
                matchCodePageDisplayName(1252, "Windows 1252: Latin 1 (ANSI)");
                matchCodePageDisplayName(1253, "ANSI Greek; Greek (Windows)");
                matchCodePageDisplayName(1254, "ANSI Turkish; Turkish (Windows)");
                matchCodePageDisplayName(1255, "ANSI Hebrew; Hebrew (Windows)");
                matchCodePageDisplayName(1256, "Windows 1256: Arabic");
                matchCodePageDisplayName(1257, "ANSI Baltic; Baltic (Windows)");
                matchCodePageDisplayName(1258, "ANSI/OEM Vietnamese; Vietnamese (Windows)");
                matchCodePageDisplayName(1361, "Korean (Johab)");
                matchCodePageDisplayName(10000, "MAC Roman; Western European (Mac)");
                matchCodePageDisplayName(10001, "Japanese (Mac)");
                matchCodePageDisplayName(10002, "MAC Traditional Chinese (Big5); Chinese Traditional (Mac)");
                matchCodePageDisplayName(10003, "Korean (Mac)");
                matchCodePageDisplayName(10004, "Arabic (Mac)");
                matchCodePageDisplayName(10005, "Hebrew (Mac)");
                matchCodePageDisplayName(10006, "Greek (Mac)");
                matchCodePageDisplayName(10007, "Cyrillic (Mac)");
                matchCodePageDisplayName(10008, "MAC Simplified Chinese (GB 2312); Chinese Simplified (Mac)");
                matchCodePageDisplayName(10010, "Romanian (Mac)");
                matchCodePageDisplayName(10017, "Ukrainian (Mac)");
                matchCodePageDisplayName(10021, "Thai (Mac)");
                matchCodePageDisplayName(10029, "MAC Latin 2; Central European (Mac)");
                matchCodePageDisplayName(10079, "Icelandic (Mac)");
                matchCodePageDisplayName(10081, "Turkish (Mac)");
                matchCodePageDisplayName(10082, "Croatian (Mac)");
                matchCodePageDisplayName(12000, "UTF-32LE");
                matchCodePageDisplayName(12001, "UTF-32BE");
                matchCodePageDisplayName(20000, "CNS Taiwan; Chinese Traditional (CNS)");
                matchCodePageDisplayName(20001, "TCA Taiwan");
                matchCodePageDisplayName(20002, "Eten Taiwan; Chinese Traditional (Eten)");
                matchCodePageDisplayName(20003, "IBM5550 Taiwan");
                matchCodePageDisplayName(20004, "TeleText Taiwan");
                matchCodePageDisplayName(20005, "Wang Taiwan");
                matchCodePageDisplayName(20105, "IA5 (IRV International Alphabet No. 5, 7-bit); Western European (IA5)");
                matchCodePageDisplayName(20106, "IA5 German (7-bit)");
                matchCodePageDisplayName(20107, "IA5 Swedish (7-bit)");
                matchCodePageDisplayName(20108, "IA5 Norwegian (7-bit)");
                matchCodePageDisplayName(20127, "US-ASCII (7-bit)");
                matchCodePageDisplayName(20261, "T.61");
                matchCodePageDisplayName(20269, "ISO 6937 Non-Spacing Accent");
                matchCodePageDisplayName(20273, "IBM EBCDIC Germany");
                matchCodePageDisplayName(20277, "IBM EBCDIC Denmark-Norway");
                matchCodePageDisplayName(20278, "IBM EBCDIC Finland-Sweden");
                matchCodePageDisplayName(20280, "IBM EBCDIC Italy");
                matchCodePageDisplayName(20284, "IBM EBCDIC Latin America-Spain");
                matchCodePageDisplayName(20285, "IBM EBCDIC United Kingdom");
                matchCodePageDisplayName(20290, "IBM EBCDIC Japanese Katakana Extended");
                matchCodePageDisplayName(20297, "IBM EBCDIC France");
                matchCodePageDisplayName(20420, "IBM EBCDIC Arabic");
                matchCodePageDisplayName(20423, "IBM EBCDIC Greek");
                matchCodePageDisplayName(20424, "IBM EBCDIC Hebrew");
                matchCodePageDisplayName(20833, "IBM EBCDIC Korean Extended");
                matchCodePageDisplayName(20838, "IBM EBCDIC Thai");
                matchCodePageDisplayName(20866, "Russian (KOI8-R); Cyrillic (KOI8-R)");
                matchCodePageDisplayName(20871, "IBM EBCDIC Icelandic");
                matchCodePageDisplayName(20880, "IBM EBCDIC Cyrillic Russian");
                matchCodePageDisplayName(20905, "IBM EBCDIC Turkish");
                matchCodePageDisplayName(20924, "IBM EBCDIC Latin 1/Open System (1047 + Euro symbol)");
                matchCodePageDisplayName(20932, "Japanese (JIS 0208-1990 and 0121-1990)");
                matchCodePageDisplayName(20936, "GB2312-1980");
                matchCodePageDisplayName(20949, "Korean Wansung");
                matchCodePageDisplayName(21025, "IBM EBCDIC Cyrillic Serbian-Bulgarian");
                matchCodePageDisplayName(21027, "Ext Alpha Lowercase");
                matchCodePageDisplayName(21866, "Ukrainian (KOI8-U); Cyrillic (KOI8-U)");
                matchCodePageDisplayName(28591, "ISO 8859-1 Latin 1");
                matchCodePageDisplayName(28592, "ISO 8859-2 Central European");
                matchCodePageDisplayName(28593, "ISO 8859-3 Latin 3");
                matchCodePageDisplayName(28594, "ISO 8859-4 Baltic");
                matchCodePageDisplayName(28595, "ISO 8859-5 Cyrillic");
                matchCodePageDisplayName(28596, "ISO 8859-6 Arabic");
                matchCodePageDisplayName(28597, "ISO 8859-7 Greek");
                matchCodePageDisplayName(28598, "ISO 8859-8 Hebrew");
                matchCodePageDisplayName(28599, "ISO 8859-9 Turkish");
                matchCodePageDisplayName(28603, "ISO 8859-13 Estonian");
                matchCodePageDisplayName(28605, "ISO 8859-15 Latin 9");
                matchCodePageDisplayName(38598, "ISO 8859-8 Hebrew; Hebrew (ISO-Logical)");
                matchCodePageDisplayName(50220, "ISO 2022 Japanese with no halfwidth Katakana; Japanese (JIS)");
                matchCodePageDisplayName(50221, "ISO 2022 Japanese with halfwidth Katakana; Japanese (JIS-Allow 1 byte Kana)");
                matchCodePageDisplayName(50222, "ISO 2022 Japanese JIS X 0201-1989; Japanese (JIS-Allow 1 byte Kana - SO/SI)");
                matchCodePageDisplayName(50225, "ISO 2022 Korean");
                matchCodePageDisplayName(50227, "ISO 2022 Simplified Chinese; Chinese Simplified (ISO 2022)");
                matchCodePageDisplayName(50229, "ISO 2022 Traditional Chinese");
                matchCodePageDisplayName(51949, "EUC Korean");
                matchCodePageDisplayName(52936, "HZ-GB2312 Simplified Chinese; Chinese Simplified (HZ)");
                matchCodePageDisplayName(54936, "GB18030");
                matchCodePageDisplayName(57002, "ISCII Devanagari");
                matchCodePageDisplayName(57003, "ISCII Bengali");
                matchCodePageDisplayName(57004, "ISCII Tamil");
                matchCodePageDisplayName(57005, "ISCII Telugu");
                matchCodePageDisplayName(57006, "ISCII Assamese");
                matchCodePageDisplayName(57007, "ISCII Odia (was Oriya)");
                matchCodePageDisplayName(57008, "ISCII Kannada");
                matchCodePageDisplayName(57009, "ISCII Malayalam");
                matchCodePageDisplayName(57010, "ISCII Gujarati");
                matchCodePageDisplayName(57011, "ISCII Punjabi");
                matchCodePageDisplayName(65000, "UTF-7");
                matchCodePageDisplayName(65001, "UTF-8");
            }

            public static ECIInfo[] AllCharasetECIInfo()
            {
                ECIInfo[] allinfo = null;
                if (ECI_TO_CODEPAGE.ContainsKey(0))
                {
                    allinfo = new ECIInfo[ECI_TO_CODEPAGE.Count - 1];
                }
                else
                {
                    allinfo = new ECIInfo[ECI_TO_CODEPAGE.Count];
                }

                int pos = 0;

                foreach (int eci in ECI_TO_CODEPAGE.Keys)
                {
                    if (0 != eci)
                    {
                        ECIInfo info = new ECIInfo();

                        info.ECIValue = eci;
                        info.ECI = eci.ToString("000000");
                        info.CodePage = ECI_TO_CODEPAGE[eci];
                        info.EncodingDisplayName = CODEPAGE_DISPLAYNAME[info.CodePage];

                        allinfo[pos] = info;
                        ++pos;
                    }
                }

                return allinfo;
            }

            public Encoding TextEncoding
            {
                get
                {
                    Encoding enc = null;

                    int codePage = -1;

                    if ((ECIValue >= 0) && (ECIValue <= 999999))
                    {
                        if (ECI_TO_CODEPAGE.ContainsKey(ECIValue))
                        {
                            codePage = ECI_TO_CODEPAGE[ECIValue];

                            try
                            {
                                enc = Encoding.GetEncoding(codePage);
                            }
                            catch
                            {
                                enc = null;
                            }
                        }
                    }
                    return enc;
                }
            }

            public int CodePage
            {
                get
                {
                    int codePage = -1;

                    if (null != TextEncoding)
                    {
                        codePage = TextEncoding.CodePage;
                    }

                    return codePage;
                }
                set
                {
                    if (CODEPAGE_TO_ECI.ContainsKey(value))
                    {
                        ECIValue = CODEPAGE_TO_ECI[value];
                    }
                }
            }

            public string EncodingWebName
            {
                get
                {
                    string name = "";

                    if (null != TextEncoding)
                    {
                        name = TextEncoding.WebName;
                    }

                    return name;
                }
            }

            public string EncodingName
            {
                get
                {
                    string name = "";

                    if (null != TextEncoding)
                    {
                        name = TextEncoding.EncodingName;
                    }

                    return name;
                }
            }

            private byte[] _SegmentData = null;

            public byte[] SegmentData 
            {
                get
                {
                    return _SegmentData;
                }
                set
                {
                    _SegmentData = null;
                    if (null != value)
                    {
                        List<byte> data = new List<byte>(value.Length);
                        byte iiByte;
                        int ii;

                        for (ii = 0;ii < value.Length;++ii)
                        {
                            iiByte = value[ii];
                            data.Add(iiByte);
                            if ((0x5C == iiByte) && (ii + 1 < value.Length) && (0x5C == value[ii + 1]))
                            {
                                ++ii;
                            }
                        }

                        _SegmentData = data.ToArray();
                    }
                }
            }

            public string SegmentText
            {
                get
                {
                    string text = "";

                    Encoding enc = TextEncoding;

                    if (null != enc)
                    {
                        try
                        {
                            text = enc.GetString(SegmentData);
                        }
                        catch
                        {
                            text = "";
                        }
                    }
                    return text;
                }
                set
                {
                    Encoding enc = TextEncoding;

                    if (null != enc)
                    {
                        try
                        {
                            SegmentData = enc.GetBytes(value);
                        }
                        catch
                        {
                            SegmentData = null;
                        }
                    }
                }
            }

            public byte[] EscapedSegmentData
            {
                get
                {
                    if (null == SegmentData)
                    {
                        return null;
                    }
                    else
                    {
                        List<byte> data = new List<byte>(SegmentData.Length);

                        foreach (byte iiByte in SegmentData)
                        {
                            data.Add(iiByte);
                            if (0x5C == iiByte)
                            {
                                data.Add(iiByte);
                            }
                        }

                        return data.ToArray();
                    }
                }
            }

            public string EscapedSegmentText
            {
                get
                {
                    string text = "";

                    Encoding enc = TextEncoding;

                    if (null != enc)
                    {
                        try
                        {
                            text = enc.GetString(EscapedSegmentData);
                        }
                        catch
                        {
                            text = "";
                        }
                    }
                    return text;
                }
            }

            
        }

        /// <summary>
        /// Parse and analysis string input ECI data into ECI segments
        /// </summary>
        /// <param name="data">the data is string format input ECI data, such as "\000003ABC\000029中文".
        /// This function will analysis this data into ECI segments.</param>
        /// <returns>ECI analysis result</returns>
        public static List<ECISegment> parseECI(string data)
        {
            List<ECISegment> segments = new List<ECISegment>();

            if (String.IsNullOrEmpty(data)) data = "";

            ECISegment seg = null;
            string seg_text = "";

            int posSegment = -1;

            if (!Regex.IsMatch(data, @"^\\\d{6}"))
            {
                seg = new ECISegment();
                segments.Add(seg);
                seg.ECIValue = 0;
                seg_text = "";
                ++posSegment;
            }
            for (int ii = 0;ii < data.Length;++ii)
            {
                if (Regex.IsMatch(data.Substring(ii), @"^\\\d{6}"))
                {
                    if (posSegment >= 0)
                    {
                        seg.SegmentText = seg_text;
                    }
                    
                    seg = new ECISegment();
                    segments.Add(seg);
                    seg_text = "";
                    ++posSegment;

                    seg.ECI = data.Substring(ii + 1, 6);
                    
                    ii += 6;
                    continue;
                }

                seg_text += data.Substring(ii, 1);
            }

            if (posSegment >= 0)
            {
                seg.SegmentText = seg_text;
            }

            return segments;
        }

        /// <summary>
        /// Parse and analysis transmit data under ECI protocol into ECI segments
        /// </summary>
        /// <param name="data">
        /// the data is the byte stream of transmit data under ECI protocol, such as "5C 30 30 30 30 32 39 B1 B1 BE A9 5C 30 30 30 30 30 33 42 65 69 6A 69 6E 67".
        /// This function will analysis this data into ECI segments.</param>
        /// <returns>ECI analysis result</returns>
        public static List<ECISegment> parseECI(byte[] data)
        {
            List<ECISegment> segments = new List<ECISegment>();

            if (null != data)
            {
                ECISegment seg = null;
                List<byte> segData = new List<byte>();

                int posSegment = -1;

                int ii = 0, jj;

                bool isECI = false;

                if ((data.Length >= 7) && (0x5C == data[0]))
                {
                    isECI = true;
                    for (ii = 1;ii < 7;++ii)
                    {
                        if ((data[ii] < 0x30) || (data[ii] > 0x39))
                        {
                            isECI = false;
                            break;
                        }
                    }
                }

                if (!isECI)
                {
                    seg = new ECISegment();
                    segments.Add(seg);
                    seg.ECIValue = 0;
                    segData.Clear();
                    ++posSegment;
                }

                for (ii = 0; ii < data.Length; ++ii)
                {
                    isECI = false;

                    if ((0x5C == data[ii]) && (ii + 6 < data.Length))
                    {
                        isECI = true;

                        for (jj = 1;jj < 7;++jj)
                        {
                            if ((data[ii + jj] < 0x30) || (data[ii + jj] > 0x39))
                            {
                                isECI = false;
                                break;
                            }
                        }


                        if (isECI)
                        {
                            if (posSegment >= 0)
                            {
                                seg.SegmentData = segData.ToArray();
                            }

                            seg = new ECISegment();
                            segments.Add(seg);
                            segData.Clear();
                            ++posSegment;

                            seg.ECI = Encoding.ASCII.GetString(data, ii + 1, 6);

                            ii += 6;
                            continue;
                        }
                    }

                    segData.Add(data[ii]);
                }

                if (posSegment >= 0)
                {
                    seg.SegmentData = segData.ToArray();
                }
            }

            return segments;
        }

        /// <summary>
        /// The function used to convert ECI segments analysis result into transmit data under ECI protocol.
        /// </summary>
        /// <param name="segments">ECI segments analysis result</param>
        /// <returns>byte sequence of transmit data under ECI protocol.</returns>
        public static byte[] ToECITransmitData(List<ECISegment> segments)
        {
            List<byte> data = new List<byte>();

            bool isStart = true;

            foreach (ECISegment seg in segments)
            {
                byte[] eciSequence = seg.EscapeECISequence;
                byte[] dataSequence = seg.EscapedSegmentData;

                if ((!isStart) && (null == eciSequence))
                {
                    throw new Exception("ECI error: no set ECI in one eci segment");
                }

                if (null != eciSequence)
                {
                    data.AddRange(eciSequence);
                }

                if (null != dataSequence)
                {
                    data.AddRange(dataSequence);
                }

                if (isStart)
                {
                    isStart = false;
                }
            }

            if (data.Count <= 0)
            {
                return null;
            }
            else
            {
                return data.ToArray();
            }
        }

        /// <summary>
        /// The function return all current supported ECI Information
        /// </summary>
        /// <returns>All current supported ECI Information</returns>
        public static ECIInfo[] AllCharasetECIInfo()
        {
            return ECISegment.AllCharasetECIInfo();
        }
    }
}
