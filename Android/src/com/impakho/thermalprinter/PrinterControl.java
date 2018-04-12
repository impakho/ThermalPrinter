package com.impakho.thermalprinter;

import java.io.UnsupportedEncodingException;

public class PrinterControl {

	public int UPC_A = 65;
	public int UPC_E = 66;
	public int EAN13 = 67;
	public int EAN8 = 68;
	public int CODE39 = 69;
	public int ITF = 70;
	public int CODABAR = 71;
	public int CODE93 = 72;
	public int CODE128 = 73;
	public int CHARSET_USA = 0;
	public int CHARSET_FRANCE = 1;
	public int CHARSET_GERMANY = 2;
	public int CHARSET_UK = 3;
	public int CHARSET_DENMARK1 = 4;
	public int CHARSET_SWEDEN = 5;
	public int CHARSET_ITALY = 6;
	public int CHARSET_SPAIN1 = 7;
	public int CHARSET_JAPAN = 8;
	public int CHARSET_NORWAY = 9;
	public int CHARSET_DENMARK2 = 10;
	public int CHARSET_SPAIN2 = 11;
	public int CHARSET_LATINAMERICA = 12;
	public int CHARSET_KOREA = 13;
	public int CHARSET_SLOVENIA = 14;
	public int CHARSET_CROATIA = 14;
	public int CHARSET_CHINA = 15;
	public int CODEPAGE_CP437 = 0;
	public int CODEPAGE_KATAKANA = 1;
	public int CODEPAGE_CP850 = 2;
	public int CODEPAGE_CP860 = 3;
	public int CODEPAGE_CP863 = 4;
	public int CODEPAGE_CP865 = 5;
	public int CODEPAGE_WCP1251 = 6;
	public int CODEPAGE_CP866 = 7;
	public int CODEPAGE_MIK = 8;
	public int CODEPAGE_CP755 = 9;
	public int CODEPAGE_IRAN = 10;
	public int CODEPAGE_CP862 = 15;
	public int CODEPAGE_WCP1252 = 16;
	public int CODEPAGE_WCP1253 = 17;
	public int CODEPAGE_CP852 = 18;
	public int CODEPAGE_CP858 = 19;
	public int CODEPAGE_IRAN2 = 20;
	public int CODEPAGE_LATVIAN = 21;
	public int CODEPAGE_CP864 = 22;
	public int CODEPAGE_ISO_8859_1 = 23;
	public int CODEPAGE_CP737 = 24;
	public int CODEPAGE_WCP1257 = 25;
	public int CODEPAGE_THAI = 26;
	public int CODEPAGE_CP720 = 27;
	public int CODEPAGE_CP855 = 28;
	public int CODEPAGE_CP857 = 29;
	public int CODEPAGE_WCP1250 = 30;
	public int CODEPAGE_CP775 = 31;
	public int CODEPAGE_WCP1254 = 32;
	public int CODEPAGE_WCP1255 = 33;
	public int CODEPAGE_WCP1256 = 34;
	public int CODEPAGE_WCP1258 = 35;
	public int CODEPAGE_ISO_8859_2 = 36;
	public int CODEPAGE_ISO_8859_3 = 37;
	public int CODEPAGE_ISO_8859_4 = 38;
	public int CODEPAGE_ISO_8859_5 = 39;
	public int CODEPAGE_ISO_8859_6 = 40;
	public int CODEPAGE_ISO_8859_7 = 41;
	public int CODEPAGE_ISO_8859_8 = 42;
	public int CODEPAGE_ISO_8859_9 = 43;
	public int CODEPAGE_ISO_8859_15 = 44;
	public int CODEPAGE_THAI2 = 45;
	public int CODEPAGE_CP856 = 46;
	public int CODEPAGE_CP874 = 47;
	public int CODEPAGE_GBK2312 = 255;
	public int ASCII_TAB = '\t';
	public int ASCII_LF = '\n';
	public int ASCII_FF = '\f';
	public int ASCII_CR = '\r';
	public int ASCII_DC2 = 18;
	public int ASCII_ESC = 27;
	public int ASCII_FS = 28;
	public int ASCII_GS = 29;
	
