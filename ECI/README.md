This part of ECI specification include two example source code of ECI analysis, written in C# and JAVA (satify both PC and Android), 
which can analysis both string and byte sequence into ECI segment results, then translate into ECI escaped
byte sequence for further algorithms in two-dimensional barcode encoding and decoding processes.

In the source code, `AIMECI.parseECI(string)` and `AIMECI.parseECI(byte[])` are used for analysis ECI segments
from string input or byte sequence; `AIMECI.ToECITransmitData(List<ECISegment>)` is used to convert ECI segments
analysis result into transmit data under ECI protocol. Above three functions are most commonly used functions in
ECI relative algorithms.

This source code supports not only single ECI, but also multi-ECI in the input data, and you can use `ECISegment` to construct you own multi-ECI or multi-encoding/charset data for barcode and two-dimensional barcode

