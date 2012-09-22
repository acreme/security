import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.text.DecimalFormat;

public class Encoder{	
	
	//bunch of variables that I ended up needing.
	private static int[] nums = new int[0];
	private static double[] probs = new double[0]; 
	private static final HashMap<String, Double> lookup = new HashMap<String, Double>();
	private static final HashMap<Integer, String> getStr = new HashMap<Integer, String>();
	private static final HashMap<Character, String> encoding = new HashMap<Character, String>();
	private static final HashMap<String, Character> decoding = new HashMap<String, Character>();
	private static final HashMap<String, String> encoding2 = new HashMap<String, String>();
	private static final HashMap<String, String> decoding2 = new HashMap<String, String>();
	private static int symbols = 0;
	private static int symbols2 = 0;
	private static int count_secO = 0;
	private static int num_alpha = 0;
	
	//get a probability for a specific char
	public static double getPro(int total, int current){
		return ((double) current/total);
	}
	//get the number of occurences for a specific char
	public static int[] getOcc(String alphabet){
		int[] probs = new int[num_alpha];		
		return probs;
	}
	//create a file of random length using the probabilities we've calculated
	public static void createFile(int size, char[] chars) throws IOException{
		int count = 0;
		int random = 0;
		String outputFile = "inputTextFile";
		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
		while(count < size){
			random = random(chars.length-1);
			char c = chars[random];
			out.write(c);
			count++;
		}
		out.close();

		
	}
	//random number between 0 and max, used for selected next char
	public static int random(int max){
		int random = 0;
		random = (int)(Math.random() * (max + 1));
		return random;
	}
	//generates an array to be used as an index for selecting a random char
	public static char[] generateRandoArray(int total_occ){
		char[] rando = new char[total_occ];
		int place = 0;
		//populate
		for(int j = 0; j < num_alpha; j++){
			for(int n = 0; n < nums[j]; n++){
				rando[place] = (char)(j + 65);
				place++;
			}
		}
		return rando;
	}
	