	private int printMode,
    prevByte,      // Last character issued to printer
    column,        // Last horizontal column printed
    maxColumn,     // Page width (output 'wraps' at this point)
    charHeight,    // Height of characters, in 'dots'
    lineSpacing,   // Inter-line spacing (not line height), in dots
    barcodeHeight, // Barcode height in dots, not including text
    maxChunkHeight;
	
	public char example_bitmap[] =  {
			  0x00,0x00,0x00,0x00,0x00,0xe0,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x01,0xf0,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x03,0xf0,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x03,0xf8,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x07,0xf8,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x0f,0xf8,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x1f,0xfc,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x1f,0xfc,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x3f,0xfc,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x7f,0xfe,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x7f,0xfe,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0xff,0xfe,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x01,0xff,0xff,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x03,0xff,0xff,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x03,0xff,0xff,0x00,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x07,0xff,0xff,0x80,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x07,0xff,0xff,0x80,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x07,0xff,0xff,0x80,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x0f,0xff,0xff,0x80,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x0f,0xff,0xff,0x80,0x00,0x00,0x00,
			  0x7f,0xff,0xfc,0x0f,0xff,0xff,0x80,0x00,0x00,0x00,
			  0xff,0xff,0xff,0x0f,0xff,0xff,0x80,0x00,0x00,0x00,
			  0xff,0xff,0xff,0xcf,0xff,0xff,0x80,0x00,0x00,0x00,
			  0xff,0xff,0xff,0xef,0xff,0xff,0x80,0x00,0x00,0x00,
			  0x7f,0xff,0xff,0xf7,0xff,0xff,0x80,0x00,0x00,0x00,
			  0x3f,0xff,0xff,0xff,0xfb,0xff,0x00,0x00,0x00,0x00,
			  0x3f,0xff,0xff,0xff,0xf1,0xff,0x3f,0xf0,0x00,0x00,
			  0x1f,0xff,0xff,0xff,0xf1,0xfe,0xff,0xfe,0x00,0x00,
			  0x0f,0xff,0xff,0xff,0xf1,0xff,0xff,0xff,0xc0,0x00,
			  0x0f,0xff,0xff,0xff,0xe1,0xff,0xff,0xff,0xf8,0x00,
			  0x07,0xff,0xff,0xff,0xe1,0xff,0xff,0xff,0xff,0x00,
			  0x03,0xff,0xff,0xff,0xe1,0xff,0xff,0xff,0xff,0xc0,
			  0x01,0xff,0xff,0x3f,0xe1,0xff,0xff,0xff,0xff,0xe0,
			  0x01,0xff,0xfe,0x07,0xe3,0xff,0xff,0xff,0xff,0xe0,
			  0x00,0xff,0xff,0x03,0xe3,0xff,0xff,0xff,0xff,0xe0,
			  0x00,0x7f,0xff,0x00,0xf7,0xff,0xff,0xff,0xff,0xc0,
			  0x00,0x3f,0xff,0xc0,0xff,0xc0,0x7f,0xff,0xff,0x80,
			  0x00,0x1f,0xff,0xf0,0xff,0x00,0x3f,0xff,0xff,0x00,
			  0x00,0x0f,0xff,0xff,0xff,0x00,0x7f,0xff,0xfc,0x00,
			  0x00,0x07,0xff,0xff,0xff,0x01,0xff,0xff,0xf8,0x00,
			  0x00,0x01,0xff,0xff,0xff,0xff,0xff,0xff,0xf0,0x00,
			  0x00,0x00,0x7f,0xff,0xff,0xff,0xff,0xff,0xc0,0x00,
			  0x00,0x00,0x1f,0xfc,0x7f,0xff,0xff,0xff,0x80,0x00,
			  0x00,0x00,0x7f,0xf8,0x78,0xff,0xff,0xfe,0x00,0x00,
			  0x00,0x00,0xff,0xf0,0x78,0x7f,0xff,0xfc,0x00,0x00,
			  0x00,0x01,0xff,0xe0,0xf8,0x7f,0xff,0xf0,0x00,0x00,
			  0x00,0x03,0xff,0xc0,0xf8,0x3f,0xdf,0xc0,0x00,0x00,
			  0x00,0x07,0xff,0xc1,0xfc,0x3f,0xe0,0x00,0x00,0x00,
			  0x00,0x07,0xff,0x87,0xfc,0x1f,0xf0,0x00,0x00,0x00,
			  0x00,0x0f,0xff,0xcf,0xfe,0x1f,0xf8,0x00,0x00,0x00,
			  0x00,0x0f,0xff,0xff,0xff,0x1f,0xf8,0x00,0x00,0x00,
			  0x00,0x1f,0xff,0xff,0xff,0x1f,0xfc,0x00,0x00,0x00,
			  0x00,0x1f,0xff,0xff,0xff,0xff,0xfc,0x00,0x00,0x00,
			  0x00,0x1f,0xff,0xff,0xff,0xff,0xfe,0x00,0x00,0x00,
			  0x00,0x3f,0xff,0xff,0xff,0xff,0xfe,0x00,0x00,0x00,
			  0x00,0x3f,0xff,0xff,0xff,0xff,0xfe,0x00,0x00,0x00,
			  0x00,0x3f,0xff,0xff,0x3f,0xff,0xfe,0x00,0x00,0x00,
			  0x00,0x7f,0xff,0xff,0x3f,0xff,0xfe,0x00,0x00,0x00,
			  0x00,0x7f,0xff,0xff,0x3f,0xff,0xfe,0x00,0x00,0x00,
			  0x00,0x7f,0xff,0xfe,0x3f,0xff,0xfe,0x00,0x00,0x00,
			  0x00,0xff,0xff,0xfc,0x1f,0xff,0xfe,0x00,0x00,0x00,
			  0x00,0xff,0xff,0xf8,0x1f,0xff,0xfe,0x00,0x00,0x00,
			  0x00,0xff,0xff,0xe0,0x0f,0xff,0xfe,0x00,0x00,0x00,
			  0x01,0xff,0xff,0x80,0x07,0xff,0xfe,0x00,0x00,0x00,
			  0x01,0xff,0xfc,0x00,0x03,0xff,0xfe,0x00,0x00,0x00,
			  0x01,0xff,0xe0,0x00,0x01,0xff,0xfe,0x00,0x00,0x00,
			  0x01,0xff,0x00,0x00,0x00,0xff,0xfe,0x00,0x00,0x00,
			  0x00,0xf8,0x00,0x00,0x00,0x7f,0xfe,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x00,0x1f,0xfe,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x00,0x0f,0xfe,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x00,0x07,0xfe,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x00,0x01,0xfe,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x00,0x00,0xfe,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x00,0x00,0x7e,0x00,0x00,0x00,
			  0x00,0x00,0x00,0x00,0x00,0x00,0x1c,0x00,0x00,0x00
			};
	
