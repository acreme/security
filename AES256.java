
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class AES {
	
	//total number of rounds for AES256
	final static int rounds = 15;
	//default state matrix
	private static final byte[][] st = {{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
	
	//table giving by prof Young for mixCols
	final static int[] LogTable = {
		0,   0,  25,   1,  50,   2,  26, 198,  75, 199,  27, 104,  51, 238, 223,   3, 
		100,   4, 224,  14,  52, 141, 129, 239,  76, 113,   8, 200, 248, 105,  28, 193, 
		125, 194,  29, 181, 249, 185,  39, 106,  77, 228, 166, 114, 154, 201,   9, 120, 
		101,  47, 138,   5,  33,  15, 225,  36,  18, 240, 130,  69,  53, 147, 218, 142, 
		150, 143, 219, 189,  54, 208, 206, 148,  19,  92, 210, 241,  64,  70, 131,  56, 
		102, 221, 253,  48, 191,   6, 139,  98, 179,  37, 226, 152,  34, 136, 145,  16, 
		126, 110,  72, 195, 163, 182,  30,  66,  58, 107,  40,  84, 250, 133,  61, 186, 
		43, 121,  10,  21, 155, 159,  94, 202,  78, 212, 172, 229, 243, 115, 167,  87, 
		175,  88, 168,  80, 244, 234, 214, 116,  79, 174, 233, 213, 231, 230, 173, 232, 
		44, 215, 117, 122, 235,  22,  11, 245,  89, 203,  95, 176, 156, 169,  81, 160, 
		127,  12, 246, 111,  23, 196,  73, 236, 216,  67,  31,  45, 164, 118, 123, 183, 
		204, 187,  62,  90, 251,  96, 177, 134,  59,  82, 161, 108, 170,  85,  41, 157, 
		151, 178, 135, 144,  97, 190, 220, 252, 188, 149, 207, 205,  55,  63,  91, 209, 
		83,  57, 132,  60,  65, 162, 109,  71,  20,  42, 158,  93,  86, 242, 211, 171, 
		68,  17, 146, 217,  35,  32,  46, 137, 180, 124, 184,  38, 119, 153, 227, 165, 
		103,  74, 237, 222, 197,  49, 254,  24,  13,  99, 140, 128, 192, 247, 112,   7};
	//secondtablegiven by prof Young for mixCols
	final static int[] AlogTable = {
		1,   3,   5,  15,  17,  51,  85, 255,  26,  46, 114, 150, 161, 248,  19,  53, 
		95, 225,  56,  72, 216, 115, 149, 164, 247,   2,   6,  10,  30,  34, 102, 170, 
		229,  52,  92, 228,  55,  89, 235,  38, 106, 190, 217, 112, 144, 171, 230,  49, 
		83, 245,   4,  12,  20,  60,  68, 204,  79, 209, 104, 184, 211, 110, 178, 205, 
		76, 212, 103, 169, 224,  59,  77, 215,  98, 166, 241,   8,  24,  40, 120, 136, 
		131, 158, 185, 208, 107, 189, 220, 127, 129, 152, 179, 206,  73, 219, 118, 154, 
		181, 196,  87, 249,  16,  48,  80, 240,  11,  29,  39, 105, 187, 214,  97, 163, 
		254,  25,  43, 125, 135, 146, 173, 236,  47, 113, 147, 174, 233,  32,  96, 160, 
		251,  22,  58,  78, 210, 109, 183, 194,  93, 231,  50,  86, 250,  21,  63,  65, 
		195,  94, 226,  61,  71, 201,  64, 192,  91, 237,  44, 116, 156, 191, 218, 117, 
		159, 186, 213, 100, 172, 239,  42, 126, 130, 157, 188, 223, 122, 142, 137, 128, 
		155, 182, 193,  88, 232,  35, 101, 175, 234,  37, 111, 177, 200,  67, 197,  84, 
		252,  31,  33,  99, 165, 244,   7,   9,  27,  45, 119, 153, 176, 203,  70, 202, 
		69, 207,  74, 222, 121, 139, 134, 145, 168, 227,  62,  66, 198,  81, 243,  14, 
		18,  54,  90, 238,  41, 123, 141, 140, 143, 138, 133, 148, 167, 242,  13,  23, 
		57,  75, 221, 124, 132, 151, 162, 253,  28,  36, 108, 180, 199,  82, 246,   1};
	//sbox from wikipedia
	final static int[] sbox = {
		99, 124, 119, 123, 242, 107, 111, 197, 48, 1, 103, 43, 254, 215, 171, 118,
		202, 130, 201, 125, 250, 89, 71, 240, 173, 212, 162, 175, 156, 164, 114, 192, 
		183, 253, 147, 38, 54, 63, 247, 204, 52, 165, 229, 241, 113, 216, 49, 21, 
		4, 199, 35, 195, 24, 150, 5, 154, 7, 18, 128, 226, 235, 39, 178, 117, 
		9, 131, 44, 26, 27, 110, 90, 160, 82, 59, 214, 179, 41, 227, 47, 132, 
		83, 209, 0, 237, 32, 252, 177, 91, 106, 203, 190, 57, 74, 76, 88, 207, 
		208, 239, 170, 251, 67, 77, 51, 133, 69, 249, 2, 127, 80, 60, 159, 168, 
		81, 163, 64, 143, 146, 157, 56, 245, 188, 182, 218, 33, 16, 255, 243, 210, 
		205, 12, 19, 236, 95, 151, 68, 23, 196, 167, 126, 61, 100, 93, 25, 115, 
		96, 129, 79, 220, 34, 42, 144, 136, 70, 238, 184, 20, 222, 94, 11, 219, 
		224, 50, 58, 10, 73, 6, 36, 92, 194, 211, 172, 98, 145, 149, 228, 121, 
		231, 200, 55, 109, 141, 213, 78, 169, 108, 86, 244, 234, 101, 122, 174, 8, 
		186, 120, 37, 46, 28, 166, 180, 198, 232, 221, 116, 31, 75, 189, 139, 138, 
		112, 62, 181, 102, 72, 3, 246, 14, 97, 53, 87, 185, 134, 193, 29, 158, 
		225, 248, 152, 17, 105, 217, 142, 148, 155, 30, 135, 233, 206, 85, 40, 223, 
		140, 161, 137, 13, 191, 230, 66, 104, 65, 153, 45, 15, 176, 84, 187, 22};
	//inverted sbox from wikipedia
	final static int[] i_sbox = {
		82, 9, 106, 213, 48, 54, 165, 56, 191, 64, 163, 158, 129, 243, 215, 251, 
		124, 227, 57, 130, 155, 47, 255, 135, 52, 142, 67, 68, 196, 222, 233, 203, 
		84, 123, 148, 50, 166, 194, 35, 61, 238, 76, 149, 11, 66, 250, 195, 78, 
		8, 46, 161, 102, 40, 217, 36, 178, 118, 91, 162, 73, 109, 139, 209, 37, 
		114, 248, 246, 100, 134, 104, 152, 22, 212, 164, 92, 204, 93, 101, 182, 146, 
		108, 112, 72, 80, 253, 237, 185, 218, 94, 21, 70, 87, 167, 141, 157, 132, 
		144, 216, 171, 0, 140, 188, 211, 10, 247, 228, 88, 5, 184, 179, 69, 6, 
		208, 44, 30, 143, 202, 63, 15, 2, 193, 175, 189, 3, 1, 19, 138, 107, 
		58, 145, 17, 65, 79, 103, 220, 234, 151, 242, 207, 206, 240, 180, 230, 115, 
		150, 172, 116, 34, 231, 173, 53, 133, 226, 249, 55, 232, 28, 117, 223, 110, 
		71, 241, 26, 113, 29, 41, 197, 137, 111, 183, 98, 14, 170, 24, 190, 27, 
		252, 86, 62, 75, 198, 210, 121, 32, 154, 219, 192, 254, 120, 205, 90, 244, 
		31, 221, 168, 51, 136, 7, 199, 49, 177, 18, 16, 89, 39, 128, 236, 95, 
		96, 81, 127, 169, 25, 181, 74, 13, 45, 229, 122, 159, 147, 201, 156, 239, 
		160, 224, 59, 77, 174, 42, 245, 176, 200, 235, 187, 60, 131, 83, 153, 97, 
		23, 43, 4, 126, 186, 119, 214, 38, 225, 105, 20, 99, 85, 33, 12, 125};
	//values for the rcon box used in the key expansion algorithm
	final static int[] rcon = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512};
	//take each pair of hex characters, seperate and use as row/column lookup for sbox
	public static void subBytes(){
		int a = 0;
		int b = 0;
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				int num = st[i][j];
				if(num < 0){
					num = num + 256;
				}
				String temp = Integer.toHexString(num);
				if(temp.length() == 1){
					temp = "0" + temp;
				}
				a = Integer.parseInt(temp.substring(0,1), 16);
				b = Integer.parseInt(temp.substring(1,2), 16);
				st[i][j] =(byte)sbox[(a*16) + b];
			}
		}
	}
	//subByte alternative used in the key expansion algorith, same principle as above
	public static byte[] subBytes(byte[] col){
		byte[] temp = new byte[4];
		int a = 0;
		int b = 0;
		for(int i = 0; i < 4; i++){
			int num = col[i];
			if(num < 0){
				num = num + 256;
			}
			String x = Integer.toHexString(num);
			if(x.length() == 1){
				x = "0" + x;
			}
			a = Integer.parseInt(x.substring(0,1), 16);
			b = Integer.parseInt(x.substring(1,2), 16);
			temp[i] = (byte)sbox[(a*16) + b];
		}
		
		return temp;
	}
	//take each pair of hex characters, seperate and use as row/column lookup for i_sbox
	public static void inv_subBytes(){
		int a = 0;
		int b = 0;
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				int num = st[i][j];
				if(num < 0){
					num = num + 256;
				}
				String temp = Integer.toHexString(num);
				if(temp.length() == 1){
					temp = "0" + temp;
				}
				a = Integer.parseInt(temp.substring(0,1), 16);
				b = Integer.parseInt(temp.substring(1,2), 16);
				st[i][j] =(byte)i_sbox[(a*16) + b];
			}
		}
	}
	
	//shift rows, manually shifts rows 1, 2, and 3 by 1, 2 or 3 shifts (shifts = row)
	public static void shiftRows(){
		byte[][] temp = {{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
		for(int m = 0; m < 4; m++){
			for(int n = 0; n < 4; n++){
				temp[m][n] = st[m][n];
			}
		}
		for (int i = 0; i < 4; i++){
			if(i == 1){
				st[0][1] = temp[1][1];
				st[1][1] = temp[2][1];
				st[2][1] = temp[3][1];
				st[3][1] = temp[0][1];
			}
			if(i == 2){
				st[0][2] = temp[2][2];
				st[1][2] = temp[3][2];
				st[2][2] = temp[0][2];
				st[3][2] = temp[1][2];
			}
			if(i == 3){
				st[0][3] = temp[3][3];
				st[1][3] = temp[0][3];
				st[2][3] = temp[1][3];
				st[3][3] = temp[2][3];
			}
		}
	}
	//shifts rows back, where shifts = rows
	public static void inv_shiftRows(){
		byte[][] temp = {{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
		for(int m = 0; m < 4; m++){
			for(int n = 0; n < 4; n++){
				temp[m][n] = st[m][n];
			}
		}
		for (int i = 0; i < 4; i++){
			if(i == 1){
				st[0][1] = temp[3][1];
				st[1][1] = temp[0][1];
				st[2][1] = temp[1][1];
				st[3][1] = temp[2][1];
			}
			if(i == 2){
				st[0][2] = temp[2][2];
				st[1][2] = temp[3][2];
				st[2][2] = temp[0][2];
				st[3][2] = temp[1][2];
			}
			if(i == 3){
				st[0][3] = temp[1][3];
				st[1][3] = temp[2][3];
				st[2][3] = temp[3][3];
				st[3][3] = temp[0][3];
			}
		}
	}
	//code given by Prof. Young for mixColumns, modified slightly to work with my code
    public static void mixColumns(int c) {
    	// This is another alternate version of mixColumn, using the 
    	// logtables to do the computation.

    	byte a[] = new byte[4];

    	// note that a is just a copy of st[.][c]
    	for (int i = 0; i < 4; i++){
    		int x = st[c][i];
    		if(x < 0){
    			x = x + 256;
    		}
    		a[i] = (byte)x;
    		//System.out.println("AI = " + a[i] + " x = " + x);
    	}
    	// This is exactly the same as mixColumns1, if 
    	// the mul columns somehow match the b columns there.
    	st[c][0] = (byte)(mul(2,a[0]) ^ a[2] ^ a[3] ^ mul(3,a[1]));
    	st[c][1] = (byte)(mul(2,a[1]) ^ a[3] ^ a[0] ^ mul(3,a[2]));
    	st[c][2] = (byte)(mul(2,a[2]) ^ a[0] ^ a[1] ^ mul(3,a[3]));
    	st[c][3] = (byte)(mul(2,a[3]) ^ a[1] ^ a[2] ^ mul(3,a[0]));
    } // mixColumn2
    //code given mby Prof. Young for invert mixColumns, modified slightly to work with my code
    public static void inv_mixColumns (int c) {
    	byte a[] = new byte[4];

    	// note that a is just a copy of st[.][c]
    	for (int i = 0; i < 4; i++){
    		int x = st[c][i];
    		if(x < 0){
    			x = x + 256;
    		}
    		a[i] = (byte)x;
    	}

    	st[c][0] = (byte)(mul(14,a[0]) ^ mul(11,a[1]) ^ mul(13, a[2]) ^ mul(9,a[3]));
    	st[c][1] = (byte)(mul(14,a[1]) ^ mul(11,a[2]) ^ mul(13, a[3]) ^ mul(9,a[0]));
    	st[c][2] = (byte)(mul(14,a[2]) ^ mul(11,a[3]) ^ mul(13, a[0]) ^ mul(9,a[1]));
    	st[c][3] = (byte)(mul(14,a[3]) ^ mul(11,a[0]) ^ mul(13, a[1]) ^ mul(9,a[2]));
    } // invMixColumn2
    //gets key from the expanded key sent in, using multiples of 4 to access the 4x4 array, then xor rkey with ST
	public static void addRoundkey(byte[][] xpand, int round){
		byte[][] rkey = new byte[4][4];
		int mult = round * 4;
		for(int i = 0; i < 4; i++){
			rkey[0][i] = xpand[0][(mult + i)];
			rkey[1][i] = xpand[1][(mult + i)];
			rkey[2][i] = xpand[2][(mult + i)];
			rkey[3][i] = xpand[3][(mult + i)];
			//round++;
		}
		xor(rkey);
	}	
	//pads lines that don't have enough hex characters
	public static String pad(String input){
		int num = 32 - input.length();
		while(num > 0){
			input = input + '0';
			num--;
		}
		return input;
	}
	//rotates a single line, used for key expansion
	public static byte[] rotate(byte[] line){
		byte[] temp = new byte[4];
		temp[0] = line[1];
		temp[1] = line[2];
		temp[2] = line[3];
		temp[3] = line[0];
		return temp;		
	}
	//xors st with a 2-d byte array (4x4 from the expanded key)
	public static void xor(byte[][] b){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				st[i][j] =(byte)(st[i][j] ^ b[j][i]);
			}
		}
	}
	//xor 2 1-d byte arrays together
	public static byte[] xor(byte[] a, byte[] b){
		for(int i = 0; i < 4; i++){
			a[i] =(byte)( (int)a[i] ^ (int)b[i]);
		}
		return a;
	}
	//expand the 4xn input key
	public static byte[][] expand_key(byte[][] key){
		byte[] temp = new byte[4];
		byte[] w = new byte[4];
		int count = 0;
		int rconi = 0;
		for(int i = 8; i < 60; i++){
			while(count < 4){
				temp[count] = key[count][i-1];
				w[count] = key[count][i-8];
				count++;
			}
			//on multiples of 8 need to rotate and subByte and xor
			if((i)%8 == 0){
				temp = rotate(temp);
				temp = subBytes(temp);
				int x = (int)rcon[rconi];
				if(x < 0){
					x = x +256;
				}
				byte[] rc = {(byte)x, 0, 0, 0};
				temp = xor(temp, rc);
				temp = xor(temp, w);
				rconi++;
			}
			//on multiples of 4 (that aren't multiples of 8) need to just subbyte and xor
			else if(i%4 == 0 && i%8 != 0){
				temp = subBytes(temp);
				temp = xor(temp, w);
			}
			//everything else just xor
			else{
				temp = xor(temp, w);
			}
			for(int j = 0; j < 4; j++){
				key[j][i] = temp[j];
			}
			count = 0;
		}
		return key;
	}
	//code provided by prof. Young for mixColumns
	private static byte mul (int a, byte b) {
		int inda = (a < 0) ? (a + 256) : a;
		int indb = (b < 0) ? (b + 256) : b;

		if ( (a != 0) && (b != 0) ) {
			int index = (LogTable[inda] + LogTable[indb]);
			byte val = (byte)(AlogTable[ index % 255 ] );
			return val;
		}
		else 
			return 0;
	} // mul
	//print function used to print 4x4 array, used for testing
	public static void print(){
		for(int m = 0; m < 4; m++){
			for(int n = 0; n < 4; n++){
				int x = st[n][m];
				if(x < 0){
					x = x+256;
				}
				String temp = Integer.toHexString(x);
				System.out.print(temp + " ");
			}
			System.out.println();
		}
	}
	//not used, but thought it might be useful - oviously wasn't
	public static int hexValue(char c){
		switch (c) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'a':
		case 'A':
			return 10;
		case 'b':
		case 'B':
			return 11;
		case 'c':
		case 'C':
			return 12;
		case 'd':
		case 'D':
			return 13;
		case 'e':
		case 'E':
			return 14;
		case 'f':
		case 'F':
			return 15;
		default:
			return -1;
		}
	}
	//is the inputline valid?
	public static boolean validLine(String input){
		boolean valid = true;
		for(int q = 0; q < input.length(); q++){
			if(hexValue(input.charAt(q)) == -1){
				valid = false;
				break;
			}
		}
		return valid;
	}
	//builds a string from the 4x4 st matrix, used for output
	public static String output(byte[][] st){
		String line = "";
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				int x = st[i][j];
				if(x < 0){
					x = x + 256;
				}
				String temp = Integer.toHexString(x);
				if(temp.length() == 1){
					temp = "0" + temp;
				}
				line = line + temp;
				line = line.toUpperCase();
			}
		}
		return line;
	}
	
	public static void main(String[] args) throws IOException {
		//variable initializations
		String[] expanded = new String[15];
		int count = 0;
		String inputLine = "";
		byte[][] key = new byte[4][8];
		byte[][] xkey = new byte[4][60];

		String option = args[0];
		String keyFile = args[1];
		String inputFile = args[2];
		File file = new File(args[2]);
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		BufferedReader keyReader = new BufferedReader(new FileReader(keyFile));

		String cypher = keyReader.readLine();
		count = 0;
		//builds the base for expanded key
		while(count < cypher.length()){
			for(int r = 0; r < 8; r++){
				for(int s = 0; s < 4; s++){
					String temp = cypher.substring(count, count+2);
					int b = Integer.parseInt(temp, 16);
					//System.out.println(b);
					key[s][r] = (byte)b;
					count+=2;
				}
			}
		}
		//assigns initial values for expanded key
		for(int z = 0; z < 8; z++){
			for(int y = 0; y < 4; y++){
				xkey[y][z] = key [y][z];
			}
		}
		//expand the key from keyfile
		expand_key(xkey);
		Stopwatch sw = new Stopwatch();
		//check for encryption
		if(option.equalsIgnoreCase("e")){
			//encrypt
			sw.start();
			String outputFilename = args[2] + ".enc";
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
			while((inputLine = input.readLine()) != null){
				if(validLine(inputLine)){
					if(inputLine.length() != 32){
						inputLine = pad(inputLine);
					}
					String sub = "";
					int val;
					int valcheck;
					int row = 0;
					int col = 0;
					for(int i = 0; i < inputLine.length(); i+=2){
						sub = inputLine.substring(i, i+2);
						valcheck = (byte)Integer.parseInt(sub, 16);
						val = (valcheck < 0) ? (valcheck + 256) : valcheck;
						st[row][col] = (byte) val;
						col++;
						if(col == 4){
							row++;
							col = 0;
						}
						if(row == 4){
							break;
						}
					}
					//encryption
					for(int q = 0; q < rounds; q++){
						if(q == 0){
							addRoundkey(xkey, q);
						}
						else if (q == rounds - 1){
							subBytes();
							shiftRows();
							addRoundkey(xkey, q);
						}
						else{
							subBytes();
							shiftRows();
							for(int c = 0; c < 4; c++){
								mixColumns(c);
							}
							addRoundkey(xkey, q);
						}
					}
					String sendit = output(st);
					out.write(sendit);
					out.write("\n");
				}
				else{
					out.write(inputLine);
					out.write("\n");
				}
			}
			out.close();
			sw.stop();
//			output for timing data
//			System.out.println("Time elapsed = " + sw.time() + " seconds");
//			System.out.println("File size = " + file.length() + " bits");
		}
		//test for decryption from command line
		else if((option.equalsIgnoreCase("d"))){
			//decrypt
			sw.start();
			String outputFilename = args[2] + ".dec";
			BufferedWriter outd = new BufferedWriter(new FileWriter(outputFilename));
			while((inputLine = input.readLine()) != null){
				if(validLine(inputLine)){
					if(inputLine.length() != 32){
						inputLine = pad(inputLine);
					}
					String sub = "";
					int val;
					int valcheck;
					int row = 0;
					int col = 0;
					for(int i = 0; i < inputLine.length(); i+=2){
						sub = inputLine.substring(i, i+2);
						valcheck = (byte)Integer.parseInt(sub, 16);
						val = (valcheck < 0) ? (valcheck + 256) : valcheck;
						st[row][col] = (byte) val;
						col++;
						if(col == 4){
							row++;
							col = 0;
						}
						if(row == 4){
							break;
						}
					}
					//decryption
					for(int q = rounds; q > 0; q--){
						if(q == 15){
							addRoundkey(xkey, q-1);
							inv_subBytes();
							inv_shiftRows();
						}
						else if (q > 1){
							addRoundkey(xkey, q-1);
							for(int z = 0; z < 4; z++){
								inv_mixColumns(z);
							}
							inv_subBytes();
							inv_shiftRows();
						}
						else{
							addRoundkey(xkey, q-1);
						}
					}

					String sendit = output(st);
					if(sendit.length() != 32){
						sendit = pad(sendit);
					}
					outd.write(sendit);
					outd.write("\n");
				}
				else{
					if(inputLine.length() != 32){
						inputLine = pad(inputLine);
					}
					outd.write(inputLine);
					outd.write("\n");
				}
			}
			outd.close();
			sw.stop();
			//output for timing data
//			System.out.println("Time elapsed = " + sw.time() + " seconds");
//			System.out.println("File size = " + file.length() + " bits");
		}
		//if user enters something besides e/d, throw error message.
		else{
			System.out.println("Invalid option <" + option + "> please try again.");
		}
	}
}