	//The following code is a Huffman algorithm from http://rosettacode.org/wiki/Huffman_coding#Java
    // input is an array of frequencies, indexed by character code
    public static HuffmanTree buildTree(int[] charFreqs) {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();
        // initially, we have a forest of leaves
        // one for each non-empty character
        for (int i = 0; i < charFreqs.length; i++)
            if (charFreqs[i] > 0)
                trees.offer(new HuffmanLeaf(charFreqs[i], (char)i));
 
        assert trees.size() > 0;
        // loop until there is only one tree left
        while (trees.size() > 1) {
            // two trees with least frequency
            HuffmanTree a = trees.poll();
            HuffmanTree b = trees.poll();
 
            // put into new node and re-insert into queue
            trees.offer(new HuffmanNode(a, b));
        }
        return trees.poll();
    }
    //END BORROWED CODE
    //The following code is a Huffman algorithm fromhttp://rosettacode.org/wiki/Huffman_coding#Java
    public static void printCodes(HuffmanTree tree, StringBuffer prefix) {
        assert tree != null;
        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;
 
            // print out character, frequency, and code for this leaf (which is just the prefix)
            //System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);
            String pref = prefix.toString();
            //MODIFIED - added key/value pairs to a couple hashmaps for encoding/decoding
            encoding.put(leaf.value, pref);
            decoding.put(pref, leaf.value);
           // System.out.println(pref.length());
            symbols = symbols + pref.length();
 
        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;
 
            // traverse left
            prefix.append('0');
            printCodes(node.left, prefix);
            prefix.deleteCharAt(prefix.length()-1);
 
            // traverse right
            prefix.append('1');
            printCodes(node.right, prefix);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }
    //END BORROWED CODE
    //START MODIFIED BORROWED CODE
    public static HuffmanTree2 buildTree2(int[] charFreqs2) {
        PriorityQueue<HuffmanTree2> trees = new PriorityQueue<HuffmanTree2>();
        for (int i = 0; i < charFreqs2.length; i++)
            if (charFreqs2[i] > 0)
                trees.offer(new HuffmanLeaf2(charFreqs2[i], getStr.get(i)));
 
        assert trees.size() > 0;
        while (trees.size() > 1) {
            HuffmanTree2 a = trees.poll();
            HuffmanTree2 b = trees.poll(); 
            trees.offer(new HuffmanNode2(a, b));
        }
        return trees.poll();
    }
    public static void printCodes2(HuffmanTree2 tree, StringBuffer prefix) {
        assert tree != null;
        count_secO++;
        if (tree instanceof HuffmanLeaf2) {
            HuffmanLeaf2 leaf = (HuffmanLeaf2)tree;
           // System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);
            String pref = prefix.toString();
            //MODIFIED - added key/value pairs to a couple hashmaps for encoding/decoding
            encoding2.put(leaf.value, pref);
            decoding2.put(pref, leaf.value);
            symbols2 = symbols2 + pref.length();
            //System.out.println("Symbols2 = " + symbols2);
            //System.out.println("Count = " + count_secO);
        } else if (tree instanceof HuffmanNode2) {
            HuffmanNode2 node = (HuffmanNode2)tree;
 
            // traverse left
            prefix.append('0');
            printCodes2(node.left, prefix);
            prefix.deleteCharAt(prefix.length()-1);
 
            // traverse right
            prefix.append('1');
            printCodes2(node.right, prefix);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }
    //END BORROWED CODE
	
	public static void main (String[] args) throws IOException{
		//variables
		int t_occ = 0;
		String inputLine = "";
		String sizeLine = "";
		int temp = 0;
		int alphaNum = 0;
		double entropy = 0;
		double probability = 0;
		String inputFile = args[0];
		
		//counting the lines in the input file, used for array initialization
		BufferedReader setArrayS = new BufferedReader(new FileReader(inputFile));
		while((sizeLine = setArrayS.readLine()) != null){
			num_alpha++;
		}
		//initialize arrays
		nums = new int[num_alpha];
		probs = new double[num_alpha];
		
		//populate the arrays
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		while((inputLine = input.readLine()) != null){
			temp = Integer.parseInt(inputLine);
			nums[alphaNum] = temp;
			t_occ = t_occ + temp;
			alphaNum++;
		}
		//populate probability array
		for(int i = 0; i < num_alpha; i++){
			probability = (double)nums[i]/t_occ;
			probs[i] = probability;
			entropy = entropy + (probability * (Math.log(probability)));
		}
		//generate an array based on the number of occurences, to get frequencies
		char[] rando = generateRandoArray(t_occ);
		String freq = "";
		for(int j = 0; j < rando.length; j++){			
			char c = rando[j];
			freq = freq + c;
		}
		
		//create random input file, first number is of how many characters you'd like
		createFile(100000, rando);
		//created array for 2nd order probabilities
		double[] extended = new double[676];
		
		//populate array for 2nd order, also add strings to lookup hashmap
		int count = 0;
		String toStore = "";
		for(int k = 0; k < num_alpha; k++){
			for(int l = 0; l < num_alpha; l++){
				double comb = probs[k]*probs[k];
				extended[count] = comb;
				char first = (char)(k + 65);
				char second = (char)(l + 65);
				toStore = toStore + first + second;
				lookup.put(toStore, comb);
				toStore = "";
			}
		}
		
		//create frequency array for 2nd order, also put strings into hashmap
		int[] charFreqs2 = new int[676];
		String line = "";
		BufferedReader br2 = new BufferedReader(new FileReader("inputTextFile"));
		line = br2.readLine();
		String curr_str = "";
		for(int z = 0; z < line.length()-2; z+=2 ){
			char a = line.charAt(z);
			char b = line.charAt(z+1);
			int index = ((int)a -65) + ((int)b -65);
			curr_str = curr_str + a + b;
			getStr.put(index, curr_str);
			charFreqs2[((int)a -65) + ((int)b -65)]++;
			curr_str = "";
		}
		
		//negate entropy as per the formula
		entropy = 0 - entropy;
		//print out the entorpy
		System.out.println("Entropy = " + entropy);
		
		
		
		//The following code is a Huffman algorithm fromhttp://rosettacode.org/wiki/Huffman_coding#Java
        // we will assume that all our characters will have
        // code less than 256, for simplicity
        int[] charFreqs = new int[256];
        // read each character and record the frequencies
        for (char c : freq.toCharArray())
            charFreqs[c]++;
 
        // build tree
        HuffmanTree tree = buildTree(charFreqs);
        // print out results
        //System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");
        //END BORROWED CODE
        
        //print out results of 1st order Huffman
        printCodes(tree, new StringBuffer());
        double efficiency = (double)symbols/num_alpha;
        System.out.println("Efficiency of 1-order Huffman = " + efficiency);
        System.out.println("Percentage difference from Entropy = " + ((double)efficiency/entropy));
        
        //build and print results for 2nd order Huffman
        HuffmanTree2 tree2 = buildTree2(charFreqs2);
        printCodes2(tree2, new StringBuffer());
        double efficiency2 = (double)symbols2/count_secO;
        System.out.println("Efficiency of 2-order Huffman = " + efficiency2);
        System.out.println("Percentage difference from Entropy = " + ((double)efficiency2/entropy));
        
        //encoding using 1st order encoding
        String inputL = "";
        BufferedReader br = new BufferedReader(new FileReader("inputTextFile"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("encoded"));
        while((inputL = br.readLine()) != null){
        	for(int i = 0; i < inputL.length(); i++){
        		char ch = inputL.charAt(i);
        		String enc = encoding.get(ch);
        		bw.write(enc);
        		//System.out.print(enc);
        	}
        }
        bw.close();
        
        //decoding for 2nd order encoding
        BufferedReader brd = new BufferedReader(new FileReader("encoded"));
        BufferedWriter bwd = new BufferedWriter(new FileWriter("outputTextFile"));
        String build = "";
        while((inputL = brd.readLine()) != null){
        	for(int i = 0; i < inputL.length(); i++){
        		char ch = inputL.charAt(i);
        		build = build + ch;
        		if(decoding.get(build) != null){
        			//System.out.print(decoding.get(build));
        			bwd.write(decoding.get(build));
        			build = "";
        		}  		
        	}
        }
        bwd.close();

        
		
	}
}
//The following code is a Huffman algorithm from http://rosettacode.org/wiki/Huffman_coding#Java
abstract class HuffmanTree implements Comparable<HuffmanTree> {
    public final int frequency; // the frequency of this tree
    public HuffmanTree(int freq) { frequency = freq; }
 
    // compares on the frequency
    public int compareTo(HuffmanTree tree) {
        return frequency - tree.frequency;
    }
}
//The following code is a Huffman algorithm from http://rosettacode.org/wiki/Huffman_coding#Java
class HuffmanLeaf extends HuffmanTree {
    public final char value; // the character this leaf represents
 
    public HuffmanLeaf(int freq, char val) {
        super(freq);
        value = val;
    }
}
//The following code is a Huffman algorithm from http://rosettacode.org/wiki/Huffman_coding#Java
class HuffmanNode extends HuffmanTree {
    public final HuffmanTree left, right; // subtrees
 
    public HuffmanNode(HuffmanTree l, HuffmanTree r) {
        super(l.frequency + r.frequency);
        left = l;
        right = r;
    }
}
//END BORROWED CODE
//The following code is MODIFIED from Huffman algorithm from http://rosettacode.org/wiki/Huffman_coding#Java
abstract class HuffmanTree2 implements Comparable<HuffmanTree2> {
    public final int frequency; // the frequency of this tree
    public HuffmanTree2(int freq) { frequency = freq; }
 
    // compares on the frequency
    public int compareTo(HuffmanTree2 tree) {
        return frequency - tree.frequency;
    }
}
class HuffmanLeaf2 extends HuffmanTree2 {
    public final String value; // the character this leaf represents
    //modified this to allow for strings instead of chars
    public HuffmanLeaf2(int freq, String val) {
        super(freq);
        value = val;
    }
}

class HuffmanNode2 extends HuffmanTree2 {
    public final HuffmanTree2 left, right; // subtrees
 
    public HuffmanNode2(HuffmanTree2 l, HuffmanTree2 r) {
        super(l.frequency + r.frequency);
        left = l;
        right = r;
    }
}
//END MODIFIED BORROWED CODE