	public String print(String printStr){
		StringBuffer retBuf = new StringBuffer();
		char printChar[] = printStr.toCharArray();
		for (int i=0;i<printChar.length;i++) retBuf.append((char)printChar[i]);
		return retBuf.toString();
	}
	
	public String println(String printStr){
		StringBuffer retBuf = new StringBuffer();
		char printChar[] = printStr.toCharArray();
		for (int i=0;i<printChar.length;i++) retBuf.append((char)printChar[i]);
		retBuf.append('\n');
		return retBuf.toString();
	}
	
	public String print(String printStr, String charset){
		StringBuffer retBuf = new StringBuffer();
		byte printByte[] = null;
		try {
			printByte = printStr.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (printByte == null) return "";
		for (int i=0;i<printByte.length;i++){
			if (printByte[i]<0){
				retBuf.append((char)(printByte[i]+256));
			}else{
				retBuf.append((char)printByte[i]);
			}
		}
		return retBuf.toString();
	}
	
	public String println(String printStr, String charset){
		StringBuffer retBuf = new StringBuffer();
		byte printByte[] = null;
		try {
			printByte = printStr.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (printByte == null) return "";
		for (int i=0;i<printByte.length;i++){
			if (printByte[i]<0){
				retBuf.append((char)(printByte[i]+256));
			}else{
				retBuf.append((char)printByte[i]);
			}
		}
		retBuf.append('\n');
		return retBuf.toString();
	}
	
	public String begin(int heatTime){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(wake());
		retBuf.append(reset());
		retBuf.append((char)ASCII_ESC).append('7');
		retBuf.append((char)11).append((char)heatTime).append((char)40);
		int printDensity = 10;
		int printBreakTime = 2;
		retBuf.append((char)ASCII_DC2).append('#').append((char)((printBreakTime<<5)|printDensity));
		retBuf.append((char)ASCII_GS).append('a').append((char)(1<<5));
		maxChunkHeight = 255;
		return retBuf.toString();
	}
	
	public String begin(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(wake());
		retBuf.append(reset());
		retBuf.append((char)ASCII_ESC).append('7');
		retBuf.append((char)11).append((char)120).append((char)40);
		int printDensity = 10;
		int printBreakTime = 2;
		retBuf.append((char)ASCII_DC2).append('#').append((char)((printBreakTime<<5)|printDensity));
		retBuf.append((char)ASCII_GS).append('a').append((char)(1<<5));
		maxChunkHeight = 255;
		return retBuf.toString();
	}
	
	public String reset(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_ESC).append('@');
		prevByte = '\n';
		column = 0;
		maxColumn = 32;
		charHeight = 24;
		lineSpacing = 6;
		barcodeHeight = 50;
		retBuf.append((char)ASCII_ESC).append('D');
		retBuf.append((char)4).append((char)8).append((char)12).append((char)16);
		retBuf.append((char)20).append((char)24).append((char)28).append((char)0);
		return retBuf.toString();
	}
	
	public String setDefault(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(online());
		retBuf.append(justify('L'));
		retBuf.append(inverseOff());
		retBuf.append(doubleHeightOff());
		retBuf.append(setLineHeight(30));
		retBuf.append(boldOff());
		retBuf.append(underlineOff());
		retBuf.append(setBarcodeHeight(50));
		retBuf.append(setSize('S'));
		retBuf.append(setCharset(CHARSET_CHINA));
		retBuf.append(setCodePage(CODEPAGE_GBK2312));
		return retBuf.toString();
	}
	
	public String testPage(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_DC2).append('T');
		return retBuf.toString();
	}
	
	public String setBarcodeHeight(int val){
		StringBuffer retBuf = new StringBuffer();
		if (val<1) val=1;
		barcodeHeight = val;
		retBuf.append((char)ASCII_GS).append('h').append((char)val);
		return retBuf.toString();
	}
	
	public String printBarcode(String text, int type){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(feed(1));
		retBuf.append((char)ASCII_GS).append('H').append((char)2);
		retBuf.append((char)ASCII_GS).append('w').append((char)3);
		retBuf.append((char)ASCII_GS).append('k').append((char)type);
		int len = text.length();
		if (len>255) len=255;
		retBuf.append((char)len);
		for (int i=0;i<len;i++) retBuf.append(text.toCharArray()[i]);
		prevByte = '\n';
		return retBuf.toString();
	}
	
	public String printQRcode(String text, int size){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(feed(1));
		if (size<3) size=3;
		if (size>16) size=16;
		retBuf.append((char)ASCII_GS).append('(').append((char)107).append((char)3);
		retBuf.append((char)0).append((char)49).append((char)67).append((char)size);
		retBuf.append((char)ASCII_GS).append('(').append((char)107).append((char)3);
		retBuf.append((char)0).append((char)49).append((char)69).append((char)48);
		int len=text.length();
		if (len>7089) len=7089;
		int pl=(len+3)%256;
		int ph=((len+3)-pl)/256;
		retBuf.append((char)ASCII_GS).append('(').append((char)107).append((char)pl);
		retBuf.append((char)ph).append((char)49).append((char)80).append((char)48);
		for (int i=0;i<len;i++) retBuf.append(text.toCharArray()[i]);
		retBuf.append((char)ASCII_GS).append('(').append((char)107).append((char)3);
		retBuf.append((char)0).append((char)49).append((char)67).append((char)48);
		retBuf.append((char)ASCII_GS).append('(').append((char)82).append((char)3);
		retBuf.append((char)0).append((char)49).append((char)81).append((char)48);
		prevByte = '\n';
		return retBuf.toString();
	}
	
	private int UPDOWN_MASK = (1 << 2);
	private int BOLD_MASK = (1 << 3);
	private int DOUBLE_HEIGHT_MASK = (1 << 4);
	private int DOUBLE_WIDTH_MASK = (1 << 5);
	private int STRIKE_MASK = (1 << 6);
	
	public String setPrintMode(int mask){
		StringBuffer retBuf = new StringBuffer();
		printMode |= mask;
		retBuf.append(writePrintMode());
		charHeight = ((printMode & DOUBLE_HEIGHT_MASK)==1) ? 48 : 24;
		maxColumn = ((printMode & DOUBLE_WIDTH_MASK)==1) ? 16 : 32;
		return retBuf.toString();
	}
	
	public String unsetPrintMode(int mask){
		StringBuffer retBuf = new StringBuffer();
		printMode &= ~mask;
		retBuf.append(writePrintMode());
		charHeight = ((printMode & DOUBLE_HEIGHT_MASK)==1) ? 48 : 24;
		maxColumn = ((printMode & DOUBLE_WIDTH_MASK)==1) ? 16 : 32;
		return retBuf.toString();
	}
	
	public String writePrintMode(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_ESC).append('!').append((char)printMode);
		return retBuf.toString();
	}
	
	public String normal(){
		StringBuffer retBuf = new StringBuffer();
		printMode = 0;
		retBuf.append(writePrintMode());
		return retBuf.toString();
	}
	
	public String inverseOn(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_GS).append('B').append((char)1);
		return retBuf.toString();
	}
	
	public String inverseOff(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_GS).append('B').append((char)0);
		return retBuf.toString();
	}
	
	public String upsideDownOn(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(setPrintMode(UPDOWN_MASK));
		return retBuf.toString();
	}
	
	public String upsideDownOff(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(unsetPrintMode(UPDOWN_MASK));
		return retBuf.toString();
	}
	
	public String doubleHeightOn(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(setPrintMode(DOUBLE_HEIGHT_MASK));
		return retBuf.toString();
	}
	
	public String doubleHeightOff(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(unsetPrintMode(DOUBLE_HEIGHT_MASK));
		return retBuf.toString();
	}
	
	public String doubleWidthOn(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(setPrintMode(DOUBLE_WIDTH_MASK));
		return retBuf.toString();
	}
	
	public String doubleWidthOff(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(unsetPrintMode(DOUBLE_WIDTH_MASK));
		return retBuf.toString();
	}
	
	public String strikeOn(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(setPrintMode(STRIKE_MASK));
		return retBuf.toString();
	}
	
	public String strikeOff(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(unsetPrintMode(STRIKE_MASK));
		return retBuf.toString();
	}
	
	public String boldOn(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(setPrintMode(BOLD_MASK));
		return retBuf.toString();
	}
	
	public String boldOff(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(unsetPrintMode(BOLD_MASK));
		return retBuf.toString();
	}
	
	public String justify(char value){
		StringBuffer retBuf = new StringBuffer();
		int pos = 0;
		switch (value){
			case 'L':
				pos = 0;
				break;
			case 'C':
				pos = 1;
				break;
			case 'R':
				pos = 2;
				break;
		}
		retBuf.append((char)ASCII_ESC).append('a').append((char)pos);
		return retBuf.toString();
	}
	
	
	public String feed(int x){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_ESC).append('d').append((char)x);
		prevByte = '\n';
		column = 0;
		return retBuf.toString();
	}
	
	public String feedRows(int rows){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_ESC).append('J').append((char)rows);
		prevByte = '\n';
		column = 0;
		return retBuf.toString();
	}
	
	public String flush(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_FF);
		return retBuf.toString();
	}
	
	public String setSize(char value){
		StringBuffer retBuf = new StringBuffer();
		int size;
		switch (value){
			case 'M':
				size = 0x01;
				charHeight = 48;
				maxColumn = 32;
				break;
			case 'L':
				size = 0x11;
				charHeight = 48;
				maxColumn = 16;
				break;
			default:
				size = 0x00;
				charHeight = 24;
				maxColumn = 32;
				break;
		}
		retBuf.append((char)ASCII_GS).append('!').append((char)size);
		prevByte = '\n';
		return retBuf.toString();
	}
	
	public String underlineOn(int weight){
		StringBuffer retBuf = new StringBuffer();
		if (weight>2) weight=2;
		retBuf.append((char)ASCII_ESC).append('-').append((char)weight);
		return retBuf.toString();
	}
	
	public String underlineOn(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_ESC).append('-').append((char)1);
		return retBuf.toString();
	}
	
	public String underlineOff(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_ESC).append('-').append((char)0);
		return retBuf.toString();
	}
	
	public String printBitmap(int w, int h, char[] stream){
		StringBuffer retBuf = new StringBuffer();
		int rowBytes, rowBytesClipped, rowStart, chunkHeight, chunkHeightLimit, x, y, i, si;
		rowBytes = (w + 7) / 8;
		rowBytesClipped = (rowBytes >= 48) ? 48 : rowBytes;
		chunkHeightLimit = 255;
		for (i=rowStart=0;rowStart<h;rowStart+=chunkHeightLimit){
			chunkHeight = h - rowStart;
			if (chunkHeight > chunkHeightLimit) chunkHeight = chunkHeightLimit;
			retBuf.append((char)ASCII_DC2).append('*').append((char)chunkHeight).append((char)rowBytesClipped);
		    for (y=0;y<chunkHeight;y++){
		    	for (x=0;x<rowBytesClipped;x++,i++){
		    		if (i<stream.length) retBuf.append(stream[i]);
		        }
		    	i += rowBytes - rowBytesClipped;
		    }
		}
		prevByte = '\n';
		return retBuf.toString();
	}
	
	public String offline(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_ESC).append('=').append((char)0);
		return retBuf.toString();
	}
	
	public String online(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_ESC).append('=').append((char)1);
		return retBuf.toString();
	}
	
	public String sleep(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(sleepAfter(1));
		return retBuf.toString();
	}
	
	public String sleepAfter(int seconds){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_ESC).append('8').append((char)seconds).append((char)(seconds>>8));
		return retBuf.toString();
	}
	
	public String wake(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)255);
		retBuf.append((char)ASCII_ESC).append('8').append((char)0).append((char)0);
		return retBuf.toString();
	}
	
	public String setLineHeight(int val){
		StringBuffer retBuf = new StringBuffer();
		if (val<24) val=24;
		lineSpacing = val - 24;
		retBuf.append((char)ASCII_ESC).append('3').append((char)val);
		return retBuf.toString();
	}

	public void setMaxChunkHeight(int val){
		maxChunkHeight = val;
	}
	
	public String setCharset(int val){
		StringBuffer retBuf = new StringBuffer();
		if (val>15) val=15;
		retBuf.append((char)ASCII_ESC).append('R').append((char)val);
		return retBuf.toString();
	}
	
	public String setCodePage(int val){
		StringBuffer retBuf = new StringBuffer();
		if (val>47) val=47;
		retBuf.append((char)ASCII_ESC).append('t').append((char)val);
		return retBuf.toString();
	}
	
	public String tab(){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_TAB);
		column = (column + 4) & 0xfc;
		return retBuf.toString();
	}
	
	public String setCharSpacing(int spacing){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append((char)ASCII_ESC).append(' ').append((char)spacing);
		return retBuf.toString();
	}
	
}
